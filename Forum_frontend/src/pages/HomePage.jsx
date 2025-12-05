// Archivo: src/pages/HomePage.jsx
import { Link } from 'react-router-dom'
import { useState, useEffect } from 'react'
import CategoryList from '../components/categories/CategoryList'
import ForumList from '../components/forums/ForumList'
import forumService from '../services/forumService'

const HomePage = () => {
  const [recentForums, setRecentForums] = useState([])
  const [loading, setLoading] = useState(true)
  
  useEffect(() => {
    const fetchRecentForums = async () => {
      try {
        const response = await forumService.getAllForums(0, 3)
        setRecentForums(response.content || response)
      } catch (error) {
        console.error("Error al cargar los foros recientes:", error)
      } finally {
        setLoading(false)
      }
    }
    
    fetchRecentForums()
  }, [])
  
  return (
    <div>
      {/* Hero section */}
      <section className="relative bg-primary-700 text-white overflow-hidden">
        <div className="absolute inset-0 z-0">
          <img 
            src="/src/assets/logo.svg" 
            alt="Viajeros por el mundo" 
            className="w-full h-full object-cover opacity-30"
          />
          <div className="absolute inset-0 bg-gradient-to-r from-primary-900/90 to-primary-700/70"></div>
        </div>
        
        <div className="container relative z-10 py-16 md:py-24 lg:py-32 px-4 sm:px-6 lg:px-8">
          <div className="max-w-3xl">
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold mb-6">
              Comparte tus aventuras por el mundo
            </h1>
            <p className="text-xl md:text-2xl mb-8 text-white/90">
              Únete a la comunidad de viajeros, descubre nuevos destinos y comparte tus experiencias con personas de todo el mundo.
            </p>
            <div className="flex flex-wrap gap-4">
              <Link to="/categories" className="btn bg-white text-primary-700 hover:bg-neutral-100 hover:text-primary-800 font-medium px-6 py-3 rounded-lg">
                Explorar destinos
              </Link>
              <Link to="/register" className="btn bg-transparent hover:bg-white/10 text-white border-2 border-white font-medium px-6 py-3 rounded-lg">
                Unirse ahora
              </Link>
            </div>
          </div>
        </div>
      </section>
      
      {/* Categorías por continente */}
      <section className="py-16 bg-neutral-50">
        <div className="container px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-neutral-800 mb-4">Explora por continentes</h2>
            <p className="text-lg text-neutral-600 max-w-2xl mx-auto">
              Descubre foros y conversaciones organizados por regiones del mundo. Encuentra información sobre destinos específicos o comparte tus propias experiencias.
            </p>
          </div>
          
          <CategoryList />
          
          <div className="text-center mt-8">
            <Link to="/categories" className="btn btn-primary">
              Ver todos los continentes
            </Link>
          </div>
        </div>
      </section>
      
      {/* Foros recientes */}
      <section className="py-16 bg-white">
        <div className="container px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-neutral-800 mb-4">Foros recientes</h2>
            <p className="text-lg text-neutral-600 max-w-2xl mx-auto">
              Echa un vistazo a las conversaciones más recientes en nuestra comunidad. Únete a las discusiones o inicia tu propio tema.
            </p>
          </div>
          
          {loading ? (
            <div className="flex justify-center items-center py-20">
              <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-600"></div>
            </div>
          ) : (
            <>
              {recentForums.length > 0 ? (
                <div className="space-y-6">
                  <ForumList />
                </div>
              ) : (
                <div className="text-center py-10 bg-neutral-50 rounded-lg">
                  <p className="text-neutral-600 mb-4">No hay foros disponibles aún.</p>
                  <Link to="/forums/create" className="btn btn-primary">
                    Crear el primer foro
                  </Link>
                </div>
              )}
              
              <div className="text-center mt-8">
                <Link to="/forums" className="btn btn-primary">
                  Ver todos los foros
                </Link>
              </div>
            </>
          )}
        </div>
      </section>
      
      {/* Call to action */}
      <section className="py-16 bg-secondary-700 text-white">
        <div className="container px-4 sm:px-6 lg:px-8">
          <div className="max-w-3xl mx-auto text-center">
            <h2 className="text-3xl font-bold mb-6">¿Listo para compartir tus experiencias de viaje?</h2>
            <p className="text-lg mb-8 text-white/90">
              Únete a nuestra comunidad global de viajeros. Comparte consejos, pide recomendaciones o simplemente inspírate para tu próxima aventura.
            </p>
            <div className="flex flex-wrap justify-center gap-4">
              <Link to="/register" className="btn bg-white text-secondary-700 hover:bg-neutral-100 hover:text-secondary-800 font-medium px-6 py-3 rounded-lg">
                Crear una cuenta
              </Link>
              <Link to="/login" className="btn bg-transparent hover:bg-white/10 text-white border-2 border-white font-medium px-6 py-3 rounded-lg">
                Iniciar sesión
              </Link>
            </div>
          </div>
        </div>
      </section>
    </div>
  )
}

export default HomePage