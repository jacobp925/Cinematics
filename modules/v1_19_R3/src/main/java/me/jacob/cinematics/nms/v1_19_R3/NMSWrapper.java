package me.jacob.cinematics.nms.v1_19_R3;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import me.jacob.cinematics.nms.NMSVersion;
import me.jacob.cinematics.objects.CinematicPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

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

        try {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
            Field connectionField = connection.getClass().getDeclaredField("h");
            connectionField.setAccessible(true);
            ChannelPipeline pipeline = ((NetworkManager) connectionField.get(connection)).m.pipeline();
            pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void processChat(Player player, CinematicPlayer cinematicPlayer) {
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().b;
        cinematicPlayer.getQueuedPackets().forEach(p -> playerConnection.a((Packet<?>) p));
    }

}
