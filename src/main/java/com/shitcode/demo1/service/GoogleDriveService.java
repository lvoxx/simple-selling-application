package com.shitcode.demo1.service;

import java.io.File;
import java.io.IOException;

public interface GoogleDriveService {
    String uploadFile(File fileToBeUploaded) throws IOException;

    String createFolder(String folderName) throws IOException;
}