package me.jacob.cinematics.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import me.jacob.cinematics.Cinematics;
import me.jacob.cinematics.objects.CinematicPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

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
        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().connection.connection.channel.pipeline();
        pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }
}
