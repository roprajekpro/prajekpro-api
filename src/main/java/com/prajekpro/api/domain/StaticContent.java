package com.prajekpro.api.domain;

import com.safalyatech.common.domains.*;
import lombok.*;

import javax.persistence.*;
import java.io.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "static_content")
public class StaticContent extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contentId;
    private String content;
}
