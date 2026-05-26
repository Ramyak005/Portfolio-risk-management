/**
 * Luxury Button Component
 * 
 * Reusable button component with BLACK + GOLD luxury theme
 * Variants:
 * - primary: Gold background with black text
 * - secondary: Gold border with gold text
 * - ghost: Transparent with gold text
 */
export function Button({ 
  children, 
  variant = "primary",
  size = "md",
  disabled = false,
  onClick = null,
  className = "",
  ...props 
}) {
  const variants = {
    primary: "bg-gold text-black hover:bg-gold-light hover:shadow-lg hover:shadow-gold/30",
    secondary: "border-2 border-gold text-gold hover:bg-gold hover:text-black hover:shadow-lg hover:shadow-gold/30",
    ghost: "text-gold hover:bg-gold hover:bg-opacity-10 hover:shadow-lg hover:shadow-gold/20"
  };
  
  const sizes = {
    sm: "px-3 py-1 text-sm",
    md: "px-4 py-2 text-base",
    lg: "px-6 py-3 text-lg"
  };
  
  return (
    <button 
      className={`
        ${variants[variant]}
        ${sizes[size]}
        rounded-lg font-semibold
        transition-all duration-300 ease-in-out
        disabled:opacity-50 disabled:cursor-not-allowed
        ${className}
      `}
      disabled={disabled}
      onClick={onClick}
      {...props}
    >
      {children}
    </button>
  );
}

export default Button;
