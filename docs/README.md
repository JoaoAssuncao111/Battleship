# BATTLESHIP API Route Documentation

* `/api/home`

  * `GET` Home screen.

* `/api/leaderboard`

  * `GET` List of the top 10 scorers. One game win = 1 score point

* `/api/register`

  * `POST` Create an account, usernames are unique

* `/api/login`

  * `PUT` Log in using valid credentials

* `/api/me`

  * `GET` User Home screen. Contains all games the user has participated/is participating in
  
* `/api/lobby`

  * `POST` Join matchmaking queue
  
* `/api/games/{gameId}/setup`

  * `PUT` Ship placing phase of a matchmade game
 
* `/api/games/{gameId}`

  * `GET` Go to specified game screen. Only users participating in the game can access it
  * `PUT` Make a play in the specified game

