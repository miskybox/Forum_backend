// Archivo: src/components/categories/CategoryList.jsx
import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import CategoryCard from './CategoryCard'
import categoryService from '../../services/categoryService'

const CategoryList = () => {
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        setLoading(true)
        const data = await categoryService.getAllCategories()
        setCategories(data)
        setError(null)
      } catch (err) {
        console.error('Error al cargar las categorías:', err)
        setError('No se pudieron cargar las categorías. Por favor, inténtalo de nuevo más tarde.')
      } finally {
        setLoading(false)
      }
    }
    
    fetchCategories()
  }, [])
  
  if (loading) {
    return (
      <div className="flex justify-center items-center py-20">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-600"></div>
      </div>
    )
  }
  
  if (error) {
    return (
      <div className="text-center py-10">
        <div className="text-red-600 mb-4">{error}</div>
        <button 
          onClick={() => window.location.reload()}
          className="btn btn-primary"
        >
          Reintentar
        </button>
      </div>
    )
  }
  
  if (categories.length === 0) {
    return (
      <div className="text-center py-10">
        <h3 className="text-xl font-semibold mb-2">No hay categorías disponibles</h3>
        <p className="text-neutral-600 mb-4">Vuelve pronto, estamos trabajando en añadir nuevos destinos.</p>
      </div>
    )
  }
  
  return (
    <div>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {categories.map((category) => (
          <CategoryCard key={category.id} category={category} />
        ))}
      </div>
    </div>
  )
}

export default CategoryList