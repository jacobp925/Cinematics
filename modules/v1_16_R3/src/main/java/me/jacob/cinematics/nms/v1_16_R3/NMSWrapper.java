package me.jacob.cinematics.nms.v1_16_R3;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.jacob.cinematics.nms.NMSVersion;
import me.jacob.cinematics.objects.CinematicPlayer;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSWrapper implements NMSVersion {

    @Override
    public void inject(Player player, CinematicPlayer cinematicPlayer) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
                if (packet instanceof PacketPlayOutChat) {
                    if (cinematicPlayer.isInCinematic()) {
                        cinematicPlayer.getQueuedPackets().add(packet);
                        return;
                    }
                }
                super.write(context, packet, channelPromise);
            }
        };
        ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline().addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }

    @Override
    public void processChat(Player player, CinematicPlayer cinematicPlayer) {
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        cinematicPlayer.getQueuedPackets().forEach(p -> playerConnection.sendPacket((Packet<?>) p));
    }

}
