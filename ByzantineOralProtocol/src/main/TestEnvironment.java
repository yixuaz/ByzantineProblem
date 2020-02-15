package main;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TestEnvironment {
    private static double possibilityBanMessage = 0;
    public static boolean enableDebugInfo = false;
    public static Node[] build(int n, int m) {
        return build(n, m, Command.random(), true);
    }
    public static Node[] build(int n, int m, Command cmdCmd, boolean cmdIsTraitor) {
        if (3 * m + 1 > n) throw new IllegalArgumentException("n should >= 3 * m + 1");
        Node[] all = new Node[n];
        Random r = new Random();
        int cmdId = r.nextInt(n);
        Set<Integer> traitorId = new HashSet<>();
        if (cmdIsTraitor) {
            traitorId.add(cmdId);
            all[cmdId] = new Node(true, cmdCmd, cmdId, all, m, cmdId);
        }
        while (traitorId.size() < m) {
            int id = r.nextInt(n);
            if (id == cmdId || !traitorId.add(id)) continue;
            all[id] = new Node(true, null, id, all, m, cmdId);
        }
        for (int i = 0; i < n; i++) {
            if (traitorId.contains(i)) continue;
            all[i] = new Node(false, i == cmdId ? cmdCmd : null, i, all, m, cmdId);
        }
        return all;
    }

    public static double banMessagePossibility() {
        return possibilityBanMessage;
    }

    public static void setBanMessagePossibility(double i) {
        if (i > 1 || i < 0) throw new IllegalArgumentException("input should be in [0,1]");
        possibilityBanMessage = i;
    }
}
