import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import restCountriesService from '../../services/restCountriesService'
import toast from 'react-hot-toast'

/**
 * Modo Trivia Infinita - Preguntas ilimitadas usando API RestCountries
 * Las preguntas nunca se repiten y son generadas dinámicamente
 */
const TriviaInfinitePage = () => {
  const navigate = useNavigate()
  
  const [questions, setQuestions] = useState([])
  const [currentIndex, setCurrentIndex] = useState(0)
  const [score, setScore] = useState(0)
  const [streak, setStreak] = useState(0)
  const [bestStreak, setBestStreak] = useState(0)
  const [answered, setAnswered] = useState(false)
  const [selectedAnswer, setSelectedAnswer] = useState(null)
  const [loading, setLoading] = useState(true)
  const [usedQuestionIds, setUsedQuestionIds] = useState(new Set())
  const [totalAnswered, setTotalAnswered] = useState(0)
  const [correctAnswers, setCorrectAnswers] = useState(0)
  const [gameOver, setGameOver] = useState(false)
  const [lives, setLives] = useState(3)

  // Cargar preguntas iniciales
  useEffect(() => {
    loadQuestions()
  }, [])

  const loadQuestions = async () => {
    setLoading(true)
    try {
      const newQuestions = await restCountriesService.generateTriviaQuestions(
        10, 
        Array.from(usedQuestionIds)
      )
      
      if (newQuestions.length === 0) {
        toast.error('No hay más preguntas disponibles')
        setGameOver(true)
        return
      }

      // Marcar preguntas como usadas
      const newIds = new Set(usedQuestionIds)
      newQuestions.forEach(q => newIds.add(q.id))
      setUsedQuestionIds(newIds)
      
      setQuestions(prev => [...prev, ...newQuestions])
    } catch (error) {
      console.error('Error cargando preguntas:', error)
      toast.error('Error al cargar preguntas')
    } finally {
      setLoading(false)
    }
  }

  // Cargar más preguntas cuando quedan pocas
  useEffect(() => {
    if (questions.length - currentIndex <= 3 && !loading && !gameOver) {
      loadQuestions()
    }
  }, [currentIndex, questions.length, loading, gameOver])

  const currentQuestion = questions[currentIndex]

  const handleAnswer = (answer) => {
    if (answered || !currentQuestion) return
    
    setSelectedAnswer(answer)
    setAnswered(true)
    setTotalAnswered(prev => prev + 1)

    const isCorrect = answer === currentQuestion.correctAnswer

    if (isCorrect) {
      const streakBonus = Math.min(streak, 10) * 5
      const points = 100 + streakBonus
      setScore(prev => prev + points)
      setStreak(prev => prev + 1)
      setBestStreak(prev => Math.max(prev, streak + 1))
      setCorrectAnswers(prev => prev + 1)
      
      // Efecto visual de éxito
      toast.success(`+${points} puntos! 🔥 Racha: ${streak + 1}`, {
        icon: '✅',
        duration: 1500
      })
    } else {
      setStreak(0)
      setLives(prev => prev - 1)
      
      if (lives <= 1) {
        setGameOver(true)
        toast.error('¡Game Over! Sin vidas restantes', { duration: 3000 })
      } else {
        toast.error(`Incorrecto. Vidas: ${'❤️'.repeat(lives - 1)}`, {
          icon: '❌',
          duration: 1500
        })
      }
    }
  }

  const handleNext = () => {
    if (gameOver) return
    setCurrentIndex(prev => prev + 1)
    setAnswered(false)
    setSelectedAnswer(null)
  }

  const handleRestart = () => {
    setQuestions([])
    setCurrentIndex(0)
    setScore(0)
    setStreak(0)
    setBestStreak(0)
    setAnswered(false)
    setSelectedAnswer(null)
    setUsedQuestionIds(new Set())
    setTotalAnswered(0)
    setCorrectAnswers(0)
    setGameOver(false)
    setLives(3)
    loadQuestions()
  }

  const getOptionStyle = (option) => {
    if (!answered) {
      return 'bg-white/20 hover:bg-white/30 border-white/30'
    }
    if (option === currentQuestion?.correctAnswer) {
      return 'bg-emerald-500 border-emerald-400 text-white'
    }
    if (option === selectedAnswer && option !== currentQuestion?.correctAnswer) {
      return 'bg-red-500 border-red-400 text-white'
    }
    return 'bg-white/10 border-white/20 opacity-50'
  }

  if (loading && questions.length === 0) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-violet-900 via-purple-900 to-fuchsia-900 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-4 border-fuchsia-400 border-t-transparent mx-auto" />
          <p className="text-fuchsia-200 mt-4">Cargando preguntas desde API...</p>
          <p className="text-fuchsia-300/60 text-sm mt-2">Generando trivia con datos reales de países</p>
        </div>
      </div>
    )
  }

  if (gameOver) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-violet-900 via-purple-900 to-fuchsia-900 flex items-center justify-center p-4">
        <div className="bg-white/10 backdrop-blur-lg rounded-3xl p-8 max-w-md w-full text-center">
          <span className="text-6xl mb-4 block">🏁</span>
          <h2 className="text-3xl font-black text-white mb-2">¡Fin del Juego!</h2>
          <p className="text-fuchsia-200 mb-6">Modo Trivia Infinita</p>
          
          <div className="grid grid-cols-2 gap-4 mb-8">
            <div className="bg-white/10 rounded-xl p-4">
              <p className="text-fuchsia-300 text-sm">Puntuación</p>
              <p className="text-3xl font-bold text-white">{score}</p>
            </div>
            <div className="bg-white/10 rounded-xl p-4">
              <p className="text-fuchsia-300 text-sm">Mejor racha</p>
              <p className="text-3xl font-bold text-white">🔥 {bestStreak}</p>
            </div>
            <div className="bg-white/10 rounded-xl p-4">
              <p className="text-fuchsia-300 text-sm">Preguntas</p>
              <p className="text-3xl font-bold text-white">{totalAnswered}</p>
            </div>
            <div className="bg-white/10 rounded-xl p-4">
              <p className="text-fuchsia-300 text-sm">Precisión</p>
              <p className="text-3xl font-bold text-white">
                {totalAnswered > 0 ? Math.round((correctAnswers / totalAnswered) * 100) : 0}%
              </p>
            </div>
          </div>

          <div className="space-y-3">
            <button
              onClick={handleRestart}
              className="w-full py-4 bg-gradient-to-r from-fuchsia-500 to-pink-500 text-white rounded-xl font-bold hover:from-fuchsia-600 hover:to-pink-600 transition-all"
            >
              🔄 Jugar de nuevo
            </button>
            <button
              onClick={() => navigate('/trivia')}
              className="w-full py-4 bg-white/20 text-white rounded-xl font-bold hover:bg-white/30 transition-all"
            >
              ← Volver al menú
            </button>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-violet-900 via-purple-900 to-fuchsia-900">
      {/* Header */}
      <div className="bg-black/30 py-4 sticky top-0 z-10 backdrop-blur-sm">
        <div className="container mx-auto px-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-6">
              <div>
                <p className="text-fuchsia-300 text-xs">PUNTUACIÓN</p>
                <p className="text-2xl font-black text-white">{score}</p>
              </div>
              <div>
                <p className="text-fuchsia-300 text-xs">RACHA</p>
                <p className="text-2xl font-black text-amber-400">🔥 {streak}</p>
              </div>
            </div>
            
            <div className="flex items-center gap-4">
              <div className="text-2xl">
                {'❤️'.repeat(lives)}{'🖤'.repeat(3 - lives)}
              </div>
              <div className="text-white/60 text-sm">
                #{totalAnswered + 1}
              </div>
              <button
                onClick={() => navigate('/trivia')}
                className="px-4 py-2 bg-white/10 text-white rounded-lg hover:bg-white/20 text-sm"
              >
                Salir
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Pregunta */}
      <div className="container mx-auto px-4 py-8 max-w-2xl">
        {currentQuestion ? (
          <div className="bg-white/10 backdrop-blur-lg rounded-3xl p-6 md:p-8">
            {/* Tipo de pregunta */}
            <div className="flex items-center justify-between mb-6">
              <span className="px-3 py-1 bg-fuchsia-500/30 text-fuchsia-200 rounded-full text-sm">
                {getQuestionTypeLabel(currentQuestion.type)}
              </span>
              {currentQuestion.countryCode && (
                <span className="text-white/60 text-sm">
                  Código: {currentQuestion.countryCode}
                </span>
              )}
            </div>

            {/* Imagen de bandera si es pregunta de bandera */}
            {currentQuestion.type === 'FLAG' && currentQuestion.flagUrl && (
              <div className="mb-6 flex justify-center">
                <img 
                  src={currentQuestion.flagUrl} 
                  alt="Bandera"
                  className="h-32 md:h-40 object-contain rounded-lg shadow-lg border-4 border-white/20"
                />
              </div>
            )}

            {/* Pregunta */}
            <h2 className="text-xl md:text-2xl font-bold text-white text-center mb-8">
              {currentQuestion.question}
            </h2>

            {/* Opciones */}
            <div className="space-y-3">
              {currentQuestion.options.map((option, index) => (
                <button
                  key={index}
                  onClick={() => handleAnswer(option)}
                  disabled={answered}
                  className={`w-full p-4 rounded-xl border-2 text-left font-medium transition-all ${getOptionStyle(option)} ${!answered && 'hover:scale-[1.02]'}`}
                >
                  <span className="text-white">{option}</span>
                </button>
              ))}
            </div>

            {/* Botón siguiente */}
            {answered && (
              <button
                onClick={handleNext}
                className="w-full mt-6 py-4 bg-gradient-to-r from-fuchsia-500 to-pink-500 text-white rounded-xl font-bold hover:from-fuchsia-600 hover:to-pink-600 transition-all"
              >
                Siguiente pregunta →
              </button>
            )}
          </div>
        ) : (
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-4 border-fuchsia-400 border-t-transparent mx-auto" />
            <p className="text-fuchsia-200 mt-4">Cargando más preguntas...</p>
          </div>
        )}
      </div>

      {/* Footer con info */}
      <div className="fixed bottom-0 left-0 right-0 bg-black/30 py-2 text-center">
        <p className="text-fuchsia-300/60 text-xs">
          ♾️ Modo Infinito • Preguntas generadas con RestCountries API • {usedQuestionIds.size} preguntas únicas jugadas
        </p>
      </div>
    </div>
  )
}

const getQuestionTypeLabel = (type) => {
  const labels = {
    'CAPITAL': '🏛️ Capital',
    'FLAG': '🏴 Bandera',
    'CURRENCY': '💰 Moneda',
    'LANGUAGE': '🗣️ Idioma',
    'POPULATION': '👥 Población',
    'CONTINENT': '🌍 Continente',
    'AREA': '📐 Área'
  }
  return labels[type] || type
}

export default TriviaInfinitePage

