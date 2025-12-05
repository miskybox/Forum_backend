import { useState, useEffect } from 'react'
import { useAuth } from '../../hooks/useAuth'
import WorldMap from '../../components/travel/WorldMap'
import TravelStats from '../../components/travel/TravelStats'
import PlacesList from '../../components/travel/PlacesList'
import AddPlaceModal from '../../components/travel/AddPlaceModal'
import travelService from '../../services/travelService'
import { Link } from 'react-router-dom'

/**
 * Página principal del Mapa de Viajes
 */
const TravelMapPage = () => {
  const { user, isAuthenticated } = useAuth()
  const [places, setPlaces] = useState([])
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [editingPlace, setEditingPlace] = useState(null)

  useEffect(() => {
    if (isAuthenticated) {
      loadData()
    } else {
      setLoading(false)
    }
  }, [isAuthenticated])

  const loadData = async () => {
    setLoading(true)
    try {
      const [placesData, statsData] = await Promise.all([
        travelService.getMyPlaces(),
        travelService.getMyStats()
      ])
      setPlaces(placesData)
      setStats(statsData)
    } catch (error) {
      console.error('Error cargando datos:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleCountryClick = (isoCode) => {
    if (!isAuthenticated) return
    // Verificar si ya está en la lista
    const existingPlace = places.find(p => p.country.isoCode === isoCode)
    if (existingPlace) {
      setEditingPlace(existingPlace)
    }
    setIsModalOpen(true)
  }

  const handleEditPlace = (place) => {
    setEditingPlace(place)
    setIsModalOpen(true)
  }

  const handleModalClose = () => {
    setIsModalOpen(false)
    setEditingPlace(null)
  }

  const handleSuccess = () => {
    loadData()
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-4 border-emerald-500 border-t-transparent mx-auto" />
          <p className="text-slate-500 mt-4">Cargando tu mapa...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
      {/* Header */}
      <div className="bg-gradient-to-r from-emerald-600 to-teal-600 py-12">
        <div className="container mx-auto px-4">
          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
            <div>
              <h1 className="text-3xl md:text-4xl font-bold text-white flex items-center gap-3">
                🗺️ Mi Mapa de Viajes
              </h1>
              <p className="text-emerald-100 mt-2">
                Registra tus aventuras y descubre cuánto del mundo has explorado
              </p>
            </div>
            
            {isAuthenticated ? (
              <button
                onClick={() => setIsModalOpen(true)}
                className="px-6 py-3 bg-white text-emerald-600 rounded-xl font-bold hover:bg-emerald-50 transition-colors flex items-center gap-2 self-start"
              >
                <span className="text-xl">➕</span>
                Agregar lugar
              </button>
            ) : (
              <Link
                to="/login"
                className="px-6 py-3 bg-white text-emerald-600 rounded-xl font-bold hover:bg-emerald-50 transition-colors"
              >
                Iniciar sesión para guardar
              </Link>
            )}
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        {isAuthenticated ? (
          <div className="grid lg:grid-cols-3 gap-8">
            {/* Columna principal - Mapa */}
            <div className="lg:col-span-2 space-y-8">
              {/* Mapa */}
              <WorldMap 
                visitedPlaces={places}
                onCountryClick={handleCountryClick}
              />

              {/* Lista de lugares */}
              <PlacesList 
                places={places}
                onEdit={handleEditPlace}
                onRefresh={loadData}
              />
            </div>

            {/* Columna lateral - Stats */}
            <div className="space-y-6">
              <TravelStats stats={stats} />
              
              {/* Acciones rápidas */}
              <div className="bg-white rounded-2xl p-6 shadow-lg">
                <h3 className="font-bold text-slate-800 mb-4">Acciones rápidas</h3>
                <div className="space-y-3">
                  <QuickAction 
                    icon="🌍" 
                    label="Ver ranking de viajeros"
                    href="/travel/ranking"
                  />
                  <QuickAction 
                    icon="📊" 
                    label="Estadísticas detalladas"
                    href="/profile"
                  />
                  <QuickAction 
                    icon="🎮" 
                    label="Jugar Trivia Geográfica"
                    href="/trivia"
                  />
                </div>
              </div>
            </div>
          </div>
        ) : (
          /* Vista para usuarios no autenticados */
          <div className="max-w-4xl mx-auto">
            <WorldMap visitedPlaces={[]} />
            
            <div className="mt-8 bg-white rounded-2xl p-8 text-center shadow-lg">
              <span className="text-6xl mb-4 block">🌎</span>
              <h2 className="text-2xl font-bold text-slate-800 mb-4">
                Empieza tu aventura viajera
              </h2>
              <p className="text-slate-600 mb-6 max-w-lg mx-auto">
                Crea una cuenta para guardar los países que has visitado, 
                ver tu progreso mundial y competir con otros viajeros.
              </p>
              <div className="flex justify-center gap-4">
                <Link
                  to="/register"
                  className="px-6 py-3 bg-emerald-500 text-white rounded-xl font-bold hover:bg-emerald-600 transition-colors"
                >
                  Crear cuenta gratis
                </Link>
                <Link
                  to="/login"
                  className="px-6 py-3 border-2 border-slate-200 rounded-xl font-semibold text-slate-600 hover:bg-slate-50 transition-colors"
                >
                  Ya tengo cuenta
                </Link>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Modal */}
      <AddPlaceModal 
        isOpen={isModalOpen}
        onClose={handleModalClose}
        onSuccess={handleSuccess}
        editPlace={editingPlace}
      />
    </div>
  )
}

const QuickAction = ({ icon, label, href }) => (
  <Link
    to={href}
    className="flex items-center gap-3 p-3 rounded-xl hover:bg-slate-50 transition-colors group"
  >
    <span className="text-2xl">{icon}</span>
    <span className="text-slate-600 group-hover:text-slate-800">{label}</span>
    <span className="ml-auto text-slate-300 group-hover:text-slate-400">→</span>
  </Link>
)

export default TravelMapPage

