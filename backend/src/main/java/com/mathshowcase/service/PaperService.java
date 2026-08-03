package com.mathshowcase.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mathshowcase.entity.Paper;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface PaperService extends IService<Paper> {
    Paper createPaper(Paper paper);
    Paper uploadPaperFile(Long paperId, MultipartFile file);
    Paper uploadCover(Long paperId, MultipartFile file);
    Map<String, Object> getPapers(int page, int size, String keyword);
    Paper getPaperDetail(Long id);
}
