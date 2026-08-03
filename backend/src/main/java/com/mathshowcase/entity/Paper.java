package com.mathshowcase.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("paper")
public class Paper {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String authors;
    private String abstractText;
    private String keywords;
    private String filePath;
    private String fileName;
    private Long fileSize;
    private String coverImage;
    private String storageMode;
    private Long uploaderId;
    private String status;
    private Integer viewCount;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
