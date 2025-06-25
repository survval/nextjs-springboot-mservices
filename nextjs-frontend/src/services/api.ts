import { getAuthHeader, getTenant } from './auth';

// Types
export interface Product {
  id: number;
  name: string;
  price: number;
  category: string;
  description?: string;
  status?: 'active' | 'discontinued';
  createdAt: string;
}

export interface ProductFilter {
  q?: string;
  category?: string;
  minPrice?: number;
  maxPrice?: number;
  status?: string;
  page?: number;
  size?: number;
  sort?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// API base URL
const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

// Helper function to add tenant context to requests
const withTenant = (url: string): string => {
  const tenant = getTenant();
  if (tenant && tenant !== 'default') {
    // Add tenant as a header or query parameter based on backend expectations
    return `${url}${url.includes('?') ? '&' : '?'}tenant=${tenant}`;
  }
  return url;
};

// Generic fetch function with error handling
const fetchApi = async <T>(
  url: string, 
  options: RequestInit = {}
): Promise<T> => {
  try {
    const headers = {
      'Content-Type': 'application/json',
      ...getAuthHeader(),
      ...options.headers,
    };

    const response = await fetch(withTenant(url), {
      ...options,
      headers,
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(
        errorData.message || `API error: ${response.status} ${response.statusText}`
      );
    }

    // For 204 No Content responses
    if (response.status === 204) {
      return {} as T;
    }

    return await response.json();
  } catch (error) {
    console.error('API request failed:', error);
    throw error;
  }
};

// Product API functions
export const productApi = {
  // Get paginated products with filtering
  getProducts: async (filters: ProductFilter = {}): Promise<PageResponse<Product>> => {
    const queryParams = new URLSearchParams();
    
    if (filters.q) queryParams.append('q', filters.q);
    if (filters.category) queryParams.append('category', filters.category);
    if (filters.minPrice) queryParams.append('minPrice', filters.minPrice.toString());
    if (filters.maxPrice) queryParams.append('maxPrice', filters.maxPrice.toString());
    if (filters.status) queryParams.append('status', filters.status);
    if (filters.page !== undefined) queryParams.append('page', filters.page.toString());
    if (filters.size) queryParams.append('size', filters.size.toString());
    if (filters.sort) queryParams.append('sort', filters.sort);
    
    const url = `${API_URL}/api/products?${queryParams.toString()}`;
    return fetchApi<PageResponse<Product>>(url);
  },
  
  // Get a single product by ID
  getProduct: async (id: number): Promise<Product> => {
    return fetchApi<Product>(`${API_URL}/api/products/${id}`);
  },
  
  // Create a new product
  createProduct: async (product: Omit<Product, 'id' | 'createdAt'>): Promise<Product> => {
    return fetchApi<Product>(`${API_URL}/api/products`, {
      method: 'POST',
      body: JSON.stringify(product),
    });
  },
  
  // Update an existing product
  updateProduct: async (id: number, product: Partial<Product>): Promise<Product> => {
    return fetchApi<Product>(`${API_URL}/api/products/${id}`, {
      method: 'PUT',
      body: JSON.stringify(product),
    });
  },
  
  // Delete a product
  deleteProduct: async (id: number): Promise<void> => {
    return fetchApi<void>(`${API_URL}/api/products/${id}`, {
      method: 'DELETE',
    });
  },
  
  // Get product categories (for filtering)
  getCategories: async (): Promise<string[]> => {
    return fetchApi<string[]>(`${API_URL}/api/products/categories`);
  },
};