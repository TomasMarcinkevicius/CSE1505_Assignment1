package tudelft.wis.idm_solutions.BoardGameTracker.JDBC_Implementation;

import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BoardGame;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.Player;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Player_JDBC implements Player {
    private String name;
    private String nickName;
    private Collection<BoardGame> gameCollection = new LinkedList<>();

    public Player_JDBC(String name, String nickName, Collection<BoardGame> gameCollection) {
        this.name = name;
        this.nickName = nickName;
        this.gameCollection = gameCollection;
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
    public Collection<BoardGame> getGameCollection() {
        return gameCollection;
    }

    @Override
    public String toVerboseString() {
        return name + "(" + nickName + ")";
    }
}
