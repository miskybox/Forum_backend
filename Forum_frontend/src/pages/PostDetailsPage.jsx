import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import useAuth from '../hooks/useAuth'
import postService from '../services/postService'
import commentService from '../services/commentService'
import LoadingSpinner from '../components/common/LoadingSpinner'
import PostContent from '../components/post/PostContent'
import CommentList from '../components/comments/CommentList'
import CommentForm from '../components/comments/CommentForm'

const PostDetailsPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const { currentUser, hasRole, isAuthenticated } = useAuth()
  const [post, setPost] = useState(null)
  const [comments, setComments] = useState([])
  const [loading, setLoading] = useState(true)
  const [commentLoading, setCommentLoading] = useState(true)

  useEffect(() => {
    const fetchPostData = async () => {
      try {
        setLoading(true)
        const postData = await postService.getPostById(id)
        setPost(postData)
      } catch (error) {
        console.error('Error al cargar la publicación:', error)
        toast.error('No se pudo cargar la publicación')
        navigate('/forums')
      } finally {
        setLoading(false)
      }
    }

    const fetchComments = async () => {
      try {
        setCommentLoading(true)
        const commentsData = await commentService.getCommentsByPost(id)
        setComments(commentsData)
      } catch (error) {
        console.error('Error al cargar comentarios:', error)
        toast.error('No se pudieron cargar los comentarios')
      } finally {
        setCommentLoading(false)
      }
    }

    fetchPostData()
    fetchComments()
  }, [id, navigate])

  const handleDeletePost = async () => {
    if (window.confirm('¿Estás seguro de que quieres eliminar esta publicación? Esta acción no se puede deshacer.')) {
      try {
        await postService.deletePost(id)
        toast.success('Publicación eliminada con éxito')
        navigate(`/forums/${post.forumId}`)
      } catch (error) {
        console.error('Error al eliminar la publicación:', error)
        toast.error('No se pudo eliminar la publicación')
      }
    }
  }

  const handleCommentAdded = (newComment) => {
    setComments([...comments, newComment])
  }

  const handleCommentDeleted = (commentId) => {
    setComments(comments.filter(comment => comment.id !== commentId))
  }

  if (loading) {
    return <LoadingSpinner />
  }

  const isPostAuthor = currentUser && post.createdBy === currentUser.id
  const canEditPost = isPostAuthor || hasRole('ADMIN') || hasRole('MODERATOR')

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-4">
        <Link 
          to={`/forums/${post.forumId}`}
          className="text-primary-600 hover:text-primary-800 flex items-center"
        >
          <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
          Volver al foro
        </Link>
      </div>
      
      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        {/* Contenido del post */}
        <PostContent post={post} />
        
        {/* Acciones */}
        {canEditPost && (
          <div className="px-6 py-4 flex space-x-2 border-t border-gray-200">
            <Link
              to={`/posts/${id}/edit`}
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
            >
              Editar publicación
            </Link>
            
            {(isPostAuthor || hasRole('ADMIN')) && (
              <button
                onClick={handleDeletePost}
                className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700"
              >
                Eliminar publicación
              </button>
            )}
          </div>
        )}
      </div>
      
      {/* Sección de comentarios */}
      <div className="mt-8">
        <h2 className="text-xl font-semibold mb-4">Comentarios</h2>
        {commentLoading ? (
          <LoadingSpinner />
        ) : (
          <CommentList
            comments={comments}
            currentUser={currentUser}
            onCommentDeleted={handleCommentDeleted}
          />
        )}

        {isAuthenticated ? (
          <CommentForm postId={id} onCommentAdded={handleCommentAdded} />
        ) : (
          <div className="mt-4 text-gray-600">
            <Link to="/login" className="text-primary-600 hover:underline">
              Inicia sesión
            </Link>{' '}
            para comentar.
          </div>
        )}
      </div>
    </div>
  )
}

export default PostDetailsPage
