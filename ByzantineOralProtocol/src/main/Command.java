package main;

import java.security.SecureRandom;

public enum Command {
    ATTACK, RETREAT, PLAY, REST;
    static Command DEFAULT = RETREAT;
    private static final SecureRandom random = new SecureRandom();
    static Command random() {
        int id = random.nextInt(Command.values().length);
        return Command.values()[id];
    }
}
