package com.shitcode.demo1.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.exception.model.InitialGoogleDriveContextException;
import com.shitcode.demo1.exception.model.UploadFileOnGoogleDriveException;
import com.shitcode.demo1.properties.ClientConfigData;
import com.shitcode.demo1.service.GoogleDriveService;
import com.shitcode.demo1.utils.LogPrinter;
import com.shitcode.demo1.utils.LogPrinter.Type;
import com.shitcode.demo1.utils.LoggingModel;

/**
 * TODO(developer) - See https://developers.google.com/identity for guides on
 * implementing OAuth2 for your application.
 */
@Service
@LogCollector(loggingModel = LoggingModel.SERVICE)
public class GoogleDriveServiceImpl implements GoogleDriveService {

    private final ClientConfigData clientConfigData;
    private final GoogleCredentials googleCredentials;
    private final MessageSource messageSource;

    public GoogleDriveServiceImpl(ClientConfigData clientConfigData, GoogleCredentials googleCredentials,
            MessageSource messageSource) {
        this.clientConfigData = clientConfigData;
        this.googleCredentials = googleCredentials;
        this.messageSource = messageSource;
    }

    /**
     * Upload new file.
     * <p>
     * Sample taken from Google documentation on how to manage uploads, with link to
     * <a href=
     * "https://developers.google.com/workspace/drive/api/guides/manage-uploads">Manage
     * Uploads on Google Drive</a>
     * </p>
     * 
     * @author Google.
     * @return Inserted file metadata if successful, {@code null} otherwise.
     * @throws IOException if service account credentials file not found.
     */
    public String uploadFile(java.io.File fileToBeUploaded) throws IOException {
        // Load pre-authorized user credentials from the environment.
        // Build a new authorized API client service.
        Drive service = null;
        // Upload file photo.jpg on drive.
        File fileMetadata = null;
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
            LogPrinter.printServiceLog(Type.ERROR, "BackupOnGoogleDriveServiceImple", "uploadBasic",
                    "Unable to inital Google Drive context: " + e.getMessage());
            throw new InitialGoogleDriveContextException(
                    messageSource.getMessage("exception.google.failed-to-initial-context",
                            new Object[] {}, Locale.getDefault()));
        }
        try {
            com.google.api.services.drive.model.File file = service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            System.out.println("File ID: " + file.getId());
            return file.getId();
        } catch (GoogleJsonResponseException e) {
            LogPrinter.printServiceLog(Type.ERROR, "BackupOnGoogleDriveServiceImple", "uploadBasic",
                    "Unable to upload file: " + e.getDetails());
            throw new UploadFileOnGoogleDriveException(
                    messageSource.getMessage("exception.google.failed-to-upload-file",
                            new Object[] { fileToBeUploaded.getName() }, Locale.getDefault()));
        }
    }

    /**
     * Create new folder.
     * <p>
     * Sample taken from Google documentation on how to create folder on Drive, with
     * link to
     * <a href=
     * "https://developers.google.com/workspace/drive/api/guides/folder">Create
     * folder guide on Google Drive</a>
     * </p>
     * 
     * @author Google.
     * @return Inserted folder id if successful, {@code null} otherwise.
     * @throws IOException if service account credentials file not found.
     */
    @Override
    public String createFolder(String folderName) throws IOException {
        // Load pre-authorized user credentials from the environment.

        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(clientConfigData.getName())
                .build();
        // File's metadata.
        File fileMetadata = new File();
        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        try {
            File file = service.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
            return file.getId();
        } catch (GoogleJsonResponseException e) {
            LogPrinter.printServiceLog(Type.ERROR, "BackupOnGoogleDriveServiceImple", "createFolder",
                    "Unable to create folder: " + e.getDetails());
            throw e;
        }
    }
}
