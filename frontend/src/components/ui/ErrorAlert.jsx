export default function ErrorAlert({ message, onRetry }) {
  if (!message) return null

  return (
    <div
      role="alert"
      className="flex flex-col gap-3 rounded-xl border border-rose-500/30 bg-rose-500/10 px-4 py-3 text-rose-200 sm:flex-row sm:items-center sm:justify-between"
    >
      <p className="text-sm">{message}</p>
      {onRetry && (
        <button
          type="button"
          onClick={onRetry}
          className="shrink-0 rounded-lg bg-rose-500/20 px-3 py-1.5 text-sm font-medium text-rose-100 transition hover:bg-rose-500/30"
        >
          Retry
        </button>
      )}
    </div>
  )
}
