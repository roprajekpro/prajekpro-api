package com.prajekpro.api.domain;

import com.prajekpro.api.enums.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;
import lombok.*;
import org.codehaus.jackson.annotate.*;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@MappedSuperclass
public class FileDetails extends Auditable {

    @Column(name = "FILE_NAME")
    private String fileName;
    @Column(name = "FILE_SAVED_NAME")
    private String fileSavedName;
    @Column(name = "FILE_PATH")
    private String filePath;
    @Column(name = "FILE_EXTENSION")
    private String fileExtension;
    @Column(name = "FILE_TYPE")
    private Integer fileType = 0;

    @Transient
    @JsonIgnore
    public void updateFileDetails(FileDetailsDTO fileDetailsDTO) {
        this.fileName = fileDetailsDTO.getFileName();
        this.fileSavedName = fileDetailsDTO.getFileSavedName();
        this.filePath = fileDetailsDTO.getFilePath();
        this.fileExtension = fileDetailsDTO.getFileExtension();
        this.fileType = fileDetailsDTO.getFileType();
    }

    @JsonIgnore
    @Transient
    public DownloadImageDTO convertToDowloadImageDTO() {
        DownloadImageDTO downloadImageDTO = new DownloadImageDTO();
        downloadImageDTO.setImgExtn(this.getFileExtension());
        downloadImageDTO.setDisplayName(this.getFileName());
        String fullFilePath = this.getFilePath() + this.getFileSavedName();
        downloadImageDTO.setFilePath(fullFilePath);

        return downloadImageDTO;
    }
}
