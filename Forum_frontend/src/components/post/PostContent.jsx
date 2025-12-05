import { Link } from 'react-router-dom'
import PropTypes from 'prop-types'

const PostContent = ({ post }) => {
  const formatDate = (dateString) => {
    if (!dateString) return 'Fecha desconocida'
    const date = new Date(dateString)
    return date.toLocaleDateString('es-ES', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  return (
    <div>
      <div className="px-6 py-4">
        <h1 className="text-3xl font-bold mb-2">{post.title}</h1>
        
        <div className="flex flex-wrap items-center text-sm text-gray-500 mb-4">
          <span className="mr-4">
            Publicado: {formatDate(post.createdAt)}
          </span>
          {post.updatedAt !== post.createdAt && (
            <span className="mr-4">
              Actualizado: {formatDate(post.updatedAt)}
            </span>
          )}
          <span className="mr-4">
            <svg className="w-4 h-4 inline mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
            </svg>
            {post.viewCount || 0} visitas
          </span>
          <Link to={`/forums/${post.forumId}`} className="text-primary-600 hover:text-primary-800">
            Volver al foro
          </Link>
        </div>
        
        {post.tags && post.tags.length > 0 && (
          <div className="flex flex-wrap gap-2 mb-4">
            {post.tags.map(tag => (
              <span 
                key={tag} 
                className="bg-primary-100 text-primary-800 px-3 py-1 rounded-full text-sm"
              >
                {tag}
              </span>
            ))}
          </div>
        )}
      </div>
      
      <div className="px-6 py-4 border-t border-gray-100">
        <div className="prose max-w-none">
   
          {post.content.split('\n\n').map((paragraph) => {
            const key = paragraph.slice(0, 20) + paragraph.length;
            return <p key={key}>{paragraph}</p>;
          })}
        </div>
      </div>
    </div>
  )
}
PostContent.propTypes = {
  post: PropTypes.shape({
    title: PropTypes.string,
    createdAt: PropTypes.string,
    updatedAt: PropTypes.string,
    viewCount: PropTypes.number,
    forumId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    tags: PropTypes.arrayOf(PropTypes.string),
    content: PropTypes.string
  }).isRequired
}

export default PostContent
