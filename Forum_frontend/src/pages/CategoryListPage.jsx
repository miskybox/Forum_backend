// Archivo: src/pages/CategoryListPage.jsx
import { Link } from 'react-router-dom'
import CategoryList from '../components/categories/CategoryList'

const CategoryListPage = () => {
  return (
    <div className="bg-neutral-50 py-8 sm:py-12">
      <div className="container px-4 sm:px-6 lg:px-8 mx-auto">
        <div className="text-center mb-10">
          <h1 className="text-3xl font-bold text-neutral-800 mb-4">Explora por continentes</h1>
          <p className="text-lg text-neutral-600 max-w-2xl mx-auto">
            Descubre foros y conversaciones organizados por regiones del mundo. Encuentra información sobre destinos específicos o comparte tus propias experiencias.
          </p>
        </div>
        
        <CategoryList />
        
        <div className="mt-12 text-center">
          <h2 className="text-2xl font-bold text-neutral-800 mb-4">¿No encuentras lo que buscas?</h2>
          <p className="text-neutral-600 mb-6">
            Si tienes alguna sugerencia para nuevas categorías o regiones, no dudes en contactarnos.
          </p>
          <Link to="/forums" className="btn btn-primary">
            Ver todos los foros
          </Link>
        </div>
      </div>
    </div>
  )
}

export default CategoryListPage