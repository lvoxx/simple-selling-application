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
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.exception.model.FolderNotFoundException;
import com.shitcode.demo1.exception.model.InitialGoogleDriveContextException;
import com.shitcode.demo1.exception.model.UploadFileOnGoogleDriveException;
import com.shitcode.demo1.helper.BitSizeConverter;
import com.shitcode.demo1.properties.ClientConfigData;
import com.shitcode.demo1.properties.MediaConfigData;
import com.shitcode.demo1.properties.ZipConfigData;
import com.shitcode.demo1.service.BackupService;
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
    private final ClientConfigData clientConfigData;
    private final GoogleCredentials googleCredentials;

    public BackupOnGoogleDriveServiceImple(ZipConfigData zipConfigData, MediaConfigData mediaConfigData,
            ClientConfigData clientConfigData, GoogleCredentials googleCredentials) {
        this.zipConfigData = zipConfigData;
        this.mediaConfigData = mediaConfigData;
        this.clientConfigData = clientConfigData;
        this.googleCredentials = googleCredentials;
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
        try {
            imageZipFiles = splitFolderIntoZips(imageFolderToBeBackedUp);
        } catch (IllegalArgumentException e) {
            LogPrinter.printServiceLog(Type.ERROR, "BackupOnGoogleDriveServiceImple", "backupVideoFolderToCloud",
                    e.getMessage());
            throw new FolderNotFoundException(e.getMessage());
        }
        List<String> result = new ArrayList<String>();
        for (File imageZip : imageZipFiles) {
            result.add(uploadFile(imageZip));
        }
        return result;
    }

    @Override
    public List<String> backupVideoFolderToCloud(LocalDate date) throws IOException {
        // These are folder to be backed up.
        Path videoFolderToBeBackedUp = getBackupFolder(false, date);
        // Zip files will be uploaded to Google Drive.
        List<File> videoZipFiles;
        try {
            videoZipFiles = splitFolderIntoZips(videoFolderToBeBackedUp);
        } catch (IllegalArgumentException e) {
            LogPrinter.printServiceLog(Type.ERROR, "BackupOnGoogleDriveServiceImple", "backupVideoFolderToCloud",
                    e.getMessage());
            throw new FolderNotFoundException(e.getMessage());
        }
        List<String> result = new ArrayList<String>();
        for (File videoZip : videoZipFiles) {
            result.add(uploadFile(videoZip));
        }
        return result;
    }

    private List<File> splitFolderIntoZips(Path folderPath) throws IOException {
        if (!Files.isDirectory(folderPath)) {
            throw new IllegalArgumentException(
                    String.format("Can not found directory [%s] or it is not a directory", folderPath));
        }
        String folderName = folderPath.getFileName().toString();
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
                resultZipFiles.add(zipPart(folderPath, currentBatch, folderName, timestamp, part++));
                currentBatch = new ArrayList<>();
                currentSize = 0;
            }

            currentBatch.add(file);
            currentSize += fileSize;
        }

        if (!currentBatch.isEmpty()) {
            resultZipFiles.add(zipPart(folderPath, currentBatch, folderName, timestamp, part));
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

    /**
     * Upload new file.
     * <p>
     * Sample taken from Google documentation on how to manage uploads, with link to
     * <a href=
     * "https://developers.google.com/workspace/drive/api/guides/manage-uploads">https://developers.google.com/workspace/drive/api/guides/manage-uploads</a>
     * </p>
     * 
     * @author Google.
     * @return Inserted file metadata if successful, {@code null} otherwise.
     * @throws IOException if service account credentials file not found.
     */
    public String uploadFile(File fileToBeUploaded) throws IOException {
        // Load pre-authorized user credentials from the environment.
        // TODO(developer) - See https://developers.google.com/identity for guides on
        // implementing OAuth2 for your application.
        // Build a new authorized API client service.
        Drive service = null;
        // Upload file photo.jpg on drive.
        com.google.api.services.drive.model.File fileMetadata = null;
        // Specify media type and file-path for file.
        FileContent mediaContent = null;
        try {
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                    googleCredentials);

            service = new Drive.Builder(new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    requestInitializer)
                    .setApplicationName(clientConfigData.getName())
                    .build();
            fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setName(fileToBeUploaded.getName());
            mediaContent = new FileContent("application/zip", fileToBeUploaded);
        } catch (Exception e) {
            LogPrinter.printServiceLog(LogPrinter.Type.ERROR, "BackupOnGoogleDriveServiceImple", "uploadBasic",
                    "Unable to inital Google Drive context: " + e.getMessage());
            throw new InitialGoogleDriveContextException("Failed to initialize Google Drive context");
        }
        try {
            com.google.api.services.drive.model.File file = service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            System.out.println("File ID: " + file.getId());
            return file.getId();
        } catch (GoogleJsonResponseException e) {
            LogPrinter.printServiceLog(LogPrinter.Type.ERROR, "BackupOnGoogleDriveServiceImple", "uploadBasic",
                    "Unable to upload file: " + e.getDetails());
            throw new UploadFileOnGoogleDriveException("Unable to upload file: " + e.getDetails());
        }
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
