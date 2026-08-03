package com.mathshowcase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mathshowcase.entity.Paper;
import com.mathshowcase.entity.PaperAttachment;
import com.mathshowcase.mapper.PaperAttachmentMapper;
import com.mathshowcase.mapper.PaperMapper;
import com.mathshowcase.service.PaperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    private static final Logger log = LoggerFactory.getLogger(PaperServiceImpl.class);
    private final FileStorageService fileStorageService;
    private final PaperAttachmentMapper attachmentMapper;

    public PaperServiceImpl(FileStorageService fileStorageService, PaperAttachmentMapper attachmentMapper) {
        this.fileStorageService = fileStorageService;
        this.attachmentMapper = attachmentMapper;
    }

    @Override
    public Paper createPaper(Paper paper) {
        paper.setViewCount(0);
        paper.setStatus("PUBLISHED");
        save(paper);
        return paper;
    }

    @Override
    public Paper updatePaper(Long id, Paper paper) {
        Paper existing = getById(id);
        if (existing == null) {
            throw new RuntimeException("论文不存在");
        }
        paper.setId(id);
        updateById(paper);
        return getById(id);
    }

    @Override
    public void deletePaper(Long id) {
        Paper paper = getById(id);
        if (paper == null) {
            throw new RuntimeException("论文不存在");
        }
        removeById(id);
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
    @Transactional
    public List<PaperAttachment> uploadAttachments(Long paperId, MultipartFile[] files) {
        Paper paper = getById(paperId);
        if (paper == null) {
            throw new RuntimeException("论文不存在");
        }
        List<PaperAttachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            try {
                String filePath = fileStorageService.store(file, "attachments/" + paperId);
                PaperAttachment attachment = new PaperAttachment();
                attachment.setPaperId(paperId);
                attachment.setFileName(file.getOriginalFilename());
                attachment.setFilePath(filePath);
                attachment.setFileSize(file.getSize());
                attachment.setFileType(getFileType(file.getOriginalFilename()));
                attachmentMapper.insert(attachment);
                attachments.add(attachment);
            } catch (Exception e) {
                log.error("附件上传失败, paperId={}, fileName={}", paperId, file.getOriginalFilename(), e);
                throw new RuntimeException("附件上传失败: " + e.getMessage(), e);
            }
        }
        return attachments;
    }

    @Override
    public List<PaperAttachment> getAttachments(Long paperId) {
        return attachmentMapper.selectList(
                new LambdaQueryWrapper<PaperAttachment>()
                        .eq(PaperAttachment::getPaperId, paperId)
                        .orderByDesc(PaperAttachment::getCreateTime)
        );
    }

    private String getFileType(String fileName) {
        if (fileName == null) return "other";
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".py")) return "python";
        if (lower.endsWith(".java")) return "java";
        if (lower.endsWith(".c") || lower.endsWith(".cpp") || lower.endsWith(".h")) return "c";
        if (lower.endsWith(".js") || lower.endsWith(".ts")) return "javascript";
        if (lower.endsWith(".txt")) return "text";
        if (lower.endsWith(".zip") || lower.endsWith(".rar") || lower.endsWith(".7z")) return "archive";
        if (lower.endsWith(".md")) return "markdown";
        if (lower.endsWith(".json") || lower.endsWith(".xml") || lower.endsWith(".yml") || lower.endsWith(".yaml")) return "data";
        if (lower.endsWith(".xlsx") || lower.endsWith(".xls") || lower.endsWith(".csv")) return "excel";
        return "other";
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
