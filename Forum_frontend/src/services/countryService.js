import api from '../utils/api'

/**
 * Servicio para gestión de países
 */
const countryService = {
  /**
   * Obtiene todos los países
   */
  getAllCountries: async () => {
    const response = await api.get('/countries')
    return response.data
  },

  /**
   * Obtiene un país por ID
   */
  getCountryById: async (id) => {
    const response = await api.get(`/countries/${id}`)
    return response.data
  },

  /**
   * Obtiene un país por código ISO
   */
  getCountryByIsoCode: async (isoCode) => {
    const response = await api.get(`/countries/code/${isoCode}`)
    return response.data
  },

  /**
   * Busca países por nombre
   */
  searchCountries: async (query) => {
    const response = await api.get('/countries/search', { params: { q: query } })
    return response.data
  },

  /**
   * Obtiene países por continente
   */
  getCountriesByContinent: async (continent) => {
    const response = await api.get(`/countries/continent/${continent}`)
    return response.data
  },

  /**
   * Obtiene lista de continentes
   */
  getAllContinents: async () => {
    const response = await api.get('/countries/continents')
    return response.data
  },

  /**
   * Obtiene estadísticas globales
   */
  getCountryStats: async () => {
    const response = await api.get('/countries/stats')
    return response.data
  },

  /**
   * Obtiene países aleatorios
   */
  getRandomCountries: async (count = 5) => {
    const response = await api.get('/countries/random', { params: { count } })
    return response.data
  }
}

export default countryService

