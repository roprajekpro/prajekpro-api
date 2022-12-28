package com.prajekpro.api.service;

import com.safalyatech.common.dto.FileDetailsDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadService {

    FileDetailsDTO convertToFileDetailsDTO(MultipartFile file, String fileUploadPath);

    void transferFile(MultipartFile file, FileDetailsDTO fileDetailsDTO) throws IllegalStateException, IOException;

    FileDetailsDTO transferFile(MultipartFile file, String fileUploadPath) throws IllegalStateException, IOException;
}
