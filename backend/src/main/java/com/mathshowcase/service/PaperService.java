package com.mathshowcase.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mathshowcase.entity.Paper;
import com.mathshowcase.entity.PaperAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface PaperService extends IService<Paper> {
    Paper createPaper(Paper paper);
    Paper updatePaper(Long id, Paper paper);
    void deletePaper(Long id);
    Paper uploadPaperFile(Long paperId, MultipartFile file);
    Paper uploadCover(Long paperId, MultipartFile file);
    List<PaperAttachment> uploadAttachments(Long paperId, MultipartFile[] files);
    List<PaperAttachment> getAttachments(Long paperId);
    Map<String, Object> getPapers(int page, int size, String keyword);
    Paper getPaperDetail(Long id);
}
