package tudelft.wis.idm_solutions.BoardGameTracker.JPA_Implementation;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Date;

@Embeddable
public class PlaySession_JPA_Key implements Serializable {
    private Date date;

    private String hostName;

    private String bggUrl;

    public PlaySession_JPA_Key() {}

    public PlaySession_JPA_Key(Date date, String hostName, String bggUrl) {
        this.date = date;
        this.hostName = hostName;
        this.bggUrl = bggUrl;
    }

    public Date getDate() {
        return date;
    }

    public String getHostName() {
        return hostName;
    }

    public String getBggUrl() {
        return bggUrl;
    }
}
