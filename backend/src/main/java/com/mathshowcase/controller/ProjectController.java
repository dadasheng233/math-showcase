package com.mathshowcase.controller;

import com.mathshowcase.common.R;
import com.mathshowcase.dto.ProjectDTO;
import com.mathshowcase.entity.Project;
import com.mathshowcase.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public R<?> create(@RequestBody ProjectDTO dto, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Project project = new Project();
            project.setTitle(dto.getTitle());
            project.setDescription(dto.getDescription());
            project.setTags(dto.getTags());
            project.setCreatorId(userId);
            project.setStatus("ACTIVE");
            return R.ok(projectService.createProject(project));
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/upload")
    public R<?> uploadFiles(@PathVariable Long id, @RequestParam("files") MultipartFile[] files) {
        try {
            return R.ok(projectService.uploadFiles(id, files));
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    @GetMapping
    public R<?> list(@RequestParam(defaultValue = "1") int page,
                     @RequestParam(defaultValue = "12") int size,
                     @RequestParam(required = false) String keyword) {
        try {
            return R.ok(projectService.getProjects(page, size, keyword));
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        try {
            return R.ok(projectService.getProjectDetail(id));
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }
}
