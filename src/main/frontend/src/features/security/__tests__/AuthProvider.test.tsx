import { render, screen, renderHook } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { AuthProvider } from '../AuthProvider.tsx';
import { useAuth } from '../AuthContext.tsx';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import type { ReactNode } from 'react';

// Mock the API hook
vi.mock('../../../api/endpoints/account-info-controller/account-info-controller.ts', () => ({
    useGetAccountInfo: vi.fn(),
}));

import { useGetAccountInfo } from '../../../api/endpoints/account-info-controller/account-info-controller.ts';

const mockedUseGetAccountInfo = vi.mocked(useGetAccountInfo);

const createWrapper = () => {
    const queryClient = new QueryClient({
        defaultOptions: {
            queries: {
                retry: false,
            },
        },
    });
    return ({ children }: { children: ReactNode }) => (
        <QueryClientProvider client={queryClient}>
            <AuthProvider>{children}</AuthProvider>
        </QueryClientProvider>
    );
};

describe('AuthProvider', () => {
    beforeEach(() => {
        vi.clearAllMocks();
        // Reset window.location mock
        Object.defineProperty(window, 'location', {
            value: { href: '' },
            writable: true,
        });
    });

    it('should provide loading state while fetching account info', () => {
        mockedUseGetAccountInfo.mockReturnValue({
            data: undefined,
            isLoading: true,
            isError: false,
        } as ReturnType<typeof useGetAccountInfo>);

        const { result } = renderHook(() => useAuth(), {
            wrapper: createWrapper(),
        });

        expect(result.current.loading).toBe(true);
        expect(result.current.isAuthenticated).toBe(true); // No error = authenticated
        expect(result.current.accountInfo).toBeUndefined();
    });

    it('should provide authenticated state when account info is loaded', () => {
        const mockAccountInfo = { username: 'testuser', roles: ['USER', 'ADMIN'] };

        mockedUseGetAccountInfo.mockReturnValue({
            data: mockAccountInfo,
            isLoading: false,
            isError: false,
        } as ReturnType<typeof useGetAccountInfo>);

        const { result } = renderHook(() => useAuth(), {
            wrapper: createWrapper(),
        });

        expect(result.current.loading).toBe(false);
        expect(result.current.isAuthenticated).toBe(true);
        expect(result.current.accountInfo).toEqual(mockAccountInfo);
        expect(result.current.accountInfo?.username).toBe('testuser');
        expect(result.current.accountInfo?.roles).toContain('USER');
        expect(result.current.accountInfo?.roles).toContain('ADMIN');
    });

    it('should provide unauthenticated state when API returns error', () => {
        mockedUseGetAccountInfo.mockReturnValue({
            data: undefined,
            isLoading: false,
            isError: true,
        } as ReturnType<typeof useGetAccountInfo>);

        const { result } = renderHook(() => useAuth(), {
            wrapper: createWrapper(),
        });

        expect(result.current.loading).toBe(false);
        expect(result.current.isAuthenticated).toBe(false);
        expect(result.current.accountInfo).toBeUndefined();
    });

    it('should redirect to /login when login function is called', () => {
        mockedUseGetAccountInfo.mockReturnValue({
            data: { username: 'testuser', roles: ['USER'] },
            isLoading: false,
            isError: false,
        } as ReturnType<typeof useGetAccountInfo>);

        const { result } = renderHook(() => useAuth(), {
            wrapper: createWrapper(),
        });

        result.current.login();

        expect(window.location.href).toBe('/login');
    });

    it('should render children correctly', () => {
        mockedUseGetAccountInfo.mockReturnValue({
            data: { username: 'testuser', roles: ['USER'] },
            isLoading: false,
            isError: false,
        } as ReturnType<typeof useGetAccountInfo>);

        const queryClient = new QueryClient({
            defaultOptions: { queries: { retry: false } },
        });

        render(
            <QueryClientProvider client={queryClient}>
                <AuthProvider>
                    <div data-testid="child-component">Child Content</div>
                </AuthProvider>
            </QueryClientProvider>
        );

        expect(screen.getByTestId('child-component')).toBeInTheDocument();
        expect(screen.getByText('Child Content')).toBeInTheDocument();
    });

    it('should configure query options correctly to prevent unnecessary refetches', () => {
        mockedUseGetAccountInfo.mockReturnValue({
            data: undefined,
            isLoading: true,
            isError: false,
        } as ReturnType<typeof useGetAccountInfo>);

        renderHook(() => useAuth(), { wrapper: createWrapper() });

        // Verify that the hook was called with the correct options
        expect(mockedUseGetAccountInfo).toHaveBeenCalledWith({
            query: {
                retry: 0,
                retryOnMount: false,
                refetchOnMount: false,
                refetchOnWindowFocus: false,
                refetchOnReconnect: false,
                staleTime: Infinity,
            },
        });
    });
});

