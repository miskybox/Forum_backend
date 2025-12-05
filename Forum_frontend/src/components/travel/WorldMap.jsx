import { useState, useEffect, useMemo } from 'react'
import PropTypes from 'prop-types'
import { geoMercator, geoPath } from 'd3-geo'

/**
 * Componente de mapa mundial interactivo con GeoJSON real
 * Muestra países coloreados según su estado de visita
 */
const WorldMap = ({ visitedPlaces = [], onCountryClick, selectedCountry }) => {
  const [geoData, setGeoData] = useState(null)
  const [hoveredCountry, setHoveredCountry] = useState(null)
  const [tooltip, setTooltip] = useState({ x: 0, y: 0 })

  // Dimensiones del mapa
  const width = 960
  const height = 500

  // Colores según estado
  const statusColors = {
    VISITED: '#10b981',    // Verde esmeralda
    WISHLIST: '#f59e0b',   // Ámbar
    LIVED: '#3b82f6',      // Azul
    LIVING: '#8b5cf6',     // Violeta
    default: '#374151'     // Gris oscuro
  }

  // Cargar GeoJSON
  useEffect(() => {
    const loadGeoData = async () => {
      try {
        // Intentar cargar desde archivo local optimizado (251KB en /public/)
        const response = await fetch('/countries.geojson')
        if (!response.ok) {
          throw new Error('Local file not found')
        }
        const data = await response.json()
        setGeoData(data)
      } catch (error) {
        console.error('Error cargando GeoJSON local:', error)
        // Fallback: cargar desde CDN optimizado (johan/world.geo.json - 251KB)
        try {
          const cdnResponse = await fetch('https://raw.githubusercontent.com/johan/world.geo.json/master/countries.geo.json')
          const data = await cdnResponse.json()
          setGeoData(data)
        } catch (cdnError) {
          console.error('Error cargando desde CDN:', cdnError)
        }
      }
    }
    loadGeoData()
  }, [])

  // Crear mapa de países visitados para búsqueda rápida
  const visitedMap = useMemo(() => {
    return visitedPlaces.reduce((acc, place) => {
      const code = place.country?.code || place.countryCode
      if (code) {
        acc[code] = place.status
      }
      return acc
    }, {})
  }, [visitedPlaces])

  // Configurar proyección
  const projection = useMemo(() => {
    return geoMercator()
      .scale(140)
      .translate([width / 2, height / 1.5])
  }, [])

  // Crear generador de paths
  const pathGenerator = useMemo(() => geoPath().projection(projection), [projection])

  const getCountryColor = (isoCode) => {
    const status = visitedMap[isoCode]
    if (status) {
      return statusColors[status]
    }
    return statusColors.default
  }

  const getCountryOpacity = (isoCode) => {
    if (selectedCountry === isoCode) return 1
    if (hoveredCountry === isoCode) return 0.9
    if (visitedMap[isoCode]) return 0.85
    return 0.6
  }

  const handleMouseMove = (e, country) => {
    const rect = e.currentTarget.getBoundingClientRect()
    setTooltip({
      x: e.clientX - rect.left,
      y: e.clientY - rect.top
    })
    setHoveredCountry(country.properties.ISO_A2)
  }

  const handleMouseLeave = () => {
    setHoveredCountry(null)
  }

  const handleClick = (isoCode) => {
    if (onCountryClick && isoCode !== '-99') {
      onCountryClick(isoCode)
    }
  }

  if (!geoData) {
    return (
      <div className="relative w-full bg-gradient-to-br from-slate-900 to-slate-800 rounded-2xl p-6 shadow-2xl">
        <div className="aspect-[2/1] w-full flex items-center justify-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-500"></div>
          <span className="ml-4 text-white">Cargando mapa...</span>
        </div>
      </div>
    )
  }

  return (
    <div className="relative w-full bg-gradient-to-br from-slate-900 via-slate-800 to-indigo-900 rounded-2xl p-4 md:p-6 shadow-2xl overflow-hidden">
      {/* Estrellas decorativas */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        {[...Array(50)].map((_, i) => (
          <div
            key={i}
            className="absolute w-1 h-1 bg-white rounded-full opacity-30"
            style={{
              left: `${Math.random() * 100}%`,
              top: `${Math.random() * 100}%`,
              animationDelay: `${Math.random() * 3}s`
            }}
          />
        ))}
      </div>

      {/* Leyenda */}
      <div className="absolute top-4 right-4 bg-slate-800/95 backdrop-blur-md rounded-xl p-4 z-20 border border-slate-700/50">
        <h4 className="text-white font-semibold mb-3 text-sm flex items-center gap-2">
          <span className="text-lg">🗺️</span> Leyenda
        </h4>
        <div className="space-y-2">
          {[
            { status: 'VISITED', label: 'Visitado', color: statusColors.VISITED, emoji: '✅' },
            { status: 'WISHLIST', label: 'Quiero ir', color: statusColors.WISHLIST, emoji: '⭐' },
            { status: 'LIVED', label: 'He vivido', color: statusColors.LIVED, emoji: '🏠' },
            { status: 'LIVING', label: 'Vivo aquí', color: statusColors.LIVING, emoji: '📍' },
          ].map(item => (
            <div key={item.status} className="flex items-center gap-2">
              <div 
                className="w-4 h-4 rounded-sm shadow-sm" 
                style={{ backgroundColor: item.color }}
              />
              <span className="text-white/80 text-xs">{item.emoji} {item.label}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Tooltip */}
      {hoveredCountry && (
        <div 
          className="absolute bg-white rounded-lg px-4 py-2 shadow-xl z-30 pointer-events-none transform -translate-x-1/2 -translate-y-full"
          style={{ 
            left: tooltip.x, 
            top: tooltip.y - 10,
            minWidth: '150px'
          }}
        >
          <div className="flex items-center gap-2">
            <span className="font-semibold text-slate-800">
              {geoData.features.find(f => f.properties.ISO_A2 === hoveredCountry)?.properties.ADMIN || hoveredCountry}
            </span>
          </div>
          {visitedMap[hoveredCountry] && (
            <span 
              className="inline-block mt-1 text-xs px-2 py-1 rounded-full text-white font-medium"
              style={{ backgroundColor: statusColors[visitedMap[hoveredCountry]] }}
            >
              {visitedMap[hoveredCountry]}
            </span>
          )}
        </div>
      )}

      {/* Estadísticas rápidas */}
      <div className="absolute top-4 left-4 bg-slate-800/95 backdrop-blur-md rounded-xl p-4 z-20 border border-slate-700/50">
        <div className="text-white text-sm">
          <div className="flex items-center gap-2 mb-1">
            <span className="text-2xl">🌍</span>
            <span className="font-bold text-lg">{Object.keys(visitedMap).length}</span>
          </div>
          <span className="text-white/60 text-xs">países marcados</span>
        </div>
      </div>

      {/* Mapa SVG */}
      <div className="relative aspect-[2/1] w-full">
        <svg 
          viewBox={`0 0 ${width} ${height}`}
          className="w-full h-full"
          style={{ filter: 'drop-shadow(0 4px 6px rgba(0,0,0,0.3))' }}
        >
          {/* Gradiente para océano */}
          <defs>
            <linearGradient id="oceanGradient" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#0c4a6e" />
              <stop offset="50%" stopColor="#164e63" />
              <stop offset="100%" stopColor="#155e75" />
            </linearGradient>
            <filter id="glow">
              <feGaussianBlur stdDeviation="2" result="coloredBlur"/>
              <feMerge>
                <feMergeNode in="coloredBlur"/>
                <feMergeNode in="SourceGraphic"/>
              </feMerge>
            </filter>
          </defs>

          {/* Océano */}
          <rect x="0" y="0" width={width} height={height} fill="url(#oceanGradient)" />
          
          {/* Líneas de grid */}
          <g className="opacity-10">
            {[...Array(18)].map((_, i) => (
              <line
                key={`v-${i}`}
                x1={i * (width / 18)}
                y1={0}
                x2={i * (width / 18)}
                y2={height}
                stroke="white"
                strokeWidth="0.5"
              />
            ))}
            {[...Array(9)].map((_, i) => (
              <line
                key={`h-${i}`}
                x1={0}
                y1={i * (height / 9)}
                x2={width}
                y2={i * (height / 9)}
                stroke="white"
                strokeWidth="0.5"
              />
            ))}
          </g>

          {/* Países */}
          <g>
            {geoData.features.map((country, index) => {
              const isoCode = country.properties.ISO_A2
              const path = pathGenerator(country)
              if (!path) return null

              return (
                <path
                  key={`${isoCode}-${index}`}
                  d={path}
                  fill={getCountryColor(isoCode)}
                  fillOpacity={getCountryOpacity(isoCode)}
                  stroke={selectedCountry === isoCode ? '#fbbf24' : '#1f2937'}
                  strokeWidth={selectedCountry === isoCode ? 2 : 0.5}
                  className="cursor-pointer transition-all duration-200"
                  style={{
                    filter: hoveredCountry === isoCode ? 'url(#glow)' : 'none'
                  }}
                  onMouseMove={(e) => handleMouseMove(e, country)}
                  onMouseLeave={handleMouseLeave}
                  onClick={() => handleClick(isoCode)}
                />
              )
            })}
          </g>
        </svg>

        {/* Mensaje para interactuar */}
        <div className="absolute bottom-4 left-1/2 -translate-x-1/2 bg-slate-800/80 backdrop-blur-sm rounded-full px-4 py-2">
          <span className="text-white/80 text-sm">
            🖱️ Haz clic en un país para agregar o editar
          </span>
        </div>
      </div>
    </div>
  )
}

WorldMap.propTypes = {
  visitedPlaces: PropTypes.array,
  onCountryClick: PropTypes.func,
  selectedCountry: PropTypes.string
}

export default WorldMap
