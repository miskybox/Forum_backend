// Archivo: src/pages/PostCreatePage.jsx
import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import PostForm from '../components/post/PostForm';
import forumService from '../services/forumService';



const PostCreatePage = () => {
  const { forumId } = useParams()
  
  const [forum, setForum] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  
  useEffect(() => {
    const fetchForum = async () => {
      try {
        setLoading(true)
        const data = await forumService.getForumById(forumId)
        setForum(data)
        setError(null)
      } catch (err) {
        console.error('Error al cargar el foro:', err)
        setError('No se pudo cargar el foro. Por favor, inténtalo de nuevo más tarde.')
      } finally {
        setLoading(false)
      }
    }
    
    fetchForum()
  }, [forumId])
  
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
            <Link to="/forums" className="btn btn-primary">
              Ver foros disponibles
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
              Nueva publicación
            </h1>
            
            <Link 
              to={`/forums/${forumId}`}
              className="text-primary-600 hover:text-primary-700 text-sm font-medium"
            >
              ← Volver al foro
            </Link>
          </div>
          <p className="mt-2 text-neutral-600">
            Comparte tu experiencia, pregunta o información en el foro "{forum.title}".
          </p>
        </div>
        
        <div className="bg-white rounded-lg shadow-sm p-6">
          <PostForm />
        </div>
      </div>
    </div>
  )
}

export default PostCreatePage