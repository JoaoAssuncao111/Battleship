import * as React from 'react'
import { createBrowserRouter,RouterProvider, useParams, } from 'react-router-dom'

import { Home } from '../components/Home';
import { Register } from '../components/Register';
import { Login } from '../components/Login';
import { UserHome } from '../components/UserHome';
import { Lobby } from '../components/Lobby';
import { Leaderboard } from '../components/Leaderboard';
import { BoardSetup } from '../components/BoardSetup';
import {Game} from "../components/Game";
import '../styles.css'



const router = createBrowserRouter([
    {
        "path": "/home",
        "element": <Home />
    },
    {
        "path": "/leaderboard",
        "element": <Leaderboard />
    },
    {
        "path": "/lobby",
        "element": <Lobby />
    },

    {
        "path": "/login",
        "element": <Login />
    },
    {
        "path": "/games/:gid",
        "element": <Game />
    },
    {
        "path": "/games/:gid/setup",
        "element": <BoardSetup />
    },
    {
        "path": "/error",
        "element": <Error />
    },
    {
        "path": "/register",
        "element": <Register />
    },
    {
        "path": "/todo",
        "element": <UserGameDetail />
    },
    {
        "path": "/me",
        "element": <UserHome />
    },


])

export function App() {
    return (
        <RouterProvider router={router} />
    )
}

function UserGameDetail() {
    const { gid, uid } = useParams()
    return (
        <div>
            <h2>User Game Detail</h2>
            {uid}, {gid}
        </div>
    )
}
function Error() {
    return (
        <div>
            <h1>ERROR</h1>
        </div>
    )
}
