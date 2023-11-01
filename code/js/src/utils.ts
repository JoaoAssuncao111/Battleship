import { isHtmlElement } from "react-router-dom/dist/dom"

export function canPlaceShip(board, row, col, shipSize, direction) {

    for (let x = 0; x < shipSize; x++) {
        let newCol = col
        let newRow = row
        direction == "H" ? newCol = col + x : newRow = row + x
        for (let i = newRow - 1; i <= newRow + 1; i++) {
            for (let j = newCol - 1; j <= newCol + 1; j++) {
                if (i >= 0 && i < board.length && j >= 0 && j < board[i].length) {
                    if (board[i][j] == "ship") return false
                }
            }
        }
    }
    return true
}

export function findShipHead(board, paintedBoard, row, col) {
    //Band aid function to fix the bad backend design
    const directions = [[0, 1], [0, -1], [1, 0], [-1, 0]];
    for (const [dRow, dCol] of directions) {
        if (row + dRow >= 0 && row + dRow < paintedBoard.length && col + dCol >= 0 && col + dCol < paintedBoard[0].length) {
            for (let i = row, j = col; paintedBoard[i][j] == "ship"; i += dRow, j += dCol) {
                console.log(`Row : ${i}   Col : ${j}`)
                const ship = board.filter(item => (item.position.row == i && item.position.col == j));
                if (ship && ship.length != 0){ 
                    console.log(ship)
                    return ship[0].position;}
            }
        }
    }

}


export function buildPaintedCells(board){

    let retArray = Array(10).fill(null).map(() => Array(10).fill(null))

    let tempBoard =  board
    tempBoard = tempBoard.map(row => row.map(tile => tile.content));
    tempBoard.forEach((row, i) => {
        row.forEach((element, j) => {
            retArray[i][j] = element
        });
    });

    return retArray;

}

export function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
  

  