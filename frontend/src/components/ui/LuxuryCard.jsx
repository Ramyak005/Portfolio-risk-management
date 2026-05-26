/**
 * Luxury Card Component
 * 
 * Reusable card component with BLACK + GOLD luxury theme
 * Features:
 * - Luxury styling with gold accents
 * - Smooth hover effects
 * - Responsive design
 * - Optional gradient background
 * - Professional animations
 */
export function LuxuryCard({ 
  children, 
  className = "", 
  hover = true,
  gradient = false,
  onClick = null,
  title = null,
  icon = null
}) {
  return (
    <div 
      className={`
        bg-black border border-gold rounded-lg p-6
        ${gradient ? 'bg-gradient-to-br from-black via-gray-900 to-black' : 'bg-black'}
        ${hover ? 'hover:border-gold-light hover:shadow-lg hover:shadow-gold/20 hover:scale-105' : ''}
        transition-all duration-300 ease-in-out
        ${onClick ? 'cursor-pointer' : ''}
        ${className}
      `}
      onClick={onClick}
    >
      {(title || icon) && (
        <div className="flex items-center justify-between mb-4">
          {title && (
            <h3 className="text-gold font-bold text-lg">{title}</h3>
          )}
          {icon && (
            <div className="text-gold text-2xl">{icon}</div>
          )}
        </div>
      )}
      {children}
    </div>
  );
}

export default LuxuryCard;
