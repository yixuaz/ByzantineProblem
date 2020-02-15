package main;



import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class ByzantineTest {
    int testTimes = 20;
    @Test
    public void testOneTraitorCmdIsT() throws ExecutionException, InterruptedException {
        for (int i = 0; i < testTimes; i++)
            testTemplate(TestEnvironment.build(4, 1));

    }

    @Test
    public void testOneTraitorCmdIsNotT() throws ExecutionException, InterruptedException {
        for (int i = 0; i < testTimes; i++)
            testTemplate(TestEnvironment.build(4, 1, Command.random(), false));
    }

    @Test
    public void testTwoTraitorCmdIsT() throws ExecutionException, InterruptedException {
        for (int i = 0; i < testTimes; i++)
            testTemplate(TestEnvironment.build(7, 2));
    }

    @Test
    public void testTwoTraitorCmdIsNotT() throws ExecutionException, InterruptedException {
        for (int i = 0; i < testTimes; i++)
            testTemplate(TestEnvironment.build(7, 2, Command.REST, false));
    }

    @Test
    public void testThreeTraitorCmdIsT() throws ExecutionException, InterruptedException {
        for (int i = 0; i < testTimes; i++)
            testTemplate(TestEnvironment.build(10, 3));

    }

    @Test
    public void testThreeTraitorCmdIsNotT() throws ExecutionException, InterruptedException {
        for (int i = 0; i < testTimes; i++)
            testTemplate(TestEnvironment.build(10, 3, Command.ATTACK, false));
    }

    private void testTemplate(Node[] all) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(all.length);
        List<Future<Command>> res = new ArrayList<>();
        for (Node i : all) {
            res.add(executorService.submit(i));
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) Thread.sleep(10);
        Set<Command> commandSets = new HashSet<>();
        for (int i = 0; i < all.length; i++) {
            if (!all[i].isTraitor) commandSets.add(res.get(i).get());
        }
        Assert.assertEquals(1, commandSets.size());
    }
}
