package tudelft.wis.idm_solutions.BoardGameTracker.JPA_Implementation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BoardGame;

@Entity(name = "game")
@Table(name = "game")
public class BoardGame_JPA implements BoardGame {
    private String name;

    @Id
    private String bggURL;

    public BoardGame_JPA() {}

    public BoardGame_JPA(String name, String bggURL) {
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
