package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedTransferQueue;

public class Node implements Callable<Command> {
    final boolean isTraitor;
    final Command aim; // if null means it is lieutenant
    final int id;
    final int cmdId;
    final Node[] all;
    final int numOfTraitor;
    final BlockingQueue<Message> mq;
    final private TrieNode root;

    public Node(boolean isTraitor, Command aim, int id, Node[] all, int m, int cmdId) {
        this.isTraitor = isTraitor;
        this.aim = aim;
        this.id = id;
        this.cmdId = cmdId;
        this.all = all;
        numOfTraitor = m;
        mq = new LinkedTransferQueue<>();
        root = TrieNode.dummyNode(cmdId);
        buildTrie(root.children.get(cmdId), new HashSet<>(Arrays.asList(id, cmdId)), numOfTraitor);
    }

    private void buildTrie(TrieNode cur, Set<Integer> seen, int m) {
        if (m == 0) return;
        assert cur != null;
        for (int i = 0; i < all.length; i++) {
            if (seen.add(i)) {
                cur.children.put(i, new TrieNode());
                buildTrie(cur.children.get(i), seen, m - 1);
                seen.remove(i);
            }
        }
    }

    public Command call() throws InterruptedException {
        if (id == cmdId) {
            boardCast(aim, Collections.emptyList());
            return isTraitor ? Command.random() : aim;
        } else {
            startListenMessage();
            return isTraitor ? Command.random() : root.getResult();
        }
    }

    private void boardCast(Command action, List<Integer> fromIdsPath) {
        for (int i = 0; i < all.length; i++) {
            if (i == id || fromIdsPath.contains(i)) continue;
            List<Integer> nextFromIdsPath = new ArrayList<>(fromIdsPath);
            nextFromIdsPath.add(id);
            send(all[i], action, nextFromIdsPath, i);
        }
    }


    private void startListenMessage() {
        Thread background = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Message cur = mq.take();
                        TrieNode p = root;
                        for (int i : cur.fromIdsPath) {
                            p = p.children.get(i);
                        }
                        assert p != null;
                        p.fulfill(cur);
                        if (cur.fromIdsPath.size() <= numOfTraitor) {
                            boardCast(cur.action, cur.fromIdsPath);
                        }
                    }
                } catch (InterruptedException e) {
                    throw new IllegalStateException("INVALID AREA, BECAUSE NO INTERRUPT");
                }
            }
        });
        background.setDaemon(true);
        background.start();
    }

    private void send(Node node, Command aim, List<Integer> path, int toId) {
        assert path != null;
        if (isTraitor) {
            if (Math.random() < TestEnvironment.banMessagePossibility()) {
                if (TestEnvironment.enableDebugInfo)
                    System.out.println(name() + " reject to send any info to " + node.id);
                return; // traitor could  reject send message
            }
            aim = Command.random(); // traitor could send wrong message
        }
        Message toBeSend = new Message(aim, path, toId);
        if (TestEnvironment.enableDebugInfo)
            System.out.println(name() + " send " + toBeSend);
        node.mq.add(toBeSend);
    }

    public String name() {
        return id + (isTraitor ? "(T)" : "(L)") + (id == cmdId ? "*" : " "); // traitor vs loyal
    }
}
