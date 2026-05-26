import { useState } from 'react'
import Card from '../ui/Card'
import ErrorAlert from '../ui/ErrorAlert'
import LoadingSpinner from '../ui/LoadingSpinner'
import { calculatePortfolio } from '../../api/portfolioApi'
import { formatCurrency } from '../../utils/formatters'

const emptyRow = () => ({ id: crypto.randomUUID(), symbol: '', shares: '' })

export default function PortfolioCalculatorForm({ onCalculated }) {
  const [clientName, setClientName] = useState('')
  const [rows, setRows] = useState([emptyRow(), emptyRow()])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [result, setResult] = useState(null)

  const updateRow = (id, field, value) => {
    setRows((prev) =>
      prev.map((row) => (row.id === id ? { ...row, [field]: value } : row)),
    )
  }

  const addRow = () => setRows((prev) => [...prev, emptyRow()])

  const removeRow = (id) => {
    setRows((prev) => (prev.length > 1 ? prev.filter((row) => row.id !== id) : prev))
  }

  const buildHoldings = () => {
    const holdings = {}
    for (const row of rows) {
      const symbol = row.symbol.trim().toUpperCase()
      const shares = parseInt(row.shares, 10)
      if (symbol && !Number.isNaN(shares) && shares > 0) {
        holdings[symbol] = shares
      }
    }
    return holdings
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError(null)
    setResult(null)

    const name = clientName.trim()
    const holdings = buildHoldings()

    if (!name) {
      setError('Please enter a client name.')
      return
    }
    if (Object.keys(holdings).length === 0) {
      setError('Add at least one valid stock symbol and share count.')
      return
    }

    setLoading(true)
    try {
      const portfolio = await calculatePortfolio({ clientName: name, holdings })
      setResult(portfolio)
      onCalculated?.(portfolio)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card title="Portfolio Calculator" subtitle="Calculate value from holdings">
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label htmlFor="clientName" className="mb-1.5 block text-sm font-medium text-slate-300">
            Client name
          </label>
          <input
            id="clientName"
            type="text"
            value={clientName}
            onChange={(e) => setClientName(e.target.value)}
            placeholder="e.g. Ramya"
            className="w-full rounded-xl border border-slate-700 bg-slate-800/60 px-4 py-2.5 text-slate-100 placeholder:text-slate-500 focus:border-cyan-500 focus:outline-none focus:ring-1 focus:ring-cyan-500"
          />
        </div>

        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <p className="text-sm font-medium text-slate-300">Stock holdings</p>
            <button
              type="button"
              onClick={addRow}
              className="text-sm font-medium text-cyan-400 hover:text-cyan-300"
            >
              + Add row
            </button>
          </div>

          {rows.map((row) => (
            <div key={row.id} className="flex flex-col gap-2 sm:flex-row sm:items-end">
              <div className="flex-1">
                <label className="mb-1 block text-xs text-slate-500">Symbol</label>
                <input
                  type="text"
                  value={row.symbol}
                  onChange={(e) => updateRow(row.id, 'symbol', e.target.value)}
                  placeholder="AAPL"
                  className="w-full rounded-xl border border-slate-700 bg-slate-800/60 px-3 py-2 text-slate-100 uppercase placeholder:normal-case placeholder:text-slate-500 focus:border-cyan-500 focus:outline-none focus:ring-1 focus:ring-cyan-500"
                />
              </div>
              <div className="flex-1">
                <label className="mb-1 block text-xs text-slate-500">Shares</label>
                <input
                  type="number"
                  min="1"
                  value={row.shares}
                  onChange={(e) => updateRow(row.id, 'shares', e.target.value)}
                  placeholder="10"
                  className="w-full rounded-xl border border-slate-700 bg-slate-800/60 px-3 py-2 text-slate-100 focus:border-cyan-500 focus:outline-none focus:ring-1 focus:ring-cyan-500"
                />
              </div>
              <button
                type="button"
                onClick={() => removeRow(row.id)}
                className="rounded-xl border border-slate-700 px-3 py-2 text-sm text-slate-400 transition hover:border-rose-500/50 hover:text-rose-300 sm:mb-0"
                aria-label="Remove row"
              >
                Remove
              </button>
            </div>
          ))}
        </div>

        {error && <ErrorAlert message={error} />}

        <button
          type="submit"
          disabled={loading}
          className="w-full rounded-xl bg-indigo-500 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-400 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {loading ? 'Calculating...' : 'Calculate portfolio value'}
        </button>
      </form>

      {loading && <LoadingSpinner label="Calculating portfolio..." />}

      {result && !loading && (
        <div className="mt-5 rounded-xl border border-emerald-500/30 bg-emerald-500/10 p-4">
          <p className="text-xs font-semibold uppercase tracking-wide text-emerald-400">
            Calculated result
          </p>
          <p className="mt-2 text-2xl font-bold text-white">
            {formatCurrency(result.portfolioValue)}
          </p>
          <p className="mt-1 text-sm text-slate-300">
            Client: <span className="font-medium text-slate-100">{result.clientName}</span>
          </p>
          <ul className="mt-3 space-y-1 text-sm text-slate-400">
            {Object.entries(result.holdings ?? {}).map(([symbol, shares]) => (
              <li key={symbol}>
                {symbol}: {shares} shares
              </li>
            ))}
          </ul>
        </div>
      )}
    </Card>
  )
}
