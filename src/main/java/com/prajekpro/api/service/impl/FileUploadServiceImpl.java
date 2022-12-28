package com.prajekpro.api.service.impl;

import com.prajekpro.api.enums.*;
import com.prajekpro.api.service.*;
import com.safalyatech.common.dto.*;
import lombok.extern.slf4j.*;
import org.apache.commons.io.*;
import org.springframework.stereotype.*;
import org.springframework.web.multipart.*;

import javax.transaction.*;
import java.io.*;
import java.nio.file.*;

import static com.safalyatech.common.utility.CheckUtil.*;

@Slf4j
@Service
@Transactional(rollbackOn = Throwable.class)
public class FileUploadServiceImpl implements FileUploadService {


    @Override
    public FileDetailsDTO convertToFileDetailsDTO(MultipartFile file, String fileUploadPath) {

        Long currentTimeMillis = System.currentTimeMillis();
        String originalFileName = file.getOriginalFilename();
        System.out.println("originalFileName = " + originalFileName);

        StringBuilder sb = new StringBuilder();

        sb.append(currentTimeMillis)
                .append("_")
                .append(originalFileName);

        String savedFileName = sb.toString();

        System.out.println("File Base Name - " + FilenameUtils.getBaseName(originalFileName));
        if (!hasValue(FilenameUtils.getBaseName(originalFileName)))
            originalFileName = savedFileName;

        FileDetailsDTO fileDetailsDTO = new FileDetailsDTO(
                originalFileName,
                savedFileName,
                fileUploadPath,
                FilenameUtils.getExtension(
                        file.getOriginalFilename()),
                FileTypes.DEFAULT.value());
        log.debug("FileDetailsDTO to string = {}", fileDetailsDTO.toString());
        return fileDetailsDTO;
    }


    @Override
    public void transferFile(MultipartFile file, FileDetailsDTO fileDetailsDTO) throws IllegalStateException, IOException {

        Path path = Paths.get(fileDetailsDTO.getFilePath());
        if (!Files.exists(path))
            Files.createDirectory(path);

        Path filepath = Paths.get(fileDetailsDTO.getFilePath(), fileDetailsDTO.getFileSavedName());

        log.debug("Transfering File = {} to path = {}", fileDetailsDTO.getFileSavedName(), filepath.toString());
        file.transferTo(filepath);
    }

    @Override
    public FileDetailsDTO transferFile(MultipartFile file, String fileUploadPath) throws IllegalStateException, IOException {

        FileDetailsDTO fileDetailsDTO = convertToFileDetailsDTO(file, fileUploadPath);
        transferFile(file, fileDetailsDTO);
        log.debug("transferred successfully");
        return fileDetailsDTO;
    }
}
