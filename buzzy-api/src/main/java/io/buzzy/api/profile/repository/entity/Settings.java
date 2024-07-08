package io.buzzy.api.profile.repository.entity;

public class Settings {
    private boolean lastSeen;
    private boolean readReceipt;
    private boolean joiningGroups;
    private boolean privateMessages;
    private boolean darkMode;
    private boolean borderedTheme;
    private boolean allowNotifications;
    private boolean keepNotifications;
    private String preferredLanguage;

    public boolean getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(boolean lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean getReadReceipt() {
        return readReceipt;
    }

    public void setReadReceipt(boolean readReceipt) {
        this.readReceipt = readReceipt;
    }

    public boolean getJoiningGroups() {
        return joiningGroups;
    }

    public void setJoiningGroups(boolean joiningGroups) {
        this.joiningGroups = joiningGroups;
    }

    public boolean getPrivateMessages() {
        return privateMessages;
    }

    public void setPrivateMessages(boolean privateMessages) {
        this.privateMessages = privateMessages;
    }

    public boolean getDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public boolean getBorderedTheme() {
        return borderedTheme;
    }

    public void setBorderedTheme(boolean borderedTheme) {
        this.borderedTheme = borderedTheme;
    }

    public boolean getAllowNotifications() {
        return allowNotifications;
    }

    public void setAllowNotifications(boolean allowNotifications) {
        this.allowNotifications = allowNotifications;
    }

    public boolean getKeepNotifications() {
        return keepNotifications;
    }

    public void setKeepNotifications(boolean keepNotifications) {
        this.keepNotifications = keepNotifications;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
}
