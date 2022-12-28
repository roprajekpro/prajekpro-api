package com.prajekpro.api.dto;

import lombok.*;

import java.io.*;

@Setter
@Getter
@NoArgsConstructor
public class ProServiceCommentsDTO implements Serializable {
    private static final long serialVersionUID = -4139345465135531857L;

    private Long id;
    private Long proId;
    private String commentedBy;
    private Long serviceId;
    private String subject;
    private String comment;
    private Long commentedTs;

    public ProServiceCommentsDTO(String subject, String comment) {
        this.subject = subject;
        this.comment = comment;
    }
}
