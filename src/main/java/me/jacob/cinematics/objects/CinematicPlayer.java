package me.jacob.cinematics.objects;

import lombok.Getter;
import lombok.Setter;
import me.jacob.cinematics.Cinematics;
import me.jacob.cinematics.api.events.CinematicEndEvent;
import me.jacob.cinematics.objects.cinematics.Cinematic;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CinematicPlayer {

    @Getter private static ArrayList<CinematicPlayer> players = new ArrayList<>();

    private UUID uuid;

    private ArrayList<BukkitTask> tasks;

    private boolean inCinematic;
    private LinkedList<String> cinematicQueue;
    private Location previousLoc;
    private GameMode previousGamemode;

    private boolean creatingCinematic;
    private String creatingCinematicName;
    private ArrayList<Location> waypoints;

    private LinkedList<Packet<ClientGamePacketListener>> queuedPackets;

    public CinematicPlayer(Player p) {
        this.uuid = p.getUniqueId();
        this.tasks = new ArrayList<>();
        this.cinematicQueue = new LinkedList<>();
        this.waypoints = new ArrayList<>();
        this.queuedPackets = new LinkedList<>();
        players.add(this);
    }

    public void remove() {
        tasks.forEach(BukkitTask::cancel);
        players.remove(this);
    }

    public void playCinematics(List<String> names) {

        exitCinematic();

        List<String> cinematicNames = Cinematic.getCinematics().stream().map(Cinematic::getName).toList();
        this.cinematicQueue.addAll(names.stream().filter(cinematicNames::contains).toList());

        Player p = Bukkit.getPlayer(this.uuid);
        if (p == null || !p.isOnline()) return;

        if (this.cinematicQueue.isEmpty()) return;

        this.getTasks().add(Bukkit.getScheduler().runTaskLater(Cinematics.getInstance(), () -> {
            if (!p.isOnline()) return;
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 100, false, false, false));
        }, 15L));

        Cinematics.getInstance().getCinematicHandler().getCinematic(this.cinematicQueue.pollFirst()).play(this, p, true);
    }

    public void exitCinematic() {
        if (!inCinematic) return;
        this.inCinematic = false;

        Player p = Bukkit.getPlayer(this.uuid);
        if (p == null || !p.isOnline()) return;

        p.removePotionEffect(PotionEffectType.SLOW);
        p.getInventory().setHelmet(null);
        p.setGameMode(this.previousGamemode);
        p.teleport(this.previousLoc);
        p.resetPlayerTime();

        this.previousGamemode = null;
        this.previousLoc = null;

        this.cinematicQueue.clear();
        this.tasks.clear();

        if (Cinematics.getInstance().getCinematicHandler().isDisableChat()) {
            ServerGamePacketListenerImpl serverGamePacketListener = ((CraftPlayer) p).getHandle().connection;
            queuedPackets.forEach(serverGamePacketListener::send);
        }

        this.queuedPackets.clear();

        new CinematicEndEvent(p).callEvent();
    }

    public void endCinematicCreation() {
        this.creatingCinematic = false;
        this.creatingCinematicName = null;
        this.waypoints.clear();
    }

    public static CinematicPlayer getByUUID(UUID uuid) {
        return players.stream().filter(cinematicPlayer -> cinematicPlayer.getUuid().equals(uuid)).findAny().orElse(null);
    }


}
