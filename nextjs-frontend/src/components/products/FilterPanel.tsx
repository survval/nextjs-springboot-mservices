'use client';

import { useState, useEffect } from 'react';
import { ProductFilter } from '@/services/api';

interface FilterPanelProps {
  filters: ProductFilter;
  onFilterChange: (filters: Partial<ProductFilter>) => void;
  categories: string[];
}

export default function FilterPanel({ 
  filters, 
  onFilterChange, 
  categories 
}: FilterPanelProps) {
  const [searchQuery, setSearchQuery] = useState(filters.q || '');
  const [minPrice, setMinPrice] = useState<string>(filters.minPrice?.toString() || '');
  const [maxPrice, setMaxPrice] = useState<string>(filters.maxPrice?.toString() || '');
  const [selectedCategory, setSelectedCategory] = useState<string>(filters.category || '');
  const [selectedStatus, setSelectedStatus] = useState<string>(filters.status || '');
  
  // Debounce search input
  useEffect(() => {
    const timer = setTimeout(() => {
      if (searchQuery !== filters.q) {
        onFilterChange({ q: searchQuery || undefined });
      }
    }, 500);
    
    return () => clearTimeout(timer);
  }, [searchQuery, filters.q, onFilterChange]);
  
  const handleCategoryChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const value = e.target.value;
    setSelectedCategory(value);
    onFilterChange({ category: value || undefined });
  };
  
  const handleStatusChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const value = e.target.value;
    setSelectedStatus(value);
    onFilterChange({ status: value || undefined });
  };
  
  const handlePriceChange = () => {
    const min = minPrice ? parseFloat(minPrice) : undefined;
    const max = maxPrice ? parseFloat(maxPrice) : undefined;
    
    onFilterChange({
      minPrice: min,
      maxPrice: max
    });
  };
  
  const handleReset = () => {
    setSearchQuery('');
    setMinPrice('');
    setMaxPrice('');
    setSelectedCategory('');
    setSelectedStatus('');
    
    onFilterChange({
      q: undefined,
      minPrice: undefined,
      maxPrice: undefined,
      category: undefined,
      status: undefined
    });
  };
  
  return (
    <div className="card">
      <h2 className="text-lg font-medium mb-4">Filters</h2>
      
      {/* Search */}
      <div className="mb-4">
        <label htmlFor="search" className="form-label">
          Search
        </label>
        <input
          id="search"
          type="text"
          className="form-input"
          placeholder="Search products..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </div>
      
      {/* Category filter */}
      <div className="mb-4">
        <label htmlFor="category" className="form-label">
          Category
        </label>
        <select
          id="category"
          className="form-input"
          value={selectedCategory}
          onChange={handleCategoryChange}
        >
          <option value="">All Categories</option>
          {categories.map((category) => (
            <option key={category} value={category}>
              {category}
            </option>
          ))}
        </select>
      </div>
      
      {/* Status filter */}
      <div className="mb-4">
        <label htmlFor="status" className="form-label">
          Status
        </label>
        <select
          id="status"
          className="form-input"
          value={selectedStatus}
          onChange={handleStatusChange}
        >
          <option value="">All Statuses</option>
          <option value="active">Active</option>
          <option value="discontinued">Discontinued</option>
        </select>
      </div>
      
      {/* Price range */}
      <div className="mb-4">
        <label className="form-label">Price Range</label>
        <div className="flex space-x-2">
          <input
            type="number"
            className="form-input"
            placeholder="Min"
            value={minPrice}
            onChange={(e) => setMinPrice(e.target.value)}
            onBlur={handlePriceChange}
            min="0"
            step="0.01"
          />
          <input
            type="number"
            className="form-input"
            placeholder="Max"
            value={maxPrice}
            onChange={(e) => setMaxPrice(e.target.value)}
            onBlur={handlePriceChange}
            min="0"
            step="0.01"
          />
        </div>
      </div>
      
      {/* Reset button */}
      <button
        onClick={handleReset}
        className="btn btn-secondary w-full mt-4"
      >
        Reset Filters
      </button>
    </div>
  );
}