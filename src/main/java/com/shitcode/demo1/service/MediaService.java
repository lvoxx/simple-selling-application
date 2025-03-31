package com.shitcode.demo1.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.shitcode.demo1.service.impl.LocalMediaServiceImpl.TypeOfMedia;

public interface MediaService {
    String saveMediaFile(MultipartFile file) throws Exception;

    Resource findFile(String filePathAndNameWithExtension) throws FileNotFoundException;

    String saveFileToServer(MultipartFile file, TypeOfMedia type) throws IOException;
}
