package com.shitcode.demo1.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.exception.model.FolderNotFoundException;
import com.shitcode.demo1.helper.BitSizeConverter;
import com.shitcode.demo1.properties.MediaConfigData;
import com.shitcode.demo1.properties.ZipConfigData;
import com.shitcode.demo1.service.BackupService;
import com.shitcode.demo1.service.GoogleDriveService;
import com.shitcode.demo1.utils.LogPrinter;
import com.shitcode.demo1.utils.LogPrinter.Type;
import com.shitcode.demo1.utils.LoggingModel;

import jakarta.annotation.PostConstruct;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;

@Service
@LogCollector(loggingModel = LoggingModel.SERVICE)
@ConditionalOnProperty(havingValue = "true", matchIfMissing = true, name = "upload-locally", prefix = "media.path")
public class BackupOnGoogleDriveServiceImple implements BackupService {

    private final ZipConfigData zipConfigData;
    private final MediaConfigData mediaConfigData;
    private final MessageSource messageSource;
    private final GoogleDriveService googleDriveService;

    private final String FOLDER_NAME_FORMAT = "backup-%s-at-";
    private final String FOLDER_DATE_FORMAT = "dd/MM/yyyy";

    public BackupOnGoogleDriveServiceImple(ZipConfigData zipConfigData, MediaConfigData mediaConfigData,
            MessageSource messageSource, GoogleDriveService googleDriveService) {
        this.zipConfigData = zipConfigData;
        this.mediaConfigData = mediaConfigData;
        this.messageSource = messageSource;
        this.googleDriveService = googleDriveService;
    }

    private MediaConfigData.PathConfig pathConfig;
    private Long MAX_FILE_SIZE;
    private String ZIP_FILENAME_FORMAT;

    @PostConstruct
    void setUp() {
        pathConfig = mediaConfigData.getPath();
        MAX_FILE_SIZE = BitSizeConverter.format(zipConfigData.getMaxSize());
        ZIP_FILENAME_FORMAT = zipConfigData.getFormat();
    }

    @Override
    public List<String> backupImageFolderToCloud(LocalDate date) throws IOException {
        // These are folder to be backed up.
        Path imageFolderToBeBackedUp = getBackupFolder(true, date);
        // Zip files will be uploaded to Google Drive.
        List<File> imageZipFiles;
        String folderName = String.format(FOLDER_NAME_FORMAT,
                date.format(DateTimeFormatter.ofPattern(FOLDER_DATE_FORMAT)));
        try {
            imageZipFiles = splitFolderIntoZips(imageFolderToBeBackedUp, folderName);
        } catch (IllegalArgumentException e) {
            LogPrinter.printServiceLog(Type.ERROR, "BackupOnGoogleDriveServiceImple", "backupVideoFolderToCloud",
                    e.getMessage());
            throw new FolderNotFoundException(e.getMessage());
        }
        List<String> result = new ArrayList<String>();
        for (File imageZip : imageZipFiles) {
            result.add(googleDriveService.uploadFile(imageZip));
        }
        return result;
    }

    @Override
    public List<String> backupVideoFolderToCloud(LocalDate date) throws IOException {
        // These are folder to be backed up.
        Path videoFolderToBeBackedUp = getBackupFolder(false, date);
        // Zip files will be uploaded to Google Drive.
        List<File> videoZipFiles;
        String folderName = String.format(FOLDER_NAME_FORMAT,
                date.format(DateTimeFormatter.ofPattern(FOLDER_DATE_FORMAT)));
        try {
            videoZipFiles = splitFolderIntoZips(videoFolderToBeBackedUp, folderName);
        } catch (IllegalArgumentException e) {
            LogPrinter.printServiceLog(Type.ERROR, "BackupOnGoogleDriveServiceImple", "backupVideoFolderToCloud",
                    e.getMessage());
            throw new FolderNotFoundException(e.getMessage());
        }
        List<String> result = new ArrayList<String>();
        for (File videoZip : videoZipFiles) {
            result.add(googleDriveService.uploadFile(videoZip));
        }
        return result;
    }

    private List<File> splitFolderIntoZips(Path folderPath, String fullFolderName) throws IOException {
        if (!Files.isDirectory(folderPath)) {
            throw new IllegalArgumentException(
                    messageSource.getMessage("exception.backup.folder-not-found",
                            new Object[] { folderPath }, Locale.getDefault()));
        }
        String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss'Z'"));
        // Get all files in the folder.
        List<Path> allFiles = Files.walk(folderPath)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

        List<File> resultZipFiles = new ArrayList<>();
        List<Path> currentBatch = new ArrayList<>();
        long currentSize = 0;
        int part = 1;

        for (Path file : allFiles) {
            long fileSize = Files.size(file);
            // Stop batching, renew batch files and make zip file
            if ((currentSize + fileSize > MAX_FILE_SIZE) && !currentBatch.isEmpty()) {
                resultZipFiles.add(zipPart(folderPath, currentBatch, fullFolderName, timestamp, part++));
                currentBatch = new ArrayList<>();
                currentSize = 0;
            }

            currentBatch.add(file);
            currentSize += fileSize;
        }

        if (!currentBatch.isEmpty()) {
            resultZipFiles.add(zipPart(folderPath, currentBatch, fullFolderName, timestamp, part));
        }

        return resultZipFiles;
    }

    private File zipPart(Path baseDir, List<Path> files, String folderName, String timestamp, int partNum)
            throws IOException {
        String fileName = String.format(ZIP_FILENAME_FORMAT, folderName, timestamp, partNum);
        // Create a temporary directory for zip files
        Path tempDir = Files.createTempDirectory("backup_zips");
        Path zipPath = tempDir.resolve(fileName);
        ZipFile zipFile = new ZipFile(zipPath.toFile());

        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        parameters.setCompressionLevel(CompressionLevel.FAST);

        for (Path file : files) {
            Path relative = baseDir.relativize(file);
            parameters.setFileNameInZip(relative.toString());
            zipFile.addFile(file.toFile(), parameters);
        }
        File result = zipPath.toFile();
        zipFile.close();
        // Schedule the temp directory for deletion on JVM exit
        tempDir.toFile().deleteOnExit();
        return result;
    }

    private Path getBackupFolder(boolean isImage, LocalDate date) {
        Path mediaPath = Path.of(pathConfig.getRoot()
                .concat(isImage ? pathConfig.getImages() : pathConfig.getVideos())
                .concat(pathConfig.getCompressed())
                .concat(File.separator)
                .concat(String.valueOf(date.getYear()))
                .concat(File.separator)
                .concat(String.valueOf(date.getMonthValue()))
                .concat(File.separator)
                .concat(String.valueOf(date.getDayOfMonth())));
        LogPrinter.printServiceLog(LogPrinter.Type.DEBUG, "BackupOnGoogleDriveServiceImple", "getBackupFolder",
                "Result path: " + mediaPath);

        return mediaPath;
    }
}
