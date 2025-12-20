import { renderHook } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { useAuth, AuthContext, type AuthContextType } from '../AuthContext.tsx';
import type { ReactNode } from 'react';

describe('AuthContext', () => {
    describe('useAuth', () => {
        it('should throw error when used outside of AuthProvider', () => {
            expect(() => {
                renderHook(() => useAuth());
            }).toThrow('useAuth must be used within an AuthProvider');
        });

        it('should return context value when used within AuthProvider', () => {
            const mockContextValue: AuthContextType = {
                accountInfo: { username: 'testuser', roles: ['USER'] },
                loading: false,
                isAuthenticated: true,
                login: () => {},
            };

            const wrapper = ({ children }: { children: ReactNode }) => (
                <AuthContext.Provider value={mockContextValue}>
                    {children}
                </AuthContext.Provider>
            );

            const { result } = renderHook(() => useAuth(), { wrapper });

            expect(result.current).toBe(mockContextValue);
            expect(result.current.isAuthenticated).toBe(true);
            expect(result.current.loading).toBe(false);
            expect(result.current.accountInfo?.username).toBe('testuser');
        });

        it('should return loading state correctly', () => {
            const mockContextValue: AuthContextType = {
                accountInfo: undefined,
                loading: true,
                isAuthenticated: false,
                login: () => {},
            };

            const wrapper = ({ children }: { children: ReactNode }) => (
                <AuthContext.Provider value={mockContextValue}>
                    {children}
                </AuthContext.Provider>
            );

            const { result } = renderHook(() => useAuth(), { wrapper });

            expect(result.current.loading).toBe(true);
            expect(result.current.isAuthenticated).toBe(false);
            expect(result.current.accountInfo).toBeUndefined();
        });

        it('should return unauthenticated state correctly', () => {
            const mockContextValue: AuthContextType = {
                accountInfo: undefined,
                loading: false,
                isAuthenticated: false,
                login: () => {},
            };

            const wrapper = ({ children }: { children: ReactNode }) => (
                <AuthContext.Provider value={mockContextValue}>
                    {children}
                </AuthContext.Provider>
            );

            const { result } = renderHook(() => useAuth(), { wrapper });

            expect(result.current.loading).toBe(false);
            expect(result.current.isAuthenticated).toBe(false);
        });
    });
});

