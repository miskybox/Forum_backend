import { Link } from 'react-router-dom'
import BlogCategoryBadge from './BlogCategoryBadge'

const BlogCard = ({ post }) => {
  const formatDate = (dateString) => {
    if (!dateString) return ''
    const date = new Date(dateString)
    return date.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
  }

  const formatViews = (views) => {
    if (views >= 1000) {
      return `${(views / 1000).toFixed(1)}k`
    }
    return views
  }

  return (
    <article className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-xl transition-shadow duration-300">
      {/* Imagen */}
      <Link to={`/blog/${post.slug}`} className="block relative h-48 overflow-hidden group">
        {post.featuredImage ? (
          <img
            src={post.featuredImage}
            alt={post.title}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
          />
        ) : (
          <div className="w-full h-full bg-gradient-to-br from-primary-500 to-primary-700 flex items-center justify-center">
            <span className="text-6xl text-white/30">📝</span>
          </div>
        )}
        {post.featured && (
          <span className="absolute top-2 left-2 bg-amber-500 text-white px-3 py-1 rounded-full text-xs font-semibold">
            ⭐ Destacado
          </span>
        )}
      </Link>

      {/* Contenido */}
      <div className="p-6">
        {/* Categoría */}
        {(post.category || post.categoryName) && (
          <div className="mb-3">
            <BlogCategoryBadge
              category={{
                name: post.category?.name || post.categoryName,
                slug: post.category?.slug || post.categorySlug,
                icon: post.category?.icon || post.categoryIcon,
                color: post.category?.color || post.categoryColor
              }}
              size="sm"
            />
          </div>
        )}

        {/* Título */}
        <h3 className="text-xl font-bold text-gray-900 mb-2 hover:text-primary-600 transition-colors line-clamp-2">
          <Link to={`/blog/${post.slug}`}>
            {post.title}
          </Link>
        </h3>

        {/* Excerpt */}
        {post.excerpt && (
          <p className="text-gray-600 mb-4 line-clamp-3">
            {post.excerpt}
          </p>
        )}

        {/* Metadata */}
        <div className="flex items-center justify-between text-sm text-gray-500 pt-4 border-t border-gray-100">
          <div className="flex items-center space-x-4">
            {post.readTime && (
              <span className="flex items-center gap-1">
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                {post.readTime} min
              </span>
            )}
            {post.views !== undefined && (
              <span className="flex items-center gap-1">
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                {formatViews(post.views)}
              </span>
            )}
            {post.likes !== undefined && post.likes > 0 && (
              <span className="flex items-center gap-1">
                <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
                </svg>
                {post.likes}
              </span>
            )}
          </div>

          {post.publishedAt && (
            <span className="text-gray-400">
              {formatDate(post.publishedAt)}
            </span>
          )}
        </div>

        {/* Autor */}
        {post.authorUsername && (
          <div className="mt-4 text-sm text-gray-500">
            Por <span className="font-medium text-gray-700">{post.authorUsername}</span>
          </div>
        )}
      </div>
    </article>
  )
}

export default BlogCard
