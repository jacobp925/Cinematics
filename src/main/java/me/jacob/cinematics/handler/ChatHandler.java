package me.jacob.cinematics.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import me.jacob.cinematics.Cinematics;
import me.jacob.cinematics.objects.CinematicPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ChatHandler {

    public void inject(Player player, CinematicPlayer cinematicPlayer) {
        if (!Cinematics.getInstance().getCinematicHandler().isDisableChat()) return;
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
                if (packet instanceof ClientboundSystemChatPacket || packet instanceof ClientboundDisguisedChatPacket || packet instanceof ClientboundPlayerChatPacket) {
                    if (cinematicPlayer.isInCinematic()) {
                        cinematicPlayer.getQueuedPackets().add((Packet<ClientGamePacketListener>) packet);
                        return;
                    }
                }
                super.write(context, packet, channelPromise);
            }
        };

        try {
            ServerGamePacketListenerImpl serverGamePacketListener = ((CraftPlayer) player).getHandle().connection;
            Field connectionField = Arrays.stream(serverGamePacketListener.getClass().getDeclaredFields()).filter(f -> f.getType().equals(Connection.class)).findFirst().get();
            connectionField.setAccessible(true);
            ChannelPipeline pipeline = ((Connection) connectionField.get(serverGamePacketListener)).channel.pipeline();
            pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
