import Card from '../ui/Card'
import { formatCurrency, formatPercent, diversificationColor } from '../../utils/formatters'

export default function LeaderboardCard({ entries = [], onSelectClient, selectedClientId }) {
  return (
    <Card
      title="Leaderboard"
      subtitle={`Top ${entries.length} portfolios by value`}
      icon={
        <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" />
        </svg>
      }
    >
      <div className="max-h-80 overflow-y-auto">
        <table className="w-full text-sm">
          <thead className="sticky top-0 bg-slate-900/95 text-left text-xs uppercase tracking-wide text-slate-500">
            <tr>
              <th className="pb-2 pr-2">#</th>
              <th className="pb-2 pr-2">Client</th>
              <th className="pb-2 pr-2 text-right">Value</th>
              <th className="pb-2 pr-2 text-right">Diversification</th>
              <th className="pb-2 text-right">Top Risk</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/80">
            {entries.map((entry) => (
              <tr
                key={entry.clientId}
                onClick={() => onSelectClient?.(entry.clientId)}
                className={`text-slate-300 transition ${
                  onSelectClient ? 'cursor-pointer hover:bg-slate-800/50' : ''
                } ${selectedClientId === entry.clientId ? 'bg-cyan-500/10' : ''}`}
              >
                <td className="py-2.5 pr-2 font-semibold text-cyan-400">{entry.rank}</td>
                <td className="py-2.5 pr-2 font-medium text-slate-100">{entry.clientName}</td>
                <td className="py-2.5 pr-2 text-right tabular-nums">{formatCurrency(entry.portfolioValue)}</td>
                <td className={`py-2.5 pr-2 text-right tabular-nums ${diversificationColor(entry.diversificationScore)}`}>
                  {formatPercent(entry.diversificationScore)}
                </td>
                <td className="py-2.5 text-right tabular-nums text-amber-400">
                  {formatPercent(entry.topRiskExposure)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Card>
  )
}
