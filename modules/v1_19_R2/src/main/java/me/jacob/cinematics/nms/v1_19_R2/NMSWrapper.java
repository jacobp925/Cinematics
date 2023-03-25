package me.jacob.cinematics.nms.v1_19_R2;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.jacob.cinematics.nms.NMSVersion;
import me.jacob.cinematics.objects.CinematicPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSWrapper implements NMSVersion {

    @Override
    public void inject(Player player, CinematicPlayer cinematicPlayer) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
                if (packet instanceof ClientboundSystemChatPacket || packet instanceof ClientboundDisguisedChatPacket || packet instanceof ClientboundPlayerChatPacket) {
                    if (cinematicPlayer.isInCinematic()) {
                        cinematicPlayer.getQueuedPackets().add(packet);
                        return;
                    }
                }
                super.write(context, packet, channelPromise);
            }
        };
        ((CraftPlayer) player).getHandle().b.b.m.pipeline().addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }

    @Override
    public void processChat(Player player, CinematicPlayer cinematicPlayer) {
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().b;
        cinematicPlayer.getQueuedPackets().forEach(p -> playerConnection.a((Packet<?>) p));
    }

}
