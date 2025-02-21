package tudelft.wis.idm_solutions.BoardGameTracker.JDBC_Implementation;

import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BoardGame;

public class BoardGame_JDBC implements BoardGame {

    private String name;
    private String bggURL;

    public BoardGame_JDBC(String name, String bggURL) {
        this.name = name;
        this.bggURL = bggURL;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getBGG_URL() {
        return bggURL;
    }

    @Override
    public String toVerboseString() {
        return name + " (" + bggURL + ")";
    }
}
