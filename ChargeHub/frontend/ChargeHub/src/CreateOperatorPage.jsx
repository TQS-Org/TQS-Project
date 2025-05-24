import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./CreateOperatorPage.css";

export default function CreateOperatorPage() {
  const [formData, setFormData] = useState({
    name: "",
    mail: "",
    password: "",
    age: "",
    number: "",
    address: ""
  });

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    const token = localStorage.getItem("token");

    const response = await fetch("http://localhost:8080/api/staff/operator", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify(formData)
    });

    if (response.ok) {
      setSuccess("Operator created successfully.");
      setFormData({
        name: "",
        mail: "",
        password: "",
        age: "",
        number: "",
        address: ""
      });
    } else {
      const msg = await response.text();
      setError(msg || "Something went wrong.");
    }
  };

  return (
    <div className="create-operator-page">
      <form className="create-operator-form" onSubmit={handleSubmit}>
        <h2>Create Operator Account</h2>

        <input
          type="text"
          name="name"
          placeholder="Name"
          value={formData.name}
          onChange={handleChange}
          required
        />

        <input
          type="email"
          name="mail"
          placeholder="Email"
          value={formData.mail}
          onChange={handleChange}
          required
        />

        <input
          type="password"
          name="password"
          placeholder="Password"
          value={formData.password}
          onChange={handleChange}
          required
        />

        <input
          type="number"
          name="age"
          placeholder="Age"
          value={formData.age}
          onChange={handleChange}
          required
        />

        <input
          type="text"
          name="number"
          placeholder="Phone Number"
          value={formData.number}
          onChange={handleChange}
        />

        <input
          type="text"
          name="address"
          placeholder="Address"
          value={formData.address}
          onChange={handleChange}
        />

        <button type="submit">Create Operator</button>

        {success && <p className="success-msg">{success}</p>}
        {error && <p className="error-msg">{error}</p>}
      </form>
    </div>
  );
}
