package com.mathshowcase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mathshowcase.entity.Paper;
import com.mathshowcase.mapper.PaperMapper;
import com.mathshowcase.service.PaperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    private static final Logger log = LoggerFactory.getLogger(PaperServiceImpl.class);
    private final FileStorageService fileStorageService;

    public PaperServiceImpl(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    public Paper createPaper(Paper paper) {
        paper.setViewCount(0);
        paper.setStatus("PUBLISHED");
        save(paper);
        return paper;
    }

    @Override
    @Transactional
    public Paper uploadPaperFile(Long paperId, MultipartFile file) {
        Paper paper = getById(paperId);
        if (paper == null) {
            throw new RuntimeException("论文不存在");
        }
        try {
            String filePath = fileStorageService.store(file, "papers/" + paperId);
            paper.setFileName(file.getOriginalFilename());
            paper.setFilePath(filePath);
            paper.setFileSize(file.getSize());
            paper.setStorageMode(fileStorageService.getStorageMode());
            updateById(paper);
            return paper;
        } catch (Exception e) {
            log.error("论文文件上传失败, paperId={}", paperId, e);
            throw new RuntimeException("论文文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Paper uploadCover(Long paperId, MultipartFile file) {
        Paper paper = getById(paperId);
        if (paper == null) {
            throw new RuntimeException("论文不存在");
        }
        try {
            String filePath = fileStorageService.store(file, "covers/" + paperId);
            paper.setCoverImage("/api/files/uploads/" + filePath);
            updateById(paper);
            return paper;
        } catch (Exception e) {
            log.error("封面上传失败, paperId={}", paperId, e);
            throw new RuntimeException("封面上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> getPapers(int page, int size, String keyword) {
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Paper::getStatus, "PUBLISHED");
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Paper::getTitle, keyword)
                    .or().like(Paper::getKeywords, keyword)
                    .or().like(Paper::getAbstractText, keyword));
        }
        wrapper.orderByDesc(Paper::getCreateTime);

        Page<Paper> pageResult = page(new Page<>(page, size), wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("records", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    @Override
    public Paper getPaperDetail(Long id) {
        Paper paper = getById(id);
        if (paper == null) {
            throw new RuntimeException("论文不存在");
        }
        paper.setViewCount(paper.getViewCount() + 1);
        updateById(paper);
        return paper;
    }
}
