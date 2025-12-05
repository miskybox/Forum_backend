// Archivo: src/pages/ForumEditPage.jsx
import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import ForumForm from '../components/forums/ForumForm'
import forumService from '../services/forumService'
import useAuth from '../hooks/useAuth'

const ForumEditPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  
  const [forum, setForum] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  
  const { currentUser, hasRole } = useAuth()
  
  useEffect(() => {
    const fetchForum = async () => {
      try {
        setLoading(true)
        const data = await forumService.getForumById(id)
        setForum(data)
        
        // Verificar si el usuario tiene permiso para editar este foro
        if (currentUser && (data.creator?.id === currentUser.id || hasRole('ADMIN') || hasRole('MODERATOR'))) {
          setError(null)
        } else {
          setError('No tienes permiso para editar este foro')
          setTimeout(() => {
            navigate(`/forums/${id}`)
          }, 3000)
        }
      } catch (err) {
        console.error('Error al cargar el foro:', err)
        setError('No se pudo cargar el foro. Por favor, inténtalo de nuevo más tarde.')
      } finally {
        setLoading(false)
      }
    }
    
    fetchForum()
  }, [id, currentUser, hasRole, navigate])
  
  if (loading) {
    return (
      <div className="flex justify-center items-center py-20">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-600"></div>
      </div>
    )
  }
  
  if (error || !forum) {
    return (
      <div className="bg-neutral-50 py-10">
        <div className="container px-4 sm:px-6 lg:px-8 mx-auto">
          <div className="text-center py-10 bg-white rounded-lg shadow-sm p-6">
            <div className="text-red-600 mb-4">{error || 'Foro no encontrado'}</div>
            <p className="mb-4 text-neutral-600">Serás redirigido en unos segundos...</p>
            <Link to={`/forums/${id}`} className="btn btn-primary">
              Volver al foro
            </Link>
          </div>
        </div>
      </div>
    )
  }
  
  return (
    <div className="bg-neutral-50 py-8 sm:py-12">
      <div className="container px-4 sm:px-6 lg:px-8 mx-auto">
        <div className="mb-8">
          <div className="flex items-center justify-between">
            <h1 className="text-2xl sm:text-3xl font-bold text-neutral-800">
              Editar foro
            </h1>
            
            <Link 
              to={`/forums/${id}`}
              className="text-primary-600 hover:text-primary-700 text-sm font-medium"
            >
              ← Volver al foro
            </Link>
          </div>
          <p className="mt-2 text-neutral-600">
            Actualiza la información de tu foro "{forum.title}".
          </p>
        </div>
        
        <div className="bg-white rounded-lg shadow-sm p-6">
          <ForumForm initialData={forum} isEdit={true} />
        </div>
      </div>
    </div>
  )
}

export default ForumEditPage