package com.prajekpro.api.dto;

import com.prajekpro.api.domain.*;
import com.prajekpro.api.enums.*;
import lombok.*;

import java.io.*;

@Setter
@Getter
@NoArgsConstructor
public class ApptDocsDTO implements Serializable {
    private static final long serialVersionUID = -8919097639149727069L;

    private Long id;
    private DocType docType;
    private Long uploadDt;
    private String fileName;
    private String fileSavedName;
    private String filePath;
    private String fileExtension;
    private Integer fileType = 0;
    private Integer activeStatus;

    public ApptDocsDTO(ApptDocs apptDocs) {
        this.id = apptDocs.getId();
        this.docType = apptDocs.getDocType();
        this.uploadDt = apptDocs.getUploadDt();
        this.fileName = apptDocs.getFileName();
        this.fileSavedName = apptDocs.getFileSavedName();
        this.filePath = apptDocs.getFilePath();
        this.fileExtension = apptDocs.getFileExtension();
        this.fileType = apptDocs.getFileType();;
        this.activeStatus = apptDocs.getActiveStatus();
    }
}
