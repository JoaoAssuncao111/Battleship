import { Link } from 'react-router-dom'
import * as React from "react"
import { useEffect, useState } from 'react'

export function UserHome() {
    type game = {
        id: string,
        state: string,
        boardPlayer1,
        boardPlayer2,
        created: string,
        updated: string,
        deadline: string,
        player1,
        player2,
    }
    const username = sessionStorage.getItem("current_user")
    const [userGames, setUserGames] = useState<game[]>([])
    const [score, setScore] = useState()
    useEffect(() => {
        const fetchData = async () => {
            const data = await fetch("http://localhost:8080/usergames", {
                method: 'GET',
                headers: {
                    "content-type": "application/json;charset=UTF-8",
                    Authorization: `Bearer ${sessionStorage.getItem("user_session")}`,
                }
            })
            const data_json = await data.json()
            console.log(await data_json)
            setUserGames(await data_json)
        }

        const fetchUserScore = async () => {
            const data = await fetch(`http://localhost:8080/users/${username}`, {
                method: 'GET',
                headers: {
                    "content-type": "application/json;charset=UTF-8",
                }
            })
            const data_json = await data.json()
            console.log(await data_json)
            setScore(await data_json.score)
        }



        fetchData();
        fetchUserScore()

    }, [])

    return (
        <div>

            <div className="link-container">
                <Link className="link" to="/home">Home</Link>
                <Link className="link" to="/lobby">Join Game</Link>
                <Link className="link" to="/leaderboard">Leaderboard</Link>
            </div>

            <h1 className='user-profile'>{sessionStorage.getItem("current_user")}'s Profile</h1>
            <div className='profile-container'>
                <a className='user-games'>Your Games:</a> <a className='user-profile-score'>Your score: {score}</a>

            </div>
            <div>


                {userGames ? userGames.map(game => (

                    <p><Link className="user-game" to={
                        game.state != 'BOARD_SETUP' && game.state != 'ONE_PLAYER_READY'
                            ? `/games/${game.id}`
                            : `/games/${game.id}/setup`
                    } key={game.id}>{game.player1.username} VS {game.player2.username},  Started at: {new Date(game.created).toLocaleString()}
                        {game.state == 'NEXT_PLAYER_1' || game.state == 'NEXT_PLAYER_2'
                            ? " Ongoing"
                            : game.state == 'BOARD_SETUP' || game.state == 'ONE_PLAYER_READY'
                                ? " Setup"
                                : game.state == 'PLAYER_1_WON' 
                                ? ` ${game.player1.username} Won`
                                : ` ${game.player2.username} Won`} </Link></p>
                )) : null}
            </div>



        </div>
    )


}