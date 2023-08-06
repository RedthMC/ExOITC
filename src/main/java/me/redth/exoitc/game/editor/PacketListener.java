package me.redth.exoitc.game.editor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class PacketListener extends ChannelInboundHandlerAdapter {
    private final EditorMenu editor;

    private PacketListener(EditorMenu editor) {
        this.editor = editor;
    }

    public static void register(EditorMenu editor) {
        try {
            ChannelPipeline pipeline = ((CraftPlayer) editor.getPlayer()).getHandle().playerConnection.networkManager.channel.pipeline();

            if (pipeline.get("exoitc") == null && pipeline.get("packet_handler") != null) {
                pipeline.addBefore("packet_handler", "exoitc", new PacketListener(editor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unregister(Player player) {
        try {
            ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();

            if (pipeline.get("exoitc") != null) {
                pipeline.remove("exoitc");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
        if (packet instanceof PacketPlayInUseEntity) {
            onEntityInUse((PacketPlayInUseEntity) packet);
        }
        super.channelRead(ctx, packet);
    }

    public void onEntityInUse(PacketPlayInUseEntity packet) {
        if (packet.a() != PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) return;

        try {
            Field idField = PacketPlayInUseEntity.class.getDeclaredField("a");
            idField.setAccessible(true);

            editor.attackedId(idField.getInt(packet));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
