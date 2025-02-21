package tudelft.wis.idm_solutions.BoardGameTracker.JDBC_Implementation;

import tudelft.wis.idm_tasks.boardGameTracker.BgtException;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BgtDataManager;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BoardGame;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.PlaySession;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.Player;

import javax.swing.text.DateFormatter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

import static tudelft.wis.idm_solutions.BoardGameTracker.JDBC_Implementation.Utils.*;

public class BgtDataManager_JDBC implements BgtDataManager {
    private Connection connection;

    private Connection getConnection() throws BgtException {
        if (connection != null) {
            return connection;
        }

        Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "postgres");
        String databaseUrl = "jdbc:postgresql://localhost:5432/boardGames";
        try {
            connection = DriverManager.getConnection(databaseUrl, properties);
            connection.setAutoCommit(false);
            return connection;
        }
        catch (SQLException exception) {
            throwBgtException(exception);
            return null;
        }
    }

    public void clearAllTables() throws BgtException {
        try {
            String fileName = "src/main/resources/tables.sql";
            String sqlCode = String.join("\n", Files.readAllLines(Paths.get(fileName)));
            var jdbcConnection = getConnection();
            PreparedStatement statement = jdbcConnection.prepareStatement(sqlCode);
            statement.execute();
            jdbcConnection.commit();
        }
        catch (Exception e) {
            throwBgtException(e);
        }


    }

    @Override
    public Player createNewPlayer(String name, String nickname) throws BgtException {
        Player player = new Player_JDBC(name, nickname, new LinkedList<>());
        persistPlayer(player);
        return player;
    }

    @Override
    public Collection<Player> findPlayersByName(String name) throws BgtException {
        return findPlayersByExactName("%" + name + "%");
    }

    private Collection<Player> findPlayersByExactName(String name) throws BgtException {
        String sql = """
                SELECT * FROM player AS p
                WHERE p.name LIKE ?
                """;
        return filterPlayers(sql, List.of(name));
    }

    private Collection<Player> findAllPlayersBySession(
            String sessionBggUrl,
            Date sessionDate,
            String sessionHostName) throws BgtException {
        String sql = """
                SELECT * FROM player AS p
                    JOIN player_session AS ps ON p.name = ps.name
                WHERE ps.bggurl = ? AND ps.date = ? AND ps.host_name = ?
                """;
        return filterPlayers(sql, List.of(sessionBggUrl, new Timestamp(sessionDate.getTime()), sessionHostName));
    }

    private Collection<Player> filterPlayers(String sql, List<Object> params) throws BgtException {
        Connection jdbcConnection = getConnection();


        try (PreparedStatement statement = jdbcConnection.prepareStatement(sql)) {
            for (int i = 1; i <= params.size(); i++) {
                if (params.get(i - 1) instanceof String) {
                    statement.setString(i, (String)params.get(i - 1));
                }
                else if (params.get(i - 1) instanceof Integer){
                    statement.setInt(i, (Integer) params.get(i - 1));
                }
                else if (params.get(i - 1) instanceof Timestamp){
                    statement.setTimestamp(i, (Timestamp) params.get(i - 1));
                }
            }

            ResultSet resultSet = statement.executeQuery();
            jdbcConnection.commit();

            return resultSetToCollection(mp -> {
                try {
                    return new Player_JDBC(
                            (String) mp.get("name"),
                            (String) mp.get("nickname"),
                            findGamesByExactPlayerName((String) mp.get("name")));
                } catch (BgtException e) {
                    throw new RuntimeException(e);
                }
            }, resultSet);
        }
        catch (SQLException exception) {
            throwBgtException(exception);
            return null;
        }
    }

    @Override
    public BoardGame createNewBoardgame(String name, String bggURL) throws BgtException {
        BoardGame boardGame = new BoardGame_JDBC(name, bggURL);
        persistBoardGame(boardGame);
        return boardGame;
    }

    @Override
    public Collection<BoardGame> findGamesByName(String name) throws BgtException {
        return findGamesByExactName("%" + name + "%");
    }

    private Collection<BoardGame> findGamesByExactName(String name) throws BgtException {
        String sql = """
                SELECT *
                FROM game AS g
                WHERE g.name LIKE ?
                """;
        return filterBoardGames(sql, name);
    }

    private BoardGame findGameByBggUrl(String url) throws BgtException {
        String sql = """
                SELECT *
                FROM game AS g
                WHERE g.bggurl LIKE ?
                """;
        return pickAny(filterBoardGames(sql, url));
    }

    private Collection<BoardGame> findGamesByExactPlayerName(String playerName) throws BgtException {
        String sql = """
                SELECT g.name, g.bggurl
                FROM game AS g
                    JOIN player_game_collection AS pgg ON g.bggurl = pgg.bggurl
                WHERE pgg.name = ?
                """;

        return filterBoardGames(sql, playerName);
    }

    private Collection<BoardGame> filterBoardGames(String sql, String param) throws BgtException{
        Connection jdbcConnection = getConnection();
        try (PreparedStatement statement = jdbcConnection.prepareStatement(sql)) {
            statement.setString(1, param);
            ResultSet resultSet = statement.executeQuery();
            jdbcConnection.commit();

            return resultSetToCollection(
                    mp -> new BoardGame_JDBC(
                            (String) mp.get("name"),
                            (String) mp.get("bggurl")),
                    resultSet);
        }
        catch (SQLException exception) {
            throwBgtException(exception);
            return null;
        }
    }

    @Override
    public PlaySession createNewPlaySession(
            Date date,
            Player host,
            BoardGame game,
            int playtime,
            Collection<Player> players,
            Player winner) throws BgtException {
        PlaySession playSession = new PlaySession_JDBC(
                date,
                host,
                game,
                playtime,
                players,
                winner
        );
        persistPlaySession(playSession);
        return playSession;
    }

    @Override
    public Collection<PlaySession> findSessionByDate(Date date) throws BgtException {
        Connection jdbcConnection = getConnection();
        String sql = """
                SELECT * FROM session AS s
                WHERE s.date = ?
                """;

        try (PreparedStatement statement = jdbcConnection.prepareStatement(sql)) {
            var sqlDate = new Timestamp(date.getTime());
            statement.setTimestamp(1, sqlDate);
            ResultSet resultSet = statement.executeQuery();
            jdbcConnection.commit();

            return resultSetToCollection(
                    mp -> {
                        Timestamp sessionDate = (Timestamp) mp.get("date");
                        String sessionBggUrl = (String)mp.get("bggurl");
                        int sessionPlayTime = (int)mp.get("playtime");
                        String sessionHostName = (String)mp.get("host_name");
                        String sessionWinnerName = (String)mp.get("winner_name");

                        try {
                            return new PlaySession_JDBC(
                                    sessionDate,
                                    pickAny(findPlayersByExactName(sessionHostName)),
                                    findGameByBggUrl(sessionBggUrl),
                                    sessionPlayTime,
                                    findAllPlayersBySession(sessionBggUrl, sessionDate, sessionHostName),
                                    pickAny(findPlayersByExactName(sessionWinnerName))
                            );
                        } catch (BgtException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    resultSet);
        }
        catch (SQLException exception) {
            throwBgtException(exception);
            return null;
        }
    }

    @Override
    public void persistPlayer(Player player) throws BgtException {
        Connection jdbcConnection = getConnection();
        String updatePlayerSql = """
                INSERT INTO player (name, nickname)
                VALUES (?, ?)
                ON CONFLICT (name)
                DO UPDATE SET nickname = EXCLUDED.nickname;
                """;
        String gamesCleanSql = """
                DELETE FROM player_game_collection AS pgc
                WHERE pgc.name = ?
                """;
        String addBoardGameSql = """
                INSERT INTO player_game_collection (name, bggurl)
                VALUES (?, ?)
                """;

        try (PreparedStatement playerStatement = jdbcConnection.prepareStatement(updatePlayerSql);
             PreparedStatement boardGameStatement = jdbcConnection.prepareStatement(addBoardGameSql);
             PreparedStatement gamesCleanStatement = jdbcConnection.prepareStatement(gamesCleanSql)) {
            playerStatement.setString(1, player.getPlayerName());
            playerStatement.setString(2, player.getPlayerNickName());
            playerStatement.execute();

            gamesCleanStatement.setString(1, player.getPlayerName());
            gamesCleanStatement.execute();

            boardGameStatement.setString(1, player.getPlayerName());
            for (BoardGame boardGame : player.getGameCollection()) {
                boardGameStatement.setString(2, boardGame.getBGG_URL());
                boardGameStatement.execute();
            }

            jdbcConnection.commit();
        }
        catch (SQLException exception) {
            throwBgtException(exception);
        }
    }

    @Override
    public void persistPlaySession(PlaySession session) throws BgtException {
        Connection jdbcConnection = getConnection();
        String sessionUpdateSql = """
                INSERT INTO session (bggurl, date, playtime, winner_name, host_name)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (bggurl, date, host_name)
                DO UPDATE SET winner_name = EXCLUDED.winner_name, playtime = EXCLUDED.playtime;
                """;
        String playerCleanSql = """
                DELETE FROM player_session AS ps
                WHERE ps.bggurl = ? AND ps.date = ? AND ps.host_name = ?;
                """;
        String playerUpdateSql = """
                INSERT INTO player_session (name, bggurl, date, host_name)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement sessionUpdateStatement = jdbcConnection.prepareStatement(sessionUpdateSql);
             PreparedStatement playerUpdateStatement = jdbcConnection.prepareStatement(playerUpdateSql);
             PreparedStatement playerCleanStatement = jdbcConnection.prepareStatement(playerCleanSql)) {

            // Update session
            sessionUpdateStatement.setString(1, session.getGame().getBGG_URL());
            sessionUpdateStatement.setTimestamp(2, new Timestamp(session.getDate().getTime()));
            sessionUpdateStatement.setInt(3, session.getPlaytime());
            sessionUpdateStatement.setString(4, session.getWinner().getPlayerName());
            sessionUpdateStatement.setString(5, session.getHost().getPlayerName());
            sessionUpdateStatement.execute();

            // Remove previous data from player_session
            playerCleanStatement.setString(1, session.getGame().getBGG_URL());
            playerCleanStatement.setTimestamp(2, new Timestamp(session.getDate().getTime()));
            playerCleanStatement.setString(3, session.getHost().getPlayerName());
            playerCleanStatement.execute();

            // Put new data into player_session
            playerUpdateStatement.setString(2, session.getGame().getBGG_URL());
            playerUpdateStatement.setTimestamp(3, new Timestamp(session.getDate().getTime()));
            playerUpdateStatement.setString(4, session.getHost().getPlayerName());
            for (Player player : session.getAllPlayers()) {
                playerUpdateStatement.setString(1, player.getPlayerName());
                playerUpdateStatement.execute();
            }

            jdbcConnection.commit();
        }
        catch (SQLException exception) {
            throwBgtException(exception);
        }
    }

    @Override
    public void persistBoardGame(BoardGame game) throws BgtException {
        Connection jdbcConnection = getConnection();
        String sql = """
                INSERT INTO game (name, bggurl)
                VALUES (?, ?)
                ON CONFLICT (bggurl)
                DO UPDATE SET name = EXCLUDED.name;
                """;

        try (PreparedStatement statement = jdbcConnection.prepareStatement(sql)) {
            statement.setString(1, game.getName());
            statement.setString(2, game.getBGG_URL());
            statement.execute();
            jdbcConnection.commit();
        }
        catch (SQLException exception) {
            throwBgtException(exception);
        }
    }
}
