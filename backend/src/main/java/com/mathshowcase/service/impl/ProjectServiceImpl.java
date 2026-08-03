package com.mathshowcase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mathshowcase.entity.Project;
import com.mathshowcase.entity.ProjectFile;
import com.mathshowcase.mapper.ProjectFileMapper;
import com.mathshowcase.mapper.ProjectMapper;
import com.mathshowcase.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    private final ProjectFileMapper projectFileMapper;
    private final FileStorageService fileStorageService;

    public ProjectServiceImpl(ProjectFileMapper projectFileMapper, FileStorageService fileStorageService) {
        this.projectFileMapper = projectFileMapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public Project createProject(Project project) {
        save(project);
        return project;
    }

    @Override
    @Transactional
    public Map<String, Object> uploadFiles(Long projectId, MultipartFile[] files) {
        Project project = getById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }

        List<Map<String, Object>> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            try {
                String filePath = fileStorageService.store(file, "projects/" + projectId);
                ProjectFile pf = new ProjectFile();
                pf.setProjectId(projectId);
                pf.setFileName(file.getOriginalFilename());
                pf.setFilePath(filePath);
                pf.setFileSize(file.getSize());
                pf.setFileType(getFileType(file.getOriginalFilename()));
                pf.setStorageMode(fileStorageService.getStorageMode());
                projectFileMapper.insert(pf);

                Map<String, Object> info = new HashMap<>();
                info.put("id", pf.getId());
                info.put("fileName", pf.getFileName());
                info.put("fileSize", pf.getFileSize());
                uploadedFiles.add(info);
            } catch (Exception e) {
                throw new RuntimeException("文件上传失败: " + file.getOriginalFilename(), e);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("projectId", projectId);
        result.put("files", uploadedFiles);
        result.put("count", uploadedFiles.size());
        return result;
    }

    @Override
    public Map<String, Object> getProjectDetail(Long projectId) {
        Project project = getById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }

        Map<String, Object> detail = new HashMap<>();
        detail.put("id", project.getId());
        detail.put("title", project.getTitle());
        detail.put("description", project.getDescription());
        detail.put("tags", project.getTags());
        detail.put("coverImage", project.getCoverImage());
        detail.put("status", project.getStatus());
        detail.put("createTime", project.getCreateTime());
        detail.put("files", getProjectFiles(projectId));
        return detail;
    }

    @Override
    public Map<String, Object> getProjects(int page, int size, String keyword) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Project::getTitle, keyword).or().like(Project::getTags, keyword);
        }
        wrapper.orderByDesc(Project::getCreateTime);

        Page<Project> pageResult = page(new Page<>(page, size), wrapper);

        List<Map<String, Object>> records = pageResult.getRecords().stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("title", p.getTitle());
            map.put("description", p.getDescription());
            map.put("tags", p.getTags());
            map.put("coverImage", p.getCoverImage());
            map.put("status", p.getStatus());
            map.put("createTime", p.getCreateTime());

            LambdaQueryWrapper<ProjectFile> fw = new LambdaQueryWrapper<>();
            fw.eq(ProjectFile::getProjectId, p.getId());
            map.put("fileCount", projectFileMapper.selectCount(fw));
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", pageResult.getTotal());
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    @Override
    public List<Map<String, Object>> getProjectFiles(Long projectId) {
        LambdaQueryWrapper<ProjectFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectFile::getProjectId, projectId).orderByDesc(ProjectFile::getCreateTime);
        return projectFileMapper.selectList(wrapper).stream().map(f -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", f.getId());
            map.put("fileName", f.getFileName());
            map.put("filePath", f.getFilePath());
            map.put("fileSize", f.getFileSize());
            map.put("fileType", f.getFileType());
            map.put("storageMode", f.getStorageMode());
            map.put("createTime", f.getCreateTime());
            return map;
        }).collect(Collectors.toList());
    }

    private String getFileType(String fileName) {
        if (fileName == null) return "other";
        String name = fileName.toLowerCase();
        if (name.endsWith(".pdf")) return "pdf";
        if (name.endsWith(".doc") || name.endsWith(".docx")) return "doc";
        if (name.endsWith(".py")) return "python";
        if (name.endsWith(".m") || name.endsWith(".mat")) return "matlab";
        if (name.endsWith(".ipynb")) return "jupyter";
        if (name.endsWith(".java")) return "java";
        if (name.endsWith(".cpp") || name.endsWith(".c")) return "c";
        if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image";
        if (name.endsWith(".csv") || name.endsWith(".xlsx") || name.endsWith(".xls")) return "data";
        if (name.endsWith(".zip") || name.endsWith(".rar") || name.endsWith(".tar.gz")) return "archive";
        return "other";
    }
}
