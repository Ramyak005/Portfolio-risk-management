import { BrowserRouter, Routes, Route } from "react-router-dom";
import HomePage from "./pages/HomePage";
import MarketPage from "./pages/MarketPage";
import ClientPage from "./pages/ClientPage";
import AIPage from "./pages/AIPage";
import PortfolioBuilderPage from "./pages/PortfolioBuilderPage";
import Navbar from "./components/Navbar";

function App() {
  return (
    <BrowserRouter>
      <div className="app-shell">
        <Navbar />

        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/market" element={<MarketPage />} />
          <Route path="/clients" element={<ClientPage />} />
          <Route path="/ai" element={<AIPage />} />
          <Route path="/builder" element={<PortfolioBuilderPage />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;