package org.superbiz.moviefun.albums;

import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
public class AlbumsUpdateSchedule {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public DateTime getLastDateTime() {
        return lastDateTime;
    }

    public void setLastDateTime(DateTime lastCompletionDateTime) {
        this.lastDateTime = lastCompletionDateTime;
    }
    @Column(length=10000000)
    private DateTime lastDateTime;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    public static String StatusType_FAIL = "F";
    public static String StatusType_NEW = "N";
    public static String StatusType_COMPLETE = "C";
    public static String StatusType_START = "S";

    public boolean isOfStatus(String statusType)
    {
        String myStatusType = this.getStatus() == null ? "" : this.getStatus();
        String statusTypeToCheck = statusType==null?"":statusType;
        return statusTypeToCheck.replaceAll("\\s", "").equalsIgnoreCase(myStatusType.replaceAll("\\s", ""));
    }
}
