import { describe, it, expect } from 'vitest';
import { renderHook } from '@testing-library/react';
import { AuthContext, useAuth, type AuthContextType } from '../AuthContext';
import type { ReactNode } from 'react';

describe('AuthContext', () => {
  describe('useAuth hook', () => {
    it('should throw an error when used outside of AuthProvider', () => {
      // Expect an error to be thrown
      expect(() => {
        renderHook(() => useAuth());
      }).toThrow('useAuth must be used within an AuthProvider');
    });

    it('should return the context value when used within the provider', () => {
      const mockAuthValue: AuthContextType = {
        accountInfo: {
          authenticated: true,
          username: 'testuser',
          roles: ['USER'],
        },
        loading: false,
        isAuthenticated: true,
        login: () => {},
      };

      const wrapper = ({ children }: { children: ReactNode }) => (
        <AuthContext.Provider value={mockAuthValue}>
          {children}
        </AuthContext.Provider>
      );

      const { result } = renderHook(() => useAuth(), { wrapper });

      expect(result.current).toEqual(mockAuthValue);
      expect(result.current.isAuthenticated).toBe(true);
      expect(result.current.loading).toBe(false);
      expect(result.current.accountInfo?.username).toBe('testuser');
    });

    it('should be able to handle undefined accountInfo', () => {
      const mockAuthValue: AuthContextType = {
        accountInfo: undefined,
        loading: true,
        isAuthenticated: false,
        login: () => {},
      };

      const wrapper = ({ children }: { children: ReactNode }) => (
        <AuthContext.Provider value={mockAuthValue}>
          {children}
        </AuthContext.Provider>
      );

      const { result } = renderHook(() => useAuth(), { wrapper });

      expect(result.current.accountInfo).toBeUndefined();
      expect(result.current.isAuthenticated).toBe(false);
      expect(result.current.loading).toBe(true);
    });
  });

  describe('AuthContext creation', () => {
    it('should create a Context with undefined as default value', () => {
      expect(AuthContext._currentValue).toBeUndefined();
    });
  });
});

