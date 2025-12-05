// Archivo: src/components/forums/ForumSearch.jsx
import { useState } from 'react'
import PropTypes from 'prop-types'

const ForumSearch = ({ onSearch, initialValue = '' }) => {
  const [searchTerm, setSearchTerm] = useState(initialValue)

  const handleSubmit = (e) => {
    e.preventDefault()
    onSearch(searchTerm.trim())
  }

  return (
    <form onSubmit={handleSubmit} className="w-full max-w-md">
      <div className="relative">
        <input
          type="text"
          className="input pr-10 w-full"
          placeholder="Buscar foros..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          aria-label="Buscar foros"
        />
        <button
          type="submit"
          className="absolute inset-y-0 right-0 pr-3 flex items-center"
          aria-label="Buscar"
        >
          <svg 
            xmlns="http://www.w3.org/2000/svg" 
            className="h-5 w-5 text-neutral-500 hover:text-primary-600" 
            fill="none" 
            viewBox="0 0 24 24" 
            stroke="currentColor"
          >
            <path 
              strokeLinecap="round" 
              strokeLinejoin="round" 
              strokeWidth={2} 
              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" 
            />
          </svg>
        </button>
      </div>
    </form>
  )
}
ForumSearch.propTypes = {
  onSearch: PropTypes.func.isRequired,
  initialValue: PropTypes.string
}

export default ForumSearch