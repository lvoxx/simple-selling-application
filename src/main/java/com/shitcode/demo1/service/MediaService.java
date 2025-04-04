package com.shitcode.demo1.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.shitcode.demo1.exception.model.FileReadException;
import com.shitcode.demo1.service.impl.LocalMediaServiceImpl.TypeOfMedia;

public interface MediaService {
    List<String> saveImagesFile(List<MultipartFile> images) throws Exception;

    String saveVideoFile(MultipartFile video) throws Exception;

    Resource findFile(String filePathAndNameWithExtension) throws FileNotFoundException, FileReadException;

    String saveFileToServer(MultipartFile file, TypeOfMedia type, boolean isCompressed) throws IOException;
    
    void deleteFile(String filePathAndNameWithExtension) throws IOException;

    void deleteFiles(List<String> filePathsAndNamesWithExtensions) throws IOException;
}
