import PropTypes from 'prop-types'

/**
 * Componente que muestra las estadísticas de trivia del usuario
 */
const TriviaStats = ({ stats }) => {
  if (!stats) return null

  const expProgress = stats.experienceToNextLevel > 0
    ? (stats.experiencePoints / stats.experienceToNextLevel) * 100
    : 0

  return (
    <div className="bg-gradient-to-br from-indigo-600 via-purple-600 to-violet-700 rounded-2xl p-6 text-white shadow-xl">
      {/* Header con nivel */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <div className="flex items-center gap-3">
            <div className="w-14 h-14 bg-white/20 rounded-full flex items-center justify-center text-2xl">
              {stats.profileImageUrl ? (
                <img 
                  src={stats.profileImageUrl} 
                  alt={stats.username}
                  className="w-full h-full rounded-full object-cover"
                />
              ) : '🧠'}
            </div>
            <div>
              <h3 className="text-xl font-bold">{stats.username}</h3>
              <p className="text-purple-200">{stats.playerTitle}</p>
            </div>
          </div>
        </div>
        <div className="text-right">
          <span className="text-4xl font-black">Lv.{stats.level}</span>
          {stats.globalRank && (
            <p className="text-purple-200 text-sm">Rank #{stats.globalRank}</p>
          )}
        </div>
      </div>

      {/* Barra de experiencia */}
      <div className="mb-6">
        <div className="flex justify-between text-sm mb-2">
          <span>Experiencia</span>
          <span>{stats.experiencePoints} / {stats.experienceToNextLevel} XP</span>
        </div>
        <div className="h-3 bg-purple-900/50 rounded-full overflow-hidden">
          <div 
            className="h-full bg-gradient-to-r from-yellow-400 to-amber-500 rounded-full"
            style={{ width: `${Math.min(expProgress, 100)}%` }}
          />
        </div>
      </div>

      {/* Grid de stats principales */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-6">
        <StatCard 
          icon="⭐" 
          value={stats.totalScore?.toLocaleString() || 0} 
          label="Puntuación"
        />
        <StatCard 
          icon="🎮" 
          value={stats.totalGames || 0} 
          label="Partidas"
        />
        <StatCard 
          icon="🎯" 
          value={`${stats.accuracyPercentage?.toFixed(1) || 0}%`} 
          label="Precisión"
        />
        <StatCard 
          icon="🔥" 
          value={stats.bestStreak || 0} 
          label="Mejor racha"
        />
      </div>

      {/* Stats secundarios */}
      <div className="grid grid-cols-3 gap-3 mb-6">
        <MiniStat 
          label="Correctas" 
          value={`${stats.correctAnswers || 0}/${stats.totalQuestions || 0}`}
        />
        <MiniStat 
          label="Perfectas" 
          value={stats.perfectGames || 0}
        />
        <MiniStat 
          label="Racha actual" 
          value={`🔥 ${stats.currentStreak || 0}`}
        />
      </div>

      {/* Tiempo promedio */}
      {stats.avgResponseTime && (
        <div className="bg-white/10 rounded-xl p-4 text-center">
          <p className="text-purple-200 text-sm">Tiempo promedio de respuesta</p>
          <p className="text-2xl font-bold">
            ⚡ {(stats.avgResponseTime / 1000).toFixed(1)}s
          </p>
          {stats.bestResponseTime && (
            <p className="text-purple-300 text-xs mt-1">
              Mejor: {(stats.bestResponseTime / 1000).toFixed(2)}s
            </p>
          )}
        </div>
      )}

      {/* Racha diaria */}
      {stats.dailyStreak > 0 && (
        <div className="mt-4 bg-amber-500/30 rounded-xl p-3 text-center border border-amber-400/50">
          <span className="text-xl">🔥</span>
          <span className="ml-2 font-bold">{stats.dailyStreak} días seguidos jugando</span>
        </div>
      )}
    </div>
  )
}

const StatCard = ({ icon, value, label }) => (
  <div className="bg-white/10 rounded-xl p-3 text-center backdrop-blur-sm">
    <span className="text-2xl">{icon}</span>
    <p className="text-xl font-bold mt-1">{value}</p>
    <p className="text-purple-200 text-xs">{label}</p>
  </div>
)

StatCard.propTypes = {
  icon: PropTypes.string.isRequired,
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
  label: PropTypes.string.isRequired
}

const MiniStat = ({ label, value }) => (
  <div className="bg-white/5 rounded-lg p-2 text-center">
    <p className="text-purple-200 text-xs">{label}</p>
    <p className="font-semibold">{value}</p>
  </div>
)

MiniStat.propTypes = {
  label: PropTypes.string.isRequired,
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired
}

TriviaStats.propTypes = {
  stats: PropTypes.shape({
    username: PropTypes.string,
    profileImageUrl: PropTypes.string,
    playerTitle: PropTypes.string,
    level: PropTypes.number,
    globalRank: PropTypes.number,
    experiencePoints: PropTypes.number,
    experienceToNextLevel: PropTypes.number,
    totalScore: PropTypes.number,
    totalGames: PropTypes.number,
    accuracyPercentage: PropTypes.number,
    bestStreak: PropTypes.number,
    currentStreak: PropTypes.number,
    correctAnswers: PropTypes.number,
    totalQuestions: PropTypes.number,
    perfectGames: PropTypes.number,
    avgResponseTime: PropTypes.number,
    bestResponseTime: PropTypes.number,
    dailyStreak: PropTypes.number
  })
}

export default TriviaStats

