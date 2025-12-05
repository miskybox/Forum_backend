// Archivo: src/components/forums/ForumList.jsx
import { useState, useEffect } from 'react'
import { Link, useParams } from 'react-router-dom'
import ForumCard from './ForumCard'
import ForumSearch from './ForumSearch'
import forumService from '../../services/forumService'
import categoryService from '../../services/categoryService'
import useAuth from '../../hooks/useAuth'
import PropTypes from 'prop-types'

const ForumList = ({ categoryId: propCategoryId }) => {
  const { categoryId: paramCategoryId } = useParams()
  const categoryId = propCategoryId || paramCategoryId
  
  const [forums, setForums] = useState([])
  const [category, setCategory] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')
  
  const { isAuthenticated } = useAuth()
  
  useEffect(() => {
    const fetchForums = async () => {
      try {
        setLoading(true)
        
        let forumData = []
        
        if (categoryId) {
          // Si hay un ID de categoría, obtener los foros de esa categoría
          forumData = await forumService.getForumsByCategory(categoryId)
          
          // También obtener los detalles de la categoría
          const categoryData = await categoryService.getCategoryById(categoryId)
          setCategory(categoryData)
        } else if (searchTerm) {
          // Si hay un término de búsqueda, buscar foros que coincidan
          forumData = await forumService.searchForums(searchTerm)
        } else {
          // Por defecto, obtener todos los foros
          const response = await forumService.getAllForums()
          forumData = response.content || response // Dependiendo de cómo devuelva la API los datos paginados
        }
        
        setForums(forumData)
        setError(null)
      } catch (err) {
        console.error('Error al cargar los foros:', err)
        setError('No se pudieron cargar los foros. Por favor, inténtalo de nuevo más tarde.')
      } finally {
        setLoading(false)
      }
    }
    
    fetchForums()
  }, [categoryId, searchTerm])
  
  const handleSearch = (term) => {
    setSearchTerm(term)
  }
  
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
  
  // Extract heading into a variable for clarity
  let heading;
  if (category) {
    heading = (
      <h2 className="text-2xl font-bold text-neutral-800">
        Foros de {category.name}
      </h2>
    );
  } else if (searchTerm) {
    heading = (
      <h2 className="text-2xl font-bold text-neutral-800">
        Resultados para "{searchTerm}"
      </h2>
    );
  } else {
    heading = (
      <h2 className="text-2xl font-bold text-neutral-800">
        Todos los foros
      </h2>
    );
  }

  return (
    <div>
      <div className="mb-6 flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          {heading}
        </div>
        
        <div className="flex flex-col sm:flex-row gap-2 sm:items-center">
          <ForumSearch onSearch={handleSearch} initialValue={searchTerm} />
          
          {isAuthenticated && (
            <Link to="/forums/create" className="btn btn-primary whitespace-nowrap">
              Crear Foro
            </Link>
          )}
        </div>
      </div>
      
      {(() => {
        let emptyStateContent;
        if (searchTerm) {
          emptyStateContent = (
            <>
              <h3 className="text-xl font-semibold mb-2">No se encontraron resultados</h3>
              <p className="text-neutral-600 mb-4">No hay foros que coincidan con tu búsqueda "{searchTerm}".</p>
              <button 
                onClick={() => setSearchTerm('')}
                className="btn btn-outline"
              >
                Ver todos los foros
              </button>
            </>
          );
        } else if (category) {
          emptyStateContent = (
            <>
              <h3 className="text-xl font-semibold mb-2">No hay foros en esta categoría</h3>
              <p className="text-neutral-600 mb-4">Sé el primero en crear un foro en {category.name}.</p>
              {isAuthenticated ? (
                <Link to="/forums/create" className="btn btn-primary">
                  Crear el primer foro
                </Link>
              ) : (
                <Link to="/login" className="btn btn-primary">
                  Inicia sesión para crear un foro
                </Link>
              )}
            </>
          );
        } else {
          emptyStateContent = (
            <>
              <h3 className="text-xl font-semibold mb-2">No hay foros disponibles</h3>
              <p className="text-neutral-600 mb-4">Sé el primero en crear un foro en nuestra comunidad.</p>
              {isAuthenticated ? (
                <Link to="/forums/create" className="btn btn-primary">
                  Crear el primer foro
                </Link>
              ) : (
                <Link to="/login" className="btn btn-primary">
                  Inicia sesión para crear un foro
                </Link>
              )}
            </>
          );
        }

        return forums.length === 0 ? (
          <div className="text-center py-10 bg-white rounded-lg shadow-sm p-6">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 mx-auto text-neutral-400 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
            </svg>
            {emptyStateContent}
          </div>
        ) : (
          <div className="space-y-4">
            {forums.map((forum) => (
              <ForumCard key={forum.id} forum={forum} />
            ))}
          </div>
        );
      })()}
    </div>
  )
}
ForumList.propTypes = {
  categoryId: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number
  ])
}

export default ForumList
