import api from '../utils/api'

/**
 * Servicio para el juego de trivia geográfica
 */
const triviaService = {
  // === PARTIDAS ===

  /**
   * Inicia una nueva partida
   * @param {Object} options - Opciones de la partida
   * @param {string} options.gameMode - Modo: QUICK, CHALLENGE, DUEL, PRACTICE, DAILY
   * @param {number} options.totalQuestions - Número de preguntas (5-50)
   * @param {number} options.difficulty - Dificultad (1-5)
   * @param {string} options.questionType - Tipo específico de pregunta
   * @param {string} options.continent - Filtrar por continente
   * @param {number} options.opponentId - ID del oponente para duelos
   */
  startGame: async (options) => {
    const response = await api.post('/trivia/games', options)
    return response.data
  },

  /**
   * Obtiene el estado de una partida
   */
  getGameStatus: async (gameId) => {
    const response = await api.get(`/trivia/games/${gameId}`)
    return response.data
  },

  /**
   * Obtiene la siguiente pregunta
   */
  getNextQuestion: async (gameId) => {
    const response = await api.get(`/trivia/games/${gameId}/question`)
    return response.data
  },

  /**
   * Responde una pregunta
   * @param {Object} answer - Datos de la respuesta
   * @param {number} answer.gameId - ID de la partida
   * @param {number} answer.questionId - ID de la pregunta
   * @param {string} answer.selectedAnswer - Respuesta seleccionada
   * @param {number} answer.responseTimeMs - Tiempo de respuesta en ms
   * @param {boolean} answer.hintUsed - Si usó pista
   * @param {boolean} answer.timedOut - Si se agotó el tiempo
   */
  answerQuestion: async (answer) => {
    const response = await api.post('/trivia/games/answer', answer)
    return response.data
  },

  /**
   * Finaliza una partida
   */
  finishGame: async (gameId) => {
    const response = await api.post(`/trivia/games/${gameId}/finish`)
    return response.data
  },

  /**
   * Abandona una partida
   */
  abandonGame: async (gameId) => {
    await api.delete(`/trivia/games/${gameId}`)
  },

  /**
   * Obtiene el historial de partidas
   */
  getGameHistory: async (page = 0, size = 10) => {
    const response = await api.get('/trivia/my-games', { params: { page, size } })
    return response.data
  },

  // === PUNTUACIONES ===

  /**
   * Obtiene mis estadísticas
   */
  getMyScore: async () => {
    const response = await api.get('/trivia/my-score')
    return response.data
  },

  /**
   * Obtiene estadísticas de un usuario
   */
  getUserScore: async (userId) => {
    const response = await api.get(`/trivia/users/${userId}/score`)
    return response.data
  },

  /**
   * Obtiene el ranking global
   * @param {string} type - Tipo de ranking: score, accuracy, streak
   */
  getLeaderboard: async (type = 'score', page = 0, size = 20) => {
    const response = await api.get('/trivia/leaderboard', { params: { type, page, size } })
    return response.data
  },

  /**
   * Obtiene mi posición en el ranking
   */
  getMyRank: async () => {
    const response = await api.get('/trivia/my-rank')
    return response.data
  },

  // === PREGUNTAS (PRÁCTICA) ===

  /**
   * Obtiene una pregunta aleatoria
   */
  getRandomQuestion: async () => {
    const response = await api.get('/trivia/questions/random')
    return response.data
  },

  /**
   * Verifica una respuesta (modo práctica)
   */
  checkAnswer: async (questionId, answer) => {
    const response = await api.post(`/trivia/questions/${questionId}/check`, null, {
      params: { answer }
    })
    return response.data
  }
}

export default triviaService

