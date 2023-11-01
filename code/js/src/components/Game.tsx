import * as React from "react"
import { useState, useEffect } from "react"
import { Navigate, useParams, Link } from 'react-router-dom'
import { Timer } from "./Timer";
import { Grid } from './Grid';
import { buildPaintedCells, sleep } from '../utils';



export function Game() {
    //not optimized at all, optimize if there's time
    const token = sessionStorage.getItem("user_session")
    const current_user_name = sessionStorage.getItem("current_user")

    type gametype = {
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
    type pair = [number | undefined, number | undefined]
    let { gid } = useParams()
    const [error, setError] = useState("")
    const [redirect, setRedirect] = useState(false)//dont know if it is necessary yet
    const [userPaintedCells, setUserPaintedCells] = useState(Array(10).fill(null).map(() => Array(10).fill(null)))
    const [opponentPaintedCells, setOpponentPaintedCells] = useState(Array(10).fill(null).map(() => Array(10).fill(null)))
    const [paintedShot, setPaintedShot] = useState<pair>([undefined, undefined]);
    const [game, setGame] = useState<gametype>()
    const [userDetails, setUserDetails] = useState([current_user_name, "board"])
    const [opponentDetails, setOpponentDetails] = useState(["name", "board"])
    const [turn, setTurn] = useState<string>("")
    const [winner, setWinner] = useState<string>("")


    useEffect(() => {
        const fetchGame = async () => {
            try {
                const response = await fetch(`http://localhost:8080/games/${gid}`, {
                    method: "GET",
                    headers: {
                        "content-type": "application/json;charset=UTF-8",
                        Authorization: `Bearer ${token}`,
                    },
                });
                const data = await response.json()

                if (data) {

                    setGame(data)

                }
            } catch (error) {
                console.error(error)
                return
            }
        }
        //so the boards loadup immediately
        fetchGame()
        //So the userboard is updated
        let intervalId = setInterval(fetchGame, 1000);

        return () => clearInterval(intervalId);


    }, [])

    //Reading current player board and transfering its content to paintedCells to later be drawn

    useEffect(() => {
        if (game) {

            if (current_user_name == game.player1.username) {
                setOpponentDetails([game.player2.username, game.boardPlayer2.board])
                setUserDetails([current_user_name, game.boardPlayer1.board])
            } else {
                setOpponentDetails([game.player1.username, game.boardPlayer1.board])
                setUserDetails([current_user_name, game.boardPlayer2.board])
            }

            if (game.state == "NEXT_PLAYER_1" && current_user_name == game.player1.username ||
                game.state == "NEXT_PLAYER_2" && current_user_name == game.player2.username) { setTurn(current_user_name!!) }

            else if (game.state == "NEXT_PLAYER_2" && current_user_name == game.player1.username ||
                game.state == "NEXT_PLAYER_1" && current_user_name == game.player2.username) { setTurn(opponentDetails[0]) }

            //gameover  
            else {
                if (game.state == "PLAYER_1_WON") {
                    setWinner(game.player1.username)
                    setTurn("")
                }
                if (game.state == "PLAYER_2_WON") {
                    setWinner(game.player2.username)
                    setTurn("")
                }

                //setting the boards to show the position of the boats after the game ends
                if (current_user_name == game.player1.username) {
                    setUserPaintedCells([...buildPaintedCells(game.boardPlayer1.board)])
                    setOpponentPaintedCells([...buildPaintedCells(game.boardPlayer2.board)])
                } else {
                    setUserPaintedCells([...buildPaintedCells(game.boardPlayer2.board)])
                    setOpponentPaintedCells([...buildPaintedCells(game.boardPlayer1.board)])
                }

                return
            }

            let currentUserBoard = current_user_name == game.player1.username ? game.boardPlayer1.board : game.boardPlayer2.board
            currentUserBoard = currentUserBoard.map(row => row.map(tile => tile.content));
            currentUserBoard.forEach((row, i) => {
                row.forEach((element, j) => {
                    userPaintedCells[i][j] = element
                });
            });

            setUserPaintedCells([...userPaintedCells])


            let opponentUserBoard
            if (current_user_name == game.player1.username) {
                opponentUserBoard = game.boardPlayer2.board
            } else { opponentUserBoard = game.boardPlayer1.board }



            opponentUserBoard = opponentUserBoard.map(row => row.map(tile => tile.content));
            opponentUserBoard.forEach((row, i) => {
                row.forEach((element, j) => {
                    if (element == "HIT_WATER" || element == "HIT_SHIP" || element == "SUNK_SHIP") {
                        opponentPaintedCells[i][j] = element
                    }
                });
            });

            setOpponentPaintedCells([...opponentPaintedCells])

        }

    }, [game])


    //needed to see the changes


    //verifies if possible play and updates painted cells to latest user click
    function applyShot(row, col) {

        if (paintedShot[0] != undefined && paintedShot[1] != undefined) {
            opponentPaintedCells[paintedShot[0]][paintedShot[1]] = null
        }
        opponentPaintedCells[row][col] = "SHOT"
        setOpponentPaintedCells([...opponentPaintedCells])
        setPaintedShot([row, col]);

    }


    const handleSubmit = async () => {

        if ((current_user_name == game!!.player1.username && game!!.state == "NEXT_PLAYER_2") || (current_user_name == game!!.player2.username && game!!.state == "NEXT_PLAYER_1")) {
            console.log("Not your turn")
            window.alert("Not your turn")
        } else {

            const position = { position: { row: paintedShot[0], col: paintedShot[1] } }
            console.log("position", position)
            //make actual submit
            const resp = await fetch(`http://localhost:8080/games/${gid}`, {
                method: 'PUT',
                headers: {
                    "content-type": "application/json;charset=UTF-8",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(position)
            })
            const resp_json = await resp.json()

            if (await resp_json.properties) {
                setGame(resp_json.properties)
                setPaintedShot([undefined, undefined])
                console.log("Game updated")
            }
        }
    }

    //if the game ends or error
    if (redirect) {
        return <Navigate to={`/me`} replace={true} />
    }

    const endGameByTimeout = async () => {
        //timeout delay
        await sleep(2000)
        if ((current_user_name == game!!.player1.username && game!!.state == "NEXT_PLAYER_1") || (current_user_name == game!!.player2.username && game!!.state == "NEXT_PLAYER_2")) {
            const resp = await fetch(`http://localhost:8080/games/${gid}`, {
                method: 'PUT',
                headers: {
                    "content-type": "application/json;charset=UTF-8",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({ position: { row: 0, col: 0 } })
            })
            const resp_json = await resp.json()
            setGame(await resp_json.properties)
        }
    }

    return (


        <div className="wallpaper">
            <div className="center_items">
                {turn != "" ? (<p className="turn_or_win_text"> {turn}'s Turn</p>) :
                    (<div>
                        <p className="turn_or_win_text"> {winner} Won</p>

                    </div>)}
            </div>
            <div className="grid_container">
                <div style={{ opacity: turn == current_user_name ? 0.2 : 1 }} className="grid_user">
                    <div className="user-board-text">{userDetails[0]}'s Board</div>
                    {/*`first grid is for the current user, is watch only and the second grid is the clickable one*/}
                    <Grid handleClick={null} handlePaintedClick={null} rows={10} cols={10} paintedCells={userPaintedCells}></Grid>
                </div>
                <div style={{ opacity: turn == opponentDetails[0] ? 0.2 : 1 }} className="grid_opponent">
                    <div className="user-board-text">{opponentDetails[0]}'s Board</div>
                    <Grid handleClick={applyShot} handlePaintedClick={null} rows={10} cols={10} paintedCells={opponentPaintedCells}></Grid>

                </div>

                <div></div>
            </div>

            <div className="shoot-button-container"><button className="shoot-button"onClick={handleSubmit} disabled={turn !== current_user_name}>Shoot!</button></div>
            {error}
            <div className="timer-container"><p className="timer"><Timer deadline={game?.deadline} timeoutFunc={endGameByTimeout}></Timer></p></div>
            <div className="game_links">
                <Link className="game_link" to={"/home"}>Home</Link>
                <Link className="game_link" to={"/me"}>Profile</Link>
            </div>

        </div>
    )
}

