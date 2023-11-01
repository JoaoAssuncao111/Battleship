import { useState, useEffect } from 'react';
import * as React from "react"
import { Navigate, Link } from 'react-router-dom'

export function Register() {
    const [redirect, setRedirect] = useState(false)
    const [formInputData, setInputData] = useState({
        username: "",
        password: ""
    });
    const [error, setError] = useState("")


    const data = formInputData

    //handles form input change
    const handleChange = (event) => {
        setInputData({
            ...formInputData,
            [event.target.name]: event.target.value
        })
    }

    const handleSubmit = (event) => {
        event.preventDefault();
        fetch("http://localhost:8080/register",
            {
                method: 'POST',
                body: JSON.stringify(formInputData),
                headers: { 'content-type': 'application/json;charset=UTF-8' }
            })
            .then(resp => resp.json())
            .then(data => {
                if (typeof data === 'string') setError("Username already in use or password too weak")
                else setRedirect(true)
            })

    }

    useEffect(() => {
        const timeoutId = setTimeout(() => {
            setError("");
        }, 3000);

        return () => {
            clearTimeout(timeoutId);
        };
    }, [error]);

    if (redirect) {
        return <Navigate to={"/login"} replace={true} />
    }

    return (
        <div>
            <div className="link-container">
                <Link className="link" to="/home">Home</Link>
                <Link className="link" to="/leaderboard">Leaderboard</Link>
            </div>

            <div className="form">
                <form onSubmit={handleSubmit}>
                    <h3>Register</h3>
                    <input placeholder="Username" type="text" name="username" onChange={handleChange} />
                    <input placeholder="Password" type="password" name="password" onChange={handleChange} />

                    <button className="form-button" type="submit">Register</button>
                    {error}
                    <p><Link to='/login'>Already have an account?</Link></p>
                </form>
                <div className='error'>{error}</div>
            </div>
        </div>


    );
}