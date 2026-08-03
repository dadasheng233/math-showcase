package com.mathshowcase.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mathshowcase.entity.Project;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProjectService extends IService<Project> {
    Project createProject(Project project);
    Project updateProject(Long id, Project project);
    void deleteProject(Long id);
    Map<String, Object> uploadFiles(Long projectId, MultipartFile[] files);
    Map<String, Object> getProjectDetail(Long projectId);
    Map<String, Object> getProjects(int page, int size, String keyword);
    List<Map<String, Object>> getProjectFiles(Long projectId);
    void togglePublish(Long projectId);
}
