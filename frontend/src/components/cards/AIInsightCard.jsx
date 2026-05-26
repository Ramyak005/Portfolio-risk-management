import Card from '../ui/Card'

export default function AIInsightCard({ insight, loading }) {
  return (
    <Card
      title="AI Insight"
      subtitle={loading ? 'Generating recommendation...' : 'Powered by OpenAI'}
      icon={
        <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
        </svg>
      }
    >
      {loading && (
        <div className="flex items-center gap-3 py-6 text-sm text-slate-400">
          <svg className="h-5 w-5 animate-spin text-cyan-400" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          Analyzing portfolio with AI...
        </div>
      )}

      {!loading && insight && (
        <>
          <p className="text-base leading-relaxed text-slate-200">{insight.insight}</p>
          <div className="mt-4 rounded-xl border border-cyan-500/20 bg-cyan-500/5 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-cyan-400">
              Recommendation
            </p>
            <p className="mt-2 text-sm leading-relaxed text-slate-300">
              {insight.recommendation}
            </p>
          </div>
        </>
      )}

      {!loading && !insight && (
        <p className="text-sm text-slate-400">Select a client to view AI recommendations.</p>
      )}
    </Card>
  )
}
