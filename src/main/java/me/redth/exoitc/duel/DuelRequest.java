package me.redth.exoitc.duel;

import me.redth.exoitc.ExOITC;
import me.redth.exoitc.game.Game;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class DuelRequest {
    private static final Map<UUID, DuelRequest> requests = new HashMap<>();
    private final Game map;
    private final Player challenger;
    private final Player accepter;
    private int countdownTask;

    private DuelRequest(Game map, Player challenger, Player accepter) {
        this.map = map;
        this.challenger = challenger;
        this.accepter = accepter;
    }

    public static void request(Game map, Player challenger, Player accepter) {
        if (requests.containsKey(accepter.getUniqueId())) {
            DuelRequest a = requests.get(accepter.getUniqueId());
            if (a.accepter.equals(challenger))
                a.accept();
            return;
        }
        DuelRequest newReq = new DuelRequest(map, challenger, accepter);
        DuelRequest lastReq = requests.put(challenger.getUniqueId(), newReq);
        if (lastReq != null) {
            lastReq.cancel();
        }
        newReq.run();
    }

    public static boolean accept(Player challenger, Player accepter) {
        if (requests.containsKey(accepter.getUniqueId())) {
            DuelRequest a = requests.get(accepter.getUniqueId());
            if (a.accepter.equals(challenger)) {
                a.accept();
                return true;
            }
        }
        return false;
    }

    public void accept() {
        List<Game> duelMaps = Game.GAMES.values().stream().filter(game -> game.isDuel).collect(Collectors.toList());
        Game finalMap = map == null ? duelMaps.get(new Random().nextInt(duelMaps.size())) : map;
        if (!finalMap.players.isEmpty()) return;
        requests.remove(challenger.getUniqueId());
        finalMap.joinQueue(challenger);
        finalMap.joinQueue(accepter);
        cancel();
    }

    private void run() {
        challenger.sendMessage("Duel Request sent");
        TextComponent textComponent = new TextComponent(challenger.getName() + " sent u a request");
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel " + challenger.getName()));
        accepter.sendMessage(textComponent);

        countdownTask = ExOITC.scheduleDelayed(() -> {
            challenger.sendMessage("Duel Request Canceled");
            accepter.sendMessage("Duel Request Canceled");
        }, 1200L);
    }

    private void cancel() {
        ExOITC.cancelTask(countdownTask);
    }
}
