import {createBrowserRouter, RouterProvider} from "react-router-dom";
import DashboardPage from "./pages/DashboardPage.tsx";
import {AuthProvider} from "@/features/security/AuthProvider.tsx";
import {ProtectedPage} from "@/features/security/ProtectedPage.tsx";
import LoginPage from "@/pages/LoginPage.tsx";
import {useEffect} from "react";
import axios from "axios";

const router = createBrowserRouter([
    {
        path: "/",
        element: <ProtectedPage><DashboardPage /></ProtectedPage>,
    },
    {
        path: "/login",
        element: <LoginPage />
    }
]);

export default function App() {
    useEffect(() => {
        // To retrieve CSRF token cookie
        axios.get("/api/csrf").then();
    }, []);

    return (
        <AuthProvider>
            <RouterProvider router={router}/>
        </AuthProvider>
    )
}

