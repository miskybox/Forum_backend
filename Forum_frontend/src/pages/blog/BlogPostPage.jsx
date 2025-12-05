import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import BlogCategoryBadge from '../../components/blog/BlogCategoryBadge'
import blogService from '../../services/blogService'
import useAuth from '../../hooks/useAuth'
import toast from 'react-hot-toast'

const BlogPostPage = () => {
  const { slug } = useParams()
  const navigate = useNavigate()
  const { isAuthenticated, currentUser } = useAuth()
  const [post, setPost] = useState(null)
  const [relatedPosts, setRelatedPosts] = useState([])
  const [loading, setLoading] = useState(true)
  const [liked, setLiked] = useState(false)

  useEffect(() => {
    loadPost()
  }, [slug])

  const loadPost = async () => {
    try {
      setLoading(true)
      const postData = await blogService.getPostBySlug(slug)
      setPost(postData)

      // Cargar posts relacionados de la misma categoría
      if (postData.category) {
        const related = await blogService.getPostsByCategory(postData.category.slug, 0, 3)
        setRelatedPosts((related.content || []).filter(p => p.id !== postData.id))
      }
    } catch (error) {
      console.error('Error loading post:', error)
      toast.error('Error al cargar el artículo')
      navigate('/blog')
    } finally {
      setLoading(false)
    }
  }

  const handleLike = async () => {
    if (!isAuthenticated) {
      toast.error('Debes iniciar sesión para dar like')
      return
    }

    try {
      if (liked) {
        await blogService.unlikePost(post.id)
        setPost(prev => ({ ...prev, likes: prev.likes - 1 }))
        setLiked(false)
      } else {
        await blogService.likePost(post.id)
        setPost(prev => ({ ...prev, likes: prev.likes + 1 }))
        setLiked(true)
      }
    } catch (error) {
      console.error('Error toggling like:', error)
      toast.error('Error al actualizar el like')
    }
  }

  const handleShare = () => {
    if (navigator.share) {
      navigator.share({
        title: post.title,
        text: post.excerpt,
        url: window.location.href
      })
    } else {
      navigator.clipboard.writeText(window.location.href)
      toast.success('Link copiado al portapapeles')
    }
  }

  const formatDate = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="container mx-auto px-4 py-12">
          <div className="max-w-4xl mx-auto">
            <div className="animate-pulse space-y-4">
              <div className="h-8 bg-gray-200 rounded w-3/4"></div>
              <div className="h-64 bg-gray-200 rounded"></div>
              <div className="h-4 bg-gray-200 rounded"></div>
              <div className="h-4 bg-gray-200 rounded"></div>
              <div className="h-4 bg-gray-200 rounded w-5/6"></div>
            </div>
          </div>
        </div>
      </div>
    )
  }

  if (!post) return null

  const canEdit = isAuthenticated && (
    currentUser?.username === post.authorUsername ||
    currentUser?.roles?.some(role => role.name === 'ROLE_ADMIN')
  )

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Back button */}
      <div className="bg-white border-b">
        <div className="container mx-auto px-4 py-4">
          <Link to="/blog" className="inline-flex items-center text-primary-600 hover:text-primary-700">
            <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
            Volver al blog
          </Link>
        </div>
      </div>

      <article className="container mx-auto px-4 py-12">
        <div className="max-w-4xl mx-auto">
          {/* Header */}
          <header className="mb-8">
            {post.category && (
              <div className="mb-4">
                <BlogCategoryBadge category={post.category} />
              </div>
            )}

            <h1 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
              {post.title}
            </h1>

            <div className="flex flex-wrap items-center gap-4 text-gray-600 mb-6">
              <span className="flex items-center">
                Por <strong className="ml-1 text-gray-900">{post.authorUsername}</strong>
              </span>
              <span>•</span>
              <span>{formatDate(post.publishedAt)}</span>
              {post.readTime && (
                <>
                  <span>•</span>
                  <span>{post.readTime} min de lectura</span>
                </>
              )}
              {post.views && (
                <>
                  <span>•</span>
                  <span>{post.views} vistas</span>
                </>
              )}
            </div>

            {/* Actions */}
            <div className="flex items-center gap-4">
              <button
                onClick={handleLike}
                className={`flex items-center gap-2 px-4 py-2 rounded-lg transition-colors ${
                  liked
                    ? 'bg-red-100 text-red-600'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                <svg className="w-5 h-5" fill={liked ? 'currentColor' : 'none'} stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
                {post.likes}
              </button>

              <button
                onClick={handleShare}
                className="flex items-center gap-2 px-4 py-2 rounded-lg bg-gray-100 text-gray-700 hover:bg-gray-200 transition-colors"
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z" />
                </svg>
                Compartir
              </button>

              {canEdit && (
                <Link
                  to={`/blog/edit/${post.id}`}
                  className="flex items-center gap-2 px-4 py-2 rounded-lg bg-primary-600 text-white hover:bg-primary-700 transition-colors ml-auto"
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                  Editar
                </Link>
              )}
            </div>
          </header>

          {/* Featured Image */}
          {post.featuredImage && (
            <div className="mb-8 rounded-lg overflow-hidden">
              <img
                src={post.featuredImage}
                alt={post.title}
                className="w-full h-auto"
              />
            </div>
          )}

          {/* Content */}
          <div className="prose prose-lg max-w-none mb-12">
            <div className="whitespace-pre-wrap text-gray-700 leading-relaxed">
              {post.content}
            </div>
          </div>

          {/* Tags */}
          {post.tags && post.tags.length > 0 && (
            <div className="flex flex-wrap gap-2 mb-8">
              {post.tags.map(tag => (
                <span key={tag.id} className="px-3 py-1 bg-gray-100 text-gray-700 rounded-full text-sm">
                  #{tag.name}
                </span>
              ))}
            </div>
          )}

          {/* Related Posts */}
          {relatedPosts.length > 0 && (
            <section className="mt-16 pt-8 border-t">
              <h2 className="text-2xl font-bold text-gray-900 mb-6">
                Artículos Relacionados
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                {relatedPosts.map(relatedPost => (
                  <Link
                    key={relatedPost.id}
                    to={`/blog/${relatedPost.slug}`}
                    className="group"
                  >
                    <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-xl transition-shadow">
                      {relatedPost.featuredImage && (
                        <img
                          src={relatedPost.featuredImage}
                          alt={relatedPost.title}
                          className="w-full h-40 object-cover group-hover:scale-105 transition-transform"
                        />
                      )}
                      <div className="p-4">
                        <h3 className="font-semibold text-gray-900 group-hover:text-primary-600 transition-colors line-clamp-2">
                          {relatedPost.title}
                        </h3>
                        {relatedPost.excerpt && (
                          <p className="text-sm text-gray-600 mt-2 line-clamp-2">
                            {relatedPost.excerpt}
                          </p>
                        )}
                      </div>
                    </div>
                  </Link>
                ))}
              </div>
            </section>
          )}
        </div>
      </article>
    </div>
  )
}

export default BlogPostPage
