import request from './request'

export function createPaper(data: { title: string; authors: string; abstractText: string; keywords: string; coverImage: string }) {
  return request.post('/papers', data)
}

export function updatePaper(id: number, data: { title: string; authors: string; abstractText: string; keywords: string }) {
  return request.put(`/papers/${id}`, data)
}

export function deletePaper(id: number) {
  return request.delete(`/papers/${id}`)
}

export function uploadPaperFile(id: number, formData: FormData) {
  return request.post(`/papers/${id}/upload`, formData)
}

export function uploadPaperCover(id: number, formData: FormData) {
  return request.post(`/papers/${id}/cover`, formData)
}

export function uploadPaperAttachments(id: number, formData: FormData) {
  return request.post(`/papers/${id}/attachments`, formData)
}

export function getPapers(params: { page: number; size: number; keyword?: string }) {
  return request.get('/papers', { params })
}

export function getPaperDetail(id: number) {
  return request.get(`/papers/${id}`)
}
