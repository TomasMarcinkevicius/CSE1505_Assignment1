package tudelft.wis.idm_solutions.BoardGameTracker.JDBC_Implementation;

import org.junit.jupiter.api.Test;
import org.tinylog.Logger;
import tudelft.wis.idm_solutions.BoardGameTracker.AbstractBGTDemo;
import tudelft.wis.idm_solutions.BoardGameTracker.POJO_Implementation.BgtDataManager_POJO;
import tudelft.wis.idm_tasks.boardGameTracker.BgtException;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BgtDataManager;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BoardGame;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.PlaySession;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.Player;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class BgtDataManager_JDBCTest extends AbstractBGTDemo {
    private BgtDataManager_JDBC dataManager = new BgtDataManager_JDBC();

    @Override
    public BgtDataManager getBgtDataManager() {
        return dataManager;
    }

    /**
     * Just runs the application with some simple queries and assertions. It's
     * not very comprehensive, essentially, just a single session is retrieved
     * and the hist and the game is being checked.
     */
    @Test
    public void basicTest() throws BgtException {

        // Make sure to start this test with an empty DB - trivial for POJO though...
        dataManager.clearAllTables();
        // Create dummy data
        Collection<PlaySession> testSessions = this.createDummyData(12, 6);

        for (PlaySession session : testSessions) {
            Logger.info("Session Created: \n" + session.toVerboseString());
        }

        // Get dummy session & related data
        PlaySession firstsession = testSessions.iterator().next();
        Player host = firstsession.getHost();
        BoardGame game = firstsession.getGame();

        // Retrieve the host from the database and check if it returns correctly
        Player retrievedPlayer = this.getBgtDataManager().findPlayersByName(host.getPlayerName()).iterator().next();
        assertEquals(retrievedPlayer.getPlayerNickName(), host.getPlayerNickName());
        assertEquals(retrievedPlayer.getGameCollection().size(), host.getGameCollection().size());
        Logger.info("Player check passed: " + retrievedPlayer.getPlayerName() + "; collectionSize: " + retrievedPlayer.getGameCollection().size());

        // Retrieve the game from the database and check if it returns correctly
        BoardGame retrievedGame = this.getBgtDataManager().findGamesByName(game.getName()).iterator().next();
        assertEquals(retrievedGame.getBGG_URL(), game.getBGG_URL());

        // Retrieve session by date
        Collection<PlaySession> retrievedSession = this.getBgtDataManager().findSessionByDate(firstsession.getDate());
        assertEquals(firstsession.getDate(), retrievedSession.iterator().next().getDate());

        //*** If the host has no games in his collection, we add one manually
        if (host.getGameCollection().isEmpty()) {
            host.getGameCollection().add(firstsession.getGame());
            this.getBgtDataManager().persistPlayer(host);
        }

        // Remove a game from the host's collection, add  it again
        BoardGame firstGame = host.getGameCollection().iterator().next();
        int numOfGames = host.getGameCollection().size();
        host.getGameCollection().remove(firstGame);
        this.getBgtDataManager().persistPlayer(host);

        // Load the host again from DB
        Player hostFromDB = this.getBgtDataManager().findPlayersByName(host.getPlayerName()).iterator().next();
        assertEquals(numOfGames - 1, hostFromDB.getGameCollection().size());

        // Add the game again
        hostFromDB.getGameCollection().add(firstGame);
        this.getBgtDataManager().persistPlayer(hostFromDB);

        // Load the host again from DB
        Player hostFromDB2 = this.getBgtDataManager().findPlayersByName(host.getPlayerName()).iterator().next();
        assertEquals(numOfGames, hostFromDB2.getGameCollection().size());

        // Some extra tests
        // Adding and removing a player from session
        if (!firstsession.getAllPlayers().contains(host)) {
            firstsession.getAllPlayers().add(host);
        }

        this.getBgtDataManager().persistPlaySession(firstsession);
        PlaySession sessionFromDB1 = this.getBgtDataManager().findSessionByDate(firstsession.getDate()).iterator().next();
        assertTrue(sessionFromDB1.getAllPlayers().stream().map(Player::toVerboseString).anyMatch(s -> s.equals(host.toVerboseString())));

        sessionFromDB1.getAllPlayers().removeIf(p -> p.toVerboseString().equals(host.toVerboseString()));
        this.getBgtDataManager().persistPlaySession(sessionFromDB1);
        PlaySession sessionFromDB2 = this.getBgtDataManager().findSessionByDate(sessionFromDB1.getDate()).iterator().next();
        assertFalse(sessionFromDB2.getAllPlayers().stream().map(Player::toVerboseString).anyMatch(s -> s.equals(host.toVerboseString())));

        sessionFromDB2.getAllPlayers().add(host);
        this.getBgtDataManager().persistPlaySession(sessionFromDB2);
        PlaySession sessionFromDB3 = this.getBgtDataManager().findSessionByDate(sessionFromDB2.getDate()).iterator().next();
        assertTrue(sessionFromDB3.getAllPlayers().stream().map(Player::toVerboseString).anyMatch(s -> s.equals(host.toVerboseString())));

    }
}