import { useEffect, useState } from "react";
import {
  fetchClients,
  fetchAIInsight,
  fetchRiskAlerts,
} from "../api/portfolioApi";

function AIPage() {
  const [clients, setClients] = useState([]);
  const [selectedClient, setSelectedClient] =
    useState(null);

  const [insight, setInsight] = useState(null);
  const [alerts, setAlerts] = useState([]);

  useEffect(() => {
    fetchClients().then(setClients);

    fetchAIInsight()
      .then(setInsight)
      .catch(console.error);

    fetchRiskAlerts()
      .then(setAlerts)
      .catch(console.error);
  }, []);

  function getClientAdvice(client) {
    if (!client)
      return {
        risk: "Select Client",
        advice: "Choose a client",
      };

    const risk = client.topRiskExposure;

    if (risk > 30) {
      return {
        risk: "High Risk",
        advice:
          "Reduce concentration in high exposure brands and diversify using Nike, Sephora or Zara.",
      };
    }

    if (risk > 18) {
      return {
        risk: "Moderate Risk",
        advice:
          "Portfolio is moderately concentrated. Minor diversification recommended.",
      };
    }

    return {
      risk: "Balanced",
      advice:
        "Portfolio looks healthy. Maintain current allocation.",
    };
  }

  const recommendation =
    getClientAdvice(selectedClient);

  return (
    <div className="page">
      <h1>Luxury AI Advisor</h1>

      <p
        style={{
          color: "#aaa",
          marginBottom: "30px",
        }}
      >
        AI-powered portfolio strategy center.
      </p>

      {/* CLIENT SELECT */}

      <div
        className="hero-card"
        style={{ marginBottom: "25px" }}
      >
        <h2
          style={{
            color: "#d4af37",
            marginBottom: "15px",
          }}
        >
          Client AI Portfolio Advisor
        </h2>

        <select
          value={selectedClient?.id || ""}
          onChange={(e) => {
            const found = clients.find(
              (c) =>
                c.id ===
                Number(e.target.value)
            );
            setSelectedClient(found);
          }}
          style={{
            width: "100%",
            padding: "15px",
            borderRadius: "14px",
            background: "#111",
            border: "1px solid #333",
            color: "#eee",
            marginBottom: "20px",
          }}
        >
          <option value="">
            Select Client
          </option>

          {clients.map((c) => (
            <option
              key={c.id}
              value={c.id}
            >
              {c.name}
            </option>
          ))}
        </select>

        {selectedClient && (
          <div
            style={{
              background:
                "rgba(212,175,55,.05)",
              border:
                "1px solid #d4af37",
              borderRadius: "16px",
              padding: "20px",
            }}
          >
            <h3
              style={{
                color: "#d4af37",
                marginBottom: "12px",
              }}
            >
              {selectedClient.name}
            </h3>

            <p
              style={{
                color: "#aaa",
                marginBottom: "8px",
              }}
            >
              Risk Status
            </p>

            <h2
              style={{
                color:
                  recommendation.risk ===
                  "High Risk"
                    ? "#ffb347"
                    : "#d4af37",
              }}
            >
              {recommendation.risk}
            </h2>

            <div
              style={{
                marginTop: "20px",
              }}
            >
              <h3
                style={{
                  color: "#d4af37",
                  marginBottom: "10px",
                }}
              >
                AI Strategy
              </h3>

              <p style={{ color: "#ddd" }}>
                {recommendation.advice}
              </p>
            </div>
          </div>
        )}
      </div>

      {/* GLOBAL AI */}

      <div
        className="hero-card"
        style={{ marginBottom: "25px" }}
      >
        <h2
          style={{
            color: "#d4af37",
            marginBottom: "15px",
          }}
        >
          Global AI Insight
        </h2>

        {insight ? (
          <>
            <p
              style={{
                color: "#ddd",
                marginBottom: "18px",
              }}
            >
              {insight.insight}
            </p>

            <div
              style={{
                background:
                  "rgba(212,175,55,.08)",
                border:
                  "1px solid #d4af37",
                padding: "18px",
                borderRadius: "14px",
              }}
            >
              {insight.recommendation}
            </div>
          </>
        ) : (
          <p>Loading...</p>
        )}
      </div>

      {/* ALERTS */}

      <div className="hero-card">
        <h2
          style={{
            color: "#d4af37",
            marginBottom: "18px",
          }}
        >
          Risk Alerts
        </h2>

        {alerts.map((alert, i) => (
          <div
            key={i}
            style={{
              background:
                "rgba(255,179,71,.08)",
              border:
                "1px solid #ffb347",
              borderRadius: "14px",
              padding: "16px",
              marginBottom: "14px",
            }}
          >
            <h3
              style={{
                color: "#ffb347",
              }}
            >
              {alert.type}
            </h3>

            <p style={{ color: "#ddd" }}>
              {alert.message}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default AIPage;