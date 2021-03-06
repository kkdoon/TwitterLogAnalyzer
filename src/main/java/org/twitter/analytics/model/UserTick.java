package org.twitter.analytics.model;

/**
 * Stores tick value.
 */
public class UserTick {
    private String userID;
    private long timestamp;
    private String operation;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    
    @Override
    public int hashCode() {
        if (this.getUserID() != null) {
            return this.getUserID().hashCode();
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        if (!(obj instanceof UserTick)) return false;

        if (this == obj) return true;

        UserTick model = (UserTick) obj;

        if (this.getUserID() != null && this.getUserID().equals(model.getUserID())) return true;

        return false;
    }
}
