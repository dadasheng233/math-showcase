const apiBase = import.meta.env.VITE_API_BASE_URL || '/api'

export function getApiUrl(path: string): string {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return apiBase + path
}
