import api from '../utils/api'

const authService = {

  async register(userData) {
    try {
      console.log('Enviando datos de registro:', userData)
      const response = await api.post('/auth/register', userData)
      return response.data
    } catch (error) {
      console.error('Error en registro:', error.response?.data || error.message)
      throw error
    }
  },


  async login(credentials) {
    try {
      console.log('Enviando solicitud de login:', credentials)
      const response = await api.post('/auth/login', credentials)
      
      const { accessToken, refreshToken } = response.data
      
      console.log('Login exitoso, recibidos tokens')
      
      localStorage.setItem('token', accessToken)
      localStorage.setItem('refreshToken', refreshToken)
      
      return response.data
    } catch (error) {
      console.error('Error en login:', error.response?.data || error.message)
      throw error
    }
  },

  async getCurrentUser() {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        throw new Error('No hay token disponible');
      }
      
      console.log('Obteniendo usuario actual con token disponible');
      const response = await api.get('/users/me')
      return response.data
    } catch (error) {
      console.error('Error al obtener usuario actual:', error.response?.data || error.message)
      throw error
    }
  },

  async logout() {
    try {
      await api.post('/auth/logout')
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
    } catch (error) {
      console.error('Error en logout:', error.response?.data || error.message)

      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      throw error
    }
  },

  async refreshToken(refreshToken) {
    try {
      const response = await api.post('/auth/refresh', { refreshToken })
      
      const { accessToken, refreshToken: newRefreshToken } = response.data
      
  
      localStorage.setItem('token', accessToken)
      localStorage.setItem('refreshToken', newRefreshToken)
      
      return response.data
    } catch (error) {
      console.error('Error al renovar token:', error.response?.data || error.message)
      throw error
    }
  },


  isAuthenticated() {
    return !!localStorage.getItem('token')
  }
}

export default authService