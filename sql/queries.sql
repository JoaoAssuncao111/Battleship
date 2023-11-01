

select * from games where (player1 in (
    SELECT id from users where username = 'usertest')
    OR player2 in (SELECT id from users where username = 'usertest'));

select * from users where username= 'usertest';

select * from games
where cast(player1 as users) in (
    SELECT users from users where username = 'usertest')
   OR cast(player2 as users) in (SELECT users from users where username = 'usertest');


select  games.id,games.state,games.boardplayer1,games.boardplayer2,games.created, games.updated,games.deadline,games.player1,games.player2,u.id as player1ID , u.username as player1Username , u.password_validation as player1PassVal, u.score as player1Score from  games
         join users u on games.player1 = u.id
where (player1 in (
    SELECT id from users where username = 'usertest')
    OR player2 in (SELECT id from users where username = 'usertest'));


select *, u.id as player1Id from games
join users u on u.id = games.player1 or  games.player2 = u.id
where player1=u.id or player2=u.id;

select * from users
where id='1';

--apanha os jogos todos dos users com o username correto
select games.id, games.state, games.boardPlayer1,games.boardPlayer2, games.created, games.updated, games.deadline,
       users_1.id as player1_id, users_1.username as player1_username, users_1.password_validation as player1_password_validation, users_1.score as player1_score,
       users_2.id as player2_id, users_2.username as player2_username, users_2.password_validation as player2_password_validation, users_2.score as player2_score
from Games games
         inner join Users users_1 on games.player1 = users_1.id
         inner join Users users_2 on games.player2 = users_2.id
where (player1 in (
    SELECT id from users where username = 'usertest')
    OR player2 in (SELECT id from users where username = 'usertest'))