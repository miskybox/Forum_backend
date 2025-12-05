import api from '../utils/api'

const userService = {
  getAllUsers: async () => {
    try {
      const response = await api.get('/users')
      return response.data
    } catch (error) {
      console.error('Error al obtener usuarios:', error)
      throw error
    }
  },
  
  getUserById: async (id) => {
    try {
      const response = await api.get(`/users/${id}`)
      return response.data
    } catch (error) {
      console.error(`Error al obtener usuario con id ${id}:`, error)
      throw error
    }
  },
  
  createUser: async (userData, roles) => {
    try {
      const response = await api.post('/users', userData, {
        params: { roles }
      })
      return response.data
    } catch (error) {
      console.error('Error al crear usuario:', error)
      throw error
    }
  },
  
  updateUser: async (id, userData) => {
    try {
      const response = await api.put(`/users/${id}`, userData)
      return response.data
    } catch (error) {
      console.error(`Error al actualizar usuario con id ${id}:`, error)
      throw error
    }
  },
  
  deleteUser: async (id) => {
    try {
      const response = await api.delete(`/users/${id}`)
      return response.data
    } catch (error) {
      console.error(`Error al eliminar usuario con id ${id}:`, error)
      throw error
    }
  },
  
  changePassword: async (id, currentPassword, newPassword) => {
    try {
      const response = await api.put(`/users/${id}/change-password`, null, {
        params: { currentPassword, newPassword }
      })
      return response.data
    } catch (error) {
      console.error(`Error al cambiar contraseña del usuario con id ${id}:`, error)
      throw error
    }
  }
}

export default userService