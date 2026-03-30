// Use relative path - nginx will proxy to backend
const BASE_URL = '/api';

const getHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {})
  };
};

interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
  error?: string;
  timestamp?: string;
}

class ApiServiceError extends Error {
  constructor(public status: number, message: string) {
    super(message);
    this.name = 'ApiServiceError';
  }
}

export const apiService = {
  // Generic GET method
  get: async <T>(endpoint: string, options: RequestInit = {}): Promise<ApiResponse<T>> => {
    const response = await fetch(`${BASE_URL}${endpoint}`, {
      ...options,
      headers: {
        ...getHeaders(),
        ...options.headers,
      },
    });

    const data = await response.json();

    if (!response.ok) {
      throw new ApiServiceError(response.status, data.error || data.message || 'API Error');
    }

    return data;
  },

  private: async <T>(endpoint: string, options: RequestInit = {}): Promise<ApiResponse<T>> => {
    const response = await fetch(`${BASE_URL}${endpoint}`, {
      ...options,
      headers: {
        ...getHeaders(),
        ...options.headers,
      },
    });

    const data = await response.json();

    if (!response.ok) {
      throw new ApiServiceError(response.status, data.error || data.message || 'API Error');
    }

    return data;
  },

  // Auth endpoints
  auth: {
    login: async (email: string, password: string) => {
      return apiService.private<any>('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
      });
    },

    register: async (data: any) => {
      return apiService.private<any>('/auth/register', {
        method: 'POST',
        body: JSON.stringify(data),
      });
    },

    getProfile: async () => {
      return apiService.private<any>('/customers/profile');
    },

    updateProfile: async (data: any) => {
      return apiService.private<any>('/customers/profile', {
        method: 'PUT',
        body: JSON.stringify(data),
      });
    },
  },

  // Product endpoints (Public)
  products: {
    getAll: async (page = 0, size = 20, filters?: any) => {
      let query = `?page=${page}&size=${size}`;
      if (filters?.category) query += `&category=${filters.category}`;
      if (filters?.search) query += `&search=${filters.search}`;
      if (filters?.minPrice) query += `&minPrice=${filters.minPrice}`;
      if (filters?.maxPrice) query += `&maxPrice=${filters.maxPrice}`;
      return apiService.get<any>(`/products${query}`);
    },

    getById: async (id: string) => {
      return apiService.get<any>(`/products/${id}`);
    },

    search: async (keyword: string) => {
      return apiService.get<any>(`/products/search?keyword=${keyword}`);
    },

    getCategories: async () => {
      return apiService.get<any>('/products/categories');
    },

    // Admin product management
    create: async (data: any) => {
      return apiService.private<any>('/admin/products', {
        method: 'POST',
        body: JSON.stringify(data),
      });
    },

    update: async (id: string, data: any) => {
      return apiService.private<any>(`/admin/products/${id}`, {
        method: 'PUT',
        body: JSON.stringify(data),
      });
    },

    delete: async (id: string) => {
      return apiService.private<any>(`/admin/products/${id}`, {
        method: 'DELETE',
      });
    },
  },

  // Cart endpoints
  cart: {
    get: async (customerId: string) => {
      return apiService.private<any>(`/cart/${customerId}`);
    },

    addItem: async (customerId: string, productId: string, quantity: number) => {
      return apiService.private<any>(`/cart/${customerId}/items`, {
        method: 'POST',
        body: JSON.stringify({ productId, quantity }),
      });
    },

    updateItem: async (customerId: string, productId: string, quantity: number) => {
      return apiService.private<any>(`/cart/${customerId}/items/${productId}`, {
        method: 'PUT',
        body: JSON.stringify({ quantity }),
      });
    },

    removeItem: async (customerId: string, productId: string) => {
      return apiService.private<any>(`/cart/${customerId}/items/${productId}`, {
        method: 'DELETE',
      });
    },

    clear: async (customerId: string) => {
      return apiService.private<any>(`/cart/${customerId}/clear`, {
        method: 'DELETE',
      });
    },
  },

  // Order endpoints
  orders: {
    create: async (data: any) => {
      return apiService.private<any>('/orders', {
        method: 'POST',
        body: JSON.stringify(data),
      });
    },

    getAll: async (page = 0, size = 50, status?: string) => {
      let query = `?page=${page}&size=${size}`;
      if (status) query += `&status=${status}`;
      return apiService.private<any>(`/admin/orders${query}`);
    },

    getByCustomer: async (customerId?: string, page = 0, size = 10) => {
      if (customerId) {
        return apiService.private<any>(`/orders/customer/${customerId}?page=${page}&size=${size}`);
      }
      return apiService.private<any>(`/orders/my-orders?page=${page}&size=${size}`);
    },

    getById: async (id: string) => {
      return apiService.private<any>(`/orders/${id}`);
    },

    getByOrderNumber: async (orderNumber: string) => {
      return apiService.private<any>(`/orders/by-number/${orderNumber}`);
    },

    updateStatus: async (id: string, status: string) => {
      return apiService.private<any>(`/admin/orders/${id}/status`, {
        method: 'PUT',
        body: JSON.stringify({ status }),
      });
    },

    cancel: async (id: string, reason: string) => {
      return apiService.private<any>(`/orders/${id}/cancel`, {
        method: 'PUT',
        body: JSON.stringify({ reason }),
      });
    },
  },

  // Payment endpoints
  payments: {
    createPaymentIntent: async (orderId: string) => {
      return apiService.private<any>(`/payments/${orderId}/create-intent`, {
        method: 'POST',
      });
    },

    confirm: async (paymentId: string, success: boolean, transactionId: string) => {
      return apiService.private<any>(`/payments/${paymentId}/process`, {
        method: 'PUT',
        body: JSON.stringify({ success, transactionId }),
      });
    },
  },

  // Analytics endpoints
  analytics: {
    getDashboard: async () => {
      return apiService.private<any>('/analytics/dashboard');
    },

    getSalesReport: async (startDate: string, endDate: string) => {
      return apiService.private<any>(`/analytics/sales?startDate=${startDate}&endDate=${endDate}`);
    },

    getInventory: async () => {
      return apiService.private<any>('/analytics/inventory');
    },

    // AI endpoints
    runForecasting: async () => {
      return apiService.private<any>('/analytics/ai/forecast', {
        method: 'POST',
      });
    },

    getCustomerSegmentation: async () => {
      return apiService.private<any>('/analytics/ai/customers/segmentation');
    },

    getPriceOptimization: async () => {
      return apiService.private<any>('/analytics/ai/pricing/optimization');
    },
  },

  // Recommendations endpoints
  recommendations: {
    getTrending: async (limit = 10) => {
      return apiService.private<any>(`/recommendations/trending?limit=${limit}`);
    },

    getPersonal: async (customerId: string, limit = 10) => {
      return apiService.private<any>(`/recommendations/personal/${customerId}?limit=${limit}`);
    },

    getFrequentlyBoughtTogether: async (productId: string, limit = 5) => {
      return apiService.private<any>(`/recommendations/frequently-together/${productId}?limit=${limit}`);
    },
  },

  // Reviews endpoints
  reviews: {
    getByProduct: async (productId: string) => {
      return apiService.private<any>(`/reviews/product/${productId}`);
    },

    create: async (data: any) => {
      return apiService.private<any>('/reviews', {
        method: 'POST',
        body: JSON.stringify(data),
      });
    },
  },

  // WebSocket info
  ws: {
    getInfo: async () => {
      return apiService.private<any>('/ws/info');
    },
  },
};

export { ApiServiceError };
