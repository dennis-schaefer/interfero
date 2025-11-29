import {createBrowserRouter, RouterProvider} from "react-router-dom";
import DashboardView from "./dashboard/dashboard-view.tsx";

const router = createBrowserRouter([
    {
        path: "/",
        element: <DashboardView/>,
    }
]);

export default function App() {
  return (
      <RouterProvider router={router}/>
  )
}

