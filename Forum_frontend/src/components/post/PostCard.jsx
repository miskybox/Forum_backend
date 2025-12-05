// Archivo: src/components/posts/PostCard.jsx
import { Link } from 'react-router-dom'
import { formatDistanceToNow } from 'date-fns'
import { es } from 'date-fns/locale'

const PostCard = ({ post }) => {
  // Función para formatear la fecha relativa (ej: "hace 2 días")
  const formatRelativeDate = (dateString) => {
    const date = new Date(dateString)
    return formatDistanceToNow(date, { addSuffix: true, locale: es })
  }

  // Función para truncar el texto a un número específico de caracteres
  const truncateText = (text, maxLength = 150) => {
    if (!text || text.length <= maxLength) return text
    return text.substr(0, maxLength) + '...'
  }

  return (
    <div className="card overflow-hidden hover:shadow-md transition-all">
      <Link to={`/posts/${post.id}`} className="block">
        <div className="p-5">
          {/* Título y metadata */}
          <div className="mb-3">
            <h3 className="text-xl font-bold text-neutral-800 mb-1 hover:text-primary-700">
              {post.title}
            </h3>
            <div className="flex items-center text-sm text-neutral-500">
              <div className="flex items-center">
                <div className="w-6 h-6 bg-primary-500 rounded-full flex items-center justify-center text-white text-xs overflow-hidden mr-2">
                  {post.author?.profileImage ? (
                    <img 
                      src={post.author.profileImage} 
                      alt={post.author.username}
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    post.author?.username?.charAt(0)?.toUpperCase() || 'U'
                  )}
                </div>
                <span>{post.author?.username || 'Usuario'}</span>
              </div>
              <span className="mx-2">•</span>
              <time dateTime={post.createdAt}>
                {formatRelativeDate(post.createdAt)}
              </time>
            </div>
          </div>
          
          {/* Contenido */}
          <div className="mb-4">
            <p className="text-neutral-700">
              {truncateText(post.content)}
            </p>
          </div>
          
          {/* Galería (si hay imágenes) */}
          {post.images && post.images.length > 0 && (
            <div className="mb-4">
              <div className={`grid ${post.images.length > 1 ? 'grid-cols-2' : 'grid-cols-1'} gap-2`}>
                {post.images.slice(0, 4).map((image, index) => (
                  <div 
                    key={image.id || index} 
                    className={`relative ${post.images.length > 2 && index === 0 ? 'col-span-2' : ''} bg-neutral-100 rounded-md overflow-hidden aspect-video`}
                  >
                    <img 
                      src={image.url} 
                      alt={`Imagen ${index + 1} de la publicación`} 
                      className="w-full h-full object-cover"
                    />
                    {post.images.length > 4 && index === 3 && (
                      <div className="absolute inset-0 bg-black/50 flex items-center justify-center text-white font-semibold text-lg">
                        +{post.images.length - 4}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}
          
          {/* Footer con estadísticas */}
          <div className="flex justify-between items-center pt-3 border-t border-neutral-100 text-sm text-neutral-500">
            <div className="flex items-center space-x-4">
              <div className="flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z" />
                </svg>
                {post.commentCount || 0} {post.commentCount === 1 ? 'comentario' : 'comentarios'}
              </div>
              
              <div className="flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                {post.viewCount || 0} {post.viewCount === 1 ? 'vista' : 'vistas'}
              </div>
            </div>
            
            <div>
              <span className="text-primary-600 hover:text-primary-700 font-medium">
                Leer más
              </span>
            </div>
          </div>
        </div>
      </Link>
    </div>
  )
}

import PropTypes from 'prop-types'

PostCard.propTypes = {
  post: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
    title: PropTypes.string,
    content: PropTypes.string,
    createdAt: PropTypes.string,
    author: PropTypes.shape({
      username: PropTypes.string,
      profileImage: PropTypes.string,
    }),
    images: PropTypes.arrayOf(
      PropTypes.shape({
        id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        url: PropTypes.string,
      })
    ),
    commentCount: PropTypes.number,
    viewCount: PropTypes.number,
  }).isRequired,
}

export default PostCard