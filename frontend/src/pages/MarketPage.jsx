import { useEffect, useState } from "react";
import { fetchMarketPrices } from "../api/portfolioApi";

function MarketPage() {
  const [prices, setPrices] = useState([]);

  useEffect(() => {
    fetchMarketPrices()
      .then((data) => {
        setPrices(data);
      })
      .catch((err) => {
        console.error(err);
      });
  }, []);

  return (
    <div className="page">
      <h1>Brand Market Overview</h1>
      <p>Luxury and beauty brand pricing.</p>

      <div className="hero-card">
        <h2
          style={{
            marginBottom: "20px",
            color: "#d4af37",
          }}
        >
          Live Brand Prices
        </h2>

        {prices.length === 0 ? (
          <p>Loading market data...</p>
        ) : (
          <div
            style={{
              display: "grid",
              gridTemplateColumns:
                "repeat(auto-fill,minmax(220px,1fr))",
              gap: "20px",
            }}
          >
            {prices.map((brand, index) => (
              <div
                key={index}
                style={{
                  position: "relative",
                  background: "#151515",
                  border: "1px solid #2c2c2c",
                  borderRadius: "20px",
                  padding: "20px",
                  cursor: "pointer",
                  overflow: "visible",
                  transition: ".3s",
                }}
                onMouseEnter={(e) => {
                  const popup =
                    e.currentTarget.querySelector(
                      ".hover-popup"
                    );
                  if (popup) popup.style.opacity = 1;
                }}
                onMouseLeave={(e) => {
                  const popup =
                    e.currentTarget.querySelector(
                      ".hover-popup"
                    );
                  if (popup) popup.style.opacity = 0;
                }}
              >
                <h3 style={{ color: "#d4af37" }}>
                  {brand.stock || brand.symbol}
                </h3>

                <p
                  style={{
                    marginTop: "10px",
                    fontSize: "28px",
                    color: "#fff",
                  }}
                >
                  ${brand.price?.toFixed(2)}
                </p>

                <div
                  className="hover-popup"
                  style={{
                    opacity: 0,
                    position: "absolute",
                    top: 20,
                    right: -250,
                    width: 230,
                    background: "#0d0d0d",
                    border: "1px solid #d4af37",
                    borderRadius: "18px",
                    padding: "18px",
                    transition: ".25s",
                    zIndex: 999,
                    boxShadow:
                      "0 12px 35px rgba(0,0,0,.55)",
                  }}
                >
                  <h4
                    style={{
                      color: "#d4af37",
                      marginBottom: "8px",
                    }}
                  >
                    {brand.stock || brand.symbol}
                  </h4>

                  <p
                    style={{
                      color: "#aaa",
                      marginBottom: "8px",
                    }}
                  >
                    Luxury / Beauty Brand
                  </p>

                  <p
                    style={{
                      color: "#fff",
                      fontWeight: 700,
                      fontSize: "22px",
                    }}
                  >
                    ${brand.price?.toFixed(2)}
                  </p>

                  <div
                    style={{
                      display: "flex",
                      gap: "6px",
                      alignItems: "end",
                      height: "70px",
                      marginTop: "16px",
                    }}
                  >
                    {[25, 45, 35, 60, 50].map(
                      (v, i) => (
                        <div
                          key={i}
                          style={{
                            width: "18px",
                            height: `${v}px`,
                            background: "#d4af37",
                            borderRadius: "5px",
                          }}
                        />
                      )
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default MarketPage;