package com.hakan.core.ui.sign.wrapper;

import com.hakan.core.HCore;
import com.hakan.core.ui.GUIHandler;
import com.hakan.core.ui.sign.HSign;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.PacketPlayInUpdateSign;
import net.minecraft.server.v1_13_R2.PacketPlayOutBlockChange;
import net.minecraft.server.v1_13_R2.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_13_R2.TileEntitySign;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.block.CraftSign;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * {@inheritDoc}
 */
public final class HSign_v1_13_R2 extends HSign {

    /**
     * {@inheritDoc}
     */
    public HSign_v1_13_R2(@Nonnull Player player, @Nonnull Material type, @Nonnull String... lines) {
        super(player, type, lines);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open() {

        Location location = super.player.getLocation();
        BlockPosition blockPosition = new BlockPosition(location.getBlockX(), LOWEST_Y_AXIS + 1, location.getBlockZ());

        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(((CraftWorld) super.player.getWorld()).getHandle(), blockPosition);
        packet.block = CraftMagicNumbers.getBlock(this.type).getBlockData();
        HCore.sendPacket(super.player, packet);

        IChatBaseComponent[] components = CraftSign.sanitizeLines(this.lines);
        TileEntitySign sign = new TileEntitySign();
        sign.setPosition(new BlockPosition(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ()));
        System.arraycopy(components, 0, sign.lines, 0, sign.lines.length);
        HCore.sendPacket(super.player, sign.getUpdatePacket());

        HCore.sendPacket(super.player, new PacketPlayOutOpenSignEditor(blockPosition));
        GUIHandler.getContent().put(super.player.getUniqueId(), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void listen(@Nonnull T packet) {
        Objects.requireNonNull(packet, "packet cannot be null!");
        PacketPlayInUpdateSign packetPlayInUpdateSign = (PacketPlayInUpdateSign) packet;

        BlockPosition position = packetPlayInUpdateSign.b();
        Block block = super.player.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ());
        block.setType(block.getType());

        if (this.consumer != null)
            this.consumer.accept(packetPlayInUpdateSign.c());

        GUIHandler.getContent().remove(super.player.getUniqueId());
    }
}