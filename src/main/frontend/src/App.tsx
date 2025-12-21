import {createBrowserRouter, RouterProvider} from "react-router-dom";
import DashboardPage from "./pages/DashboardPage.tsx";

const router = createBrowserRouter([
    {
        path: "/",
        element: <DashboardPage/>,
    }
]);

export default function App() {
  return (
      <RouterProvider router={router}/>
  )
}

