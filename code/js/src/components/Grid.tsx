import { useState } from 'react';
import * as React from "react"

export function Grid({ handleClick, handlePaintedClick, rows, cols, paintedCells }) {

    const [preview, setPreview] = useState(null);
    const [board, setBoard] = useState(
        Array(rows).fill(null).map(() => Array(cols).fill(null))
    );

    function handleCellClick(row, col) {
        handleClick(row, col)
        console.log(`Row: ${row}, Column: ${col}`);
    }

    function handlePaintedCellClick(row, col) {
        handlePaintedClick(row, col)
        console.log(`Painted Row: ${row}, Column: ${col}`);
    }

    /*const handleMouseEnter = (row, col) => {
        setPreview(handleHover(row, col));
      };
    
      const handleMouseLeave = () => {
        setPreview(null);
      }
      */

    

   return (
        <div className='center_items'>
            {board.map((row, i) => (
                <div key={i}>
                    {row.map((cell, j) => {
                        switch(paintedCells[j][i]){
                            case null : return <div className='grid_cell_setup' key={j} onClick={() => handleCellClick(j, i)}></div>
                            case "WATER": return <div className='grid_cell_game' key={j}></div>
                            //ship in placingPhase
                            case "ship": return <div className='blue_cell_setup' key={j} onClick={() => handlePaintedCellClick(j, i)}></div>
                            //user ship in game
                            case "SHIP": return <div className='blue_cell_game' key={j}></div>
                            //represents hit water in Game
                            case "HIT_WATER": return <div className='hit_water_cell_game' key={j}></div>
                            //represents hit ship in Game
                            case "HIT_SHIP": return <div className='hit_ship_cell_game' key={j}></div>
                            //represents sunk ship in Game
                            case "SUNK_SHIP": return <div className='sunk_ship_cell_game' key={j}></div>
                            //represents the preview of the shot before user makes it
                            case "SHOT": return <div className='shotPreview' key={j}></div>
                            default : return <div className='grid_cell_game' key={j} onClick={() => handlePaintedCellClick(j, i)}></div>

                        }

                    })}
                </div>
            ))}
        </div>
    );
    
}
