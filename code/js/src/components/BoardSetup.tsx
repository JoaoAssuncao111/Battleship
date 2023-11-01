import { useState, useEffect } from 'react';
import * as React from "react"
import { Navigate, useParams, Link } from 'react-router-dom'
import { Grid } from './Grid';
import { canPlaceShip, findShipHead } from '../utils';

export function BoardSetup() {
    //not optimized at all, optimize if there's time
    let { gid } = useParams();
    const [error, setError] = useState("")
    const [redirect, setRedirect] = useState(false)
    const [waiting, setWaiting] = useState(false)
    const [selectedShip, setSelectedShip] = useState("Submarine" as "Submarine" | "Cruiser " | "Battleship" | "Carrier")
    const [placedShips, setUserShips] = useState([] as placedShip[]);
    const [paintedCells, setPaintedCells] = useState(Array(10).fill(null).map(() => Array(10).fill(null)));

    const [currentDirection, setCurrentDirection] = useState({
        button_text: 'Horizontal',
        value: 'H' as direction
    })

    const [availableShips, setAvailableShips] = useState<Map<string, { "size": number, "count": number }>>(
        new Map([
            ["Submarine", { size: 2, count: 4 }],
            ["Cruiser", { size: 3, count: 3 }],
            ["Battleship", { size: 4, count: 2 }],
            ["Carrier", { size: 5, count: 1 }],
        ])
    );


    useEffect(() => {

        paintedCells.map((row) => row.fill(null));
        setPaintedCells([...paintedCells])
        toDraw()
    }, [placedShips])

    useEffect(() => {
        const timeoutId = setTimeout(() => {
            setError("");
        }, 3000); // 3 seconds

        return () => {
            clearTimeout(timeoutId);
        };
    }, [error]);

    //needed so the newShip's direction in placeship can be the hook value
    type direction = "H" | "V"
    type placedShip = { position: { row: number, col: number }, ship: { name: "Submarine" | "Cruiser " | "Battleship" | "Carrier" }, direction: direction }


    function toDraw() {
        type pair = [number, number];
        placedShips.forEach(ship => {
            let updates = [] as pair[]
            const position = ship.position
            const direction = ship.direction
            const size = availableShips.get(ship.ship.name)!!.size

            //depending on direction one of the position values will remain the same
            direction == 'H'
                ? (() => {
                    for (let i = 0; i < size; i++) {
                        updates.push([position.row, position.col + i])
                    }
                })()
                : (() => {
                    for (let j = 0; j < size; j++) {
                        updates.push([position.row + j, position.col])
                    }
                })()

            updates.forEach(([row, col]) => {
                paintedCells[row][col] = "ship"
            })


        })

        setPaintedCells([...paintedCells])

    }

    function removeShip(row, col) {
        const shipHead = findShipHead(placedShips, paintedCells, row, col)
        const updatedUserShips = placedShips.filter(item => (item.position.row != shipHead.row || item.position.col != shipHead.col))
        const removed = placedShips.filter(item => (item.position.row == shipHead.row && item.position.col == shipHead.col))
        const ship = availableShips.get(removed[0].ship.name)
        ship!!.count++
        setAvailableShips(new Map(availableShips.set(removed[0].ship.name, ship!!)))
        setUserShips(updatedUserShips)

    }

    function placeShip(row, col) {

        const shipDirection = currentDirection.value
        const newShip: placedShip = { position: { row, col }, ship: { name: selectedShip }, direction: shipDirection }
        const size = availableShips.get(newShip.ship.name)!!.size
        if (!canPlaceShip(paintedCells, row, col, size, shipDirection)) return
        if ((currentDirection.value == "H" && newShip.position.col + size > 10) || (currentDirection.value == 'V' && newShip.position.row + size > 10)) return
        for (let i = 0; i < size; i++) {

        }
        //never null since ship names are hardcoded and hook has default values
        let ship = availableShips.get(selectedShip)!!
        if (ship.count <= 0) return
        setAvailableShips(new Map(availableShips.set(selectedShip, ship)))
        placedShips.length == 0 ? setUserShips([newShip]) : setUserShips([...placedShips, newShip])
        ship.count--

    }

    const handleButtonClick = () => {
        currentDirection.button_text == 'Horizontal' ? setCurrentDirection({ button_text: 'Vertical', value: 'V' }) : setCurrentDirection({ button_text: 'Horizontal', value: 'H' })

    }

    const handleSubmit = async () => {
        //can be optimized
        if (availableShips.get("Submarine")!!.count != 0 || availableShips.get("Cruiser")!!.count != 0 ||
            availableShips.get("Battleship")!!.count != 0 || availableShips.get("Carrier")!!.count != 0) {
            setError("There are still ships to be placed")
            return
        }
        //Make actual submit
        const resp = await fetch(`http://localhost:8080/games/${gid}/setup`, {
            method: 'PUT',
            headers: {
                "content-type": "application/json;charset=UTF-8",
                Authorization: `Bearer ${sessionStorage.getItem("user_session")}`,
            },
            body: JSON.stringify({ userShips: [...placedShips] })
        })
        const resp_json = await resp.json()
        if (await resp_json.properties.state == "NEXT_PLAYER_1") { setRedirect(true) }

        setWaiting(true)

        // every 2 seconds check if other user has also submitted ships
        const timer = setInterval(async () => {
            const res = await fetch(`http://localhost:8080/games/${gid}`, {
                method: 'GET',
                headers: {
                    "content-type": "application/json;charset=UTF-8",
                    Authorization: `Bearer ${sessionStorage.getItem("user_session")}`,
                },
            })
            const data = await res.json();

            if (data.state === "NEXT_PLAYER_1") {
                setRedirect(true);
                clearInterval(timer);
            }
        }, 2000);
    }

    const handleShipChange = (event) => {
        event.preventDefault()
        setSelectedShip(event.target.value);
    };

    if (redirect) {
        return <Navigate to={`/games/${gid}`} replace={true} />
    }

    return (

        <div className='wallpaper' >
            <div>
                <select value={selectedShip} onChange={handleShipChange}>
                    <option value="Submarine">Submarine, ({availableShips.get("Submarine")!!.count})</option>
                    <option value="Cruiser">Cruiser, ({availableShips.get("Cruiser")!!.count})</option>
                    <option value="Battleship">Battleship, ({availableShips.get("Battleship")!!.count})</option>
                    <option value="Carrier">Carrier, ({availableShips.get("Carrier")!!.count})</option>

                </select>
                <Grid handleClick={placeShip} handlePaintedClick={removeShip} rows={10} cols={10} paintedCells={paintedCells}></Grid>
            </div>
            <div className='setup-buttons'>
                <button className="direction-button" onClick={handleButtonClick}>{currentDirection.button_text}</button>
                {waiting ? (
                    <p>Waiting for other player...</p>
                ) : (
                    <button className="submit-ship-button" onClick={handleSubmit}>Ready!</button>
                )}
               
            </div>
            <div className='setup-error'>{error}</div>
            <div className="setup_links">
                <Link className="setup_link" to={"/home"}>Home</Link>
                <Link className="setup_link" to={"/me"}>Profile</Link>
            </div>
        </div>

    )

}