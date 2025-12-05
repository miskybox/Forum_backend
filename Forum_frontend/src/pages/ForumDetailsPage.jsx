import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import useAuth from '../hooks/useAuth'
import forumService from '../services/forumService'
import postService from '../services/postService'
import LoadingSpinner from '../components/common/LoadingSpinner'
import PostList from '../components/post/PostList'

const ForumDetailsPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const { currentUser, hasRole, isAuthenticated } = useAuth()
  const [forum, setForum] = useState(null)
  const [posts, setPosts] = useState([])
  const [loading, setLoading] = useState(true)
  const [postLoading, setPostLoading] = useState(true)

  useEffect(() => {
    const fetchForumData = async () => {
      try {
        setLoading(true)
        const forumData = await forumService.getForumById(id)
        setForum(forumData)
      } catch (error) {
        console.error('Error al cargar el foro:', error)
        toast.error('No se pudo cargar el foro')
        navigate('/forums')
      } finally {
        setLoading(false)
      }
    }

    const fetchPosts = async () => {
      try {
        setPostLoading(true)
        const postsData = await postService.getPostsByForum(id)
        setPosts(postsData)
      } catch (error) {
        console.error('Error al cargar publicaciones:', error)
        toast.error('No se pudieron cargar las publicaciones')
      } finally {
        setPostLoading(false)
      }
    }

    fetchForumData()
    fetchPosts()
  }, [id, navigate])

  const handleDeleteForum = async () => {
    if (window.confirm('¿Estás seguro de que quieres eliminar este foro? Esta acción no se puede deshacer.')) {
      try {
        await forumService.deleteForum(id)
        toast.success('Foro eliminado con éxito')
        navigate('/forums')
      } catch (error) {
        console.error('Error al eliminar el foro:', error)
        toast.error('No se pudo eliminar el foro')
      }
    }
  }

  if (loading) {
    return <LoadingSpinner />
  }

  const isForumAuthor = currentUser && forum.createdBy === currentUser.id
  const canEditForum = isForumAuthor || hasRole('ADMIN') || hasRole('MODERATOR')

  // Componente para mostrar cuando no hay publicaciones
  const EmptyPostsMessage = () => (
    <div className="text-center py-8 bg-gray-50 rounded-lg">
      <p className="text-gray-500 mb-4">Aún no hay publicaciones en este foro</p>
      {isAuthenticated ? (
        <Link 
          to={`/forums/${id}/posts/create`}
          className="px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700"
        >
          Sé el primero en publicar
        </Link>
      ) : (
        <Link 
          to="/login"
          className="px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700"
        >
          Inicia sesión para publicar
        </Link>
      )}
    </div>
  );

  // Renderiza el contenido de las publicaciones basado en el estado
  const renderPostContent = () => {
    if (postLoading) {
      return <LoadingSpinner />;
    } 
    
    if (posts.length > 0) {
      return <PostList posts={posts} />;
    }
    
    return <EmptyPostsMessage />;
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        {/* Cabecera del foro */}
        <div className="relative">
          {forum.imagePath && (
            <div className="h-48 bg-gradient-to-r from-primary-600 to-primary-800 relative">
              <img
                src={forum.imagePath}
                alt={forum.title}
                className="w-full h-full object-cover opacity-50"
              />
            </div>
          )}
          
          {!forum.imagePath && (
            <div className="h-32 bg-gradient-to-r from-primary-600 to-primary-800"></div>
          )}
          
          <div className={`px-6 py-4 ${forum.imagePath ? '-mt-16' : ''}`}>
            <h1 className={`text-3xl font-bold ${forum.imagePath ? 'text-white' : ''}`}>
              {forum.title}
            </h1>
          </div>
        </div>
        
        {/* Contenido del foro */}
        <div className="px-6 py-4">
          <p className="text-gray-700 mb-4">{forum.description}</p>
          
          {forum.rules && (
            <div className="mb-6 p-4 bg-gray-50 rounded-lg border border-gray-200">
              <h3 className="text-lg font-semibold mb-2">Reglas del foro</h3>
              <div className="text-gray-700 whitespace-pre-line">{forum.rules}</div>
            </div>
          )}
          
          {/* Acciones */}
          <div className="flex flex-wrap justify-between items-center mt-4 mb-6">
            <div>
              {canEditForum && (
                <div className="flex space-x-2">
                  <Link
                    to={`/forums/${id}/edit`}
                    className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                  >
                    Editar foro
                  </Link>
                  <button
                    onClick={handleDeleteForum}
                    className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700"
                  >
                    Eliminar foro
                  </button>
                </div>
              )}
            </div>
          </div>
          
          {/* Lista de publicaciones */}
          <div className="mt-8">
            <h2 className="text-2xl font-bold mb-4">Publicaciones</h2>
            {renderPostContent()}
          </div>
        </div>
      </div>
    </div>
  )
}

export default ForumDetailsPage