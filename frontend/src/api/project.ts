import request from './request'

export function createProject(data: { title: string; description: string; tags: string }) {
  return request.post('/projects', data)
}

export function getProjects(params: { page: number; size: number; keyword?: string }) {
  return request.get('/projects', { params })
}

export function getProjectDetail(id: number) {
  return request.get(`/projects/${id}`)
}

export function uploadProjectFiles(id: number, formData: FormData) {
  return request.post(`/projects/${id}/upload`, formData)
}
