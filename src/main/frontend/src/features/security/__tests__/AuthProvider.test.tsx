import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { AuthProvider } from '../AuthProvider';
import { useAuth } from '../AuthContext';
import type { AccountInfo } from '@/api/schemas';

// Mock for the API
vi.mock('@/api/endpoints/account-info-controller.ts', () => ({
  useGetAccountInfo: vi.fn(),
}));

import { useGetAccountInfo } from '@/api/endpoints/account-info-controller.ts';

// Test component that uses the Auth-Context
function TestComponent() {
  const { accountInfo, loading, isAuthenticated } = useAuth();

  return (
    <div>
      <div data-testid="loading">{loading ? 'loading' : 'not-loading'}</div>
      <div data-testid="authenticated">{isAuthenticated ? 'authenticated' : 'not-authenticated'}</div>
      <div data-testid="username">{accountInfo?.username || 'no-username'}</div>
    </div>
  );
}

describe('AuthProvider', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    // Mock window.location.href
    delete (window as any).location;
    window.location = { href: '' } as any;
  });

  it('should correctly display loading state', () => {
    vi.mocked(useGetAccountInfo).mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
    } as any);

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    expect(screen.getByTestId('loading')).toHaveTextContent('loading');
    expect(screen.getByTestId('authenticated')).toHaveTextContent('not-authenticated');
  });

  it('should correctly display authenticated user', () => {
    const mockAccountInfo: AccountInfo = {
      authenticated: true,
      username: 'testuser',
      roles: ['USER'],
    };

    vi.mocked(useGetAccountInfo).mockReturnValue({
      data: mockAccountInfo,
      isLoading: false,
      isError: false,
    } as any);

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    expect(screen.getByTestId('loading')).toHaveTextContent('not-loading');
    expect(screen.getByTestId('authenticated')).toHaveTextContent('authenticated');
    expect(screen.getByTestId('username')).toHaveTextContent('testuser');
  });

  it('should correctly handle unauthenticated user', () => {
    const mockAccountInfo: AccountInfo = {
      authenticated: false,
      username: undefined,
      roles: [],
    };

    vi.mocked(useGetAccountInfo).mockReturnValue({
      data: mockAccountInfo,
      isLoading: false,
      isError: false,
    } as any);

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    expect(screen.getByTestId('loading')).toHaveTextContent('not-loading');
    expect(screen.getByTestId('authenticated')).toHaveTextContent('not-authenticated');
    expect(screen.getByTestId('username')).toHaveTextContent('no-username');
  });

  it('should correctly handle API errors', () => {
    vi.mocked(useGetAccountInfo).mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: true,
    } as any);

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    expect(screen.getByTestId('loading')).toHaveTextContent('not-loading');
    expect(screen.getByTestId('authenticated')).toHaveTextContent('not-authenticated');
  });

  it('should call useGetAccountInfo with correct options', () => {
    vi.mocked(useGetAccountInfo).mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
    } as any);

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    expect(useGetAccountInfo).toHaveBeenCalledWith({
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

  it('should provide login function', async () => {
    const mockAccountInfo: AccountInfo = {
      authenticated: true,
      username: 'testuser',
      roles: ['USER'],
    };

    vi.mocked(useGetAccountInfo).mockReturnValue({
      data: mockAccountInfo,
      isLoading: false,
      isError: false,
    } as any);

    function TestLoginComponent() {
      const { login } = useAuth();
      return <button onClick={login}>Login</button>;
    }

    render(
      <AuthProvider>
        <TestLoginComponent />
      </AuthProvider>
    );

    const button = screen.getByRole('button', { name: 'Login' });
    button.click();

    await waitFor(() => {
      expect(window.location.href).toBe('/login');
    });
  });

  it('should render children', () => {
    vi.mocked(useGetAccountInfo).mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: false,
    } as any);

    render(
      <AuthProvider>
        <div data-testid="child">Test Child</div>
      </AuthProvider>
    );

    expect(screen.getByTestId('child')).toBeInTheDocument();
    expect(screen.getByTestId('child')).toHaveTextContent('Test Child');
  });

  it('should correctly handle transition from Loading to Authenticated', () => {
    const mockFn = vi.mocked(useGetAccountInfo);

    // First Loading
    mockFn.mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
    } as any);

    const { rerender } = render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    expect(screen.getByTestId('loading')).toHaveTextContent('loading');

    // Then Authenticated
    const mockAccountInfo: AccountInfo = {
      authenticated: true,
      username: 'testuser',
      roles: ['USER'],
    };

    mockFn.mockReturnValue({
      data: mockAccountInfo,
      isLoading: false,
      isError: false,
    } as any);

    rerender(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    expect(screen.getByTestId('loading')).toHaveTextContent('not-loading');
    expect(screen.getByTestId('authenticated')).toHaveTextContent('authenticated');
    expect(screen.getByTestId('username')).toHaveTextContent('testuser');
  });
});

