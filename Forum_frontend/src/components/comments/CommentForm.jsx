import { useState } from 'react'
import { toast } from 'react-hot-toast'
import useAuth from '../../hooks/useAuth'
import commentService from '../../services/commentService'
import PropTypes from 'prop-types'

const CommentForm = ({ postId, onCommentAdded }) => {
  const [content, setContent] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const { isAuthenticated } = useAuth()

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!content.trim()) {
      toast.error('El comentario no puede estar vacío')
      return
    }

    if (!isAuthenticated) {
      toast.error('Debes iniciar sesión para comentar')
      return
    }

    try {
      setIsSubmitting(true)

      const commentData = {
        content: content.trim(),
        postId,
      }

      const newComment = await commentService.createComment(postId, commentData)

      setContent('')
      toast.success('Comentario publicado con éxito')

      if (onCommentAdded) {
        onCommentAdded(newComment)
      }
    } catch (error) {
      toast.error('Error al publicar el comentario')
      console.error('Error al publicar comentario:', error)
    } finally {
      setIsSubmitting(false)
    }
  }

  if (!isAuthenticated) {
    return (
      <div className="bg-white rounded-lg p-4 shadow mb-4">
        <p className="text-center text-gray-600">
          Debes{' '}
          <a href="/login" className="text-primary-600 hover:underline">
            iniciar sesión
          </a>{' '}
          para comentar.
        </p>
      </div>
    )
  }

  return (
    <div className="bg-white rounded-lg p-4 shadow mb-6">
      <h3 className="text-lg font-semibold mb-3">Deja un comentario</h3>
      <form onSubmit={handleSubmit}>
        <textarea
          className="w-full border border-gray-300 rounded-lg p-3 min-h-[100px] focus:ring-2 focus:ring-primary-500 focus:border-transparent"
          placeholder="Escribe tu comentario aquí..."
          value={content}
          onChange={(e) => setContent(e.target.value)}
          disabled={isSubmitting}
          required
        ></textarea>
        <div className="flex justify-end mt-3">
          <button
            type="submit"
            className={`px-4 py-2 rounded-lg font-medium text-white bg-green-600 hover:bg-green-700 transition-colors ${
              isSubmitting ? 'opacity-50 cursor-not-allowed' : ''
            }`}
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Enviando...' : 'Publicar comentario'}
          </button>
        </div>
      </form>
    </div>
  )
}

CommentForm.propTypes = {
  postId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
  onCommentAdded: PropTypes.func,
}

export default CommentForm
