import { createContext, useContext } from 'react';
import type {AccountInfo} from "@/api/schemas";

export interface AuthContextType
{
    accountInfo: AccountInfo | undefined;
    loading: boolean;
    isAuthenticated: boolean;
    login: () => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function useAuth(): AuthContextType {
    const context = useContext(AuthContext);
    if (!context)
        throw new Error('useAuth must be used within an AuthProvider');

    return context;
}