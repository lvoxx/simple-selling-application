package com.shitcode.demo1.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.component.MediaDetector;
import com.shitcode.demo1.exception.model.EmptyFileException;
import com.shitcode.demo1.exception.model.FileReadException;
import com.shitcode.demo1.exception.model.FolderNotFoundException;
import com.shitcode.demo1.exception.model.ImageEncodeException;
import com.shitcode.demo1.exception.model.UnknownFileExtension;
import com.shitcode.demo1.exception.model.VideoEncodeException;
import com.shitcode.demo1.helper.CustomMultipartFile;
import com.shitcode.demo1.properties.LvoxxServerConfigData;
import com.shitcode.demo1.properties.MediaConfigData;
import com.shitcode.demo1.service.MediaService;
import com.shitcode.demo1.utils.LogPrinter;
import com.shitcode.demo1.utils.LogPrinter.Flag;
import com.shitcode.demo1.utils.LogPrinter.Type;
import com.shitcode.demo1.utils.LoggingModel;

import jakarta.annotation.PostConstruct;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import net.coobird.thumbnailator.Thumbnails;

/**
 * Service implementation for managing media files locally.
 * Supports saving, retrieving, and compressing images and videos.
 */
@Service
@LogCollector(loggingModel = LoggingModel.SERVICE)
@ConditionalOnProperty(havingValue = "true", matchIfMissing = true, name = "upload-locally", prefix = "media.path")
public class LocalMediaServiceImpl implements MediaService {
    private static final String MEDIA_ROUTE = "/media";
    private final MediaConfigData mediaConfigData;
    private final LvoxxServerConfigData lvoxxServerConfigData;
    private final MessageSource messageSource;

    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;
    private final String servletContextPath;

    private String imagesPath;
    private String videosPath;
    private MediaConfigData.ImageCompressConfig imageCompressConfig;
    private MediaConfigData.VideoCompressConfig videoCompressConfig;
    private MediaConfigData.PathConfig pathConfig;

    /**
     * Constructor for LocalMediaServiceImpl.
     * 
     * @param mediaConfigData       The media configuration data
     * @param ffmpegPath            The path to the FFmpeg executable
     * @param ffprobePath           The path to the FFprobe executable
     * @param lvoxxServerConfigData The Lvoxx server configuration data
     * @throws IOException If the FFmpeg or FFprobe path is not found
     */
    public LocalMediaServiceImpl(MediaConfigData mediaConfigData,
            @Value("${server.compression.ffmpeg}") String ffmpegPath,
            @Value("${server.compression.ffprobe}") String ffprobePath,
            @Value("${server.servlet.context-path}") String servletContextPath,
            LvoxxServerConfigData lvoxxServerConfigData,
            MessageSource messageSource)
            throws IOException {
        this.mediaConfigData = mediaConfigData;
        this.lvoxxServerConfigData = lvoxxServerConfigData;
        this.messageSource = messageSource;
        this.servletContextPath = servletContextPath;
        if (!StringUtils.isEmpty(ffmpegPath)) {
            this.ffmpeg = new FFmpeg(ffmpegPath);
            LogPrinter.printLog(Type.INFO, Flag.START_UP, "Found FFmpeg path. Init FFmpeg.");
        } else {
            this.ffmpeg = new FFmpeg();
            LogPrinter.printLog(Type.ERROR, Flag.START_UP, "Can not found FFmpeg path.");
        }
        if (!StringUtils.isEmpty(ffprobePath)) {
            this.ffprobe = new FFprobe(ffprobePath);
            LogPrinter.printLog(Type.INFO, Flag.START_UP, "Found FFprobe path. Init FFprobe.");
        } else {
            LogPrinter.printLog(Type.ERROR, Flag.START_UP, "Can not found FFprobe path.");
            this.ffprobe = new FFprobe();
        }
    }

    /**
     * Initializes media paths after dependency injection.
     */
    @PostConstruct
    void setUp() {
        pathConfig = mediaConfigData.getPath();
        imagesPath = pathConfig.getImages();
        videosPath = pathConfig.getVideos();

        // Log the media root path for verification
        LogPrinter.printLog(Type.INFO, "MEDIA_CONFIG",
                String.format("Media root path: %s", pathConfig.getRoot()));

        imageCompressConfig = mediaConfigData.getCompress().getImage();
        videoCompressConfig = mediaConfigData.getCompress().getVideo();
    }

