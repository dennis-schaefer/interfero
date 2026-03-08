import { createBrowserRouter, RouterProvider } from "react-router-dom";
import DashboardPage from "./pages/DashboardPage.tsx";
import { AuthProvider } from "@/features/security/AuthProvider.tsx";
import { ProtectedPage } from "@/features/security/ProtectedPage.tsx";
import LoginPage from "@/pages/LoginPage.tsx";
import { useEffect } from "react";
import axios from "axios";
import SetupLayout from "@/layouts/SetupLayout.tsx";
import ClusterSetupPage from "@/pages/ClusterSetupPage.tsx";
import MainLayout from "@/layouts/MainLayout.tsx";

const router = createBrowserRouter([
    {
        path: "/login/",
        Component: SetupLayout,
        children: [
            {
                index: true,
                Component: LoginPage
            },
            {
                path: "setup",
                element: <ProtectedPage><ClusterSetupPage /></ProtectedPage>
            }
        ]
    },
    {
        path: "/",
        Component: MainLayout,
        children: [
            {
                index: true,
                element: <DashboardPage />
            }
        ]
    },

]);

export default function App() {
    useEffect(() => {
        // To retrieve CSRF token cookie
        axios.get("/api/csrf").then();
    }, []);

    return (
        <AuthProvider>
            <RouterProvider router={router} />
        </AuthProvider>
    )
}

