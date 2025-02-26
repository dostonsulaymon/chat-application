import React from "react";
import {Link, useNavigate} from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Dashboard: React.FC = () => {
    const {logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    return (
        <div className="container">
            <h1>Welcome to the Dashboard</h1>
            <p>You have successfully logged in!</p>
            <button onClick={handleLogout} className="back-btn">Logout</button>
            <Link to="/" className="back-btn">Go Back</Link>
        </div>
    );
};

export default Dashboard;