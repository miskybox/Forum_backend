import { useState } from 'react'
import PropTypes from 'prop-types'
import CountrySelector from './CountrySelector'
import travelService from '../../services/travelService'
import toast from 'react-hot-toast'

/**
 * Modal para agregar/editar un lugar visitado
 */
const AddPlaceModal = ({ isOpen, onClose, onSuccess, editPlace = null }) => {
  const [selectedCountry, setSelectedCountry] = useState(editPlace?.country || null)
  const [formData, setFormData] = useState({
    cityName: editPlace?.cityName || '',
    status: editPlace?.status || 'VISITED',
    visitDate: editPlace?.visitDate || '',
    notes: editPlace?.notes || '',
    rating: editPlace?.rating || 0,
    favorite: editPlace?.favorite || false
  })
  const [loading, setLoading] = useState(false)

  const statusOptions = [
    { value: 'VISITED', label: '✅ Visitado', color: 'bg-emerald-500' },
    { value: 'WISHLIST', label: '⭐ Quiero ir', color: 'bg-amber-500' },
    { value: 'LIVED', label: '🏠 He vivido', color: 'bg-blue-500' },
    { value: 'LIVING', label: '📍 Vivo aquí', color: 'bg-violet-500' }
  ]

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!selectedCountry) {
      toast.error('Selecciona un país')
      return
    }

    setLoading(true)

    try {
      const placeData = {
        countryId: selectedCountry.id,
        cityName: formData.cityName || null,
        status: formData.status,
        visitDate: formData.visitDate || null,
        notes: formData.notes || null,
        rating: formData.rating || null,
        favorite: formData.favorite
      }

      if (editPlace) {
        await travelService.updatePlace(editPlace.id, placeData)
        toast.success('Lugar actualizado')
      } else {
        await travelService.addPlace(placeData)
        toast.success(`${selectedCountry.flagEmoji} ${selectedCountry.name} agregado a tu mapa!`)
      }

      onSuccess()
      onClose()
    } catch (error) {
      console.error('Error:', error)
      toast.error(error.response?.data?.message || 'Error al guardar')
    } finally {
      setLoading(false)
    }
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      {/* Overlay */}
      <div 
        className="absolute inset-0 bg-black/60 backdrop-blur-sm"
        onClick={onClose}
      />

      {/* Modal */}
      <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-lg max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="sticky top-0 bg-gradient-to-r from-emerald-600 to-teal-600 px-6 py-4 rounded-t-2xl">
          <h2 className="text-xl font-bold text-white">
            {editPlace ? '✏️ Editar lugar' : '🌍 Agregar lugar'}
          </h2>
          <p className="text-emerald-100 text-sm">
            {editPlace ? 'Modifica los detalles de tu visita' : 'Añade un nuevo destino a tu mapa'}
          </p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-5">
          {/* Selector de país */}
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-2">
              País *
            </label>
            <CountrySelector 
              onSelect={setSelectedCountry}
              selectedCountry={selectedCountry}
            />
          </div>

          {/* Ciudad (opcional) */}
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-2">
              Ciudad (opcional)
            </label>
            <input
              type="text"
              value={formData.cityName}
              onChange={(e) => setFormData({ ...formData, cityName: e.target.value })}
              placeholder="Ej: Barcelona, Tokio..."
              className="w-full px-4 py-3 border-2 border-slate-200 rounded-xl focus:border-emerald-400 focus:ring-0 transition-colors"
            />
          </div>

          {/* Estado */}
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-2">
              Estado
            </label>
            <div className="grid grid-cols-2 gap-2">
              {statusOptions.map(option => (
                <button
                  key={option.value}
                  type="button"
                  onClick={() => setFormData({ ...formData, status: option.value })}
                  className={`px-4 py-3 rounded-xl border-2 transition-all ${
                    formData.status === option.value
                      ? `${option.color} border-transparent text-white`
                      : 'border-slate-200 text-slate-600 hover:border-slate-300'
                  }`}
                >
                  {option.label}
                </button>
              ))}
            </div>
          </div>

          {/* Fecha de visita */}
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-2">
              Fecha de visita
            </label>
            <input
              type="date"
              value={formData.visitDate}
              onChange={(e) => setFormData({ ...formData, visitDate: e.target.value })}
              className="w-full px-4 py-3 border-2 border-slate-200 rounded-xl focus:border-emerald-400 focus:ring-0 transition-colors"
            />
          </div>

          {/* Rating */}
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-2">
              Puntuación
            </label>
            <div className="flex gap-2">
              {[1, 2, 3, 4, 5].map(star => (
                <button
                  key={star}
                  type="button"
                  onClick={() => setFormData({ ...formData, rating: star })}
                  className={`text-3xl transition-transform hover:scale-110 ${
                    star <= formData.rating ? 'grayscale-0' : 'grayscale opacity-30'
                  }`}
                >
                  ⭐
                </button>
              ))}
              {formData.rating > 0 && (
                <button
                  type="button"
                  onClick={() => setFormData({ ...formData, rating: 0 })}
                  className="text-sm text-slate-400 hover:text-slate-600 ml-2"
                >
                  Quitar
                </button>
              )}
            </div>
          </div>

          {/* Notas */}
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-2">
              Notas
            </label>
            <textarea
              value={formData.notes}
              onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
              placeholder="¿Qué te pareció? ¿Algún lugar especial?"
              rows={3}
              className="w-full px-4 py-3 border-2 border-slate-200 rounded-xl focus:border-emerald-400 focus:ring-0 transition-colors resize-none"
            />
          </div>

          {/* Favorito */}
          <label className="flex items-center gap-3 cursor-pointer">
            <input
              type="checkbox"
              checked={formData.favorite}
              onChange={(e) => setFormData({ ...formData, favorite: e.target.checked })}
              className="w-5 h-5 rounded border-slate-300 text-rose-500 focus:ring-rose-400"
            />
            <span className="text-slate-700">❤️ Marcar como lugar favorito</span>
          </label>

          {/* Botones */}
          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-6 py-3 border-2 border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading || !selectedCountry}
              className="flex-1 px-6 py-3 bg-gradient-to-r from-emerald-500 to-teal-500 text-white rounded-xl hover:from-emerald-600 hover:to-teal-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? (
                <span className="flex items-center justify-center gap-2">
                  <div className="animate-spin rounded-full h-5 w-5 border-2 border-white border-t-transparent" />
                  Guardando...
                </span>
              ) : (
                editPlace ? 'Actualizar' : 'Agregar al mapa'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

AddPlaceModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  onSuccess: PropTypes.func.isRequired,
  editPlace: PropTypes.object
}

export default AddPlaceModal

