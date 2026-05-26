export default function ClientSelector({ clients = [], selectedId, onChange, loading }) {
  return (
    <div className="rounded-2xl border border-slate-800/80 bg-slate-900/60 p-4 backdrop-blur-sm">
      <label htmlFor="client-select" className="block text-xs font-semibold uppercase tracking-wide text-slate-400">
        Client Portfolio
      </label>
      <select
        id="client-select"
        value={selectedId ?? ''}
        onChange={(e) => onChange(Number(e.target.value))}
        disabled={loading || clients.length === 0}
        className="mt-2 w-full rounded-xl border border-slate-700 bg-slate-950 px-4 py-2.5 text-sm font-medium text-slate-100 outline-none transition focus:border-cyan-500/50 focus:ring-2 focus:ring-cyan-500/20 disabled:cursor-not-allowed disabled:opacity-60 sm:max-w-md"
      >
        {clients.map((client) => (
          <option key={client.id} value={client.id}>
            {client.name} — {formatCompactCurrency(client.portfolioValue)}
          </option>
        ))}
      </select>
      <p className="mt-2 text-xs text-slate-500">
        Select a client to view AI-generated portfolio recommendations
      </p>
    </div>
  )
}

function formatCompactCurrency(value) {
  if (value >= 1_000_000) return `$${(value / 1_000_000).toFixed(1)}M`
  if (value >= 1_000) return `$${(value / 1_000).toFixed(0)}K`
  return `$${value.toFixed(0)}`
}
