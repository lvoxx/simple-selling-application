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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.component.MediaDetector;
import com.shitcode.demo1.exception.model.EmptyFileException;
import com.shitcode.demo1.exception.model.UnknownFileExtension;
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
     * Constructs LocalMediaServiceImpl with dependencies.
     *
     * @param mediaConfigData Media configuration data
     * @param ffmpegPath      Path to FFmpeg binary
     * @param ffprobePath     Path to FFprobe binary
     * @throws IOException If FFmpeg or FFprobe initialization fails
     */
    public LocalMediaServiceImpl(MediaConfigData mediaConfigData,
            @Value("${server.compression.ffmpeg}") String ffmpegPath,
            @Value("${server.compression.ffprobe}") String ffprobePath, LvoxxServerConfigData lvoxxServerConfigData)
            throws IOException {
        this.mediaConfigData = mediaConfigData;
        this.lvoxxServerConfigData = lvoxxServerConfigData;
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
        imagesPath = mediaConfigData.getPath().getRoot().concat(mediaConfigData.getPath().getImages());
        videosPath = mediaConfigData.getPath().getRoot().concat(mediaConfigData.getPath().getVideos());
    }

    @Override
    public List<String> saveImagesFile(List<MultipartFile> images) throws Exception {
        if (images.isEmpty()) {
            throw new EmptyFileException("{validation.product.images.not-null}");
        }
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String mimeType = MediaDetector.detect(image.getInputStream());

            if (!(mimeType.startsWith("image/"))) {
                throw new UnknownFileExtension("{validation.product.images.valid}");
            }
            String location = saveFileToServer(image, TypeOfMedia.Images);
            // C:/Users/${current_user_name}/user/media/images/compressed/31/3/2025/{uuid}.webp
            imageUrls.add(
                    generateMediaUrl(
                            extractCompressPath(compressImage(location))));
        }

        return imageUrls;
    }

    @Override
    public String saveVideoFile(MultipartFile video) throws Exception {
        if (video == null) {
            return null;
        }
        String mimeType = MediaDetector.detect(video.getInputStream());

        if (!(mimeType.startsWith("video/"))) {
            throw new UnknownFileExtension("{validation.product.video.valid}");
        }
        // C:/Users/${current_user_name}/user/media/videos/original/31/3/2025/{UUID}.mp4
        String location = saveFileToServer(video, TypeOfMedia.Videos);
        // C:/Users/${current_user_name}/user/media/videos/compressed/31/3/2025/{uuid}.mp4
        return generateMediaUrl(extractCompressPath(compressVideo(location)));
    }

    /**
     * Retrieves a media file as a Resource.
     * Example: Home user's path + rootMedia + filePathAndNameWithExtension
     * Equivalent: C:/Users/${current_user_name} + /media +
     * /images(videos)/original(compressed)/01/01/2025/<file>.extension
     * 
     * @param filePathAndNameWithExtension The relative path to the file
     * @return An Optional containing the file resource if found
     * @throws FileNotFoundException If the file does not exist
     */
    @SuppressWarnings("null")
    @Override
    public Resource findFile(String filePathAndNameWithExtension) throws FileNotFoundException {
        Resource resource = null;
        try {
            // C:/Users/${current_user_name}
            // /images(videos)/original(compressed)/01/01/2025/<file>.extension
            Path filePath = Paths.get(getHomeDir().concat(mediaConfigData.getPath().getRoot()))
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
            throw new FileNotFoundException("{exception.media.file-not-found}");
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
    public String saveFileToServer(MultipartFile file, TypeOfMedia type) throws IOException {
        // C:/Users/${current_user_name}/images(videos)/original(compressed)/01/01/2025/<file>.extension
        String dirPath = makeMediaPath(type, false);
        String fileName = String.format(
                "%s.%s",
                UUID.randomUUID().toString(),
                FilenameUtils.getExtension(file.getOriginalFilename()));
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String location = new StringBuilder(dirPath).append(File.separator).append(fileName).toString();
        Path path = Paths.get(location);
        Files.write(path, file.getBytes());

        return location;
    }

    /**
     * Compresses an image and saves it as webp.
     *
     * @param originalLocation Path to the original image
     * @return Path to the compressed image
     * @throws IOException If compression fails
     */
    private String compressImage(String originalLocation) throws IOException {
        // C:/Users/${current_user_name}/images(videos)/compressed/01/01/2025/<file>.extension
        String path = makeMediaPath(TypeOfMedia.Images, true);

        InputStream inputStream = new FileInputStream(originalLocation);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .size(IMAGE_COMPRESSION_WIDTH, IMAGE_COMPRESSION_HEIGHT)
                .outputQuality(IMAGE_COMPRESSION_QUALITY)
                .outputFormat(IMAGE_FORMAT)
                .toOutputStream(outputStream);
        Path filePath = Files.write(Path.of(path), outputStream.toByteArray());
        return filePath.toFile().getAbsolutePath();
    }

    /**
     * Compresses a video using FFmpeg.
     *
     * @param originalLocation Path to the original video
     * @return Path to the compressed video
     * @throws IOException If compression fails
     */
    private String compressVideo(String originalLocation) throws IOException {
        // C:/Users/${current_user_name}/images(videos)/compressed/01/01/2025/<file>.extension
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

    private String makeMediaPath(TypeOfMedia type, boolean isCompress) {
        // C:\Users\${current_user_name}/media/videos/compressed/31/3/2025
        String internalFolder = isCompress ? COMPRESSED_FOLDER : ORIGINAL_FOLDER;
        LocalDate now = LocalDate.now();
        String dirPath = new StringBuilder(getHomeDir()).append(File.separator)
                .append(getPath(type)).append(File.separator)
                .append(internalFolder).append(File.separator)
                .append(now.getDayOfMonth()).append(File.separator)
                .append(now.getMonth()).append(File.separator)
                .append(now.getYear())
                .toString();

        return dirPath;
    }

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

    private static String getHomeDir() {
        return System.getProperty("user.home");
    }

    // /media/images/compressed/31/3/2025/{uuid}.webp
    // /media/video/compressed/31/3/2025/{uuid}.mp4
    public String extractCompressPath(String fullPath) {
        return fullPath.replace(getHomeDir(), "");
    }

    public String generateMediaUrl(String mediaPath) {
        boolean isDeploy = lvoxxServerConfigData.isProductDeploy();
        String mediaMap = "/media";
        String baseMediaUrl = isDeploy ? lvoxxServerConfigData.getProdServer().getBaseUrl()
                : lvoxxServerConfigData.getDevServer().getBaseUrl();

        return baseMediaUrl.concat(mediaMap).concat(mediaPath);
    }

}
