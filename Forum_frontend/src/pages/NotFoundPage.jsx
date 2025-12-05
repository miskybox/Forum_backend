// Archivo: src/pages/NotFoundPage.jsx
import { Link } from 'react-router-dom'

const NotFoundPage = () => {
  return (
    <div className="bg-neutral-50 py-16 md:py-24 min-h-[70vh] flex items-center">
      <div className="container px-4 sm:px-6 lg:px-8 mx-auto text-center">
        <div className="max-w-md mx-auto">
          <h1 className="text-9xl font-bold text-primary-500 mb-2">404</h1>
          <h2 className="text-2xl md:text-3xl font-bold text-neutral-800 mb-4">
            Página no encontrada
          </h2>
          <p className="text-neutral-600 mb-8">
            Lo sentimos, la página que estás buscando no existe o ha sido movida.
          </p>
          <div className="flex flex-col md:flex-row space-y-4 md:space-y-0 md:space-x-4 justify-center">
            <Link 
              to="/"
              className="btn btn-primary"
            >
              Volver a la página principal
            </Link>
            <Link 
              to="/forums"
              className="btn btn-outline"
            >
              Explorar foros
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}

export default NotFoundPage