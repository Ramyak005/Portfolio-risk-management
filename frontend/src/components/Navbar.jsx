import { Link } from "react-router-dom";
import { FaGem } from "react-icons/fa";

function Navbar() {
  return (
    <nav className="lux-navbar">
      <div className="logo-area">
        <FaGem />
        <span>BeautyVest</span>
      </div>

      <div className="nav-links">
        <Link to="/">Home</Link>
        <Link to="/market">Market</Link>
        <Link to="/clients">Clients</Link>
        <Link to="/ai">AI Studio</Link>
        <Link to="/builder">Builder</Link>
      </div>
    </nav>
  );
}

export default Navbar;