import { useState, useEffect, useRef } from 'react'
import PropTypes from 'prop-types'
import countryService from '../../services/countryService'

/**
 * Componente selector de país con diseño compacto y elegante
 * Selector en cascada: Continente → País
 */
const CountrySelector = ({ onSelect, selectedCountry }) => {
  const [countries, setCountries] = useState([])
  const [searchQuery, setSearchQuery] = useState('')
  const [filteredCountries, setFilteredCountries] = useState([])
  const [selectedContinent, setSelectedContinent] = useState('')
  const [continents, setContinents] = useState([])
  const [isOpen, setIsOpen] = useState(false)
  const [loading, setLoading] = useState(true)
  const [countriesByContinent, setCountriesByContinent] = useState({})
  const dropdownRef = useRef(null)

  useEffect(() => {
    loadCountries()
    loadContinents()
  }, [])

  useEffect(() => {
    filterCountries()
  }, [searchQuery, selectedContinent, countries])

  // Cerrar dropdown al hacer clic fuera
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  const loadCountries = async () => {
    try {
      const data = await countryService.getAllCountries()
      setCountries(data)
      setFilteredCountries(data)
      // Organizar por continente
      const grouped = data.reduce((acc, country) => {
        const continent = country.continent || 'Otros'
        if (!acc[continent]) acc[continent] = []
        acc[continent].push(country)
        return acc
      }, {})
      setCountriesByContinent(grouped)
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
    if (selectedContinent) {
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

  const handleContinentChange = (continent) => {
    setSelectedContinent(continent)
    setSearchQuery('')
  }

  const getContinentEmoji = (continent) => {
    const emojis = {
      'Africa': '🌍', 'África': '🌍',
      'Americas': '🌎', 'América': '🌎',
      'Asia': '🌏',
      'Europe': '🏰', 'Europa': '🏰',
      'Oceania': '🏝️', 'Oceanía': '🏝️',
      'Antarctica': '🧊', 'Antártida': '🧊'
    }
    return emojis[continent] || '🌐'
  }

  return (
    <div className="relative" ref={dropdownRef}>
      {/* Botón principal */}
      <button
        type="button"
        onClick={() => setIsOpen(!isOpen)}
        className="w-full flex items-center gap-2 px-3 py-2.5 bg-white border-2 border-slate-200 rounded-xl cursor-pointer hover:border-emerald-400 hover:shadow-md transition-all text-left"
      >
        {selectedCountry ? (
          <>
            <span className="text-xl flex-shrink-0">{selectedCountry.flagEmoji}</span>
            <div className="flex-1 min-w-0">
              <p className="font-medium text-slate-800 text-sm truncate">{selectedCountry.name}</p>
            </div>
          </>
        ) : (
          <>
            <span className="text-lg text-slate-400">🌍</span>
            <span className="text-slate-400 text-sm">Selecciona un país...</span>
          </>
        )}
        <span className="ml-auto text-slate-400 text-xs">{isOpen ? '▲' : '▼'}</span>
      </button>

      {/* Dropdown */}
      {isOpen && (
        <div 
          className="absolute top-full left-0 right-0 mt-1 bg-white rounded-xl shadow-2xl border-2 border-slate-200 z-50 overflow-hidden"
          style={{ maxHeight: '380px' }}
        >
          {/* Header: Continente */}
          <div className="bg-gradient-to-r from-emerald-600 to-teal-600 p-2.5">
            <div className="flex items-center gap-2 mb-1.5">
              <span className="text-white text-xs font-bold uppercase">🌍 Continente</span>
            </div>
            <select
              value={selectedContinent}
              onChange={(e) => handleContinentChange(e.target.value)}
              className="w-full px-2.5 py-2 rounded-lg bg-white/95 text-slate-800 text-sm font-medium cursor-pointer border-0 focus:outline-none focus:ring-2 focus:ring-white/50"
            >
              <option value="">🌐 Todos ({countries.length})</option>
              {continents.map(continent => {
                const count = countriesByContinent[continent]?.length || 0
                return (
                  <option key={continent} value={continent}>
                    {getContinentEmoji(continent)} {continent} ({count})
                  </option>
                )
              })}
            </select>
          </div>

          {/* Búsqueda */}
          <div className="p-2.5 bg-slate-50 border-b border-slate-100">
            <input
              type="text"
              placeholder="🔍 Buscar país o capital..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full px-3 py-2 border border-slate-200 rounded-lg bg-white text-sm text-slate-800 placeholder-slate-400 focus:outline-none focus:border-emerald-400 focus:ring-1 focus:ring-emerald-400"
              autoFocus
            />
          </div>

          {/* Lista de países */}
          <div className="overflow-y-auto" style={{ maxHeight: '220px' }}>
            {loading && (
              <div className="p-4 text-center">
                <div className="animate-spin rounded-full h-6 w-6 border-2 border-emerald-500 border-t-transparent mx-auto mb-2" />
                <p className="text-slate-400 text-xs">Cargando...</p>
              </div>
            )}
            
            {!loading && filteredCountries.length === 0 && (
              <div className="p-4 text-center">
                <span className="text-2xl block mb-1">🔍</span>
                <p className="text-slate-400 text-xs">No encontrado</p>
                {selectedContinent && (
                  <button
                    type="button"
                    onClick={() => setSelectedContinent('')}
                    className="mt-1 text-emerald-500 text-xs font-medium hover:underline"
                  >
                    Ver todos
                  </button>
                )}
              </div>
            )}

            {!loading && filteredCountries.length > 0 && (
              <div>
                {filteredCountries.map(country => (
                  <button
                    type="button"
                    key={country.id}
                    onClick={() => handleSelect(country)}
                    className={`w-full flex items-center gap-2 px-3 py-2 text-left transition-all cursor-pointer border-b border-slate-50 last:border-0 ${
                      selectedCountry?.id === country.id
                        ? 'bg-emerald-50'
                        : 'bg-white hover:bg-slate-50'
                    }`}
                  >
                    <span className="text-lg flex-shrink-0">{country.flagEmoji}</span>
                    <div className="flex-1 min-w-0">
                      <p className="font-medium text-slate-800 text-xs truncate">{country.name}</p>
                      <p className="text-[10px] text-slate-400 truncate">{country.capital}</p>
                    </div>
                    {selectedCountry?.id === country.id && (
                      <span className="text-emerald-500 text-sm flex-shrink-0">✓</span>
                    )}
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Footer */}
          <div className="p-2 bg-slate-50 border-t border-slate-200 flex items-center gap-2">
            <span className="text-[10px] text-slate-400">{filteredCountries.length} países</span>
            <button
              type="button"
              onClick={() => setIsOpen(false)}
              disabled={!selectedCountry}
              className={`flex-1 py-2 px-3 rounded-lg text-xs font-bold transition-all ${
                selectedCountry
                  ? 'bg-emerald-500 text-white hover:bg-emerald-600 cursor-pointer'
                  : 'bg-slate-200 text-slate-400 cursor-not-allowed'
              }`}
            >
              {selectedCountry ? '✓ Confirmar' : 'Selecciona'}
            </button>
          </div>
        </div>
      )}
    </div>
  )
}

CountrySelector.propTypes = {
  onSelect: PropTypes.func.isRequired,
  selectedCountry: PropTypes.object
}

export default CountrySelector

