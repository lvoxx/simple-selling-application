package com.shitcode.demo1.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface BackupService {
    /**
     * Backs up a image folder from the server to Google Drive cloud storage.
     * The folder will be compressed before uploading to save space and bandwidth.
     *
     * @param date The date of the folder to be backed up
     * @return The path/URLs of the backed up folder in Google Drive
     * @throws IOException If there are issues with file operations or network
     *                     connectivity
     */
    List<String> backupImageFolderToCloud(LocalDate date) throws IOException;

    /**
     * Backs up a video folder from the server to Google Drive cloud storage.
     * The folder will be compressed before uploading to save space and bandwidth.
     *
     * @param date The date of the folder to be backed up
     * @return The path/URLs of the backed up folder in Google Drive
     * @throws IOException If there are issues with file operations or network
     *                     connectivity
     */
    List<String> backupVideoFolderToCloud(LocalDate date) throws IOException;
}