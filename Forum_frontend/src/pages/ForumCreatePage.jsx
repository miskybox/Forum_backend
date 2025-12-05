// Archivo: src/pages/ForumCreatePage.jsx
import { Link } from 'react-router-dom'
import ForumForm from '../components/forums/ForumForm'

const ForumCreatePage = () => {
  return (
    <div className="bg-neutral-50 py-8 sm:py-12">
      <div className="container px-4 sm:px-6 lg:px-8 mx-auto">
        <div className="mb-8">
          <div className="flex items-center justify-between">
            <h1 className="text-2xl sm:text-3xl font-bold text-neutral-800">
              Crear nuevo foro
            </h1>
            
            <Link 
              to="/forums"
              className="text-primary-600 hover:text-primary-700 text-sm font-medium"
            >
              ← Volver a los foros
            </Link>
          </div>
          <p className="mt-2 text-neutral-600">
            Crea un nuevo foro para compartir experiencias e información sobre un destino o tema de viaje.
          </p>
        </div>
        
        <div className="bg-white rounded-lg shadow-sm p-6">
          <ForumForm />
        </div>
      </div>
    </div>
  )
}

export default ForumCreatePage