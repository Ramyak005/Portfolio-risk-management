import { useState } from "react";

export default function TickerHoverCard({ stock }) {
  const [show, setShow] = useState(false);

  const fakeTrend = [
    stock.price * 0.7,
    stock.price * 0.82,
    stock.price * 0.75,
    stock.price * 0.95,
    stock.price
  ];

  return (
    <div
      style={{ position: "relative", display: "inline-block" }}
      onMouseEnter={() => setShow(true)}
      onMouseLeave={() => setShow(false)}
    >
      <span
        style={{
          color: "#d4af37",
          fontWeight: 600,
          cursor: "pointer",
          marginRight: 35
        }}
      >
        {stock.symbol} ${stock.price}
      </span>

      {show && (
        <div
          style={{
            position: "absolute",
            top: 35,
            left: 0,
            width: 240,
            background: "#111",
            border: "1px solid #d4af37",
            borderRadius: 18,
            padding: 18,
            boxShadow: "0 10px 35px rgba(0,0,0,0.45)",
            zIndex: 999
          }}
        >
          <h3
            style={{
              color: "#d4af37",
              marginBottom: 10
            }}
          >
            {stock.name}
          </h3>

          <p style={{ color: "#bbb" }}>
            {stock.sector}
          </p>

          <p
            style={{
              color: "white",
              fontSize: 22,
              fontWeight: 700
            }}
          >
            ${stock.price}
          </p>

          <div
            style={{
              display: "flex",
              alignItems: "flex-end",
              gap: 6,
              marginTop: 18,
              height: 70
            }}
          >
            {fakeTrend.map((v, i) => (
              <div
                key={i}
                style={{
                  width: 18,
                  height: `${v / 12}px`,
                  background: "#d4af37",
                  borderRadius: 6
                }}
              />
            ))}
          </div>
        </div>
      )}
    </div>
  );
}