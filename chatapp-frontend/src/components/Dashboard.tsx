import React from "react";
import { Link } from "react-router-dom";

const Dashboard: React.FC = () => {
    return (
        <div className="container">
            <h1>Welcome to the Dashboard</h1>
            <p>This is your secured area.</p>
            <Link to="/" className="back-btn">Go Back</Link>
        </div>
    ); 
};

export default Dashboard;