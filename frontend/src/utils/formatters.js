export function formatCurrency(value) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(value ?? 0)
}

export function formatPercent(value) {
  return `${Number(value ?? 0).toFixed(1)}%`
}

export function formatCompactNumber(value) {
  return new Intl.NumberFormat('en-US', {
    notation: 'compact',
    maximumFractionDigits: 1,
  }).format(value ?? 0)
}

export function holdingsToChartData(holdings = {}, limit = 12) {
  return Object.entries(holdings)
    .map(([symbol, shares]) => ({
      symbol,
      shares: Number(shares),
    }))
    .sort((a, b) => b.shares - a.shares)
    .slice(0, limit)
}

export function marketPricesToChartData(prices = [], limit = 15) {
  return prices
    .map(({ stock, price }) => ({
      stock,
      price: Number(price),
    }))
    .sort((a, b) => b.price - a.price)
    .slice(0, limit)
}

/** Minimum chart width scales with data points for horizontal scroll on large datasets */
export function chartMinWidth(itemCount, barWidth = 56) {
  return Math.max(320, itemCount * barWidth)
}

export function diversificationColor(score) {
  if (score >= 70) return 'text-emerald-400'
  if (score >= 40) return 'text-amber-400'
  return 'text-rose-400'
}

export function diversificationLabel(score) {
  if (score >= 70) return 'Well Diversified'
  if (score >= 40) return 'Moderate'
  return 'Concentrated'
}
