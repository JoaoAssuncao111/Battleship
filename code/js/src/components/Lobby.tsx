import { useState, useEffect } from 'react';
import * as React from "react"
import { Navigate } from 'react-router-dom'

export function Lobby() {
    const token = sessionStorage.getItem("user_session")
    const current_user_name = sessionStorage.getItem("current_user")
    //redirect to login since there is no user currently logged in
    if (!token) return <Navigate to={"/login"} replace={true} />
    const [gameId, setGameId] = useState()
    const [redirect, setRedirect] = useState(false)

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch("http://localhost:8080/lobby", {
                    method: "POST",
                    headers: {
                        "content-type": "application/json;charset=UTF-8",
                        Authorization: `Bearer ${token}`,
                    },
                });
                const data = await response.json();

                if (data.properties) {
                    setGameId(data.properties.id);
                    setRedirect(true);
                }
            } catch (error) {
                console.error(error);
            }
        };

        /*Function to keep track of waiting status, if a game has been generated for the current user, the page should redirect to the placing
        phase of that game*/
        const fetchWaitData = async () => {
            try {
                const response = await fetch(
                    `http://localhost:8080/waitstatus/${current_user_name}`, { method: 'GET' }
                );
                const data = await response.json();
                setGameId(data);
                if (data != null) setRedirect(true)
            } catch (error) {
                console.error(error);
            }
        };



        fetchData();

        let intervalId = setInterval(fetchWaitData, 1000);

        return () => clearInterval(intervalId);
    }, []);


    if (redirect) {
        return <Navigate to={`/games/${gameId}/setup`} replace={true} />
    }

    return (
        <div>
            <h1 className="waiting-message">Waiting for another player...</h1>
            <div className="loading-spinner"></div>
        </div>
    )
}