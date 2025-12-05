import api from '../utils/api'

const blogService = {
  // ============ CATEGORÍAS ============

  getAllCategories: async () => {
    const response = await api.get('/blog/categories')
    return response.data
  },

  getCategoryById: async (id) => {
    const response = await api.get(`/blog/categories/${id}`)
    return response.data
  },

  getCategoryBySlug: async (slug) => {
    const response = await api.get(`/blog/categories/slug/${slug}`)
    return response.data
  },

  createCategory: async (categoryData) => {
    const response = await api.post('/blog/categories', categoryData)
    return response.data
  },

  updateCategory: async (id, categoryData) => {
    const response = await api.put(`/blog/categories/${id}`, categoryData)
    return response.data
  },

  deleteCategory: async (id) => {
    await api.delete(`/blog/categories/${id}`)
  },

  // ============ POSTS ============

  getAllPosts: async (page = 0, size = 10) => {
    const response = await api.get(`/blog/posts?page=${page}&size=${size}`)
    return response.data
  },

  getPostById: async (id) => {
    const response = await api.get(`/blog/posts/${id}`)
    return response.data
  },

  getPostBySlug: async (slug) => {
    const response = await api.get(`/blog/posts/slug/${slug}`)
    return response.data
  },

  getPostsByCategory: async (categorySlug, page = 0, size = 10) => {
    const response = await api.get(`/blog/posts/category/${categorySlug}?page=${page}&size=${size}`)
    return response.data
  },

  searchPosts: async (query, page = 0, size = 10) => {
    const response = await api.get(`/blog/posts/search?q=${encodeURIComponent(query)}&page=${page}&size=${size}`)
    return response.data
  },

  getFeaturedPosts: async () => {
    const response = await api.get('/blog/posts/featured')
    return response.data
  },

  getPopularPosts: async (limit = 5) => {
    const response = await api.get(`/blog/posts/popular?limit=${limit}`)
    return response.data
  },

  getMostLikedPosts: async (limit = 5) => {
    const response = await api.get(`/blog/posts/most-liked?limit=${limit}`)
    return response.data
  },

  getPostsByAuthor: async (username, page = 0, size = 10) => {
    const response = await api.get(`/blog/posts/author/${username}?page=${page}&size=${size}`)
    return response.data
  },

  createPost: async (postData) => {
    const response = await api.post('/blog/posts', postData)
    return response.data
  },

  updatePost: async (id, postData) => {
    const response = await api.put(`/blog/posts/${id}`, postData)
    return response.data
  },

  deletePost: async (id) => {
    await api.delete(`/blog/posts/${id}`)
  },

  likePost: async (id) => {
    await api.post(`/blog/posts/${id}/like`)
  },

  unlikePost: async (id) => {
    await api.delete(`/blog/posts/${id}/like`)
  }
}

export default blogService
