package com.mathshowcase.dto;

import lombok.Data;

@Data
public class PaperDTO {
    private String title;
    private String authors;
    private String abstractText;
    private String keywords;
    private String coverImage;
}
