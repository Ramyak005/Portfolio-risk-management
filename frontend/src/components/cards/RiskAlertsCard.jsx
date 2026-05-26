import Card from '../ui/Card'

const riskStyles = {
  HIGH_EXPOSURE: {
    border: 'border-amber-500/30',
    bg: 'bg-amber-500/10',
    badge: 'bg-amber-500/20 text-amber-300',
  },
  LOW_RISK: {
    border: 'border-emerald-500/30',
    bg: 'bg-emerald-500/10',
    badge: 'bg-emerald-500/20 text-emerald-300',
  },
}

function getStyle(riskType) {
  return riskStyles[riskType] ?? {
    border: 'border-slate-600/30',
    bg: 'bg-slate-800/40',
    badge: 'bg-slate-700 text-slate-300',
  }
}

export default function RiskAlertsCard({ alerts = [] }) {
  return (
    <Card
      title="Risk Alerts"
      icon={
        <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
      }
    >
      <ul className="space-y-3">
        {alerts.map((alert, index) => {
          const style = getStyle(alert.riskType)
          return (
            <li
              key={`${alert.riskType}-${index}`}
              className={`rounded-xl border p-4 ${style.border} ${style.bg}`}
            >
              <span
                className={`inline-block rounded-md px-2 py-0.5 text-xs font-semibold uppercase ${style.badge}`}
              >
                {alert.riskType.replace(/_/g, ' ')}
              </span>
              <p className="mt-2 text-sm text-slate-200">{alert.message}</p>
            </li>
          )
        })}
      </ul>
    </Card>
  )
}
