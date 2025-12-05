import { useState, useEffect } from 'react'
import PropTypes from 'prop-types'
import countryService from '../../services/countryService'

/**
 * Componente selector de país con búsqueda
 */
const CountrySelector = ({ onSelect, selectedCountry }) => {
  const [countries, setCountries] = useState([])
  const [searchQuery, setSearchQuery] = useState('')
  const [filteredCountries, setFilteredCountries] = useState([])
  const [selectedContinent, setSelectedContinent] = useState('all')
  const [continents, setContinents] = useState([])
  const [isOpen, setIsOpen] = useState(false)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadCountries()
    loadContinents()
  }, [])

  useEffect(() => {
    filterCountries()
  }, [searchQuery, selectedContinent, countries])

  const loadCountries = async () => {
    try {
      const data = await countryService.getAllCountries()
      setCountries(data)
      setFilteredCountries(data)
    } catch (error) {
      console.error('Error cargando países:', error)
    } finally {
      setLoading(false)
    }
  }

  const loadContinents = async () => {
    try {
      const data = await countryService.getAllContinents()
      setContinents(data)
    } catch (error) {
      console.error('Error cargando continentes:', error)
    }
  }

  const filterCountries = () => {
    let filtered = [...countries]

    if (selectedContinent !== 'all') {
      filtered = filtered.filter(c => c.continent === selectedContinent)
    }

    if (searchQuery) {
      const query = searchQuery.toLowerCase()
      filtered = filtered.filter(c => 
        c.name.toLowerCase().includes(query) ||
        c.capital?.toLowerCase().includes(query)
      )
    }

    setFilteredCountries(filtered)
  }

  const handleSelect = (country) => {
    onSelect(country)
    setIsOpen(false)
    setSearchQuery('')
  }

  return (
    <div className="relative">
      {/* Input de búsqueda */}
      <div 
        className="flex items-center gap-2 bg-white border-2 border-slate-200 rounded-xl px-4 py-3 cursor-pointer hover:border-emerald-400 transition-colors"
        onClick={() => setIsOpen(!isOpen)}
      >
        {selectedCountry ? (
          <>
            <span className="text-2xl">{selectedCountry.flagEmoji}</span>
            <span className="font-medium text-slate-800">{selectedCountry.name}</span>
            <span className="text-slate-400 text-sm">({selectedCountry.capital})</span>
          </>
        ) : (
          <>
            <span className="text-slate-400">🔍</span>
            <span className="text-slate-400">Selecciona un país...</span>
          </>
        )}
        <span className="ml-auto text-slate-400">▼</span>
      </div>

      {/* Dropdown */}
      {isOpen && (
        <div className="absolute top-full left-0 right-0 mt-2 bg-white rounded-xl shadow-2xl border border-slate-200 z-50 max-h-96 overflow-hidden">
          {/* Barra de búsqueda */}
          <div className="p-3 border-b border-slate-100">
            <input
              type="text"
              placeholder="Buscar país o capital..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full px-4 py-2 bg-slate-50 rounded-lg border-0 focus:ring-2 focus:ring-emerald-400"
              autoFocus
            />
          </div>

          {/* Filtro por continente */}
          <div className="p-3 border-b border-slate-100 flex flex-wrap gap-2">
            <button
              onClick={() => setSelectedContinent('all')}
              className={`px-3 py-1 rounded-full text-sm transition-colors ${
                selectedContinent === 'all'
                  ? 'bg-emerald-500 text-white'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              }`}
            >
              Todos
            </button>
            {continents.map(continent => (
              <button
                key={continent}
                onClick={() => setSelectedContinent(continent)}
                className={`px-3 py-1 rounded-full text-sm transition-colors ${
                  selectedContinent === continent
                    ? 'bg-emerald-500 text-white'
                    : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                }`}
              >
                {continent}
              </button>
            ))}
          </div>

          {/* Lista de países */}
          <div className="max-h-64 overflow-y-auto">
            {loading ? (
              <div className="p-8 text-center text-slate-400">
                <div className="animate-spin rounded-full h-8 w-8 border-2 border-emerald-500 border-t-transparent mx-auto mb-2" />
                Cargando países...
              </div>
            ) : filteredCountries.length === 0 ? (
              <div className="p-8 text-center text-slate-400">
                No se encontraron países
              </div>
            ) : (
              filteredCountries.map(country => (
                <div
                  key={country.id}
                  onClick={() => handleSelect(country)}
                  className="flex items-center gap-3 px-4 py-3 hover:bg-emerald-50 cursor-pointer transition-colors border-b border-slate-50 last:border-0"
                >
                  <span className="text-2xl">{country.flagEmoji}</span>
                  <div>
                    <p className="font-medium text-slate-800">{country.name}</p>
                    <p className="text-sm text-slate-400">{country.capital} • {country.continent}</p>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      )}

      {/* Overlay para cerrar */}
      {isOpen && (
        <div 
          className="fixed inset-0 z-40" 
          onClick={() => setIsOpen(false)}
        />
      )}
    </div>
  )
}

CountrySelector.propTypes = {
  onSelect: PropTypes.func.isRequired,
  selectedCountry: PropTypes.object
}

export default CountrySelector

