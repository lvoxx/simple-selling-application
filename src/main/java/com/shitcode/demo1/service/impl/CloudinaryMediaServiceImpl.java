package com.shitcode.demo1.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.exception.model.EmptyFileException;
import com.shitcode.demo1.exception.model.FileReadException;
import com.shitcode.demo1.exception.model.FolderNotFoundException;
import com.shitcode.demo1.exception.model.UnknownFileExtension;
import com.shitcode.demo1.service.MediaService;
import com.shitcode.demo1.service.impl.LocalMediaServiceImpl.TypeOfMedia;
import com.shitcode.demo1.utils.LoggingModel;

@Service
@LogCollector(loggingModel = LoggingModel.SERVICE)
@ConditionalOnProperty(havingValue = "false", name = "upload-locally", prefix = "media.path")
public class CloudinaryMediaServiceImpl implements MediaService{

    @Override
    public String saveImageFile(MultipartFile image) throws EmptyFileException, UnknownFileExtension, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveImageFile'");
    }

    @Override
    public String saveVideoFile(MultipartFile video) throws EmptyFileException, UnknownFileExtension, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveVideoFile'");
    }

    @Override
    public Resource findFile(String filePathAndNameWithExtension) throws FileNotFoundException, FileReadException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findFile'");
    }

    @Override
    public String saveFileToServer(MultipartFile file, TypeOfMedia type, boolean isCompressed) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveFileToServer'");
    }

    @Override
    public String updateImage(MultipartFile image, String oldUrl) throws FileNotFoundException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateImage'");
    }

    @Override
    public String updateVideo(MultipartFile video, String oldUrl) throws FileNotFoundException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateVideo'");
    }

    @Override
    public void deleteFile(String filePathAndNameWithExtension) throws IOException, FileNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteFile'");
    }

    @Override
    public void deleteFiles(List<String> filePathsAndNamesWithExtensions) throws IOException, FileNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteFiles'");
    }

    @Override
    public void deleteFolder(String folderName) throws FolderNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteFolder'");
    }

    @Override
    public void deleteFolder(List<String> folderNames) throws FolderNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteFolder'");
    }
    
}
