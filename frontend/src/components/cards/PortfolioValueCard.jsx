import Card from '../ui/Card'
import { formatCurrency, diversificationColor, diversificationLabel } from '../../utils/formatters'

const HOLDINGS_DISPLAY_LIMIT = 8

export default function PortfolioValueCard({ portfolio, clientMetrics }) {
  if (!portfolio) return null

  const holdings = portfolio.holdings ?? {}
  const entries = Object.entries(holdings).slice(0, HOLDINGS_DISPLAY_LIMIT)
  const remaining = Object.keys(holdings).length - entries.length
  const diversificationScore = clientMetrics?.diversificationScore

  return (
    <Card
      title="Portfolio Value"
      subtitle={portfolio.clientName}
      icon={
        <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8V6m0 12v-2m9-4a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      }
    >
      <p className="text-4xl font-bold tracking-tight text-white">
        {formatCurrency(portfolio.portfolioValue)}
      </p>
      <p className="mt-2 text-sm text-slate-400">Total market value</p>

      {diversificationScore != null && (
        <div className="mt-4 rounded-xl border border-slate-700/60 bg-slate-800/30 px-3 py-2.5">
          <div className="flex items-center justify-between text-sm">
            <span className="text-slate-400">Diversification</span>
            <span className={`font-semibold ${diversificationColor(diversificationScore)}`}>
              {diversificationScore.toFixed(1)} — {diversificationLabel(diversificationScore)}
            </span>
          </div>
        </div>
      )}

      <ul className="mt-5 max-h-48 space-y-2 overflow-y-auto border-t border-slate-800 pt-4">
        {entries.map(([symbol, shares]) => (
          <li
            key={symbol}
            className="flex items-center justify-between rounded-lg bg-slate-800/40 px-3 py-2 text-sm"
          >
            <span className="font-medium text-slate-200">{symbol}</span>
            <span className="text-slate-400">{shares} shares</span>
          </li>
        ))}
        {remaining > 0 && (
          <li className="px-3 py-1 text-xs text-slate-500">
            + {remaining} more holding{remaining > 1 ? 's' : ''}
          </li>
        )}
      </ul>
    </Card>
  )
}
