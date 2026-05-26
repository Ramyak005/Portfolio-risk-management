import api from './client'

export const fetchDashboard = () => api.get('/dashboard').then((r) => r.data)

export const fetchPortfolio = () => api.get('/portfolio').then((r) => r.data)

export const fetchRiskAlerts = () =>
  api.get('/portfolio/risk').then((r) => r.data)

export const fetchAIInsight = () =>
  api.get('/portfolio/ai-insight').then((r) => r.data)

export const fetchMarketPrices = () =>
  api.get('/market-prices').then((r) => r.data)

export const fetchClients = () => api.get('/clients').then((r) => r.data)

export const fetchClient = (id) => api.get(`/clients/${id}`).then((r) => r.data)

export const fetchClientAnalysis = (id) =>
  api.get(`/clients/${id}/analysis`).then((r) => r.data)

export const fetchClientPortfolio = (id) =>
  api.get(`/clients/${id}/portfolio`).then((r) => r.data)

export const fetchClientRisk = (id) =>
  api.get(`/clients/${id}/risk`).then((r) => r.data)

export const fetchClientAIInsight = (id) =>
  api.get(`/clients/${id}/ai-insight`).then((r) => r.data)

export const fetchStocks = () => api.get('/stocks').then((r) => r.data)

export const fetchLeaderboard = () => api.get('/leaderboard').then((r) => r.data)

export const calculatePortfolio = (request) =>
  api.post('/portfolio/calculate', request).then((r) => r.data)
