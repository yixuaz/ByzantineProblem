package main;

import java.util.Collections;
import java.util.List;

public class Message {
    final Command action;
    final List<Integer> fromIdsPath;
    final int toId;

    public Message(Command action, List<Integer> fromIdsPath, int toId) {
        assert fromIdsPath != null && action != null;
        this.action = action;
        this.fromIdsPath = Collections.unmodifiableList(fromIdsPath);
        this.toId = toId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("我给"+toId+ "发送");
        for (int i = fromIdsPath.size() - 1; i >= 1; i--) {
            sb.append(fromIdsPath.get(i)).append("听到的");
        }
        sb.append(fromIdsPath.get(0) + "的值是" + action.name());
        return sb.toString();
    }
}
