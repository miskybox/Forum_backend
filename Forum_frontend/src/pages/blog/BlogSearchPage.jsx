import { useState, useEffect } from 'react'
import { useSearchParams, Link } from 'react-router-dom'
import BlogGrid from '../../components/blog/BlogGrid'
import blogService from '../../services/blogService'
import toast from 'react-hot-toast'

const BlogSearchPage = () => {
  const [searchParams] = useSearchParams()
  const query = searchParams.get('q') || ''
  const [posts, setPosts] = useState([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  useEffect(() => {
    if (query) {
      loadPosts()
    }
  }, [query, page])

  const loadPosts = async () => {
    try {
      setLoading(true)
      const postsData = await blogService.searchPosts(query, page, 9)
      setPosts(postsData.content || [])
      setTotalPages(postsData.totalPages || 0)
    } catch (error) {
      console.error('Error searching posts:', error)
      toast.error('Error al buscar artículos')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white border-b">
        <div className="container mx-auto px-4 py-8">
          <Link to="/blog" className="inline-flex items-center text-primary-600 hover:text-primary-700 mb-4">
            <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
            Volver al blog
          </Link>

          <h1 className="text-3xl md:text-4xl font-bold text-gray-900">
            Resultados de búsqueda
          </h1>
          <p className="text-lg text-gray-600 mt-2">
            Buscando: <span className="font-semibold text-gray-900">"{query}"</span>
          </p>
          {!loading && posts.length > 0 && (
            <p className="text-sm text-gray-500 mt-2">
              {posts.length} resultado{posts.length !== 1 ? 's' : ''} encontrado{posts.length !== 1 ? 's' : ''}
            </p>
          )}
        </div>
      </div>

      {/* Results */}
      <div className="container mx-auto px-4 py-12">
        {!loading && posts.length === 0 && (
          <div className="text-center py-12">
            <svg className="mx-auto h-16 w-16 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            <h3 className="text-lg font-medium text-gray-900 mb-2">
              No se encontraron resultados
            </h3>
            <p className="text-gray-500 mb-4">
              Intenta con otras palabras clave
            </p>
            <Link
              to="/blog"
              className="inline-flex items-center px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
            >
              Ver todos los artículos
            </Link>
          </div>
        )}

        <BlogGrid posts={posts} loading={loading} />

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex justify-center gap-2 mt-12">
            <button
              onClick={() => setPage(p => Math.max(0, p - 1))}
              disabled={page === 0}
              className="px-4 py-2 rounded-lg bg-white border border-gray-300 text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Anterior
            </button>
            <span className="px-4 py-2 text-gray-700">
              Página {page + 1} de {totalPages}
            </span>
            <button
              onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
              disabled={page >= totalPages - 1}
              className="px-4 py-2 rounded-lg bg-white border border-gray-300 text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Siguiente
            </button>
          </div>
        )}
      </div>
    </div>
  )
}

export default BlogSearchPage
