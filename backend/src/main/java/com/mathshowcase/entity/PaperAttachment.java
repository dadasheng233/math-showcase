package com.mathshowcase.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("paper_attachment")
public class PaperAttachment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long paperId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
