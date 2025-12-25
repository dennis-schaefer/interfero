import {createBrowserRouter, RouterProvider} from "react-router-dom";
import DashboardPage from "./pages/DashboardPage.tsx";
import {AuthProvider} from "@/features/security/AuthProvider.tsx";
import {ProtectedPage} from "@/features/security/ProtectedPage.tsx";
import LoginPage from "@/pages/LoginPage.tsx";

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
  return (
      <AuthProvider>
          <RouterProvider router={router}/>
      </AuthProvider>
  )
}

