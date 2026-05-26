import { useEffect, useState } from "react";
import { fetchClients } from "../api/portfolioApi";

function ClientPage() {
  const [clients, setClients] = useState([]);
  const [search, setSearch] = useState("");
  const [selectedClient, setSelectedClient] = useState(null);

  useEffect(() => {
    fetchClients()
      .then(setClients)
      .catch(console.error);
  }, []);

  const filteredClients = clients.filter((client) =>
    client.name
      .toLowerCase()
      .includes(search.toLowerCase())
  );

  return (
    <div className="page">
      <h1>Luxury Client Explorer</h1>

      <p
        style={{
          color: "#aaa",
          marginBottom: "25px",
        }}
      >
        Explore premium investor portfolios.
      </p>

      {/* SEARCH */}

      <input
        type="text"
        placeholder="Search client..."
        value={search}
        onChange={(e) =>
          setSearch(e.target.value)
        }
        style={{
          width: "100%",
          padding: "16px",
          marginBottom: "30px",
          borderRadius: "14px",
          border: "1px solid #333",
          background: "#121212",
          color: "#eee",
          fontSize: "16px",
        }}
      />

      {/* CLIENT GRID */}

      <div
        style={{
          display: "grid",
          gridTemplateColumns:
            "repeat(auto-fill,minmax(320px,1fr))",
          gap: "22px",
        }}
      >
        {filteredClients.map((client) => (
          <div
            key={client.id}
            className="hero-card"
            onClick={() =>
              setSelectedClient(client)
            }
            style={{
              cursor: "pointer",
              transition: ".3s",
            }}
          >
            <h2
              style={{
                color: "#d4af37",
                marginBottom: "10px",
              }}
            >
              {client.name}
            </h2>

            <p style={{ color: "#aaa" }}>
              Portfolio Value
            </p>

            <h3>
              $
              {client.portfolioValue?.toLocaleString()}
            </h3>
          </div>
        ))}
      </div>

      {/* MODAL */}

      {selectedClient && (
        <div
          onClick={() =>
            setSelectedClient(null)
          }
          style={{
            position: "fixed",
            inset: 0,
            background:
              "rgba(0,0,0,.75)",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            zIndex: 999,
          }}
        >
          <div
            onClick={(e) =>
              e.stopPropagation()
            }
            className="hero-card"
            style={{
              width: "650px",
              maxWidth: "90%",
              padding: "35px",
            }}
          >
            <h1
              style={{
                color: "#d4af37",
                marginBottom: "15px",
              }}
            >
              {selectedClient.name}
            </h1>

            <p
              style={{
                color: "#aaa",
                marginBottom: "15px",
              }}
            >
              Portfolio Value
            </p>

            <h2
              style={{
                marginBottom: "22px",
              }}
            >
              $
              {selectedClient.portfolioValue?.toLocaleString()}
            </h2>

            <div
              style={{
                display: "flex",
                justifyContent:
                  "space-between",
                marginBottom: "14px",
              }}
            >
              <span style={{ color: "#aaa" }}>
                Diversification
              </span>

              <span
                style={{
                  color: "#d4af37",
                }}
              >
                {
                  selectedClient.diversificationScore
                }
              </span>
            </div>

            <div
              style={{
                display: "flex",
                justifyContent:
                  "space-between",
                marginBottom: "25px",
              }}
            >
              <span style={{ color: "#aaa" }}>
                Risk Exposure
              </span>

              <span
                style={{
                  color: "#ffb347",
                }}
              >
                {
                  selectedClient.topRiskExposure
                }
                %
              </span>
            </div>

            {/* RISK BADGE */}

            {selectedClient.topRiskExposure >
            25 ? (
              <div
                style={{
                  background:
                    "rgba(255,179,71,.12)",
                  border:
                    "1px solid #ffb347",
                  padding: "15px",
                  borderRadius: "14px",
                  color: "#ffb347",
                  marginBottom: "20px",
                }}
              >
                ⚠ High risk exposure detected
              </div>
            ) : (
              <div
                style={{
                  background:
                    "rgba(212,175,55,.1)",
                  border:
                    "1px solid #d4af37",
                  padding: "15px",
                  borderRadius: "14px",
                  color: "#d4af37",
                  marginBottom: "20px",
                }}
              >
                ✓ Balanced portfolio
              </div>
            )}

            {/* HOLDINGS */}

            <h3
              style={{
                color: "#d4af37",
                marginBottom: "12px",
              }}
            >
              Holdings
            </h3>

            <div
              style={{
                display: "flex",
                flexWrap: "wrap",
                gap: "10px",
              }}
            >
              {selectedClient.holdings &&
              Object.keys(
                selectedClient.holdings
              ).length > 0 ? (
                Object.entries(
                  selectedClient.holdings
                ).map(([brand, qty]) => (
                  <div
                    key={brand}
                    style={{
                      background:
                        "#121212",
                      border:
                        "1px solid #333",
                      padding:
                        "10px 15px",
                      borderRadius:
                        "12px",
                    }}
                  >
                    {brand} • {qty}
                  </div>
                ))
              ) : (
                <p>No holdings</p>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ClientPage;