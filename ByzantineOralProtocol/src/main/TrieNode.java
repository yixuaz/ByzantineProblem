package main;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

public class TrieNode  {
    private static final TimeUnit unit = TimeUnit.MILLISECONDS;
    private static final int timeout = TestEnvironment.banMessagePossibility() > 0 ? 600 : 300;
    private final TransferQueue<Message> receiver = new LinkedTransferQueue<>();
    private final boolean isRoot;
    private Command majorityAction;
    final Map<Integer, TrieNode> children = new HashMap<>();

    public TrieNode() {
        this.isRoot = false;
    }
    private TrieNode(int cmdIdx) {
        isRoot = true;
        children.put(cmdIdx, new TrieNode());
    }
    public static TrieNode dummyNode(int cmdIdx) {
        return new TrieNode(cmdIdx);
    }

    public Command getResult() throws InterruptedException {
        if (!isRoot) throw new IllegalStateException("only root can call this method");
        return children.values().iterator().next().get(timeout);
    }

    public Command get(long timeoutMs) throws InterruptedException {
        if (majorityAction != null) return majorityAction;
        long startTime = System.currentTimeMillis(), passedTime = 0;
        Message msg = receiver.poll(Math.max(0, timeoutMs), unit);
        assert TestEnvironment.banMessagePossibility() > 0 || msg != null;
        Command msgAction = msg == null ? Command.DEFAULT : msg.action;
        Map<Command, Integer> freq = new HashMap<>();
        freq.put(msgAction, freq.getOrDefault(msgAction, 0) + 1);
        for (TrieNode chd : children.values()) {
            passedTime = (System.currentTimeMillis() - startTime);
            Command chdAction = chd.get(timeoutMs + timeout - passedTime);
            freq.put(chdAction, freq.getOrDefault(chdAction, 0) + 1);
        }
        int totalSize = children.size() + 1;
        for (Map.Entry<Command, Integer> e : freq.entrySet()) {
            if (e.getValue() > totalSize / 2)
                return majorityAction = e.getKey();
        }
        return majorityAction = Command.DEFAULT;
    }

    public void fulfill(Message msg) {
        receiver.add(msg);
    }

}
