package me.redth.exoitc.game.editor.anvil;

import me.redth.exoitc.game.editor.EditorMenu;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.ContainerAnvil;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryView;

public final class RenameAnvil extends ContainerAnvil {
    private final EntityPlayer entityPlayer;
    final EditorMenu editorMenu;

    public RenameAnvil(EditorMenu editorMenu) {
        this(editorMenu, ((CraftPlayer) editorMenu.getPlayer()).getHandle());
    }

    private RenameAnvil(EditorMenu editorMenu, EntityPlayer entityPlayer) {
        super(entityPlayer.inventory, entityPlayer.world, new BlockPosition(0, 0, 0), entityPlayer);
        this.entityPlayer = entityPlayer;
        this.editorMenu = editorMenu;

    }

    @Override
    public boolean a(EntityHuman entityHuman) {
        return true;
    }

    public void display() {
        int containerId = entityPlayer.nextContainerCounter();

        entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, "minecraft:anvil", new ChatMessage("Repairing"), 0));

        entityPlayer.activeContainer = this;
        entityPlayer.activeContainer.windowId = containerId;
        entityPlayer.activeContainer.addSlotListener(entityPlayer);
        entityPlayer.activeContainer = this;
        entityPlayer.activeContainer.windowId = containerId;

    }

    @Override
    public CraftInventoryView getBukkitView() {
        return new AnvilView(editorMenu, super.getBukkitView());
    }

    @Override
    public void b(EntityHuman entityhuman) {
    }
}
