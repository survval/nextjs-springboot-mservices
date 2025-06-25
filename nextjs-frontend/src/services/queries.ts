import { 
  useQuery, 
  useMutation, 
  useQueryClient,
  QueryClient,
  QueryClientProvider
} from '@tanstack/react-query';
import { productApi, Product, ProductFilter, PageResponse } from './api';

// Create a client
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5, // 5 minutes
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

// Provider component
export const QueryProvider = QueryClientProvider;

// Product queries
export const useProducts = (filters: ProductFilter = {}) => {
  return useQuery({
    queryKey: ['products', filters],
    queryFn: () => productApi.getProducts(filters),
  });
};

export const useProduct = (id: number) => {
  return useQuery({
    queryKey: ['product', id],
    queryFn: () => productApi.getProduct(id),
    enabled: !!id,
  });
};

export const useCategories = () => {
  return useQuery({
    queryKey: ['categories'],
    queryFn: () => productApi.getCategories(),
  });
};

// Product mutations
export const useCreateProduct = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (product: Omit<Product, 'id' | 'createdAt'>) => 
      productApi.createProduct(product),
    onSuccess: () => {
      // Invalidate products list queries
      queryClient.invalidateQueries({ queryKey: ['products'] });
    },
  });
};

export const useUpdateProduct = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ id, product }: { id: number; product: Partial<Product> }) => 
      productApi.updateProduct(id, product),
    onSuccess: (updatedProduct) => {
      // Update the product in the cache
      queryClient.setQueryData(['product', updatedProduct.id], updatedProduct);
      
      // Invalidate products list queries
      queryClient.invalidateQueries({ queryKey: ['products'] });
    },
  });
};

export const useDeleteProduct = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (id: number) => productApi.deleteProduct(id),
    onSuccess: (_, id) => {
      // Remove the product from the cache
      queryClient.removeQueries({ queryKey: ['product', id] });
      
      // Invalidate products list queries
      queryClient.invalidateQueries({ queryKey: ['products'] });
    },
  });
};

// Optimistic updates helper
export const useOptimisticUpdate = () => {
  const queryClient = useQueryClient();
  
  const updateProductOptimistically = async (
    id: number, 
    updatedFields: Partial<Product>,
    onSuccess?: () => void
  ) => {
    // Get the current product from cache
    const currentProduct = queryClient.getQueryData<Product>(['product', id]);
    
    if (!currentProduct) return;
    
    // Optimistically update the UI
    queryClient.setQueryData(['product', id], {
      ...currentProduct,
      ...updatedFields,
    });
    
    try {
      // Perform the actual update
      await productApi.updateProduct(id, updatedFields);
      if (onSuccess) onSuccess();
    } catch (error) {
      // Revert to the original data on error
      queryClient.setQueryData(['product', id], currentProduct);
      throw error;
    } finally {
      // Refetch to ensure data consistency
      queryClient.invalidateQueries({ queryKey: ['product', id] });
      queryClient.invalidateQueries({ queryKey: ['products'] });
    }
  };
  
  return { updateProductOptimistically };
};