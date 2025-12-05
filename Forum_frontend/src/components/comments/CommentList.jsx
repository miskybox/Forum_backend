import { useState } from 'react'
import PropTypes from 'prop-types'
import CommentItem from './CommentItems'

const CommentList = ({ comments, onCommentDeleted }) => {
  const [sortOrder, setSortOrder] = useState('newest') 
  
  const sortedComments = [...comments].sort((a, b) => {
    const dateA = new Date(a.createdAt)
    const dateB = new Date(b.createdAt)
    return sortOrder === 'newest' ? dateB - dateA : dateA - dateB
  })

  return (
    <div className="mt-6">
      <div className="flex justify-end mb-4">
        <div className="text-sm text-gray-600">
          <span>Ordenar por:</span>
          <button
            onClick={() => setSortOrder('newest')}
            className={`ml-2 px-2 py-1 rounded ${
              sortOrder === 'newest' 
                ? 'bg-primary-100 text-primary-800' 
                : 'hover:bg-gray-100'
            }`}
          >
            Más recientes
          </button>
          <button
            onClick={() => setSortOrder('oldest')}
            className={`ml-2 px-2 py-1 rounded ${
              sortOrder === 'oldest' 
                ? 'bg-primary-100 text-primary-800' 
                : 'hover:bg-gray-100'
            }`}
          >
            Más antiguos
          </button>
        </div>
      </div>
      
      <div className="space-y-4">
        {sortedComments.map(comment => (
          <CommentItem 
            key={comment.id} 
            comment={comment} 
            onDeleted={onCommentDeleted}
          />
        ))}
      </div>
    </div>
  )
}
CommentList.propTypes = {
  comments: PropTypes.arrayOf(PropTypes.object).isRequired,
  onCommentDeleted: PropTypes.func.isRequired,
}

export default CommentList
