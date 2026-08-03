package com.mathshowcase.controller;

import com.mathshowcase.common.R;
import com.mathshowcase.dto.PaperDTO;
import com.mathshowcase.entity.Paper;
import com.mathshowcase.service.PaperService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/papers")
public class PaperController {

    private final PaperService paperService;

    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    @PostMapping
    public R<?> create(@RequestBody PaperDTO dto, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Paper paper = new Paper();
            paper.setTitle(dto.getTitle());
            paper.setAuthors(dto.getAuthors());
            paper.setAbstractText(dto.getAbstractText());
            paper.setKeywords(dto.getKeywords());
            paper.setCoverImage(dto.getCoverImage());
            paper.setUploaderId(userId);
            return R.ok(paperService.createPaper(paper));
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/upload")
    public R<?> uploadPaper(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            return R.ok(paperService.uploadPaperFile(id, file));
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/cover")
    public R<?> uploadCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            return R.ok(paperService.uploadCover(id, file));
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/attachments")
    public R<?> uploadAttachments(@PathVariable Long id, @RequestParam("files") MultipartFile[] files) {
        try {
            return R.ok(paperService.uploadAttachments(id, files));
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/attachments")
    public R<?> getAttachments(@PathVariable Long id) {
        try {
            return R.ok(paperService.getAttachments(id));
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    @GetMapping
    public R<?> list(@RequestParam(defaultValue = "1") int page,
                     @RequestParam(defaultValue = "12") int size,
                     @RequestParam(required = false) String keyword) {
        return R.ok(paperService.getPapers(page, size, keyword));
    }

    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        try {
            Paper paper = paperService.getPaperDetail(id);
            Map<String, Object> data = new HashMap<>();
            data.put("paper", paper);
            data.put("attachments", paperService.getAttachments(id));
            return R.ok(data);
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public R<?> update(@PathVariable Long id, @RequestBody PaperDTO dto) {
        try {
            Paper paper = new Paper();
            paper.setTitle(dto.getTitle());
            paper.setAuthors(dto.getAuthors());
            paper.setAbstractText(dto.getAbstractText());
            paper.setKeywords(dto.getKeywords());
            paper.setCoverImage(dto.getCoverImage());
            return R.ok(paperService.updatePaper(id, paper));
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        try {
            paperService.deletePaper(id);
            return R.ok("删除成功");
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }
}
