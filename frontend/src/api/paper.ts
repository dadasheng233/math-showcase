import request from './request'

export function createPaper(data: { title: string; authors: string; abstractText: string; keywords: string; coverImage: string }) {
  return request.post('/papers', data)
}

export function uploadPaperFile(id: number, formData: FormData) {
  return request.post(`/papers/${id}/upload`, formData)
}

export function uploadPaperCover(id: number, formData: FormData) {
  return request.post(`/papers/${id}/cover`, formData)
}

export function getPapers(params: { page: number; size: number; keyword?: string }) {
  return request.get('/papers', { params })
}

export function getPaperDetail(id: number) {
  return request.get(`/papers/${id}`)
}
