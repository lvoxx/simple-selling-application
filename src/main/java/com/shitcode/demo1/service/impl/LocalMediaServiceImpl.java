package com.shitcode.demo1.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
import com.shitcode.demo1.exception.model.UnknownFileExtension;
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
    private final MediaConfigData mediaConfigData;
    private final LvoxxServerConfigData lvoxxServerConfigData;
    private final MessageSource messageSource;

    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;

    public static final String ORIGINAL_FOLDER = "original";
    public static final String COMPRESSED_FOLDER = "compressed";
    public static final String IMAGE_FORMAT = "jpg";
    public static final String VIDEO_FORMAT = "mp4";

    private static final int IMAGE_COMPRESSION_WIDTH = 1200;
    private static final int IMAGE_COMPRESSION_HEIGHT = 800;
    private static final double IMAGE_COMPRESSION_QUALITY = 0.7;

    private String imagesPath;
    private String videosPath;

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
            @Value("${server.compression.ffprobe}") String ffprobePath, LvoxxServerConfigData lvoxxServerConfigData,
            MessageSource messageSource)
            throws IOException {
        this.mediaConfigData = mediaConfigData;
        this.lvoxxServerConfigData = lvoxxServerConfigData;
        this.messageSource = messageSource;
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
        imagesPath = mediaConfigData.getPath().getImages();
        videosPath = mediaConfigData.getPath().getVideos();
    }

    /**
     * Saves a list of image files to the server and returns their URLs.
     * 
     * This method iterates over the list of provided image files, detects their
     * MIME type, and
     * ensures they are valid images. It then saves each image to the server,
     * compresses it if necessary,
     * and generates a URL for the compressed image. The URLs of the compressed
     * images are returned
     * as a list.
     * 
     * @param images A list of image files to be saved
     * @return A list of URLs pointing to the saved and compressed images
     * @throws Exception If any error occurs during the saving or compression
     *                   process
     */
    @Override
    public List<String> saveImagesFile(List<MultipartFile> images) throws Exception {
        if (images.isEmpty()) {
            throw new EmptyFileException(messageSource.getMessage("validation.product.images.not-null",
                    new Object[] {}, Locale.getDefault()));
        }
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String mimeType = MediaDetector.detect(image.getInputStream());

            if (!(mimeType.startsWith("image/"))) {
                throw new UnknownFileExtension(messageSource.getMessage("validation.product.images.valid",
                        new Object[] {}, Locale.getDefault()));
            }
            String originalLocation = saveFileToServer(image, TypeOfMedia.Images, false);
            imageUrls.add(
                    generateMediaUrl(
                            extractCompressPath(compressImage(originalLocation))));
        }

        return imageUrls;
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
    public String saveVideoFile(MultipartFile video) throws Exception {
        if (video == null || video.isEmpty()) {
            return null;
        }

        String mimeType = MediaDetector.detect(video.getInputStream());

        if (!(mimeType.startsWith("video/"))) {
            throw new UnknownFileExtension(messageSource.getMessage("validation.product.video.valid",
                    new Object[] {}, Locale.getDefault()));
        }
        String location = saveFileToServer(video, TypeOfMedia.Videos, false);

        return generateMediaUrl(extractCompressPath(compressVideo(location)));
    }

    /**
     * Finds a file in the local media storage by its path and name with extension.
     *
     * @param filePathAndNameWithExtension The path and name of the file with its
     *                                     extension
     * @return The resource representing the file if found, otherwise throws a
     *         FileNotFoundException
     * @throws FileNotFoundException If the file is not found or is not readable
     */
    @SuppressWarnings("null")
    @Override
    public Resource findFile(String filePathAndNameWithExtension) throws FileNotFoundException {
        Resource resource = null;
        try {
            Path filePath = Paths.get(mediaConfigData.getPath().getRoot().concat(mediaConfigData.getPath().getRoot()))
                    .resolve(filePathAndNameWithExtension)
                    .normalize();
            resource = new UrlResource(filePath.toUri());

        } catch (Exception e) {
            LogPrinter.printServiceLog(Type.ERROR,
                    "LocalMediaServiceImpl",
                    "findFile", LocalDateTime.now().toString(),
                    e.getMessage());
            e.printStackTrace();
        }
        if (!(resource.exists()) || !(resource.isReadable())) {
            throw new FileNotFoundException(messageSource.getMessage("exception.media.file-not-found",
                    new Object[] { filePathAndNameWithExtension }, Locale.getDefault()));
        }
        return resource;
    }

    /**
     * Saves a file to the local media storage.
     *
     * @param file The file to save
     * @param type The type of media (image or video)
     * @return The full path to the saved file
     * @throws IOException If the file cannot be saved
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

        Thumbnails.of(inputStream)
                .size(IMAGE_COMPRESSION_WIDTH, IMAGE_COMPRESSION_HEIGHT)
                .outputQuality(IMAGE_COMPRESSION_QUALITY)
                .outputFormat(IMAGE_FORMAT)
                .toOutputStream(outputStream);
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
                .append(File.separator).append(UUID.randomUUID().toString()).append(".").append(VIDEO_FORMAT)
                .toString();

        FFmpegProbeResult input = ffprobe.probe(originalLocation);

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(input).overrideOutputFiles(true)

                .addOutput(filePath)
                .setFormat(VIDEO_FORMAT)
                .disableSubtitle()

                // Audio Configuration (lightweight)
                .setAudioChannels(1)
                .setAudioCodec("aac")
                .setAudioSampleRate(44_100) // Lower sample rate to save space
                .setAudioBitRate(48_000) // Slightly higher bitrate for clarity

                // Video Configuration (optimized for high requests)
                .setVideoCodec("libx264")
                .setVideoFrameRate(42, 1)
                .setVideoResolution(1280, 720)
                .addExtraArgs("-crf", "26") // Lower CRF for better quality (22-28 range)
                .addExtraArgs("-preset", "faster") // Faster encoding, slight file size tradeoff
                .addExtraArgs("-tune", "zerolatency") // Low-latency tuning for fast processing

                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder, new ProgressListener() {

            final double duration_ns = input.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

            @Override
            public void progress(Progress progress) {
                double percentage = (double) progress.out_time_ns / duration_ns;
                LogPrinter.printServiceLog(LogPrinter.Type.INFO,
                        "MediaServiceImpl",
                        "compressVideo",
                        LocalDateTime.now().toString(),
                        String.format("filename: %s -> %d status: %s time: %d ms",
                                input.getFormat().filename, // Correctly referenced
                                String.format("%.0f", percentage * 100), // Properly formatted percentage
                                progress.status,
                                FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS)));
            }
        }).run();

        return filePath;
    }

    /**
     * This method generates the media path based on the type of media and
     * compression status.
     * 
     * @param type       The type of media (Images or Videos).
     * @param isCompress The compression status (true for compressed, false for
     *                   original).
     * @return The generated media path.
     */
    private String makeMediaPath(TypeOfMedia type, boolean isCompress) {
        String internalFolder = isCompress ? COMPRESSED_FOLDER : ORIGINAL_FOLDER;
        LocalDate now = LocalDate.now();
        String dirPath = new StringBuilder(mediaConfigData.getPath().getRoot()).append(File.separator)
                .append(getPath(type)).append(File.separator)
                .append(internalFolder).append(File.separator)
                .append(now.getDayOfMonth()).append(File.separator)
                .append(now.getMonthValue()).append(File.separator)
                .append(now.getYear())
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

    public String extractCompressPath(String fullPath) {
        return fullPath.replace(mediaConfigData.getPath().getRoot(), "");
    }

    /**
     * This method generates the media URL based on the media path.
     * 
     * @param mediaPath The media path.
     * @return The generated media URL.
     */
    public String generateMediaUrl(String mediaPath) {
        boolean isDeploy = lvoxxServerConfigData.isProductDeploy();
        String mediaMap = "/media";
        String baseMediaUrl = isDeploy ? lvoxxServerConfigData.getProdServer().getBaseUrl()
                : lvoxxServerConfigData.getDevServer().getBaseUrl();

        return baseMediaUrl.concat(mediaMap).concat(mediaPath);
    }

}
