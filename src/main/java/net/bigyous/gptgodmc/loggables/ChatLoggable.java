package net.bigyous.gptgodmc.loggables;

public class ChatLoggable extends BaseLoggable {
    /**
     * Chat or voice chat event
     */
    String playerName;
    String message;

    public ChatLoggable(String playerName, String message) {
        this.playerName = playerName;
        this.message = message;
    }

    public String getLog() {
        return playerName + " said \"" + message + "\"";
    }

    public boolean combine(Loggable event) {
        /**
         * TODO: combine chat messages for same player
         */
        return false;
    }
}
