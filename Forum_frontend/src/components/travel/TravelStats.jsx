import PropTypes from 'prop-types'

/**
 * Componente que muestra las estadísticas de viaje del usuario
 */
const TravelStats = ({ stats }) => {
  if (!stats) return null

  const progressPercentage = stats.worldPercentageByCountries || 0

  return (
    <div className="bg-gradient-to-br from-emerald-600 to-teal-700 rounded-2xl p-6 text-white shadow-xl">
      {/* Header con nivel */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h3 className="text-2xl font-bold">{stats.username}</h3>
          <p className="text-emerald-100">{stats.travelerLevel}</p>
        </div>
        {stats.globalRanking && (
          <div className="text-right">
            <span className="text-4xl font-bold">#{stats.globalRanking}</span>
            <p className="text-emerald-100 text-sm">Ranking Global</p>
          </div>
        )}
      </div>

      {/* Barra de progreso del mundo */}
      <div className="mb-6">
        <div className="flex justify-between text-sm mb-2">
          <span>Progreso mundial</span>
          <span className="font-semibold">{progressPercentage.toFixed(1)}%</span>
        </div>
        <div className="h-4 bg-emerald-900/50 rounded-full overflow-hidden">
          <div 
            className="h-full bg-gradient-to-r from-yellow-400 to-orange-500 rounded-full transition-all duration-1000"
            style={{ width: `${Math.min(progressPercentage * 2, 100)}%` }}
          />
        </div>
        <p className="text-emerald-100 text-xs mt-1">
          {stats.totalAreaVisitedSqKm?.toLocaleString()} km² explorados
        </p>
      </div>

      {/* Grid de estadísticas */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        <StatCard 
          icon="🌍" 
          value={stats.countriesVisited || 0} 
          label="Países visitados"
          subLabel="/195"
        />
        <StatCard 
          icon="🏙️" 
          value={stats.citiesVisited || 0} 
          label="Ciudades"
        />
        <StatCard 
          icon="🌎" 
          value={stats.continentsVisited || 0} 
          label="Continentes"
          subLabel="/7"
        />
        <StatCard 
          icon="📍" 
          value={stats.countriesWishlist || 0} 
          label="En wishlist"
        />
      </div>

      {/* Continentes visitados */}
      {stats.continentsList && stats.continentsList.length > 0 && (
        <div className="mb-6">
          <h4 className="text-sm font-semibold mb-2">Continentes explorados</h4>
          <div className="flex flex-wrap gap-2">
            {stats.continentsList.map(continent => (
              <span 
                key={continent}
                className="px-3 py-1 bg-white/20 rounded-full text-sm"
              >
                {getContinentEmoji(continent)} {continent}
              </span>
            ))}
          </div>
        </div>
      )}

      {/* Badges */}
      {stats.badges && stats.badges.length > 0 && (
        <div>
          <h4 className="text-sm font-semibold mb-2">Logros desbloqueados</h4>
          <div className="flex flex-wrap gap-2">
            {stats.badges.slice(0, 6).map((badge, idx) => (
              <span 
                key={idx}
                className="px-3 py-1 bg-yellow-500/30 border border-yellow-400/50 rounded-full text-sm"
                title={badge}
              >
                {badge}
              </span>
            ))}
            {stats.badges.length > 6 && (
              <span className="px-3 py-1 bg-white/10 rounded-full text-sm">
                +{stats.badges.length - 6} más
              </span>
            )}
          </div>
        </div>
      )}

      {/* Lugar favorito */}
      {stats.favoritePlace && (
        <div className="mt-4 pt-4 border-t border-white/20">
          <span className="text-emerald-100 text-sm">Lugar favorito: </span>
          <span className="font-semibold">❤️ {stats.favoritePlace}</span>
        </div>
      )}
    </div>
  )
}

/**
 * Tarjeta individual de estadística
 */
const StatCard = ({ icon, value, label, subLabel }) => (
  <div className="bg-white/10 rounded-xl p-4 text-center backdrop-blur-sm">
    <span className="text-2xl">{icon}</span>
    <div className="text-2xl font-bold mt-1">
      {value}
      {subLabel && <span className="text-sm font-normal text-emerald-200">{subLabel}</span>}
    </div>
    <p className="text-emerald-100 text-xs">{label}</p>
  </div>
)

StatCard.propTypes = {
  icon: PropTypes.string.isRequired,
  value: PropTypes.number.isRequired,
  label: PropTypes.string.isRequired,
  subLabel: PropTypes.string
}

/**
 * Obtiene emoji según continente
 */
const getContinentEmoji = (continent) => {
  const emojis = {
    'Europa': '🇪🇺',
    'América': '🌎',
    'Asia': '🌏',
    'África': '🌍',
    'Oceanía': '🏝️',
    'Antártida': '🧊'
  }
  return emojis[continent] || '🌐'
}

TravelStats.propTypes = {
  stats: PropTypes.shape({
    username: PropTypes.string,
    travelerLevel: PropTypes.string,
    globalRanking: PropTypes.number,
    worldPercentageByCountries: PropTypes.number,
    totalAreaVisitedSqKm: PropTypes.number,
    countriesVisited: PropTypes.number,
    citiesVisited: PropTypes.number,
    continentsVisited: PropTypes.number,
    countriesWishlist: PropTypes.number,
    continentsList: PropTypes.arrayOf(PropTypes.string),
    badges: PropTypes.arrayOf(PropTypes.string),
    favoritePlace: PropTypes.string
  })
}

export default TravelStats

