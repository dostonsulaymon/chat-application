import React from "react";
import { FcGoogle } from "react-icons/fc";
import { FaGithub } from "react-icons/fa";
import { Link } from "react-router-dom";

const Home: React.FC = () => {
    const handleLogin = (provider: "google" | "github" | "custom") => {
        const urls = {
            google: "http://localhost:8080/oauth2/authorization/google",
            github: "http://localhost:8080/oauth2/authorization/github",
            custom: "http://localhost:8080/api/auth/login"
        };
        window.location.href = urls[provider];
    };

    return (
        <div className="container">
            <h2>Login with your preferred provider:</h2>
            <button onClick={() => handleLogin("google")} className="login-btn">
                <FcGoogle /> Login with Google
            </button>
            <button onClick={() => handleLogin("github")} className="login-btn">
                <FaGithub /> Login with GitHub
            </button>
            <Link to="/login" className="login-btn">Custom Login</Link>
        </div>
    );
};

export default Home;