import Card from '../ui/Card'
import { formatCurrency, formatPercent } from '../../utils/formatters'

export default function TopRiskExposuresCard({ exposures = [] }) {
  return (
    <Card
      title="Top Risk Exposures"
      subtitle="Firm-wide concentration by stock"
      icon={
        <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" />
        </svg>
      }
    >
      <ul className="space-y-3">
        {exposures.map((exposure) => (
          <li key={exposure.stock} className="rounded-xl bg-slate-800/40 px-4 py-3">
            <div className="flex items-center justify-between gap-3">
              <span className="font-semibold text-slate-100">{exposure.stock}</span>
              <span className="text-sm font-medium text-amber-400">
                {formatPercent(exposure.exposurePercent)}
              </span>
            </div>
            <div className="mt-2 h-1.5 overflow-hidden rounded-full bg-slate-700">
              <div
                className="h-full rounded-full bg-gradient-to-r from-amber-500 to-rose-500"
                style={{ width: `${Math.min(exposure.exposurePercent, 100)}%` }}
              />
            </div>
            <p className="mt-1.5 text-xs text-slate-500">
              Position value: {formatCurrency(exposure.positionValue)}
            </p>
          </li>
        ))}
      </ul>
    </Card>
  )
}
