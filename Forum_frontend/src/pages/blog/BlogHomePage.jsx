import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import BlogHero from '../../components/blog/BlogHero'
import BlogGrid from '../../components/blog/BlogGrid'
import BlogCard from '../../components/blog/BlogCard'
import blogService from '../../services/blogService'
import toast from 'react-hot-toast'

const BlogHomePage = () => {
  const [posts, setPosts] = useState([])
  const [featuredPosts, setFeaturedPosts] = useState([])
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  useEffect(() => {
    loadData()
  }, [page])

  const loadData = async () => {
    try {
      setLoading(true)
      const [postsData, featuredData, categoriesData] = await Promise.all([
        blogService.getAllPosts(page, 9),
        page === 0 ? blogService.getFeaturedPosts() : Promise.resolve([]),
        page === 0 ? blogService.getAllCategories() : Promise.resolve([])
      ])

      setPosts(postsData.content || [])
      setTotalPages(postsData.totalPages || 0)

      if (page === 0) {
        setFeaturedPosts(featuredData || [])
        setCategories(categoriesData || [])
      }
    } catch (error) {
      console.error('Error loading blog data:', error)
      toast.error('Error al cargar los artículos')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <BlogHero />

      <div className="container mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {/* Featured Posts */}
        {featuredPosts.length > 0 && page === 0 && (
          <section className="mb-16">
            <h2 className="text-3xl font-bold text-gray-900 mb-8">
              ⭐ Artículos Destacados
            </h2>
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {featuredPosts.slice(0, 2).map((post) => (
                <BlogCard key={post.id} post={post} />
              ))}
            </div>
          </section>
        )}

        {/* Categories */}
        {categories.length > 0 && page === 0 && (
          <section className="mb-16">
            <h2 className="text-3xl font-bold text-gray-900 mb-8">
              📚 Categorías
            </h2>
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
              {categories.map((category) => (
                <Link
                  key={category.id}
                  to={`/blog/category/${category.slug}`}
                  className="bg-white rounded-lg shadow-md p-6 hover:shadow-xl transition-shadow text-center group"
                  style={{
                    borderTop: `4px solid ${category.color || '#059669'}`
                  }}
                >
                  <div className="text-4xl mb-3 group-hover:scale-110 transition-transform">
                    {category.icon || '📁'}
                  </div>
                  <h3 className="font-semibold text-gray-900 mb-1">
                    {category.name}
                  </h3>
                  {category.description && (
                    <p className="text-sm text-gray-500 line-clamp-2">
                      {category.description}
                    </p>
                  )}
                </Link>
              ))}
            </div>
          </section>
        )}

        {/* All Posts */}
        <section>
          <h2 className="text-3xl font-bold text-gray-900 mb-8">
            ✨ Últimas Publicaciones
          </h2>
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
        </section>
      </div>
    </div>
  )
}

export default BlogHomePage
