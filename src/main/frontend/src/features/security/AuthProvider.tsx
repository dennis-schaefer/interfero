import {type ReactNode } from 'react';
import { AuthContext } from './AuthContext';
import {useGetAccountInfo} from "../../api/endpoints/account-info-controller/account-info-controller.ts";

interface AuthProviderProps
{
    children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
    const {data, isLoading, isError} = useGetAccountInfo({
        query: {
            retry: 0,
            retryOnMount: false,
            refetchOnMount: false,
            refetchOnWindowFocus: false,
            refetchOnReconnect: false,
            staleTime: Infinity,
        }
    });

    const login = () => {
        window.location.href = '/login';
    };

    return (
        <AuthContext.Provider
            value={{
                accountInfo: data,
                loading: isLoading,
                isAuthenticated: !isError,
                login,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}