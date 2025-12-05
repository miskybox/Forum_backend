import { Link } from 'react-router-dom'
import { formatDistanceToNow } from 'date-fns'
import { es } from 'date-fns/locale'

const ForumCard = ({ forum }) => {
  const formatRelativeDate = (dateString) => {
    const date = new Date(dateString)
    return formatDistanceToNow(date, { addSuffix: true, locale: es })
  }

  return (
    <div className="card overflow-hidden hover:shadow-md transition-all">
      <div className="md:flex">
        <div className="md:w-1/3 bg-neutral-100">
          {forum.imageUrl ? (
            <Link to={`/forums/${forum.id}`} className="block h-full">
              <img 
                src={forum.imageUrl} 
                alt={forum.title} 
                className="w-full h-full object-cover aspect-video md:aspect-auto"
              />
            </Link>
          ) : (
            <div className="flex items-center justify-center h-full min-h-[160px] bg-primary-50">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 text-primary-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M17 8h2a2 2 0 012 2v6a2 2 0 01-2 2h-2v4l-4-4H9a1.994 1.994 0 01-1.414-.586m0 0L11 14h4a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2v4l.586-.586z" />
              </svg>
            </div>
          )}
        </div>
        
        <div className="md:w-2/3 p-4 flex flex-col">
          <div className="flex-grow">
            <h3 className="text-lg font-semibold mb-2">
              <Link to={`/forums/${forum.id}`} className="hover:text-primary-700 text-neutral-800">
                {forum.title}
              </Link>
            </h3>
            
            <p className="text-neutral-600 mb-4 line-clamp-2">{forum.description}</p>
            
            {forum.tags && forum.tags.length > 0 && (
              <div className="flex flex-wrap gap-2 mb-3">
                {forum.tags.map(tag => (
                  <span key={tag.id} className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-secondary-100 text-secondary-800">
                    {tag.name}
                  </span>
                ))}
              </div>
            )}
          </div>
 
          <div className="mt-2 pt-2 border-t border-neutral-100 flex justify-between items-center text-xs text-neutral-500">
            <div className="flex items-center">
              <div className="flex items-center mr-4">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z" />
                </svg>
                {forum.postCount || 0} {forum.postCount === 1 ? 'publicación' : 'publicaciones'}
              </div>
              
              <div className="flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                {forum.viewCount || 0} {forum.viewCount === 1 ? 'vista' : 'vistas'}
              </div>
            </div>
            
            <div className="flex items-center">
              <div className="flex items-center space-x-1">
                <div className="w-5 h-5 bg-primary-500 rounded-full flex items-center justify-center text-white text-xs overflow-hidden">
                  {forum.creator?.profileImage ? (
                    <img 
                      src={forum.creator.profileImage} 
                      alt={forum.creator.username}
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    forum.creator?.username?.charAt(0)?.toUpperCase() || 'U'
                  )}
                </div>
                <span>{forum.creator?.username || 'Usuario'}</span>
              </div>
              <span className="mx-2">•</span>
              <time dateTime={forum.createdAt}>
                {formatRelativeDate(forum.createdAt)}
              </time>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

import PropTypes from 'prop-types'

ForumCard.propTypes = {
  forum: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
    imageUrl: PropTypes.string,
    title: PropTypes.string.isRequired,
    description: PropTypes.string,
    tags: PropTypes.arrayOf(
      PropTypes.shape({
        id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
        name: PropTypes.string.isRequired,
      })
    ),
    postCount: PropTypes.number,
    viewCount: PropTypes.number,
    creator: PropTypes.shape({
      username: PropTypes.string,
      profileImage: PropTypes.string,
    }),
    createdAt: PropTypes.string.isRequired,
  }).isRequired,
}

export default ForumCard