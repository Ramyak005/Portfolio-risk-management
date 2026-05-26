import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  fetchDashboard,
  fetchMarketPrices,
} from "../api/portfolioApi";
import brandLogos from "../assets/logos/brandLogos";

function HomePage() {
  const [dashboard, setDashboard] = useState(null);
  const [stocks, setStocks] = useState([]);

  const featuredBrands = [
    {
      name: "Dior",
      score: "★★★★★",
      why: "Luxury leader with strong premium pricing.",
      trend: "+12%",
    },
    {
      name: "Chanel",
      score: "★★★★★",
      why: "High exclusivity and luxury demand.",
      trend: "+10%",
    },
    {
      name: "LV",
      score: "★★★★★",
      why: "Strong market dominance and pricing power.",
      trend: "+15%",
    },
    {
      name: "Gucci",
      score: "★★★★☆",
      why: "High exposure and luxury momentum.",
      trend: "+8%",
    },
    {
      name: "Prada",
      score: "★★★★☆",
      why: "Premium growth with strong positioning.",
      trend: "+7%",
    },
    {
      name: "Nike",
      score: "★★★★☆",
      why: "Diversification and mass luxury appeal.",
      trend: "+6%",
    },
    {
      name: "Sephora",
      score: "★★★★☆",
      why: "Beauty sector outperformer.",
      trend: "+9%",
    },
    {
      name: "Zara",
      score: "★★★☆☆",
      why: "Fast-fashion diversification exposure.",
      trend: "+4%",
    },
  ];

  useEffect(() => {
    fetchDashboard().then(setDashboard).catch(console.error);
    fetchMarketPrices().then(setStocks).catch(console.error);
  }, []);

  return (
    <div className="page">

      {/* HERO */}

      <div
        className="hero-card"
        style={{
          padding: "55px",
          marginBottom: "35px",
        }}
      >
        <h1
          style={{
            fontSize: "64px",
            color: "#d4af37",
            marginBottom: "12px",
          }}
        >
          BeautyVest
        </h1>

        <p
          style={{
            fontSize: "28px",
            color: "#ddd",
            marginBottom: "10px",
          }}
        >
          Luxury Fashion Investment Intelligence
        </p>

        <p
          style={{
            color: "#999",
            marginBottom: "30px",
            fontSize: "18px",
          }}
        >
          Track • Analyze • Build • AI Insights
        </p>

        <div
          style={{
            display: "flex",
            gap: "18px",
            flexWrap: "wrap",
          }}
        >
          <Link
            to="/market"
            style={{
              background: "#d4af37",
              color: "#111",
              padding: "14px 28px",
              borderRadius: "14px",
              textDecoration: "none",
              fontWeight: 700,
            }}
          >
            Explore Market
          </Link>

          <Link
            to="/clients"
            style={{
              border: "1px solid #d4af37",
              color: "#d4af37",
              padding: "14px 28px",
              borderRadius: "14px",
              textDecoration: "none",
            }}
          >
            View Clients
          </Link>
        </div>
      </div>

      {/* LIVE TICKER */}

      <div
        style={{
          overflow: "hidden",
          whiteSpace: "nowrap",
          borderTop: "1px solid #222",
          borderBottom: "1px solid #222",
          padding: "14px 0",
          marginBottom: "40px",
          width: "100%",
        }}
      >
        <div
          style={{
            display: "flex",
            width: "max-content",
            animation: "ticker 30s linear infinite",
          }}
        >
          {[...stocks, ...stocks].map((s, i) => (
            <span
              key={i}
              style={{
                flexShrink: 0,
                marginRight: "55px",
                color: "#d4af37",
                fontWeight: 600,
                fontSize: "18px",
              }}
            >
              {s.stock || s.symbol} ${s.price}
            </span>
          ))}
        </div>
      </div>

      {/* KPI STATS */}

      <div
        style={{
          display: "grid",
          gridTemplateColumns:
            "repeat(auto-fit,minmax(220px,1fr))",
          gap: "20px",
          marginBottom: "35px",
        }}
      >
        <div className="hero-card">
          <h3 style={{ color: "#d4af37" }}>Brands</h3>
          <h1>{stocks.length}</h1>
        </div>

        <div className="hero-card">
          <h3 style={{ color: "#d4af37" }}>Clients</h3>
          <h1>100</h1>
        </div>

        <div className="hero-card">
          <h3 style={{ color: "#d4af37" }}>Total AUM</h3>
          <h1>
            $
            {dashboard?.summary?.totalAssetsUnderManagement
              ? dashboard.summary.totalAssetsUnderManagement.toLocaleString()
              : "Loading..."}
          </h1>
        </div>

        <div className="hero-card">
          <h3 style={{ color: "#d4af37" }}>Luxury Leader</h3>
          <h1>LV</h1>
        </div>
      </div>

      {/* FEATURED BRANDS */}

      <div className="hero-card" style={{ marginBottom: 35 }}>
        <h2
          style={{
            color: "#d4af37",
            marginBottom: "25px",
          }}
        >
          Featured Luxury Brands
        </h2>

        <div
          style={{
            display: "grid",
            gridTemplateColumns:
              "repeat(auto-fit,minmax(180px,1fr))",
            gap: "18px",
          }}
        >
          {featuredBrands.map((brand) => (
            <div
              key={brand.name}
              className="brand-hover-card"
              style={{
                position: "relative",
                background: "#121212",
                border: "1px solid #2a2a2a",
                borderRadius: 18,
                padding: 20,
                textAlign: "center",
                color: "#eee",
                cursor: "pointer",
                transition: ".3s",
              }}
            >
              <img
                src={brandLogos[brand.name]}
                alt={brand.name}
                style={{
                  height: 38,
                  marginBottom: 14,
                  objectFit: "contain",
                }}
              />

              <div>{brand.name}</div>

              <div className="brand-popup">
                <img
                  src={brandLogos[brand.name]}
                  alt=""
                  style={{
                    height: 45,
                    marginBottom: 10,
                  }}
                />

                <h3 style={{ color: "#d4af37" }}>
                  {brand.name}
                </h3>

                <p style={{ color: "#d4af37" }}>
                  {brand.score}
                </p>

                <p style={{ color: "#bbb" }}>
                  {brand.why}
                </p>

                <div
                  style={{
                    height: 55,
                    background:
                      "linear-gradient(90deg,#d4af37,#ffdd78)",
                    borderRadius: 12,
                    margin: "12px 0",
                  }}
                />

                <div
                  style={{
                    color: "#d4af37",
                    fontWeight: 700,
                  }}
                >
                  Trend {brand.trend}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

    </div>
  );
}

export default HomePage;