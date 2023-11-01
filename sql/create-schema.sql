

create table Users(
                          id   int generated always as identity primary key,
                          username VARCHAR(64) unique not null,
                          password_validation VARCHAR(256) not null,
                          score int not null
);

create table Tokens(
                           token_validation VARCHAR(256) primary key,
                           user_id int references Users(id)
);

create table Games(
                          id UUID  not null PRIMARY KEY,
                          state VARCHAR(64) not null,
                          boardPlayer1 VARCHAR(50000) not null,
                          boardPlayer2 VARCHAR(50000) not null,
                          created bigint not null,
                          updated bigint not null,
                          deadline int,
                          player1 int references Users(id),
                          player2 int references Users(id)
);

create table Lobby(
                        user_id int references Users(id),
                        lobby_username VARCHAR(64) references Users(username)
);

create table WaitStatus(
                        username varchar(64) references Users(username),
                        game_id UUID references Games(id)


)