import { useState, useEffect, useCallback } from 'react'
import PropTypes from 'prop-types'

/**
 * Componente que muestra una pregunta de trivia
 */
const TriviaQuestion = ({ question, onAnswer, timeLimit = 15 }) => {
  const [selectedAnswer, setSelectedAnswer] = useState(null)
  const [timeLeft, setTimeLeft] = useState(timeLimit)
  const [answered, setAnswered] = useState(false)
  const [startTime] = useState(Date.now())

  // Timer
  useEffect(() => {
    if (answered) return

    const timer = setInterval(() => {
      setTimeLeft(prev => {
        if (prev <= 1) {
          handleTimeout()
          return 0
        }
        return prev - 1
      })
    }, 1000)

    return () => clearInterval(timer)
  }, [answered])

  // Reset cuando cambia la pregunta
  useEffect(() => {
    setSelectedAnswer(null)
    setAnswered(false)
    setTimeLeft(question.timeLimitSeconds || timeLimit)
  }, [question.id])

  const handleTimeout = useCallback(() => {
    if (answered) return
    setAnswered(true)
    onAnswer({
      questionId: question.id,
      selectedAnswer: null,
      responseTimeMs: (question.timeLimitSeconds || timeLimit) * 1000,
      timedOut: true,
      hintUsed: false
    })
  }, [answered, question, onAnswer, timeLimit])

  const handleSelectAnswer = (answer) => {
    if (answered) return

    setSelectedAnswer(answer)
    setAnswered(true)

    const responseTime = Date.now() - startTime

    onAnswer({
      questionId: question.id,
      selectedAnswer: answer,
      responseTimeMs: responseTime,
      timedOut: false,
      hintUsed: false
    })
  }

  // Color del timer según tiempo restante
  const getTimerColor = () => {
    if (timeLeft > 10) return 'text-emerald-400'
    if (timeLeft > 5) return 'text-amber-400'
    return 'text-red-400 animate-pulse'
  }

  // Porcentaje para barra de progreso
  const timerPercent = (timeLeft / (question.timeLimitSeconds || timeLimit)) * 100

  return (
    <div className="bg-white rounded-2xl shadow-xl overflow-hidden">
      {/* Header con progreso y timer */}
      <div className="bg-gradient-to-r from-indigo-600 to-purple-600 px-6 py-4">
        <div className="flex items-center justify-between mb-3">
          <span className="text-white/80 text-sm">
            Pregunta {question.questionIndex} de {question.totalQuestions}
          </span>
          <div className={`flex items-center gap-2 font-mono text-2xl font-bold ${getTimerColor()}`}>
            <span>⏱️</span>
            <span>{timeLeft}s</span>
          </div>
        </div>
        
        {/* Barra de tiempo */}
        <div className="h-2 bg-white/20 rounded-full overflow-hidden">
          <div 
            className="h-full bg-white rounded-full transition-all duration-1000 ease-linear"
            style={{ width: `${timerPercent}%` }}
          />
        </div>

        {/* Puntos */}
        <div className="flex items-center justify-between mt-3 text-white/80 text-sm">
          <span>{question.countryFlag} {question.countryName}</span>
          <span>+{question.points} pts</span>
        </div>
      </div>

      {/* Pregunta */}
      <div className="p-6">
        {/* Imagen si existe */}
        {question.imageUrl && (
          <div className="mb-6 flex justify-center">
            <img 
              src={question.imageUrl} 
              alt="Pregunta"
              className="max-h-40 rounded-xl shadow-lg"
            />
          </div>
        )}

        {/* Texto de la pregunta */}
        <h2 className="text-xl md:text-2xl font-bold text-slate-800 text-center mb-8">
          {question.questionText}
        </h2>

        {/* Opciones */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {question.options.map((option, idx) => (
            <button
              key={idx}
              onClick={() => handleSelectAnswer(option)}
              disabled={answered}
              className={`p-4 rounded-xl border-2 text-left font-medium transition-all ${
                answered
                  ? selectedAnswer === option
                    ? 'border-indigo-500 bg-indigo-50 text-indigo-700'
                    : 'border-slate-200 bg-slate-50 text-slate-400'
                  : 'border-slate-200 hover:border-indigo-400 hover:bg-indigo-50 text-slate-700'
              }`}
            >
              <span className="inline-flex items-center justify-center w-8 h-8 rounded-full bg-slate-100 text-slate-600 mr-3 text-sm">
                {String.fromCharCode(65 + idx)}
              </span>
              {option}
            </button>
          ))}
        </div>

        {/* Indicador de dificultad */}
        <div className="mt-6 flex items-center justify-center gap-1">
          {[1, 2, 3, 4, 5].map(level => (
            <div 
              key={level}
              className={`w-2 h-2 rounded-full ${
                level <= question.difficulty 
                  ? 'bg-amber-400' 
                  : 'bg-slate-200'
              }`}
            />
          ))}
          <span className="text-slate-400 text-xs ml-2">
            Nivel {question.difficulty}
          </span>
        </div>
      </div>
    </div>
  )
}

TriviaQuestion.propTypes = {
  question: PropTypes.shape({
    id: PropTypes.number.isRequired,
    questionText: PropTypes.string.isRequired,
    options: PropTypes.arrayOf(PropTypes.string).isRequired,
    imageUrl: PropTypes.string,
    difficulty: PropTypes.number,
    points: PropTypes.number,
    timeLimitSeconds: PropTypes.number,
    questionIndex: PropTypes.number,
    totalQuestions: PropTypes.number,
    countryName: PropTypes.string,
    countryFlag: PropTypes.string
  }).isRequired,
  onAnswer: PropTypes.func.isRequired,
  timeLimit: PropTypes.number
}

export default TriviaQuestion

