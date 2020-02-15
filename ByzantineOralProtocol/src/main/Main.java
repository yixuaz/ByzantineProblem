package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Node[] all = TestEnvironment.build(4,1, Command.ATTACK, false);
        TestEnvironment.setBanMessagePossibility(0.3);
        TestEnvironment.enableDebugInfo = true;
        ExecutorService executorService = Executors.newFixedThreadPool(all.length);
        List<Future<Command>> res = new ArrayList<>();
        for (Node i : all) {
            res.add(executorService.submit(i));
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) Thread.sleep(10);
        for (int i = 0; i < all.length; i++) {
            System.out.println(all[i].name() + ":" + res.get(i).get());
        }
    }
}
