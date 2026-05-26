/**
 * Risk Badge Component
 * 
 * Displays risk severity with color coding
 * Severities:
 * - LOW: Green
 * - MEDIUM: Yellow
 * - HIGH: Orange
 * - CRITICAL: Red
 */
export function Badge({ 
  children, 
  severity = "low",
  className = ""
}) {
  const severityColors = {
    low: "bg-green-900/30 text-green-300 border border-green-700",
    medium: "bg-yellow-900/30 text-yellow-300 border border-yellow-700",
    high: "bg-orange-900/30 text-orange-300 border border-orange-700",
    critical: "bg-red-900/30 text-red-300 border border-red-700"
  };
  
  return (
    <span className={`
      px-3 py-1 rounded-full text-sm font-semibold
      ${severityColors[severity.toLowerCase()]}
      ${className}
    `}>
      {children}
    </span>
  );
}

export default Badge;
