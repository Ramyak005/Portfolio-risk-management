import { useEffect, useState } from "react";
import { fetchMarketPrices } from "../api/portfolioApi";

function Ticker() {
  const [prices, setPrices] = useState([]);

  useEffect(() => {
    fetchMarketPrices().then(setPrices);
  }, []);

  return (
    <div className="ticker-wrap">
      <div className="ticker">
        {prices.concat(prices).map((item, index) => (
          <span key={index}>
            {item.stock || item.symbol}
            {" "}
            ${item.price}
          </span>
        ))}
      </div>
    </div>
  );
}

export default Ticker;