import { BrowserRouter, Routes, Route } from "react-router-dom";

import Navigation from "./components/Navigation";

import ClientsPage from "./pages/ClientsPage";
import CardsPage from "./pages/CardsPage";

export default function App() {

    return (

        <BrowserRouter>

            <Navigation />

            <Routes>

                <Route
                    path="/clients"
                    element={<ClientsPage />}
                />

                <Route
                    path="/cards"
                    element={<CardsPage />}
                />

            </Routes>

        </BrowserRouter>

    );

}