    /**
     * Saves a single image file to the server and returns its URL.
     * 
     * This method detects the MIME type of the provided image file and ensures it
     * is a valid image. It then saves the image to the server, compresses it if
     * necessary, and generates a URL for the compressed image. The URL of the
     * compressed image is returned.
     * 
     * @param image A single image file to be saved
     * @return The URL pointing to the saved and compressed image
     * @throws Exception If any error occurs during the saving or compression
     *                   process
     */
    @Override
    public String saveImageFile(MultipartFile image)
            throws EmptyFileException, UnknownFileExtension, IOException {
        if (image.isEmpty()) {
            throw new EmptyFileException(messageSource.getMessage("validation.product.images.not-null",
                    new Object[] {}, Locale.getDefault()));
        }

        String mimeType = MediaDetector.detect(image.getInputStream());

        if (!(mimeType.startsWith("image/"))) {
            throw new UnknownFileExtension(messageSource.getMessage("validation.product.images.valid",
                    new Object[] {}, Locale.getDefault()));
        }
        String originalLocation = saveFileToServer(image, TypeOfMedia.Images, false);;

        return generateMediaUrl(
            removeRootPath(compressImage(originalLocation)));
    }

    /**
     * Saves a video file to the server and returns its URL.
     * 
     * This method detects the MIME type of the provided video file and ensures it
     * is a valid video.
     * It then saves the video to the server, compresses it if necessary, and
     * generates a URL for the
     * compressed video. The URL of the compressed video is returned.
     * 
     * @param video The video file to be saved
     * @return The URL pointing to the saved and compressed video
     * @throws Exception If any error occurs during the saving or compression
     *                   process
     */
    @Override
    public String saveVideoFile(MultipartFile video) throws EmptyFileException, UnknownFileExtension, IOException {
        if (video == null || video.isEmpty()) {
            return null;
        }

        String mimeType = MediaDetector.detect(video.getInputStream());

        if (!(mimeType.startsWith("video/"))) {
            throw new UnknownFileExtension(messageSource.getMessage("validation.product.video.valid",
                    new Object[] {}, Locale.getDefault()));
        }
        String location = saveFileToServer(video, TypeOfMedia.Videos, false);

        return generateMediaUrl(removeRootPath(compressVideo(location)));
    }

    /**
     * Retrieves a media file from the local storage system as a Spring Resource.
     * 
     * <p>
     * This method attempts to locate and return a media file based on its relative
     * path
     * within the media storage directory. The path should be in the format:
     * {@code /images|videos/original|compressed/day/month/year/filename.extension}
     * </p>
     * 
     * <p>
     * Example valid paths:
     * </p>
     * <ul>
     * <li>{@code /images/compressed/2025/4/4/image-uuid.jpg}</li>
     * <li>{@code /videos/original/2025/4/4/video-uuid.mp4}</li>
     * </ul>
     *
     * @param filePathAndNameWithExtension The relative path to the media file,
     *                                     including filename and extension.
     *                                     Must start with a forward slash.
     * @return A Spring {@link Resource} object representing the found media file
     * @throws FileNotFoundException    if the file doesn't exist, is not readable,
     *                                  or the path is invalid
     * @throws IllegalArgumentException if the provided path is null or empty
     * @see org.springframework.core.io.Resource
     * @see #saveFileToServer(MultipartFile, TypeOfMedia, boolean)
     */
    @Override
    public Resource findFile(String filePathAndNameWithExtension) throws FileNotFoundException, FileReadException {
        Resource resource = null;
        try {
            Path filePath = Paths.get(pathConfig.getRoot().concat(filePathAndNameWithExtension))
                    .normalize();

            LogPrinter.printLog(Type.DEBUG, Flag.SERVICE_FLAG,
                    String.format("Attempting to find file at: %s", filePath));

            resource = new UrlResource(filePath.toUri());
            if (!(resource.exists()) || !(resource.isReadable())) {
                throw new FileNotFoundException(messageSource.getMessage("exception.media.file-not-found",
                        new Object[] { filePathAndNameWithExtension }, Locale.getDefault()));
            }
        } catch (Exception e) {
            LogPrinter.printServiceLog(Type.ERROR,
                    "LocalMediaServiceImpl",
                    "findFile",
                    e.getMessage());
            throw new FileReadException(messageSource.getMessage("exception.media.file-not-found",
                    new Object[] { filePathAndNameWithExtension }, Locale.getDefault()));
        }

        return resource;
    }

