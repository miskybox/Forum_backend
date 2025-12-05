import { Link } from 'react-router-dom'

const BlogCategoryBadge = ({ category, size = 'sm' }) => {
  if (!category) return null

  const sizeClasses = {
    xs: 'text-xs px-2 py-0.5',
    sm: 'text-sm px-3 py-1',
    md: 'text-base px-4 py-2'
  }

  return (
    <Link
      to={`/blog/category/${category.slug || category.categorySlug}`}
      className={`inline-flex items-center gap-1 rounded-full font-medium transition-all hover:opacity-80 ${sizeClasses[size]}`}
      style={{
        backgroundColor: category.color ? `${category.color}20` : '#f3f4f6',
        color: category.color || '#374151'
      }}
    >
      {(category.icon || category.categoryIcon) && (
        <span className="text-lg">{category.icon || category.categoryIcon}</span>
      )}
      <span>{category.name || category.categoryName}</span>
    </Link>
  )
}

export default BlogCategoryBadge
