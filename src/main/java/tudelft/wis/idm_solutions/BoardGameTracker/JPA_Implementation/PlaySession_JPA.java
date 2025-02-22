package tudelft.wis.idm_solutions.BoardGameTracker.JPA_Implementation;

import jakarta.persistence.*;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BoardGame;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.PlaySession;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.Player;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

@Entity(name = "session")
@Table(name = "session")
public class PlaySession_JPA implements PlaySession {

    @EmbeddedId
    private PlaySession_JPA_Key key;

    @ManyToOne
    @MapsId("hostName")
    private Player_JPA host;

    @ManyToOne
    @MapsId("bggUrl")
    private BoardGame_JPA game;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Player_JPA> players;

    @ManyToOne
    private Player_JPA winner;

    private int playTime;

    public PlaySession_JPA() {}

    public PlaySession_JPA(
            Date date,
            Player host,
            BoardGame game,
            int playTime,
            Collection<Player> players,
            Player winner) {
        this.key = new PlaySession_JPA_Key(date, host.getPlayerName(), game.getBGG_URL());
        this.host = (Player_JPA)host;
        this.game = (BoardGame_JPA)game;
        this.players = players.stream().map(p -> (Player_JPA)p).collect(Collectors.toCollection(LinkedList::new));
        this.winner = (Player_JPA)winner;
        this.playTime = playTime;
    }

    @Override
    public Date getDate() {
        return key.getDate();
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
    @SuppressWarnings("unchecked")
    public Collection<Player> getAllPlayers() {
        return (Collection<Player>)(Collection<?>)players;
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
        builder.append("\n  Date: ").append(key.getDate().toString());
        builder.append("\n  Playtime: ").append(playTime);
        builder.append("\n  Host: ").append(host.toVerboseString());
        builder.append("\n  Players: ");
        for (Player player : players) {
            builder.append(player.toVerboseString()).append("; ");
        }
        return builder.append("\n}\n").toString();
    }
}
