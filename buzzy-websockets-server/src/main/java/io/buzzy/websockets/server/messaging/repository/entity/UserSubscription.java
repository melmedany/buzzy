package io.buzzy.websockets.server.messaging.repository.entity;

public class UserSubscription {
    private String id;
    private String destination;

    public UserSubscription() {
    }

    public UserSubscription(String id, String destination) {
        this.id = id;
        this.destination = destination;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
