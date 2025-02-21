package tudelft.wis.idm_solutions.BoardGameTracker.JDBC_Implementation;

import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BoardGame;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.PlaySession;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.Player;

import java.util.Collection;
import java.util.Date;

public class PlaySession_JDBC implements PlaySession {
    private Date date;
    private Player host;
    private BoardGame game;
    private int playTime;
    private Collection<Player> players;
    private Player winner;

    public PlaySession_JDBC(
            Date date,
            Player host,
            BoardGame game,
            int playTime,
            Collection<Player> players,
            Player winner) {
        this.date = date;
        this.host = host;
        this.game = game;
        this.playTime = playTime;
        this.players = players;
        this.winner = winner;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public Player getHost() {
        return host;
    }

    @Override
    public BoardGame getGame() {
        return game;
    }

    @Override
    public Collection<Player> getAllPlayers() {
        return players;
    }

    @Override
    public Player getWinner() {
        return winner;
    }

    @Override
    public int getPlaytime() {
        return playTime;
    }

    @Override
    public String toVerboseString() {
        StringBuilder builder = new StringBuilder(game.toVerboseString() + " {");
        builder.append("\n  Date: ").append(date.toString());
        builder.append("\n  Playtime: ").append(playTime);
        builder.append("\n  Host: ").append(host.toVerboseString());
        builder.append("\n  Players: ");
        for (Player player : players) {
            builder.append(player.toVerboseString()).append("; ");
        }
        return builder.append("\n}\n").toString();
    }
}
