insert into users(username,password_validation,score)
VALUES ('ze','pass123',0),
       ('test1','pass456',1),
       ('test2','pass456',2),
       ('test3','pass456',3),
       ('test4','pass456',4),
       ('test5','pass456',24),
       ('test6','pass456',25),
       ('test7','pass456',26),
       ('test8','pass456',27),
       ('test9','pass456',1),
       ('test10','pass456',1),
       ('test11','pass456',2),
       ('test12','pass456',10),
       ('test13','pass456',12),
       ('test14','pass456',14),
       ('test15','pass456',17),
       ('test16','pass456',10);

/*insert into tokens(token_validation, user_id,created_at,last_used_at) VALUES ('zzzzzz',1, now()::timestamp,now()::timestamp),
                          ('cccccc',2,now()::timestamp,now()::timestamp);*/

insert into games(id, state, boardplayer1,boardplayer2, created, updated, deadline, player1, player2)
VALUES ('40e6215d-b5c6-4896-987c-f30f3678f608','NEXT_PLAYER_1','boardPlayer1','boardPlayer2',now()::timestamp,now()::timestamp,300000,1,2);
--'2022-10-14 04:05:06' ,'2022-10-14 04:05:06'

insert into lobby(user_id) values (1);

select games.id, games.state, games.boardPlayer1,games.boardPlayer2, games.created, games.updated, games.deadline,
       users_1.id as player1_id, users_1.username as player1_username, users_1.password_validation as player1_password_validation, users_1.score as player1_score,
       users_2.id as player2_id, users_2.username as player2_username, users_2.password_validation as player2_password_validation, users_2.score as player2_score
from Games games
         inner join Users users_1 on games.player1 = users_1.id
         inner join Users users_2 on games.player2 = users_2.id
where games.id = 'f6340297-d9e5-49df-b16b-8ccd1b3f2327'