// Archivo: src/pages/PostEditPage.jsx
import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import PostForm from '../components/post/PostForm';
import postService from '../services/postService'
import useAuth from '../hooks/useAuth'

const PostEditPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  
  const [post, setPost] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  
  const { currentUser, hasRole } = useAuth()
  
  useEffect(() => {
    const fetchPost = async () => {
      try {
        setLoading(true)
        const data = await postService.getPostById(id)
        setPost(data)
        
        // Verificar si el usuario tiene permiso para editar esta publicación
        if (currentUser && (data.author?.id === currentUser.id || hasRole('ADMIN') || hasRole('MODERATOR'))) {
          setError(null)
        } else {
          setError('No tienes permiso para editar esta publicación')
          setTimeout(() => {
            navigate(`/posts/${id}`)
          }, 3000)
        }
      } catch (err) {
        console.error('Error al cargar la publicación:', err)
        setError('No se pudo cargar la publicación. Por favor, inténtalo de nuevo más tarde.')
      } finally {
        setLoading(false)
      }
    }
    
    fetchPost()
  }, [id, currentUser, hasRole, navigate])
  
  if (loading) {
    return (
      <div className="flex justify-center items-center py-20">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-600"></div>
      </div>
    )
  }
  
  if (error || !post) {
    return (
      <div className="bg-neutral-50 py-10">
        <div className="container px-4 sm:px-6 lg:px-8 mx-auto">
          <div className="text-center py-10 bg-white rounded-lg shadow-sm p-6">
            <div className="text-red-600 mb-4">{error || 'Publicación no encontrada'}</div>
            <p className="mb-4 text-neutral-600">Serás redirigido en unos segundos...</p>
            <Link to={`/posts/${id}`} className="btn btn-primary">
              Volver a la publicación
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
              Editar publicación
            </h1>
            
            <Link 
              to={`/posts/${id}`}
              className="text-primary-600 hover:text-primary-700 text-sm font-medium"
            >
              ← Volver a la publicación
            </Link>
          </div>
          <p className="mt-2 text-neutral-600">
            Actualiza la información de tu publicación "{post.title}".
          </p>
        </div>
        
        <div className="bg-white rounded-lg shadow-sm p-6">
          <PostForm initialData={post} isEdit={true} />
        </div>
      </div>
    </div>
  )
}

export default PostEditPage