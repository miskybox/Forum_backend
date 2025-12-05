import api from '../utils/api'

const categoryService = {
  getAllCategories: async () => {
    try {
      const response = await api.get('/categories')
      return response.data
    } catch (error) {
      console.error('Error al obtener categorías:', error)
      throw error
    }
  },
  
  getCategoryById: async (id) => {
    try {
      const response = await api.get(`/categories/${id}`)
      return response.data
    } catch (error) {
      console.error(`Error al obtener categoría con id ${id}:`, error)
      throw error
    }
  },
  
  createCategory: async (categoryData) => {
    try {
      const response = await api.post('/categories', categoryData)
      return response.data
    } catch (error) {
      console.error('Error al crear categoría:', error)
      throw error
    }
  },
  
  updateCategory: async (id, categoryData) => {
    try {
      const response = await api.put(`/categories/${id}`, categoryData)
      return response.data
    } catch (error) {
      console.error(`Error al actualizar categoría con id ${id}:`, error)
      throw error
    }
  },
  
  deleteCategory: async (id) => {
    try {
      const response = await api.delete(`/categories/${id}`)
      return response.data
    } catch (error) {
      console.error(`Error al eliminar categoría con id ${id}:`, error)
      throw error
    }
  },
  
  uploadCategoryImage: async (id, imageFile) => {
    try {
      const formData = new FormData()
      formData.append('file', imageFile)
      
      const response = await api.post(`/categories/${id}/image`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      
      return response.data
    } catch (error) {
      console.error(`Error al subir imagen para categoría con id ${id}:`, error)
      throw error
    }
  }
}

export default categoryService