    @Override
    public String updateImage(MultipartFile image, String oldUrl) throws FileNotFoundException, IOException {
        String imageFolderPath = removeBaseServerUrlThenChangeToRootPath(oldUrl);
        deleteFile(imageFolderPath);
        return saveImageFile(image);
    }

    @Override
    public String updateVideo(MultipartFile video, String oldUrl) throws FileNotFoundException, IOException {
        // Map url to folder path
        String videoFolderPath = removeBaseServerUrlThenChangeToRootPath(oldUrl);
        // Delete old video from folder path
        deleteFile(videoFolderPath);
        // Upload new video
        return saveVideoFile(video);
    }

    /**
     * Deletes a media file from the local storage system.
     * 
     * <p>
     * This implementation first validates the file path, then attempts to delete
     * the file.
     * If the file does not exist, a {@link FileNotFoundException} is thrown.
     * If the file is a directory and not empty, a {@link IOException} is thrown.
     * If the file cannot be deleted for any other reason, a {@link IOException} is
     * thrown.
     * </p>
     * 
     * @param filePathAndNameWithExtension The relative path to the media file,
     *                                     including filename and extension.
     *                                     Must start with a forward slash.
     * @throws IOException              if the file cannot be deleted due to an I/O
     *                                  error, such as
     *                                  a disk full or file system read-only.
     * @throws FileNotFoundException    if the file doesn't exist, is not readable,
     *                                  or the path is invalid
     * @throws IllegalArgumentException if the provided path is null or empty
     * @throws EmptyFileException       if the file is empty
     * @throws FolderNotFoundException  if the directory of the file does not exist
     * @see #saveFileToServer(MultipartFile, TypeOfMedia, boolean)
     */
    @Override
    public void deleteFile(String filePathAndNameWithExtension) throws IOException, FileNotFoundException {
        String realPathInServer = removeBaseServerUrlThenChangeToRootPath(filePathAndNameWithExtension);
        LogPrinter.printLog(Type.DEBUG, Flag.SERVICE_FLAG,
                String.format("Attempting to delete file at the real path in server: %s", realPathInServer));

        Path filePath = Paths.get(realPathInServer)
                .normalize();

        LogPrinter.printLog(Type.DEBUG, Flag.SERVICE_FLAG,
                String.format("Attempting to delete file at: %s", filePath));

        try {
            Files.delete(filePath);
            LogPrinter.printLog(Type.DEBUG, Flag.SERVICE_FLAG,
                    String.format("File at %s has been successfully deleted", filePath));
        } catch (NoSuchFileException e) {
            throw new FileNotFoundException(messageSource.getMessage("exception.media.file-not-found",
                    new Object[] { realPathInServer }, Locale.getDefault()));
        } catch (DirectoryNotEmptyException e) {
            throw new IOException(messageSource.getMessage("exception.media.directory-not-empty",
                    new Object[] { realPathInServer }, Locale.getDefault()));
        } catch (IOException e) {
            LogPrinter.printServiceLog(Type.ERROR,
                    "LocalMediaServiceImpl",
                    "deleteFile",
                    e.getMessage());
            throw new IOException(messageSource.getMessage("exception.media.file-delete-failed",
                    new Object[] { realPathInServer }, Locale.getDefault()));
        }
    }

