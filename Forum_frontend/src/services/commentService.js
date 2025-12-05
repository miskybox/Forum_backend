import api from '../utils/api'

const commentService = {
  getAllComments: async () => {
    try {
      const response = await api.get('/comments')
      return response.data
    } catch (error) {
      console.error('Error al obtener comentarios:', error)
      throw error
    }
  },
  
  getCommentById: async (id) => {
    try {
      const response = await api.get(`/comments/${id}`)
      return response.data
    } catch (error) {
      console.error(`Error al obtener comentario con id ${id}:`, error)
      throw error
    }
  },
  
  getCommentsByPost: async (postId) => {
    try {
      const response = await api.get(`/comments/post/${postId}`)
      return response.data
    } catch (error) {
      console.error(`Error al obtener comentarios del post con id ${postId}:`, error)
      throw error
    }
  },
  
  createComment: async (postId, commentData) => {
    try {
      // Asegúrese de incluir el postId en el objeto commentData
      const commentDataWithPostId = {
        ...commentData,
        postId
      }
      
      const response = await api.post(`/comments/post/${postId}`, commentDataWithPostId)
      return response.data
    } catch (error) {
      console.error(`Error al crear comentario para el post con id ${postId}:`, error)
      throw error
    }
  },
  
  updateComment: async (id, commentData) => {
    try {
      const response = await api.put(`/comments/${id}`, commentData)
      return response.data
    } catch (error) {
      console.error(`Error al actualizar comentario con id ${id}:`, error)
      throw error
    }
  },
  
  deleteComment: async (id) => {
    try {
      const response = await api.delete(`/comments/${id}`)
      return response.data
    } catch (error) {
      console.error(`Error al eliminar comentario con id ${id}:`, error)
      throw error
    }
  }
}

export default commentService