import { useState, useEffect } from 'react';
import { fetchRiskSummary, fetchMarketPrices } from '../api/portfolioApi';

/**
 * Real-Time Data Hook
 * 
 * Fetches data from backend APIs at regular intervals
 * Features:
 * - Automatic polling every 5 seconds
 * - Error handling and retry logic
 * - Loading state management
 * - Configurable refresh interval
 * 
 * @param {number} interval - Refresh interval in milliseconds (default: 5000)
 * @returns {Object} - { data, loading, error, refresh }
 */
export function useRealTimeData(interval = 5000) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchData = async () => {
    try {
      setLoading(true);
      
      // Fetch risk summary and market prices in parallel
      const [riskData, pricesData] = await Promise.all([
        fetchRiskSummary(),
        fetchMarketPrices()
      ]);
      
      setData({
        risk: riskData,
        prices: pricesData,
        timestamp: new Date().toISOString()
      });
      
      setError(null);
    } catch (err) {
      console.error('Error fetching real-time data:', err);
      setError(err.message || 'Failed to fetch data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // Fetch immediately on mount
    fetchData();
    
    // Set up interval for periodic updates
    const intervalId = setInterval(fetchData, interval);
    
    // Cleanup interval on unmount
    return () => clearInterval(intervalId);
  }, [interval]);

  return { 
    data, 
    loading, 
    error,
    refresh: fetchData 
  };
}

/**
 * Risk Data Hook
 * 
 * Fetches risk-specific data
 * 
 * @param {string} clientId - Client ID (optional)
 * @param {number} interval - Refresh interval in milliseconds
 * @returns {Object} - { riskData, loading, error }
 */
export function useRiskData(clientId = null, interval = 5000) {
  const [riskData, setRiskData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchRiskData = async () => {
      try {
        setLoading(true);
        
        if (clientId) {
          // Fetch specific client risk data
          const response = await fetch(`/api/v1/risk/clients/${clientId}`);
          const data = await response.json();
          setRiskData(data);
        } else {
          // Fetch firm-wide risk summary
          const response = await fetch('/api/v1/risk/summary');
          const data = await response.json();
          setRiskData(data);
        }
        
        setError(null);
      } catch (err) {
        console.error('Error fetching risk data:', err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchRiskData();
    const intervalId = setInterval(fetchRiskData, interval);
    
    return () => clearInterval(intervalId);
  }, [clientId, interval]);

  return { riskData, loading, error };
}

/**
 * Market Data Hook
 * 
 * Fetches market price data
 * 
 * @param {number} interval - Refresh interval in milliseconds
 * @returns {Object} - { prices, loading, error }
 */
export function useMarketData(interval = 5000) {
  const [prices, setPrices] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchPrices = async () => {
      try {
        setLoading(true);
        const response = await fetch('/api/v1/market/prices');
        const data = await response.json();
        setPrices(data);
        setError(null);
      } catch (err) {
        console.error('Error fetching market data:', err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchPrices();
    const intervalId = setInterval(fetchPrices, interval);
    
    return () => clearInterval(intervalId);
  }, [interval]);

  return { prices, loading, error };
}

export default useRealTimeData;
