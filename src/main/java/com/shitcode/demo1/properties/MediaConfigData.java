package com.shitcode.demo1.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "media")
public class MediaConfigData {
    private PathConfig path;
    private CompressConfig compress;
    private Boolean uploadLocally;

    @Data
    public static class PathConfig {
        private String root;
        private String images;
        private String videos;
        private String compressed;
        private String original;
    }

    @Data
    public static class CompressConfig {
        private ImageCompressConfig image;
        private VideoCompressConfig video;
    }

    @Data
    public static class ImageCompressConfig {
        private String format;
        private Integer width;
        private Integer height;
        private Double quality;
    }

    @Data
    public static class VideoCompressConfig {
        private String format;
        private Double resolution;
        // Audio settings
        private Integer audioChannel;
        private String audioCodec;
        private Integer audioSampleRate;
        private Integer audioBitRate;
        // Video settings
        private String videoCodec;
        private Integer videoFrameRate;
        // Extra configuration
        private String extraArgsCrf;
        private String extraArgsPreset;
        private String extraArgsTune;
    }
}