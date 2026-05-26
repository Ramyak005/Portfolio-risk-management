import Card from '../ui/Card'
import { formatCurrency, formatPercent, diversificationColor, diversificationLabel } from '../../utils/formatters'

export default function DashboardSummaryCard({ summary }) {
  if (!summary) return null

  const stats = [
    { label: 'Total Clients', value: summary.totalClients?.toLocaleString() },
    { label: 'Stocks Tracked', value: summary.totalStocks?.toLocaleString() },
    { label: 'Total AUM', value: formatCurrency(summary.totalAssetsUnderManagement) },
    { label: 'Avg Portfolio', value: formatCurrency(summary.averagePortfolioValue) },
  ]

  return (
    <Card
      title="Firm Overview"
      subtitle="Portfolio simulation at scale"
      icon={
        <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
        </svg>
      }
    >
      <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
        {stats.map(({ label, value }) => (
          <div key={label} className="rounded-xl bg-slate-800/40 px-3 py-3">
            <p className="text-xs font-medium uppercase tracking-wide text-slate-500">{label}</p>
            <p className="mt-1 text-lg font-bold text-white">{value}</p>
          </div>
        ))}
      </div>

      <div className="mt-4 rounded-xl border border-slate-700/60 bg-slate-800/30 px-4 py-3">
        <p className="text-xs font-medium uppercase tracking-wide text-slate-500">
          Average Diversification Score
        </p>
        <div className="mt-2 flex items-baseline gap-2">
          <span className={`text-3xl font-bold ${diversificationColor(summary.averageDiversificationScore)}`}>
            {formatPercent(summary.averageDiversificationScore).replace('%', '')}
          </span>
          <span className="text-sm text-slate-400">/ 100</span>
          <span className={`ml-auto text-sm font-medium ${diversificationColor(summary.averageDiversificationScore)}`}>
            {diversificationLabel(summary.averageDiversificationScore)}
          </span>
        </div>
        <div className="mt-3 h-2 overflow-hidden rounded-full bg-slate-700">
          <div
            className="h-full rounded-full bg-gradient-to-r from-cyan-500 to-indigo-500 transition-all"
            style={{ width: `${Math.min(summary.averageDiversificationScore ?? 0, 100)}%` }}
          />
        </div>
      </div>
    </Card>
  )
}
