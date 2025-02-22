package tudelft.wis.idm_solutions.BoardGameTracker.JPA_Implementation;

import jakarta.persistence.*;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BoardGame;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.Player;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

@Entity(name = "player")
@Table(name = "player")
public class Player_JPA implements Player {
    @Id
    private String name;

    private String nickName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            inverseJoinColumns = @JoinColumn(name = "game_bggUrl")
    )
    private Collection<BoardGame_JPA> gameCollection;

    public Player_JPA() {}

    public Player_JPA(String name, String nickName, Collection<BoardGame> gameCollection) {
        this.name = name;
        this.nickName = nickName;
        this.gameCollection = gameCollection.stream()
                .map(b -> (BoardGame_JPA)b).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public String getPlayerName() {
        return name;
    }

    @Override
    public String getPlayerNickName() {
        return nickName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<BoardGame> getGameCollection() {
        return (Collection<BoardGame>)(Collection<?>)gameCollection;
    }

    @Override
    public String toVerboseString() {
        return name + "(" + nickName + ")";
    }
}
