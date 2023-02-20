package com.prajekpro.api.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class StaticContentDTO {

    private Long id;
    private String contentId;
    private String content;
}
