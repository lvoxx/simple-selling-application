package com.shitcode.demo1.scheduler;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shitcode.demo1.exception.model.FolderNotFoundException;
import com.shitcode.demo1.properties.MediaConfigData;
import com.shitcode.demo1.service.MediaService;
import com.shitcode.demo1.utils.LogPrinter;

import jakarta.annotation.PostConstruct;

@Component
public class ClearUpUnsualOridinalMediaScheduler {
    private final MediaService mediaService;
    private final MediaConfigData mediaConfigData;

    public ClearUpUnsualOridinalMediaScheduler(MediaService mediaService, MediaConfigData mediaConfigData) {
        this.mediaService = mediaService;
        this.mediaConfigData = mediaConfigData;
    }

    private final Integer DAYS_BETWEEN = 7;
    private String IMAGE_PATHS;
    private String VIDEO_PATHS;

    @PostConstruct
    void setUp() {
        String rootPath = mediaConfigData.getPath().getRoot();
        String compressedPath = mediaConfigData.getPath().getCompressed();
        IMAGE_PATHS = rootPath.concat(mediaConfigData.getPath().getImages()).concat(compressedPath);
        VIDEO_PATHS = rootPath.concat(mediaConfigData.getPath().getVideos()).concat(compressedPath);
    }

    /**
     * Clears old media folders on the original folder.
     * 
     * This method is scheduled to run every Monday at 10:00 AM. It calculates the
     * dates between the last Monday and the current date,
     * then iterates through each date to construct the path for both image and
     * video folders. It calls the deleteMediaFolder method to remove these folders.
     * 
     * @see #deleteMediaFolder(String, String)
     */
    @Scheduled(cron = "0 0 10 ? * MON")
    void clearOldMediaFoldersOnOriginalFolder() {
        OffsetDateTime lastMonday = OffsetDateTime.now().minusDays(DAYS_BETWEEN);

        List<LocalDate> dateBetween = LongStream.range(0, DAYS_BETWEEN)
                .mapToObj(i -> lastMonday.plusDays(i).toLocalDate())
                .collect(Collectors.toList());

        for (LocalDate date : dateBetween) {
            // Creates path string: /year/month/day (e.g., /2024/3/15)
            StringBuilder dateBuilder = (new StringBuilder(File.separatorChar))
                    .append(date.getYear())
                    .append(File.pathSeparatorChar)
                    .append(date.getMonthValue())
                    .append(File.pathSeparatorChar)
                    .append(date.getDayOfMonth());

            // Folder path (e.g., /media/compressed/images/2024/3/15)
            String imageFolderToBeDeleted = dateBuilder.insert(0, IMAGE_PATHS).toString();
            // Folder path (e.g., /media/compressed/videos/2024/3/15)
            String videoFolderToBeDeleted = dateBuilder.insert(0, VIDEO_PATHS).toString();

            deleteMediaFolder(imageFolderToBeDeleted, "image");
            deleteMediaFolder(videoFolderToBeDeleted, "video");
        }
    }

    private void deleteMediaFolder(String folderPath, String mediaType) {
        try {
            mediaService.deleteFolder(folderPath);
        } catch (FolderNotFoundException e) {
            LogPrinter.printSchedulerLog(
                    LogPrinter.Type.INFO,
                    "ClearUpUnsualOridinalMediaScheduler",
                    "clearOldMediaFoldersOnOriginalFolder",
                    LocalDateTime.now().toString(),
                    String.format("No %s folder found to delete: %s", mediaType, e.getMessage()));
        }
    }
}
