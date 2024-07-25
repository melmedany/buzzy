package io.buzzy.websockets.server.messaging.repository.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RedisHash(value = "userActivity")
public class UserActivity {
    @Id
    private String username;
    private boolean online;
    private OffsetDateTime lastSeen;
    private Map<String, UserSubscription> subscriptions = new ConcurrentHashMap<>();

    public UserActivity() {
    }

    public UserActivity(String username, boolean online, OffsetDateTime lastSeen) {
        this.username = username;
        this.online = online;
        this.lastSeen = lastSeen;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public OffsetDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(OffsetDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Map<String, UserSubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Map<String, UserSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
