import { Link } from "react-router-dom"
import { useFetch } from "../router/useFetch"
import * as React from "react"

export function Leaderboard() {
    type leaderboard_json = [{ "username": string, "score": number }]
    const data = useFetch<leaderboard_json>("http://localhost:8080/leaderboard", { method: 'GET' })
    if (data) {

        return (

            <div>
                <div className="link-container">
                    <Link className="link" to="/home">Home</Link>
                    {sessionStorage.getItem("user_session") ? <Link className="link" to="/lobby">Join Game</Link> : <Link className="link" to="/register">Register</Link>}
                    {sessionStorage.getItem("user_session") ? <Link className="link" to="/me">My Profile</Link> : <Link className="link" to="/login">Login</Link>}
                </div>
                <div className="leaderboard">{data.map(item =>
                    <div className="item" key={item.username}>
                        <a>Username: {item.username}</a>
                        <a>Score: {item.score}</a>

                    </div>)}
                </div>
            </div>)
    } else { return <div>There are currently no users</div> }

}