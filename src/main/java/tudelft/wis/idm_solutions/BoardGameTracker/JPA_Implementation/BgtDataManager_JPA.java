package tudelft.wis.idm_solutions.BoardGameTracker.JPA_Implementation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import tudelft.wis.idm_solutions.BoardGameTracker.JDBC_Implementation.Utils;
import tudelft.wis.idm_tasks.boardGameTracker.BgtException;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BgtDataManager;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BoardGame;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.PlaySession;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.Player;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class BgtDataManager_JPA implements BgtDataManager {
    private EntityManagerFactory entityManagerFactory;

    public BgtDataManager_JPA() {
        entityManagerFactory = Persistence.createEntityManagerFactory("my-persistence-unit");
    }

    private void inTransaction(Consumer<EntityManager> work) throws BgtException {
        inTransaction(m -> {
            work.accept(m);
            return null;
        });
    }

    private <T> T inTransaction(Function<EntityManager, T> work) throws BgtException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try (entityManager) {
            transaction.begin();
            T result = work.apply(entityManager);
            transaction.commit();
            return result;
        }
        catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            Utils.throwBgtException(e);
            return null;
        }
    }


    @Override
    public Player createNewPlayer(String name, String nickname) throws BgtException {
        return inTransaction(m -> {
            Player player = new Player_JPA(name, nickname, new LinkedList<>());
            m.merge(player);
            return player;
        });
    }

    @Override
    public Collection<Player> findPlayersByName(String name) throws BgtException {
        return inTransaction(m -> {
            var query = m.createQuery("""
                SELECT p FROM player AS p
                WHERE p.name LIKE :playerName
            """, Player.class);
            query.setParameter("playerName", "%" + name + "%");
            return query.getResultList();
        });
    }

    @Override
    public BoardGame createNewBoardgame(String name, String bggURL) throws BgtException {
        return inTransaction(m -> {
            BoardGame boardGame = new BoardGame_JPA(name, bggURL);
            m.merge(boardGame);
            return boardGame;
        });
    }

    @Override
    public Collection<BoardGame> findGamesByName(String name) throws BgtException {
        return inTransaction(m -> {
            var query = m.createQuery("""
                SELECT g FROM game AS g
                WHERE g.name LIKE :gameName
            """, BoardGame.class);
            query.setParameter("gameName", "%" + name + "%");
            return query.getResultList();
        });
    }

    @Override
    public PlaySession createNewPlaySession(Date date, Player host, BoardGame game, int playtime, Collection<Player> players, Player winner) throws BgtException {
        return inTransaction(m -> {
            PlaySession playSession = new PlaySession_JPA(date, host, game, playtime, players, winner);
            m.merge(playSession);
            return playSession;
        });
    }

    @Override
    public Collection<PlaySession> findSessionByDate(Date date) throws BgtException {
        return inTransaction(m -> {
            var query = m.createQuery("""
                SELECT s FROM session AS s
                WHERE s.key.date = :sessionDate
            """, PlaySession.class);
            query.setParameter("sessionDate", date);
            return query.getResultList();
        });
    }

    @Override
    public void persistPlayer(Player player) throws BgtException {
        inTransaction(m -> {
            m.merge(player);
        });
    }

    @Override
    public void persistPlaySession(PlaySession session) throws BgtException {
        inTransaction(m -> {
            m.merge(session);
        });
    }

    @Override
    public void persistBoardGame(BoardGame game) throws BgtException {
        inTransaction(m -> {
            m.merge(game);
        });
    }
}
