// Archivo: src/components/posts/PostList.jsx
import { useState, useEffect } from 'react'
import { Link, useParams } from 'react-router-dom'
import PostCard from './PostCard'
import postService from '../../services/postService'
import forumService from '../../services/forumService'
import useAuth from '../../hooks/useAuth'

const PostList = ({ forumId: propForumId }) => {
  const { forumId: paramForumId } = useParams()
  const forumId = propForumId || paramForumId
  
  const [posts, setPosts] = useState([])
  const [forum, setForum] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  
  const { isAuthenticated } = useAuth()
  
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true)
        
        // Obtener detalles del foro
        const forumData = await forumService.getForumById(forumId)
        setForum(forumData)
        
        // Obtener publicaciones del foro
        const postData = await postService.getPostsByForum(forumId)
        setPosts(postData)
        
        setError(null)
      } catch (err) {
        console.error('Error al cargar las publicaciones:', err)
        setError('No se pudieron cargar las publicaciones. Por favor, inténtalo de nuevo más tarde.')
      } finally {
        setLoading(false)
      }
    }
    
    if (forumId) {
      fetchData()
    }
  }, [forumId])
  
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
  
  if (!forum) {
    return (
      <div className="text-center py-10">
        <div className="text-red-600 mb-4">Foro no encontrado</div>
        <Link to="/forums" className="btn btn-primary">
          Ver todos los foros
        </Link>
      </div>
    )
  }
  
  return (
    <div>
      <div className="mb-6 flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-neutral-800">
            Publicaciones en {forum.title}
          </h2>
          <p className="text-neutral-600">{forum.description}</p>
        </div>
        
        {isAuthenticated && (
          <Link 
            to={`/forums/${forumId}/posts/create`} 
            className="btn btn-primary whitespace-nowrap"
          >
            Nueva publicación
          </Link>
        )}
      </div>
      
      {posts.length === 0 ? (
        <div className="text-center py-10 bg-white rounded-lg shadow-sm p-6">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 mx-auto text-neutral-400 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
          </svg>
          
          <h3 className="text-xl font-semibold mb-2">No hay publicaciones en este foro</h3>
          <p className="text-neutral-600 mb-4">Sé el primero en compartir algo en {forum.title}.</p>
          
          {isAuthenticated ? (
            <Link to={`/forums/${forumId}/posts/create`} className="btn btn-primary">
              Crear la primera publicación
            </Link>
          ) : (
            <Link to="/login" className="btn btn-primary">
              Inicia sesión para crear una publicación
            </Link>
          )}
        </div>
      ) : (
        <div className="space-y-6">
          {posts.map((post) => (
            <PostCard key={post.id} post={post} />
          ))}
        </div>
      )}
    </div>
  )
}

import PropTypes from 'prop-types'

PostList.propTypes = {
  forumId: PropTypes.string
}

export default PostList