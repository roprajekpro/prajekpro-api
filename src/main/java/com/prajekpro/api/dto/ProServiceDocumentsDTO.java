package com.prajekpro.api.dto;

import com.prajekpro.api.domain.*;
import com.prajekpro.api.enums.*;
import lombok.*;

import java.io.*;

@Setter
@Getter
@NoArgsConstructor
public class ProServiceDocumentsDTO implements Serializable {
    private static final long serialVersionUID = -890215583465731329L;

    private Long id;
    private DocType docType;
    private String fileName;
    private String fileSavedName;
    private String filePath;
    private String fileExtension;
    private Integer fileType = 0;

    public ProServiceDocumentsDTO(ProServiceDocuments proServiceDocuments) {
        this.id = proServiceDocuments.getId();
        this.docType = proServiceDocuments.getDocType();
        this.fileName = proServiceDocuments.getFileName();
        this.fileSavedName = proServiceDocuments.getFileSavedName();
        this.filePath = proServiceDocuments.getFilePath();
        this.fileExtension = proServiceDocuments.getFileExtension();
        this.fileType = proServiceDocuments.getFileType();
    }
}
