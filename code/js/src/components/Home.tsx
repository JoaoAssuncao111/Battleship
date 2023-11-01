import { Link } from 'react-router-dom'
import { useState } from 'react'
import * as React from "react"

export function Home() {

    React.useEffect(() => {
        

    },[])
    
    function handleLogout() {
        sessionStorage.removeItem('user_session');
        location.reload()
      }

    return (
        
        <div>
            <div className="link-container">
                <Link className="link" to="/home">Home</Link>
                {sessionStorage.getItem("user_session") ? <Link className="link" to="/lobby">Join Game</Link> : <Link className="link" to="/register">Register</Link>}
                {sessionStorage.getItem("user_session") ? <Link className="link" to="/me">My Profile</Link> : <Link className="link" to="/login">Login</Link>}
                <Link className="link" to="/leaderboard">Leaderboard</Link>
            </div>
            {sessionStorage.getItem("user_session") ? <div><button className="logout-button" onClick={handleLogout}>Logout</button></div> : null }
            <h1 className='home-header'>Battleship</h1>
            <div className='authors'>by 47181 João Assunção and 47531 Guilherme Cepeda </div>
        </div>
            
    );
    
    }