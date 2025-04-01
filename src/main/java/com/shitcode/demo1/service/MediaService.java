package com.shitcode.demo1.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.shitcode.demo1.service.impl.LocalMediaServiceImpl.TypeOfMedia;

public interface MediaService {
    List<String> saveMediaFiles(List<MultipartFile> files) throws Exception;

    String saveMediaFile(MultipartFile file) throws Exception;

    Resource findFile(String filePathAndNameWithExtension) throws FileNotFoundException;

    String saveFileToServer(MultipartFile file, TypeOfMedia type) throws IOException;
}
