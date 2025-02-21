/*
 Drop schema and recreate to reset
 */
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;


/*
 CREATE TABLE statements
 */
CREATE TABLE IF NOT EXISTS player (
    name VARCHAR(100) PRIMARY KEY,
    nickname VARCHAR(100) NOT NULL
);
CREATE TABLE IF NOT EXISTS game (
    name VARCHAR(100) NOT NULL,
    bggURL VARCHAR(500) PRIMARY KEY
);
CREATE TABLE IF NOT EXISTS player_game_collection (
    name VARCHAR(100),
    bggURL VARCHAR(500),
    PRIMARY KEY (name, bggURL),
    FOREIGN KEY (name) REFERENCES player(name),
    FOREIGN KEY (bggURL) REFERENCES game(bggURL)
);
CREATE TABLE IF NOT EXISTS session (
    bggURL VARCHAR(500) NOT NULL,
    date TIMESTAMP NOT NULL,
    playTime INT NOT NULL,
    winner_name VARCHAR(100) NOT NULL,
    host_name VARCHAR(100) NOT NULL,
    PRIMARY KEY (bggURL, host_name, date),
    FOREIGN KEY (bggURL) REFERENCES game(bggURL),
    FOREIGN KEY (winner_name) REFERENCES player(name),
    FOREIGN KEY (host_name) REFERENCES player(name)
);
CREATE TABLE IF NOT EXISTS player_session (
    name VARCHAR(100),
    bggURL VARCHAR(500),
    date TIMESTAMP NOT NULL,
    host_name VARCHAR(100) NOT NULL,
    PRIMARY KEY (name, bggURL, date, host_name),
    FOREIGN KEY (name) REFERENCES player(name),
    FOREIGN KEY (bggURL, host_name, date) REFERENCES session(bggURL, host_name, date)
);


/*
 Sample data insertions
 */

/*
INSERT INTO player
VALUES
    ('testName1', 'testNickname1'),
    ('testName2', 'testNickname2'),
    ('testName3', 'testNickname3');

INSERT INTO game
VALUES
    ('testGame1', 'testUrl1'),
    ('testGame2', 'testUrl2');

INSERT INTO player_game_collection
VALUES
    ('testName1', 'testUrl1'),
    ('testName1', 'testUrl2'),
    ('testName2', 'testUrl1');

INSERT INTO session (bggURL, date, playTime, winner_name, host_name)
VALUES
    ('testUrl1', '2024-10-10', 60, 'testName3', 'testName1'),
    ('testUrl2', '2024-08-10', 120, 'testName2', 'testName2');

INSERT INTO player_session
VALUES
    ('testName1', 'testUrl1', '2024-10-10', 'testName1'),
    ('testName2', 'testUrl1', '2024-10-10', 'testName1'),
    ('testName3', 'testUrl1', '2024-10-10', 'testName1'),

    ('testName1', 'testUrl2', '2024-08-10', 'testName2'),
    ('testName2', 'testUrl2', '2024-08-10', 'testName2');
*/

/*
 Sql queries
 */

/*
SELECT * FROM session AS s
WHERE s.date = '2024-10-10'
*/