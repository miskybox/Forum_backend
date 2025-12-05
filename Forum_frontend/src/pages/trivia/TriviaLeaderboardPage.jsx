import { useAuth } from '../../hooks/useAuth'
import TriviaLeaderboard from '../../components/trivia/TriviaLeaderboard'
import { Link } from 'react-router-dom'

/**
 * Página del ranking de trivia
 */
const TriviaLeaderboardPage = () => {
  const { user } = useAuth()

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-900 via-purple-900 to-violet-900">
      {/* Header */}
      <div className="bg-black/20 py-6">
        <div className="container mx-auto px-4">
          <div className="flex items-center justify-between">
            <div>
              <Link 
                to="/trivia"
                className="text-purple-300 hover:text-white transition-colors text-sm mb-2 inline-block"
              >
                ← Volver al menú
              </Link>
              <h1 className="text-2xl font-bold text-white">🏆 Ranking de Trivia</h1>
            </div>
            <Link
              to="/trivia"
              className="px-6 py-3 bg-gradient-to-r from-indigo-500 to-purple-500 text-white rounded-xl font-bold hover:from-indigo-600 hover:to-purple-600 transition-colors"
            >
              🎮 Jugar ahora
            </Link>
          </div>
        </div>
      </div>

      {/* Contenido */}
      <div className="container mx-auto px-4 py-8 max-w-3xl">
        <TriviaLeaderboard currentUserId={user?.id} />
      </div>
    </div>
  )
}

export default TriviaLeaderboardPage

