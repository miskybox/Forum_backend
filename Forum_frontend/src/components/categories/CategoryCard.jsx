// Archivo: src/components/categories/CategoryCard.jsx
import { Link } from 'react-router-dom'
import PropTypes from 'prop-types';

const CategoryCard = ({ category }) => {
  return (
    <Link 
      to={`/forums/category/${category.id}`}
      className="group"
    >
      <div className="card overflow-hidden transition-all group-hover:shadow-lg">
        <div className="aspect-video relative bg-neutral-200 overflow-hidden">
          {category.imageUrl ? (
            <img 
              src={category.imageUrl} 
              alt={category.name} 
              className="w-full h-full object-cover transition-transform group-hover:scale-105"
            />
          ) : (
            <div className="flex items-center justify-center h-full bg-primary-100 text-primary-700">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-16 w-16" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
          )}
          <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
          <div className="absolute bottom-0 left-0 right-0 p-4">
            <h3 className="text-white text-xl font-bold">{category.name}</h3>
          </div>
        </div>
        <div className="p-4">
          <div className="text-sm text-neutral-600 mb-2">
            {category.forumCount || 0} {category.forumCount === 1 ? 'foro' : 'foros'} disponibles
          </div>
          <p className="text-neutral-700 line-clamp-2">{category.description}</p>
        </div>
      </div>
    </Link>
  )
}
CategoryCard.propTypes = {
  category: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
    name: PropTypes.string.isRequired,
    imageUrl: PropTypes.string,
    forumCount: PropTypes.number,
    description: PropTypes.string,
  }).isRequired,
};

export default CategoryCard
