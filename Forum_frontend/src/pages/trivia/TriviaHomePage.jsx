import { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import TriviaStats from '../../components/trivia/TriviaStats'
import triviaService from '../../services/triviaService'
import toast from 'react-hot-toast'

/**
 * Página principal de la Trivia Geográfica
 */
const TriviaHomePage = () => {
  const { isAuthenticated } = useAuth()
  const navigate = useNavigate()
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)
  const [starting, setStarting] = useState(false)

  useEffect(() => {
    if (isAuthenticated) {
      loadStats()
    } else {
      setLoading(false)
    }
  }, [isAuthenticated])

  const loadStats = async () => {
    try {
      const data = await triviaService.getMyScore()
      setStats(data)
    } catch (error) {
      console.error('Error cargando stats:', error)
    } finally {
      setLoading(false)
    }
  }

  const startGame = async (mode, options = {}) => {
    if (!isAuthenticated) {
      toast.error('Inicia sesión para jugar')
      navigate('/login')
      return
    }

    // Modo infinito usa API externa, redirigir directamente
    if (mode === 'INFINITE') {
      navigate('/trivia/infinite')
      return
    }

    setStarting(true)
    try {
      const game = await triviaService.startGame({
        gameMode: mode,
        totalQuestions: options.questions || 10,
        difficulty: options.difficulty,
        continent: options.continent
      })
      navigate(`/trivia/play/${game.id}`)
    } catch (error) {
      toast.error(error.response?.data?.message || 'Error al iniciar partida')
    } finally {
      setStarting(false)
    }
  }

  const gameModes = [
    {
      mode: 'QUICK',
      icon: '⚡',
      title: 'Quiz Rápido',
      description: '10 preguntas aleatorias para calentar',
      color: 'from-amber-500 to-orange-500',
      questions: 10
    },
    {
      mode: 'CHALLENGE',
      icon: '🏆',
      title: 'Desafío',
      description: '20 preguntas para demostrar tu conocimiento',
      color: 'from-emerald-500 to-teal-500',
      questions: 20
    },
    {
      mode: 'INFINITE',
      icon: '♾️',
      title: 'Modo Infinito',
      description: 'Preguntas ilimitadas de API externa. ¡Sin repeticiones!',
      color: 'from-fuchsia-500 to-pink-500',
      questions: '∞',
      special: true
    },
    {
      mode: 'DAILY',
      icon: '📅',
      title: 'Trivia Diaria',
      description: 'Una partida especial cada día',
      color: 'from-violet-500 to-purple-500',
      questions: 15
    }
  ]

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-900 via-purple-900 to-violet-900">
      {/* Header */}
      <div className="relative overflow-hidden">
        <div className="absolute inset-0 bg-[url('/src/assets/pattern.svg')] opacity-10" />
        <div className="container mx-auto px-4 py-12 relative">
          <div className="text-center max-w-3xl mx-auto">
            <h1 className="text-4xl md:text-5xl font-black text-white mb-4">
              🌍 Trivia Geográfica
            </h1>
            <p className="text-xl text-purple-200">
              Pon a prueba tus conocimientos sobre países, capitales, banderas y más
            </p>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <div className="grid lg:grid-cols-3 gap-8">
          {/* Columna principal - Modos de juego */}
          <div className="lg:col-span-2 space-y-6">
            <h2 className="text-2xl font-bold text-white mb-6">Elige un modo de juego</h2>
            
            <div className="grid md:grid-cols-2 gap-4">
              {gameModes.map(mode => (
                <button
                  key={mode.mode}
                  onClick={() => startGame(mode.mode, { questions: mode.questions })}
                  disabled={starting}
                  className={`bg-gradient-to-br ${mode.color} p-6 rounded-2xl text-left text-white hover:scale-[1.02] transition-transform disabled:opacity-50 disabled:cursor-not-allowed`}
                >
                  <span className="text-4xl mb-3 block">{mode.icon}</span>
                  <h3 className="text-xl font-bold mb-2">{mode.title}</h3>
                  <p className="text-white/80 text-sm">{mode.description}</p>
                  <div className="mt-4 flex items-center justify-between">
                    <span className="text-white/60 text-sm">{mode.questions} preguntas</span>
                    <span className="text-white font-bold">Jugar →</span>
                  </div>
                </button>
              ))}
            </div>

            {/* Filtros por continente */}
            <div className="bg-white/10 backdrop-blur-sm rounded-2xl p-6">
              <h3 className="text-lg font-bold text-white mb-4">🌎 Jugar por continente</h3>
              <div className="flex flex-wrap gap-3">
                {['Europa', 'América', 'Asia', 'África', 'Oceanía'].map(continent => (
                  <button
                    key={continent}
                    onClick={() => startGame('CHALLENGE', { questions: 15, continent })}
                    disabled={starting}
                    className="px-4 py-2 bg-white/20 hover:bg-white/30 rounded-xl text-white transition-colors disabled:opacity-50"
                  >
                    {getContinentEmoji(continent)} {continent}
                  </button>
                ))}
              </div>
            </div>

            {/* Links adicionales */}
            <div className="grid md:grid-cols-2 gap-4">
              <Link
                to="/trivia/leaderboard"
                className="bg-white/10 backdrop-blur-sm rounded-2xl p-6 hover:bg-white/20 transition-colors group"
              >
                <span className="text-3xl mb-3 block">🏆</span>
                <h3 className="text-lg font-bold text-white">Ranking Global</h3>
                <p className="text-purple-200 text-sm">Compite con otros jugadores</p>
                <span className="text-purple-300 mt-4 block group-hover:text-white">Ver ranking →</span>
              </Link>

              <Link
                to="/travel"
                className="bg-white/10 backdrop-blur-sm rounded-2xl p-6 hover:bg-white/20 transition-colors group"
              >
                <span className="text-3xl mb-3 block">🗺️</span>
                <h3 className="text-lg font-bold text-white">Mi Mapa de Viajes</h3>
                <p className="text-purple-200 text-sm">Registra los países que has visitado</p>
                <span className="text-purple-300 mt-4 block group-hover:text-white">Explorar →</span>
              </Link>
            </div>
          </div>

          {/* Columna lateral - Stats */}
          <div className="space-y-6">
            {loading ? (
              <div className="bg-white/10 rounded-2xl p-12 text-center">
                <div className="animate-spin rounded-full h-12 w-12 border-4 border-purple-400 border-t-transparent mx-auto" />
              </div>
            ) : isAuthenticated && stats ? (
              <TriviaStats stats={stats} />
            ) : (
              <div className="bg-white/10 backdrop-blur-sm rounded-2xl p-6 text-center">
                <span className="text-6xl mb-4 block">🎮</span>
                <h3 className="text-xl font-bold text-white mb-2">¡Únete al juego!</h3>
                <p className="text-purple-200 mb-4">
                  Inicia sesión para guardar tu progreso y competir en el ranking
                </p>
                <Link
                  to="/login"
                  className="inline-block px-6 py-3 bg-white text-purple-600 rounded-xl font-bold hover:bg-purple-50 transition-colors"
                >
                  Iniciar sesión
                </Link>
              </div>
            )}

            {/* Tips */}
            <div className="bg-white/10 backdrop-blur-sm rounded-2xl p-6">
              <h3 className="text-lg font-bold text-white mb-4">💡 Consejos</h3>
              <ul className="space-y-3 text-purple-200 text-sm">
                <li className="flex items-start gap-2">
                  <span>⚡</span>
                  <span>Responde rápido para ganar puntos extra</span>
                </li>
                <li className="flex items-start gap-2">
                  <span>🔥</span>
                  <span>Mantén tu racha para multiplicar puntos</span>
                </li>
                <li className="flex items-start gap-2">
                  <span>📅</span>
                  <span>Juega la trivia diaria para bonificaciones</span>
                </li>
                <li className="flex items-start gap-2">
                  <span>🏆</span>
                  <span>Completa partidas perfectas para subir de nivel</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

const getContinentEmoji = (continent) => {
  const emojis = {
    'Europa': '🇪🇺',
    'América': '🌎',
    'Asia': '🌏',
    'África': '🌍',
    'Oceanía': '🏝️'
  }
  return emojis[continent] || '🌐'
}

export default TriviaHomePage

