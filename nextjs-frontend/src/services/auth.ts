import Keycloak from 'keycloak-js';
import { useState, useEffect } from 'react';

// Types
export interface AuthUser {
  id: string;
  username: string;
  email: string;
  roles: string[];
  token: string;
  refreshToken: string;
}

// Initialize Keycloak instance
let keycloak: Keycloak | null = null;

// Get tenant from subdomain or path
export const getTenant = (): string => {
  if (typeof window === 'undefined') return '';
  
  // Check for subdomain pattern (tenant1.app.com)
  const hostParts = window.location.hostname.split('.');
  if (hostParts.length > 2 && hostParts[0] !== 'www') {
    return hostParts[0];
  }
  
  // Check for path pattern (/tenant1/products)
  const pathParts = window.location.pathname.split('/');
  if (pathParts.length > 1 && pathParts[1] !== '') {
    return pathParts[1];
  }
  
  return 'default'; // Fallback to default tenant
};

// Initialize Keycloak
export const initKeycloak = async (): Promise<Keycloak | null> => {
  if (keycloak) return keycloak;
  
  const tenant = getTenant();
  const realm = tenant !== 'default' 
    ? `${tenant}-realm` 
    : process.env.NEXT_PUBLIC_KEYCLOAK_REALM || 'microservice-realm';
  
  try {
    keycloak = new Keycloak({
      url: process.env.NEXT_PUBLIC_KEYCLOAK_URL || 'http://localhost:8180',
      realm: realm,
      clientId: process.env.NEXT_PUBLIC_KEYCLOAK_CLIENT_ID || 'frontend'
    });
    
    const authenticated = await keycloak.init({
      onLoad: 'check-sso',
      silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
      pkceMethod: 'S256'
    });
    
    if (authenticated) {
      console.log('User is authenticated');
    } else {
      console.log('User is not authenticated');
    }
    
    return keycloak;
  } catch (error) {
    console.error('Failed to initialize Keycloak', error);
    return null;
  }
};

// Custom hook for authentication
export const useAuth = () => {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<Error | null>(null);
  
  useEffect(() => {
    const initAuth = async () => {
      try {
        setLoading(true);
        const kc = await initKeycloak();
        
        if (kc && kc.authenticated) {
          // Parse token to get user info
          const tokenParsed = kc.tokenParsed as any;
          
          setUser({
            id: tokenParsed.sub,
            username: tokenParsed.preferred_username,
            email: tokenParsed.email || '',
            roles: tokenParsed.realm_access?.roles || [],
            token: kc.token || '',
            refreshToken: kc.refreshToken || ''
          });
          
          // Set up token refresh
          kc.onTokenExpired = () => {
            kc.updateToken(30).then((refreshed) => {
              if (refreshed) {
                console.log('Token refreshed');
                setUser(prev => prev ? {
                  ...prev,
                  token: kc.token || '',
                  refreshToken: kc.refreshToken || ''
                } : null);
              }
            }).catch(() => {
              console.error('Failed to refresh token');
              logout();
            });
          };
        }
      } catch (err) {
        setError(err instanceof Error ? err : new Error('Unknown error'));
      } finally {
        setLoading(false);
      }
    };
    
    initAuth();
  }, []);
  
  const login = async () => {
    try {
      const kc = await initKeycloak();
      if (kc) {
        kc.login();
      }
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Failed to login'));
    }
  };
  
  const logout = async () => {
    try {
      if (keycloak) {
        await keycloak.logout();
        setUser(null);
      }
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Failed to logout'));
    }
  };
  
  const hasRole = (role: string): boolean => {
    return user?.roles.includes(role) || false;
  };
  
  return {
    user,
    loading,
    error,
    login,
    logout,
    hasRole,
    getToken: () => user?.token || null,
    isAuthenticated: !!user
  };
};

// Get auth header for API requests
export const getAuthHeader = (): { Authorization: string } | {} => {
  if (keycloak && keycloak.token) {
    return { Authorization: `Bearer ${keycloak.token}` };
  }
  return {};
};