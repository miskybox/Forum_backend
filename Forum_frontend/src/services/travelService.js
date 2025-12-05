import api from '../utils/api'

/**
 * Servicio para gestión del mapa de viajes
 */
const travelService = {
  // === LUGARES VISITADOS ===

  /**
   * Agrega un nuevo lugar visitado
   */
  addPlace: async (placeData) => {
    const response = await api.post('/travel/places', placeData)
    return response.data
  },

  /**
   * Actualiza un lugar visitado
   */
  updatePlace: async (placeId, placeData) => {
    const response = await api.put(`/travel/places/${placeId}`, placeData)
    return response.data
  },

  /**
   * Elimina un lugar visitado
   */
  deletePlace: async (placeId) => {
    await api.delete(`/travel/places/${placeId}`)
  },

  /**
   * Obtiene un lugar por ID
   */
  getPlaceById: async (placeId) => {
    const response = await api.get(`/travel/places/${placeId}`)
    return response.data
  },

  /**
   * Obtiene mis lugares visitados
   */
  getMyPlaces: async () => {
    const response = await api.get('/travel/my-places')
    return response.data
  },

  /**
   * Obtiene mis lugares con paginación
   */
  getMyPlacesPaginated: async (page = 0, size = 20, sortBy = 'createdAt', direction = 'desc') => {
    const response = await api.get('/travel/my-places/paginated', {
      params: { page, size, sortBy, direction }
    })
    return response.data
  },

  /**
   * Obtiene lugares por estado
   */
  getMyPlacesByStatus: async (status) => {
    const response = await api.get(`/travel/my-places/status/${status}`)
    return response.data
  },

  /**
   * Obtiene mis lugares favoritos
   */
  getMyFavorites: async () => {
    const response = await api.get('/travel/my-places/favorites')
    return response.data
  },

  /**
   * Marca/desmarca un lugar como favorito
   */
  toggleFavorite: async (placeId) => {
    const response = await api.patch(`/travel/places/${placeId}/favorite`)
    return response.data
  },

  // === ESTADÍSTICAS ===

  /**
   * Obtiene mis estadísticas de viaje
   */
  getMyStats: async () => {
    const response = await api.get('/travel/my-stats')
    return response.data
  },

  /**
   * Obtiene estadísticas de un usuario
   */
  getUserStats: async (userId) => {
    const response = await api.get(`/travel/users/${userId}/stats`)
    return response.data
  },

  /**
   * Obtiene lugares de un usuario
   */
  getUserPlaces: async (userId) => {
    const response = await api.get(`/travel/users/${userId}/places`)
    return response.data
  },

  // === RANKING ===

  /**
   * Obtiene el ranking de viajeros
   */
  getRanking: async (limit = 10) => {
    const response = await api.get('/travel/ranking', { params: { limit } })
    return response.data
  },

  /**
   * Obtiene mi posición en el ranking
   */
  getMyRanking: async () => {
    const response = await api.get('/travel/my-ranking')
    return response.data
  },

  /**
   * Verifica si he visitado un país
   */
  hasVisitedCountry: async (countryId) => {
    const response = await api.get(`/travel/check/${countryId}`)
    return response.data
  }
}

export default travelService

