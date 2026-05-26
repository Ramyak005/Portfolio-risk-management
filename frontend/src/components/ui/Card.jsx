export default function Card({ title, subtitle, icon, children, className = '' }) {
  return (
    <section
      className={`rounded-2xl border border-slate-800/80 bg-slate-900/60 p-5 shadow-xl shadow-black/20 backdrop-blur-sm ${className}`}
    >
      {(title || subtitle) && (
        <header className="mb-4 flex items-start justify-between gap-3">
          <div>
            {title && (
              <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-400">
                {title}
              </h2>
            )}
            {subtitle && (
              <p className="mt-1 text-lg font-semibold text-slate-100">{subtitle}</p>
            )}
          </div>
          {icon && (
            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-cyan-500/10 text-cyan-400">
              {icon}
            </div>
          )}
        </header>
      )}
      {children}
    </section>
  )
}
