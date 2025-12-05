import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import BlogGrid from '../../components/blog/BlogGrid'
import blogService from '../../services/blogService'
import toast from 'react-hot-toast'

const BlogCategoryPage = () => {
  const { slug } = useParams()
  const [category, setCategory] = useState(null)
  const [posts, setPosts] = useState([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  useEffect(() => {
    loadCategory()
  }, [slug])

  useEffect(() => {
    loadPosts()
  }, [slug, page])

  const loadCategory = async () => {
    try {
      const categoryData = await blogService.getCategoryBySlug(slug)
      setCategory(categoryData)
    } catch (error) {
      console.error('Error loading category:', error)
      toast.error('Error al cargar la categoría')
    }
  }

  const loadPosts = async () => {
    try {
      setLoading(true)
      const postsData = await blogService.getPostsByCategory(slug, page, 9)
      setPosts(postsData.content || [])
      setTotalPages(postsData.totalPages || 0)
    } catch (error) {
      console.error('Error loading posts:', error)
      toast.error('Error al cargar los artículos')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div
        className="bg-gradient-to-r from-primary-700 to-primary-500 text-white py-16"
        style={category?.color ? {
          background: `linear-gradient(135deg, ${category.color} 0%, ${category.color}dd 100%)`
        } : {}}
      >
        <div className="container mx-auto px-4">
          <Link to="/blog" className="inline-flex items-center text-white/90 hover:text-white mb-4">
            <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
            Volver al blog
          </Link>

          {category && (
            <div className="flex items-center gap-4">
              <span className="text-6xl">{category.icon || '📁'}</span>
              <div>
                <h1 className="text-4xl md:text-5xl font-bold mb-2">
                  {category.name}
                </h1>
                {category.description && (
                  <p className="text-xl text-white/90">
                    {category.description}
                  </p>
                )}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Posts */}
      <div className="container mx-auto px-4 py-12">
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

export default BlogCategoryPage
