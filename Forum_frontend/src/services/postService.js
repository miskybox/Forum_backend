import api from '../utils/api'

const postService = {
  getAllPosts: async (page = 0, size = 10) => {
    try {
      const response = await api.get('/posts', {
        params: { page, size }
      })
      return response.data
    } catch (error) {
      console.error('Error al obtener posts:', error)
      throw error
    }
  },
  
  getPostById: async (id) => {
    try {
      const response = await api.get(`/posts/${id}`)
      return response.data
    } catch (error) {
      console.error(`Error al obtener post con id ${id}:`, error)
      throw error
    }
  },
  
  // Alias para compatibilidad
  fetchPostById: async (id) => {
    return postService.getPostById(id)
  },
  
  getPostsByForum: async (forumId) => {
    try {
      const response = await api.get(`/posts/forum/${forumId}`)
      return response.data
    } catch (error) {
      console.error(`Error al obtener posts del foro con id ${forumId}:`, error)
      throw error
    }
  },
  
  createPost: async (postData) => {
    try {
      const response = await api.post('/posts', postData)
      return response.data
    } catch (error) {
      console.error('Error al crear post:', error)
      throw error
    }
  },
  
  updatePost: async (id, postData) => {
    try {
      const response = await api.put(`/posts/${id}`, postData)
      return response.data
    } catch (error) {
      console.error(`Error al actualizar post con id ${id}:`, error)
      throw error
    }
  },
  
  deletePost: async (id) => {
    try {
      const response = await api.delete(`/posts/${id}`)
      return response.data
    } catch (error) {
      console.error(`Error al eliminar post con id ${id}:`, error)
      throw error
    }
  },
  
  uploadPostImages: async (id, imageFiles) => {
    try {
      const formData = new FormData()
      
      // Manejar múltiples archivos
      if (Array.isArray(imageFiles)) {
        imageFiles.forEach((file) => {
          formData.append('files', file)
        })
      } else {
        formData.append('files', imageFiles)
      }
      
      const response = await api.post(`/posts/${id}/images`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      
      return response.data
    } catch (error) {
      console.error(`Error al subir imágenes para post con id ${id}:`, error)
      throw error
    }
  },
  
  deletePostImage: async (postId, imageId) => {
    try {
      const response = await api.delete(`/posts/${postId}/images/${imageId}`)
      return response.data
    } catch (error) {
      console.error(`Error al eliminar imagen ${imageId} del post ${postId}:`, error)
      throw error
    }
  },
  
  getCurrentUserPosts: async () => {
    try {
      const response = await api.get('/posts/user')
      return response.data
    } catch (error) {
      console.error('Error al obtener posts del usuario actual:', error)
      throw error
    }
  }
}

export default postService
// Exportar función específica para compatibilidad con importaciones directas
export const fetchPostById = postService.fetchPostById