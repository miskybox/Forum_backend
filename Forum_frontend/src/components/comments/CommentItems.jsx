// Archivo: src/components/comments/CommentItem.jsx
import { useState } from 'react'
import { formatDistanceToNow } from 'date-fns'
import { es } from 'date-fns/locale'
import { toast } from 'react-hot-toast'
import commentService from '../../services/commentService'
import useAuth from '../../hooks/useAuth'

const CommentItem = ({ comment, onUpdate, onDelete }) => {
  const [isEditing, setIsEditing] = useState(false)
  const [editContent, setEditContent] = useState(comment.content)
  const [isSubmitting, setIsSubmitting] = useState(false)
  
  const { currentUser } = useAuth()
  
  // Comprobar si el usuario actual es el autor del comentario
  const isAuthor = currentUser?.id === comment.author?.id
  
  // Formatear fecha relativa (ej: "hace 5 minutos")
  const formatRelativeDate = (dateString) => {
    const date = new Date(dateString)
    return formatDistanceToNow(date, { addSuffix: true, locale: es })
  }
  
  const handleEditClick = () => {
    setIsEditing(true)
    setEditContent(comment.content)
  }
  
  const handleCancelEdit = () => {
    setIsEditing(false)
  }
  
  const handleUpdateComment = async () => {
    if (!editContent.trim()) {
      toast.error('El comentario no puede estar vacío')
      return
    }
    
    setIsSubmitting(true)
    
    try {
      const updatedComment = await commentService.updateComment(comment.id, { content: editContent })
      onUpdate(updatedComment)
      setIsEditing(false)
      toast.success('Comentario actualizado')
    } catch (error) {
      console.error('Error al actualizar el comentario:', error)
      toast.error('Error al actualizar el comentario')
    } finally {
      setIsSubmitting(false)
    }
  }
  
  const handleDeleteComment = async () => {
    if (!window.confirm('¿Estás seguro de que quieres eliminar este comentario?')) {
      return
    }
    
    setIsSubmitting(true)
    
    try {
      await commentService.deleteComment(comment.id)
      onDelete(comment.id)
      toast.success('Comentario eliminado')
    } catch (error) {
      console.error('Error al eliminar el comentario:', error)
      toast.error('Error al eliminar el comentario')
    } finally {
      setIsSubmitting(false)
    }
  }
  
  return (
    <div className="bg-white p-4 rounded-lg shadow-sm border border-neutral-100">
      <div className="flex items-start space-x-3">
        {/* Avatar del autor */}
        <div className="flex-shrink-0">
          <div className="w-10 h-10 bg-primary-500 rounded-full flex items-center justify-center text-white text-sm overflow-hidden">
            {comment.author?.profileImage ? (
              <img 
                src={comment.author.profileImage} 
                alt={comment.author.username}
                className="w-full h-full object-cover"
              />
            ) : (
              comment.author?.username?.charAt(0)?.toUpperCase() || 'U'
            )}
          </div>
        </div>
        
        {/* Contenido del comentario */}
        <div className="flex-grow min-w-0">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-1">
            <div>
              <span className="font-medium text-neutral-800">
                {comment.author?.username || 'Usuario desconocido'}
              </span>
              <span className="mx-1">•</span>
              <time className="text-sm text-neutral-500" dateTime={comment.createdAt}>
                {formatRelativeDate(comment.createdAt)}
              </time>
            </div>
            
            {/* Botones de acción para el autor */}
            {isAuthor && !isEditing && (
              <div className="flex items-center space-x-2 mt-1 sm:mt-0">
                <button
                  type="button"
                  className="text-neutral-500 hover:text-primary-600 text-sm font-medium"
                  onClick={handleEditClick}
                  disabled={isSubmitting}
                >
                  Editar
                </button>
                <button
                  type="button"
                  className="text-neutral-500 hover:text-red-600 text-sm font-medium"
                  onClick={handleDeleteComment}
                  disabled={isSubmitting}
                >
                  Eliminar
                </button>
              </div>
            )}
          </div>
          
          {isEditing ? (
            <div className="space-y-3">
              <textarea
                className="input w-full"
                value={editContent}
                onChange={(e) => setEditContent(e.target.value)}
                rows={3}
                disabled={isSubmitting}
              />
              
              <div className="flex space-x-2">
                <button
                  type="button"
                  className="btn btn-primary text-sm py-1 px-3 h-auto"
                  onClick={handleUpdateComment}
                  disabled={isSubmitting}
                >
                  {isSubmitting ? 'Guardando...' : 'Guardar'}
                </button>
                <button
                  type="button"
                  className="btn btn-outline text-sm py-1 px-3 h-auto"
                  onClick={handleCancelEdit}
                  disabled={isSubmitting}
                >
                  Cancelar
                </button>
              </div>
            </div>
          ) : (
            <div className="text-neutral-700 break-words">
              {comment.content}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

import PropTypes from 'prop-types'

CommentItem.propTypes = {
  comment: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
    content: PropTypes.string.isRequired,
    createdAt: PropTypes.string.isRequired,
    author: PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      username: PropTypes.string,
      profileImage: PropTypes.string,
    }),
  }).isRequired,
  onUpdate: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
}

export default CommentItem