import PropTypes from 'prop-types'
import { Link } from 'react-router-dom'

/**
 * Componente que muestra el resumen final de una partida
 */
const TriviaGameSummary = ({ game, onPlayAgain }) => {
  const accuracy = game.totalQuestions > 0 
    ? (game.correctAnswers / game.totalQuestions) * 100 
    : 0

  const getGrade = () => {
    if (accuracy >= 90) return { grade: 'S', color: 'text-amber-400', message: '¡Perfecto!' }
    if (accuracy >= 80) return { grade: 'A', color: 'text-emerald-400', message: '¡Excelente!' }
    if (accuracy >= 70) return { grade: 'B', color: 'text-blue-400', message: '¡Muy bien!' }
    if (accuracy >= 60) return { grade: 'C', color: 'text-indigo-400', message: '¡Bien hecho!' }
    if (accuracy >= 50) return { grade: 'D', color: 'text-orange-400', message: 'Puedes mejorar' }
    return { grade: 'F', color: 'text-red-400', message: 'Sigue practicando' }
  }

  const { grade, color, message } = getGrade()

  return (
    <div className="bg-white rounded-2xl shadow-xl overflow-hidden max-w-lg mx-auto">
      {/* Header con confeti si es partida perfecta */}
      <div className={`relative px-6 py-8 text-center ${
        game.perfectGame 
          ? 'bg-gradient-to-br from-amber-400 via-yellow-500 to-orange-500' 
          : 'bg-gradient-to-br from-indigo-600 to-purple-600'
      }`}>
        {game.perfectGame && (
          <div className="absolute inset-0 overflow-hidden">
            {[...Array(20)].map((_, i) => (
              <span 
                key={i}
                className="absolute animate-bounce text-2xl"
                style={{
                  left: `${Math.random() * 100}%`,
                  top: `${Math.random() * 100}%`,
                  animationDelay: `${Math.random() * 2}s`,
                  animationDuration: `${2 + Math.random() * 2}s`
                }}
              >
                ✨
              </span>
            ))}
          </div>
        )}
        
        <div className="relative z-10">
          <span className={`text-8xl font-black ${color}`}>{grade}</span>
          <p className="text-white text-xl mt-2">{message}</p>
          
          {game.perfectGame && (
            <div className="mt-4 bg-white/20 rounded-full px-4 py-2 inline-block">
              <span className="text-white font-bold">🏆 ¡PARTIDA PERFECTA!</span>
            </div>
          )}
        </div>
      </div>

      {/* Estadísticas */}
      <div className="p-6">
        <div className="grid grid-cols-2 gap-4 mb-6">
          <StatBox 
            icon="🎯" 
            value={game.score} 
            label="Puntuación"
          />
          <StatBox 
            icon="✅" 
            value={`${game.correctAnswers}/${game.totalQuestions}`} 
            label="Correctas"
          />
          <StatBox 
            icon="📊" 
            value={`${accuracy.toFixed(0)}%`} 
            label="Precisión"
          />
          <StatBox 
            icon="⏱️" 
            value={formatTime(game.totalTimeSeconds)} 
            label="Tiempo"
          />
        </div>

        {/* Barra de precisión */}
        <div className="mb-6">
          <div className="flex justify-between text-sm text-slate-600 mb-2">
            <span>Precisión</span>
            <span className="font-semibold">{accuracy.toFixed(1)}%</span>
          </div>
          <div className="h-3 bg-slate-100 rounded-full overflow-hidden">
            <div 
              className={`h-full rounded-full transition-all duration-1000 ${
                accuracy >= 80 ? 'bg-emerald-500' :
                accuracy >= 60 ? 'bg-blue-500' :
                accuracy >= 40 ? 'bg-amber-500' : 'bg-red-500'
              }`}
              style={{ width: `${accuracy}%` }}
            />
          </div>
        </div>

        {/* Botones */}
        <div className="flex flex-col gap-3">
          <button
            onClick={onPlayAgain}
            className="w-full py-3 bg-gradient-to-r from-indigo-500 to-purple-500 text-white rounded-xl font-bold hover:from-indigo-600 hover:to-purple-600 transition-colors"
          >
            🎮 Jugar otra vez
          </button>
          
          <Link
            to="/trivia/leaderboard"
            className="w-full py-3 border-2 border-slate-200 rounded-xl font-semibold text-slate-600 hover:bg-slate-50 transition-colors text-center"
          >
            🏆 Ver ranking
          </Link>
          
          <Link
            to="/trivia"
            className="w-full py-3 text-slate-500 hover:text-slate-700 transition-colors text-center"
          >
            ← Volver al menú
          </Link>
        </div>
      </div>
    </div>
  )
}

const StatBox = ({ icon, value, label }) => (
  <div className="bg-slate-50 rounded-xl p-4 text-center">
    <span className="text-2xl">{icon}</span>
    <p className="text-2xl font-bold text-slate-800 mt-1">{value}</p>
    <p className="text-slate-500 text-sm">{label}</p>
  </div>
)

StatBox.propTypes = {
  icon: PropTypes.string.isRequired,
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
  label: PropTypes.string.isRequired
}

const formatTime = (seconds) => {
  if (!seconds) return '0:00'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

TriviaGameSummary.propTypes = {
  game: PropTypes.shape({
    score: PropTypes.number.isRequired,
    correctAnswers: PropTypes.number.isRequired,
    totalQuestions: PropTypes.number.isRequired,
    totalTimeSeconds: PropTypes.number,
    perfectGame: PropTypes.bool,
    accuracyPercentage: PropTypes.number
  }).isRequired,
  onPlayAgain: PropTypes.func.isRequired
}

export default TriviaGameSummary

