import PropTypes from 'prop-types'

/**
 * Componente que muestra el resultado de una respuesta
 */
const TriviaResult = ({ result, onNext, isLastQuestion }) => {
  return (
    <div className={`p-6 rounded-2xl ${
      result.correct 
        ? 'bg-gradient-to-br from-emerald-500 to-teal-600' 
        : 'bg-gradient-to-br from-red-500 to-rose-600'
    } text-white shadow-xl`}>
      {/* Icono y mensaje */}
      <div className="text-center mb-6">
        <span className="text-6xl">
          {result.correct ? '🎉' : '😔'}
        </span>
        <h3 className="text-2xl font-bold mt-4">
          {result.correct ? '¡Correcto!' : 'Incorrecto'}
        </h3>
        
        {!result.correct && (
          <p className="mt-2 text-white/90">
            La respuesta correcta era: <strong>{result.correctAnswer}</strong>
          </p>
        )}
      </div>

      {/* Puntos ganados */}
      <div className="bg-white/20 rounded-xl p-4 mb-6">
        <div className="grid grid-cols-3 gap-4 text-center">
          <div>
            <p className="text-white/70 text-sm">Puntos</p>
            <p className="text-2xl font-bold">+{result.pointsEarned}</p>
          </div>
          <div>
            <p className="text-white/70 text-sm">Puntuación</p>
            <p className="text-2xl font-bold">{result.currentGameScore}</p>
          </div>
          <div>
            <p className="text-white/70 text-sm">Racha</p>
            <p className="text-2xl font-bold">🔥 {result.currentStreak}</p>
          </div>
        </div>
      </div>

      {/* Explicación */}
      {result.explanation && (
        <div className="bg-white/10 rounded-xl p-4 mb-6">
          <p className="text-sm">
            <span className="font-semibold">💡 Dato curioso:</span> {result.explanation}
          </p>
        </div>
      )}

      {/* Botón continuar */}
      <button
        onClick={onNext}
        className="w-full py-4 bg-white text-slate-800 rounded-xl font-bold text-lg hover:bg-white/90 transition-colors"
      >
        {isLastQuestion ? '🏆 Ver resultados' : '➡️ Siguiente pregunta'}
      </button>
    </div>
  )
}

TriviaResult.propTypes = {
  result: PropTypes.shape({
    correct: PropTypes.bool.isRequired,
    correctAnswer: PropTypes.string.isRequired,
    pointsEarned: PropTypes.number.isRequired,
    explanation: PropTypes.string,
    currentGameScore: PropTypes.number.isRequired,
    correctAnswersCount: PropTypes.number.isRequired,
    currentStreak: PropTypes.number.isRequired,
    hasNextQuestion: PropTypes.bool.isRequired
  }).isRequired,
  onNext: PropTypes.func.isRequired,
  isLastQuestion: PropTypes.bool
}

export default TriviaResult

