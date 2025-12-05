import { useState, useEffect } from 'react'
import PropTypes from 'prop-types'
import triviaService from '../../services/triviaService'

/**
 * Componente de ranking/leaderboard de trivia
 */
const TriviaLeaderboard = ({ currentUserId }) => {
  const [leaderboard, setLeaderboard] = useState(null)
  const [loading, setLoading] = useState(true)
  const [type, setType] = useState('score')

  useEffect(() => {
    loadLeaderboard()
  }, [type])

  const loadLeaderboard = async () => {
    setLoading(true)
    try {
      const data = await triviaService.getLeaderboard(type, 0, 20)
      setLeaderboard(data)
    } catch (error) {
      console.error('Error cargando ranking:', error)
    } finally {
      setLoading(false)
    }
  }

  const getRankStyle = (rank) => {
    switch (rank) {
      case 1: return 'bg-gradient-to-r from-amber-400 to-yellow-500 text-white'
      case 2: return 'bg-gradient-to-r from-slate-300 to-slate-400 text-white'
      case 3: return 'bg-gradient-to-r from-amber-600 to-orange-700 text-white'
      default: return 'bg-slate-100 text-slate-600'
    }
  }

  const getRankIcon = (rank) => {
    switch (rank) {
      case 1: return '🥇'
      case 2: return '🥈'
      case 3: return '🥉'
      default: return rank
    }
  }

  return (
    <div className="bg-white rounded-2xl shadow-xl overflow-hidden">
      {/* Header */}
      <div className="bg-gradient-to-r from-indigo-600 to-purple-600 px-6 py-6">
        <h2 className="text-2xl font-bold text-white mb-4">🏆 Ranking Global</h2>
        
        {/* Tabs */}
        <div className="flex gap-2">
          {[
            { value: 'score', label: '⭐ Puntuación' },
            { value: 'accuracy', label: '🎯 Precisión' },
            { value: 'streak', label: '🔥 Rachas' }
          ].map(tab => (
            <button
              key={tab.value}
              onClick={() => setType(tab.value)}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                type === tab.value
                  ? 'bg-white text-indigo-600'
                  : 'bg-white/20 text-white/80 hover:bg-white/30'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>
      </div>

      {/* Lista */}
      <div className="divide-y divide-slate-100">
        {loading ? (
          <div className="p-12 text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-4 border-indigo-500 border-t-transparent mx-auto" />
            <p className="text-slate-400 mt-4">Cargando ranking...</p>
          </div>
        ) : leaderboard?.entries?.length === 0 ? (
          <div className="p-12 text-center">
            <span className="text-6xl">🏜️</span>
            <p className="text-slate-500 mt-4">Aún no hay jugadores</p>
          </div>
        ) : (
          leaderboard?.entries?.map((entry, idx) => (
            <div 
              key={entry.userId}
              className={`flex items-center gap-4 p-4 ${
                entry.userId === currentUserId ? 'bg-indigo-50' : ''
              }`}
            >
              {/* Rank */}
              <div className={`w-10 h-10 rounded-full flex items-center justify-center font-bold ${getRankStyle(entry.rank)}`}>
                {getRankIcon(entry.rank)}
              </div>

              {/* Avatar y nombre */}
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2">
                  <span className="text-2xl">
                    {entry.profileImageUrl ? (
                      <img 
                        src={entry.profileImageUrl} 
                        alt={entry.username}
                        className="w-8 h-8 rounded-full"
                      />
                    ) : '👤'}
                  </span>
                  <div>
                    <p className="font-semibold text-slate-800 truncate">
                      {entry.username}
                      {entry.userId === currentUserId && (
                        <span className="ml-2 text-xs bg-indigo-100 text-indigo-600 px-2 py-0.5 rounded-full">
                          Tú
                        </span>
                      )}
                    </p>
                    <p className="text-sm text-slate-400">{entry.playerTitle}</p>
                  </div>
                </div>
              </div>

              {/* Stats */}
              <div className="text-right">
                <p className="text-xl font-bold text-slate-800">
                  {type === 'score' && entry.score?.toLocaleString()}
                  {type === 'accuracy' && `${entry.accuracyPercentage?.toFixed(1)}%`}
                  {type === 'streak' && `🔥 ${entry.bestStreak}`}
                </p>
                <p className="text-xs text-slate-400">
                  Nivel {entry.level} • {entry.totalGames} partidas
                </p>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Footer con total */}
      {leaderboard && (
        <div className="bg-slate-50 px-6 py-4 text-center text-slate-500 text-sm">
          {leaderboard.totalPlayers?.toLocaleString()} jugadores en el ranking
        </div>
      )}
    </div>
  )
}

TriviaLeaderboard.propTypes = {
  currentUserId: PropTypes.number
}

export default TriviaLeaderboard

