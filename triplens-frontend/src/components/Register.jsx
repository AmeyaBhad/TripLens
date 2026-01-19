import { useState } from "react";
import { addUser } from "../services/userService";
import { Link } from "react-router-dom";

function Register() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      const response = await addUser({ name, email, password });
      setMessage(response.data ? "User registered successfully ✅" : "Registration failed ❌");
    } catch (error) {
      console.error(error);
      setMessage("Server error");
    }
  };

  return (
    <div>
      <h2>Register</h2>

      <form onSubmit={handleRegister}>
        <input
          placeholder="Name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />

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

        <button type="submit">Register</button>
      </form>

      <p>
        Already have an account? <Link to="/">Login</Link>
      </p>

      <p>{message}</p>
    </div>
  );
}

export default Register;
