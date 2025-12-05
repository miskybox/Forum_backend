/**
 * Servicio para conectar con la API gratuita RestCountries
 * https://restcountries.com/
 * 
 * Esta API proporciona información detallada sobre todos los países del mundo
 */

const API_BASE_URL = 'https://restcountries.com/v3.1'

/**
 * Obtener todos los países con información básica
 */
export const getAllCountries = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/all?fields=name,capital,flags,currencies,languages,population,area,region,subregion,cca2,cca3,borders,timezones,latlng`)
    if (!response.ok) throw new Error('Error fetching countries')
    return await response.json()
  } catch (error) {
    console.error('RestCountries API error:', error)
    return []
  }
}

/**
 * Obtener país por código ISO
 */
export const getCountryByCode = async (code) => {
  try {
    const response = await fetch(`${API_BASE_URL}/alpha/${code}`)
    if (!response.ok) throw new Error('Country not found')
    const data = await response.json()
    return data[0]
  } catch (error) {
    console.error('RestCountries API error:', error)
    return null
  }
}

/**
 * Obtener países por región
 */
export const getCountriesByRegion = async (region) => {
  try {
    const response = await fetch(`${API_BASE_URL}/region/${region}`)
    if (!response.ok) throw new Error('Region not found')
    return await response.json()
  } catch (error) {
    console.error('RestCountries API error:', error)
    return []
  }
}

/**
 * Buscar países por nombre
 */
export const searchCountries = async (name) => {
  try {
    const response = await fetch(`${API_BASE_URL}/name/${name}`)
    if (!response.ok) throw new Error('No countries found')
    return await response.json()
  } catch (error) {
    console.error('RestCountries API error:', error)
    return []
  }
}

/**
 * Generar preguntas de trivia dinámicamente usando la API
 */
export const generateTriviaQuestions = async (count = 10, usedQuestionIds = []) => {
  try {
    const countries = await getAllCountries()
    if (countries.length === 0) return []

    const questions = []
    const questionTypes = ['capital', 'flag', 'currency', 'language', 'population', 'region', 'area']
    const usedCombinations = new Set(usedQuestionIds)

    // Filtrar países con datos completos
    const validCountries = countries.filter(c => 
      c.capital?.length > 0 && 
      c.currencies && 
      c.languages &&
      c.name?.common
    )

    let attempts = 0
    const maxAttempts = count * 10

    while (questions.length < count && attempts < maxAttempts) {
      attempts++
      
      const randomCountry = validCountries[Math.floor(Math.random() * validCountries.length)]
      const randomType = questionTypes[Math.floor(Math.random() * questionTypes.length)]
      const questionId = `${randomCountry.cca2}-${randomType}`

      // Evitar repetir preguntas
      if (usedCombinations.has(questionId)) continue
      usedCombinations.add(questionId)

      const question = createQuestion(randomCountry, randomType, validCountries)
      if (question) {
        questions.push({ ...question, id: questionId })
      }
    }

    return questions
  } catch (error) {
    console.error('Error generating trivia questions:', error)
    return []
  }
}

/**
 * Crear una pregunta de trivia
 */
const createQuestion = (country, type, allCountries) => {
  const countryName = country.name.common

  switch (type) {
    case 'capital':
      if (!country.capital?.[0]) return null
      return {
        type: 'CAPITAL',
        question: `¿Cuál es la capital de ${countryName}?`,
        correctAnswer: country.capital[0],
        options: shuffleArray([
          country.capital[0],
          ...getRandomCapitals(allCountries, country.cca2, 3)
        ]),
        countryCode: country.cca2,
        difficulty: 1
      }

    case 'flag':
      return {
        type: 'FLAG',
        question: `¿A qué país pertenece esta bandera?`,
        flagUrl: country.flags.svg || country.flags.png,
        correctAnswer: countryName,
        options: shuffleArray([
          countryName,
          ...getRandomCountryNames(allCountries, country.cca2, 3)
        ]),
        countryCode: country.cca2,
        difficulty: 2
      }

    case 'currency':
      const currencies = Object.values(country.currencies || {})
      if (currencies.length === 0) return null
      const currency = currencies[0]
      return {
        type: 'CURRENCY',
        question: `¿Cuál es la moneda oficial de ${countryName}?`,
        correctAnswer: currency.name,
        options: shuffleArray([
          currency.name,
          ...getRandomCurrencies(allCountries, country.cca2, 3)
        ]),
        countryCode: country.cca2,
        difficulty: 2
      }

    case 'language':
      const languages = Object.values(country.languages || {})
      if (languages.length === 0) return null
      return {
        type: 'LANGUAGE',
        question: `¿Cuál es uno de los idiomas oficiales de ${countryName}?`,
        correctAnswer: languages[0],
        options: shuffleArray([
          languages[0],
          ...getRandomLanguages(allCountries, country.cca2, 3)
        ]),
        countryCode: country.cca2,
        difficulty: 2
      }

    case 'population':
      const population = country.population
      const populationRanges = getPopulationRanges(population)
      return {
        type: 'POPULATION',
        question: `¿Cuál es aproximadamente la población de ${countryName}?`,
        correctAnswer: populationRanges.correct,
        options: shuffleArray(populationRanges.all),
        countryCode: country.cca2,
        difficulty: 3
      }

    case 'region':
      return {
        type: 'CONTINENT',
        question: `¿En qué continente se encuentra ${countryName}?`,
        correctAnswer: country.region,
        options: shuffleArray([
          country.region,
          ...getOtherRegions(country.region)
        ]).slice(0, 4),
        countryCode: country.cca2,
        difficulty: 1
      }

    case 'area':
      const area = country.area
      const areaRanges = getAreaRanges(area)
      return {
        type: 'AREA',
        question: `¿Cuál es aproximadamente el área de ${countryName}?`,
        correctAnswer: areaRanges.correct,
        options: shuffleArray(areaRanges.all),
        countryCode: country.cca2,
        difficulty: 3
      }

    default:
      return null
  }
}

// Funciones auxiliares
const shuffleArray = (array) => {
  const shuffled = [...array]
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]]
  }
  return shuffled
}

const getRandomCapitals = (countries, excludeCode, count) => {
  const capitals = countries
    .filter(c => c.cca2 !== excludeCode && c.capital?.[0])
    .map(c => c.capital[0])
  return shuffleArray(capitals).slice(0, count)
}

const getRandomCountryNames = (countries, excludeCode, count) => {
  const names = countries
    .filter(c => c.cca2 !== excludeCode)
    .map(c => c.name.common)
  return shuffleArray(names).slice(0, count)
}

const getRandomCurrencies = (countries, excludeCode, count) => {
  const currencies = countries
    .filter(c => c.cca2 !== excludeCode && c.currencies)
    .flatMap(c => Object.values(c.currencies).map(cur => cur.name))
  return [...new Set(shuffleArray(currencies))].slice(0, count)
}

const getRandomLanguages = (countries, excludeCode, count) => {
  const languages = countries
    .filter(c => c.cca2 !== excludeCode && c.languages)
    .flatMap(c => Object.values(c.languages))
  return [...new Set(shuffleArray(languages))].slice(0, count)
}

const getOtherRegions = (currentRegion) => {
  const allRegions = ['Africa', 'Americas', 'Asia', 'Europe', 'Oceania', 'Antarctic']
  return allRegions.filter(r => r !== currentRegion)
}

const getPopulationRanges = (population) => {
  const formatPopulation = (num) => {
    if (num >= 1000000000) return `${(num / 1000000000).toFixed(1)} mil millones`
    if (num >= 1000000) return `${(num / 1000000).toFixed(1)} millones`
    if (num >= 1000) return `${(num / 1000).toFixed(0)} mil`
    return num.toString()
  }

  const correct = formatPopulation(population)
  const variations = [
    population * 0.1,
    population * 0.5,
    population * 2,
    population * 5
  ]

  return {
    correct,
    all: [correct, ...variations.map(formatPopulation)]
  }
}

const getAreaRanges = (area) => {
  const formatArea = (num) => {
    if (num >= 1000000) return `${(num / 1000000).toFixed(1)} millones km²`
    if (num >= 1000) return `${(num / 1000).toFixed(0)} mil km²`
    return `${num.toFixed(0)} km²`
  }

  const correct = formatArea(area)
  const variations = [
    area * 0.2,
    area * 0.5,
    area * 2,
    area * 4
  ]

  return {
    correct,
    all: [correct, ...variations.map(formatArea)]
  }
}

export default {
  getAllCountries,
  getCountryByCode,
  getCountriesByRegion,
  searchCountries,
  generateTriviaQuestions
}

