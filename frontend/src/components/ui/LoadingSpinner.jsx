export default function LoadingSpinner({ label = 'Loading...' }) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-12 text-slate-400">
      <div
        className="h-10 w-10 animate-spin rounded-full border-2 border-slate-700 border-t-cyan-400"
        role="status"
        aria-label={label}
      />
      <p className="text-sm">{label}</p>
    </div>
  )
}
