import { useState } from "react";
import { authenticate } from "../services/userService";
import { Link } from "react-router-dom";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await authenticate(email, password);
      setMessage(response.data ? "Login successful ✅" : "Invalid credentials ❌");
    } catch (error) {
      console.error(error);
      setMessage("Server error");
    }
  };

  return (
    <div>
      <h2>Login</h2>

      <form onSubmit={handleLogin}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        <button type="submit">Login</button>
      </form>

      <p>
        New user? <Link to="/register">Register here</Link>
      </p>

      <p>{message}</p>
    </div>
  );
}

export default Login;