    /**
     * Deletes multiple media files from the local storage system.
     * 
     * <p>
     * This implementation processes each file deletion sequentially. For each file:
     * </p>
     * <ul>
     * <li>Validates the file path</li>
     * <li>Attempts to delete the file</li>
     * <li>Continues to next file even if one fails</li>
     * </ul>
     * 
     * <p>
     * <b>Note:</b> This method:
     * </p>
     * <ul>
     * <li>Processes deletions one at a time</li>
     * <li>Will attempt to delete all files even if some fail</li>
     * <li>Throws an exception on the first encountered error</li>
     * </ul>
     * 
     * <p>
     * <b>Example path format:</b>
     * {@code /images|videos/original|compressed/day/month/year/filename.ext}
     * </p>
     *
     * @param filePathsAndNamesWithExtensions List of relative paths to files to be
     *                                        deleted.
     *                                        Each path should be relative to the
     *                                        media root directory.
     * 
     * @throws FileNotFoundException if any file in the list doesn't exist
     * @throws IOException           if any deletion operation fails (e.g.,
     *                               permission issues)
     * 
     * @see #deleteFile(String) for single file deletion
     * @see MediaService#deleteFiles(List) for the interface specification
     * 
     * @implNote Consider implementing batch deletion or parallel processing for
     *           better performance
     *           with large numbers of files in future versions.
     */
    @Override
    public void deleteFiles(List<String> filePathsAndNamesWithExtensions) throws IOException, FileNotFoundException {
        for (String filePathAndNameWithExtension : filePathsAndNamesWithExtensions) {
            deleteFile(filePathAndNameWithExtension);
        }
    }

    /**
     * Deletes a single directory and all its contents from the storage system.
     * 
     * <p>
     * Uses Apache Commons IO for recursive directory deletion.
     * </p>
     *
     * @param folderName The absolute or relative path to the folder
     * @throws FolderNotFoundException if the folder doesn't exist or deletion fails
     * 
     * @see org.apache.commons.io.FileUtils#deleteDirectory(File)
     */
    @Override
    public void deleteFolder(String folderName) throws FolderNotFoundException {
        try {
            FileUtils.deleteDirectory(new File(folderName));
        } catch (IOException e) {
            throw new FolderNotFoundException(e.getMessage());
        }
    }

    /**
     * Deletes multiple directories and their contents from the storage system.
     * 
     * <p>
     * Processes each folder sequentially. Continues to next folder even if one
     * fails.
     * </p>
     *
     * @param folderNames List of folder paths to delete
     * @throws FolderNotFoundException if any folder doesn't exist or deletion fails
     * 
     * @see #deleteFolder(String) for single folder deletion
     */
    @Override
    public void deleteFolder(List<String> folderNames) throws FolderNotFoundException {
        folderNames.forEach(folder -> {
            deleteFolder(folder);
        });
    }

    /**
     * Saves a file to the local media storage directory based on the specified
     * media type.
     * This method generates a unique filename for the file, creates the necessary
     * directory structure
     * if it doesn't exist, and writes the file to the specified location.
     * 
     * @param file         The file to be saved, which must be a valid
     *                     MultipartFile.
     * @param type         The type of media to be saved, which can be either
     *                     TypeOfMedia.IMAGE or TypeOfMedia.VIDEO.
     * @param isCompressed Indicates if the file is compressed or not. This affects
     *                     the directory structure.
     * @return The full path to the saved file, including the filename and
     *         extension.
     * @throws IOException If the file cannot be saved due to an I/O error, such as
     *                     a disk full or file system read-only.
     */
    @Override
    public String saveFileToServer(MultipartFile file, TypeOfMedia type, boolean isCompressed) throws IOException {
        // Get the absolute directory path
        String dirPath = makeMediaPath(type, isCompressed);

        // Generate unique filename with extension
        String fileName = String.format("%s.%s",
                UUID.randomUUID().toString(),
                FilenameUtils.getExtension(file.getOriginalFilename()));

        // Create directory if it doesn't exist
        Path directoryPath = Paths.get(dirPath);
        Files.createDirectories(directoryPath);

        // Create absolute path by combining directory and filename
        Path absolutePath = directoryPath.resolve(fileName);

        // Write the file
        Files.write(absolutePath, file.getBytes());

        // Return the absolute path as string
        return absolutePath.toAbsolutePath().normalize().toString();
    }

    /**
     * Compresses an image and saves it in JPG format with optimized settings.
     * The compression process includes:
     * <ul>
     * <li>Resizing to
     * {@value #IMAGE_COMPRESSION_WIDTH}x{@value #IMAGE_COMPRESSION_HEIGHT}
     * pixels</li>
     * <li>Quality reduction to {@value #IMAGE_COMPRESSION_QUALITY}</li>
     * <li>Conversion to {@value #IMAGE_FORMAT} format</li>
     * </ul>
     *
     * @param originalLocation The absolute path to the original image file
     * @return The absolute path to the compressed image file
     * @throws IOException           If the original file cannot be read, or if
     *                               compression/saving fails
     * @throws FileNotFoundException If the original file does not exist
     * @see #saveFileToServer(MultipartFile, TypeOfMedia, boolean)
     */
    private String compressImage(String originalLocation) throws IOException {
        File originalImage = new File(originalLocation);
        InputStream inputStream = new FileInputStream(originalImage);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            Thumbnails.of(inputStream)
                    .size(imageCompressConfig.getWidth(), imageCompressConfig.getHeight())
                    .outputQuality(imageCompressConfig.getQuality())
                    .outputFormat(imageCompressConfig.getFormat())
                    .toOutputStream(outputStream);
        } catch (IOException e) {
            LogPrinter.printServiceLog(LogPrinter.Type.ERROR,
                    "LocalMediaServiceImpl",
                    "compressImage",
                    String.format("Error compressing image: %s", e.getMessage()));
            throw new ImageEncodeException(e.getMessage());
        }
        MultipartFile compressFile = new CustomMultipartFile(outputStream.toByteArray(), originalImage.getName());
        String compressPath = saveFileToServer(compressFile, TypeOfMedia.Images, true);
        return compressPath;
    }

    /**
     * Compresses a video using FFmpeg.
     *
     * @param originalLocation Path to the original video
     * @return Path to the compressed video
     * @throws IOException If compression fails
     */
    private String compressVideo(String originalLocation) throws IOException {
        String dirPath = makeMediaPath(TypeOfMedia.Videos, true);
        String filePath = new StringBuilder(dirPath)
                .append(File.separator).append(UUID.randomUUID().toString()).append(".")
                .append(videoCompressConfig.getFormat())
                .toString();

        // Create all necessary parent directories
        Path directory = Paths.get(dirPath);
        Files.createDirectories(directory);

        LogPrinter.printServiceLog(LogPrinter.Type.INFO,
                "LocalMediaServiceImpl",
                "compressVideo",
                String.format("Compressed video path: %s", filePath));

        FFmpegProbeResult input = ffprobe.probe(originalLocation);

        // Get original video dimensions
        int originalWidth = input.streams.stream()
                .filter(stream -> stream.codec_type == FFmpegStream.CodecType.VIDEO)
                .findFirst()
                .map(stream -> stream.width)
                .orElse(1920); // default to 1920 if can't determine

        int originalHeight = input.streams.stream()
                .filter(stream -> stream.codec_type == FFmpegStream.CodecType.VIDEO)
                .findFirst()
                .map(stream -> stream.height)
                .orElse(1080); // default to 1080 if can't determine

        // Calculate new dimensions (75% of original)
        int newWidth = (int) (originalWidth * videoCompressConfig.getResolution());
        int newHeight = (int) (originalHeight * videoCompressConfig.getResolution());

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(input)
                .overrideOutputFiles(true)
                .addOutput(filePath)
                .setFormat(videoCompressConfig.getFormat())
                .disableSubtitle()

                // Audio Configuration (lightweight)
                .setAudioChannels(videoCompressConfig.getAudioChannel())
                .setAudioCodec(videoCompressConfig.getAudioCodec())
                .setAudioSampleRate(videoCompressConfig.getAudioSampleRate()) // Lower sample rate to save space
                .setAudioBitRate(videoCompressConfig.getAudioBitRate()) // Slightly higher bitrate for clarity

                // Video Configuration (optimized for high requests)
                .setVideoCodec(videoCompressConfig.getVideoCodec())
                .setVideoFrameRate(videoCompressConfig.getVideoFrameRate(), 1)
                .setVideoResolution(newWidth, newHeight)

                // Extra Process Configuration
                .addExtraArgs("-crf", videoCompressConfig.getExtraArgsCrf()) // Lower CRF for better quality (22-28
                                                                             // range)
                .addExtraArgs("-preset", videoCompressConfig.getExtraArgsPreset()) // Faster encoding, slight file size
                                                                                   // tradeoff
                .addExtraArgs("-tune", videoCompressConfig.getExtraArgsTune()) // Low-latency tuning for fast processing

                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                .done();

        try {
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            executor.createJob(builder, new ProgressListener() {

                final double duration_ns = input.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

                @Override
                public void progress(Progress progress) {
                    double percentage = (double) progress.out_time_ns / duration_ns;
                    LogPrinter.printServiceLog(LogPrinter.Type.INFO,
                            "MediaServiceImpl",
                            "compressVideo",
                            String.format("Filename: %s -> %d status: %s time: %d ms",
                                    input.getFormat().filename, // Correctly referenced
                                    String.format("%.0f", percentage * 100), // Properly formatted percentage
                                    progress.status,
                                    FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS)));
                }
            }).run();
        } catch (Exception e) {
            LogPrinter.printLog(Type.ERROR, Flag.SERVICE_FLAG, e.getMessage());
            throw new VideoEncodeException(messageSource.getMessage("exception.media.video-encode",
                    new Object[] {}, Locale.getDefault()));
        }

        return filePath;
    }

    /**
     * Generates a media path based on the specified media type and compression
     * status.
     * 
     * This method constructs a path for storing media files based on the type of
     * media (Images or Videos) and
     * whether the media is compressed or not. The path includes the root directory,
     * media type directory, compression
     * status directory, and the current date. The structure of the folder is as
     * follows:
     * 
     * Root Directory
     * |--- Media Type Directory (Images or Videos)
     * | |--- Compression Status Directory (Compressed or Original)
     * | | |--- Year
     * | | | |--- Month
     * | | | | |--- Day
     * 
     * @param type       The type of media to be stored. This can be either Images
     *                   or Videos.
     * @param isCompress Indicates if the media is compressed or not. If true, the
     *                   path will point to the compressed media
     *                   directory; otherwise, it will point to the original media
     *                   directory.
     * @return A string representing the generated media path.
     */
    private String makeMediaPath(TypeOfMedia type, boolean isCompress) {
        String internalFolder = isCompress ? pathConfig.getCompressed()
                : pathConfig.getOriginal();
        LocalDate now = LocalDate.now();

        // Normalize the root path first
        String rootPath = pathConfig.getRoot().endsWith(File.separator)
                ? pathConfig.getRoot().substring(0, pathConfig.getRoot().length() - 1)
                : pathConfig.getRoot();

        String dirPath = Paths.get(
                rootPath,
                getPath(type),
                internalFolder,
                String.valueOf(now.getYear()),
                String.valueOf(now.getMonthValue()),
                String.valueOf(now.getDayOfMonth()))
                .toString();

        return dirPath;
    }

    /**
     * This method gets the path based on the type of media.
     * 
     * @param type The type of media (Images or Videos).
     * @return The path of the media.
     */
    private String getPath(TypeOfMedia type) {
        if (type == null) {
            throw new IllegalArgumentException("Media type and compression status cannot be null");
        }

        return switch (type) {
            case Images -> imagesPath;
            case Videos -> videosPath;
        };
    }

    public enum TypeOfMedia {
        Images, Videos
    }

    public String removeRootPath(String fullPath) {
        return fullPath.replace(pathConfig.getRoot(), "");
    }

    public String removeBaseServerUrl(String fullUrl) {
        return fullUrl.replace(getMediaRequestRoute(), "");
    }

    public String removeBaseServerUrlThenChangeToRootPath(String fullUrl) {
        return pathConfig.getRoot().concat(removeBaseServerUrl(fullUrl));
    }

    /**
     * This method generates the media URL based on the media path.
     * 
     * @param mediaPath The media path.
     * @return The generated media URL.
     */
    public String generateMediaUrl(String mediaPath) {
        String baseMediaUrl = getMediaRequestRoute();

        return baseMediaUrl.concat(mediaPath);
    }

    private String getMediaRequestRoute() {
        boolean isDeploy = lvoxxServerConfigData.isProductDeploy();
        String baseMediaUrl = isDeploy ? lvoxxServerConfigData.getProdServer().getBaseUrl()
                : lvoxxServerConfigData.getDevServer().getBaseUrl();
        return baseMediaUrl.concat(servletContextPath).concat(MEDIA_ROUTE);
    }

}
