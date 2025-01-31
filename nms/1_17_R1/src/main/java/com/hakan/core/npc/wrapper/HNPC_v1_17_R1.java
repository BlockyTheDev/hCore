package com.hakan.core.npc.wrapper;

import com.google.common.collect.ImmutableList;
import com.hakan.core.HCore;
import com.hakan.core.npc.HNPC;
import com.hakan.core.npc.HNPCHandler;
import com.hakan.core.npc.skin.HNPCSkin;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * {@inheritDoc}
 */
public final class HNPC_v1_17_R1 extends HNPC {

    private final HNPCUtils_v1_17_R1 utils;
    private EntityPlayer npc;
    private EntityArmorStand armorStand;

    /**
     * {@inheritDoc}
     */
    public HNPC_v1_17_R1(@Nonnull String id,
                         @Nonnull HNPCSkin skin,
                         @Nonnull Location location,
                         @Nonnull List<String> lines,
                         @Nonnull Set<UUID> viewers,
                         @Nonnull Map<EquipmentType, ItemStack> equipments,
                         boolean showEveryone) {
        super(id, location, lines, viewers, equipments, showEveryone);
        super.showEveryone(showEveryone);

        this.utils = new HNPCUtils_v1_17_R1();

        this.npc = this.utils.createNPC(skin, location);
        this.armorStand = this.utils.createNameHider(location);
        ((Entity) this.npc).at = ImmutableList.<Entity>builder().add(this.armorStand).build();

        HCore.syncScheduler().after(20)
                .run(() -> this.hide(super.renderer.getShownViewersAsPlayer()));
        HCore.syncScheduler().after(25)
                .run(() -> this.show(super.renderer.getShownViewersAsPlayer()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getEntityID() {
        return this.npc.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public HNPC walk(@Nonnull Location to, double speed) {
        Objects.requireNonNull(to, "to location cannot be null!");

        if (this.walking)
            throw new IllegalStateException("NPC is already walking!");

        super.walking = true;
        this.utils.walk(this, to, speed, () -> super.walking = false);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public HNPC setLocation(@Nonnull Location location) {
        Objects.requireNonNull(location, "location cannot be null!");

        this.npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        super.hologram.setLocation(location.clone().add(0, this.npc.getHeadHeight() + (this.hologram.getLines().size() * 0.125), 0));
        super.renderer.setLocation(location);

        double imp = 256f / 360f;
        float yaw = Math.round(location.getYaw() % 360f * imp);
        float pitch = Math.round(location.getPitch() % 360f * imp);
        HCore.sendPacket(super.renderer.getShownViewersAsPlayer(),
                new PacketPlayOutEntityHeadRotation(this.npc, (byte) (location.getYaw() * imp)),
                new PacketPlayOutEntity.PacketPlayOutEntityLook(this.npc.getId(), (byte) yaw, (byte) pitch, false),
                new PacketPlayOutEntityTeleport(this.npc)
        );

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public HNPC setSkin(@Nonnull HNPCSkin skin) {
        Objects.requireNonNull(skin, "skin cannot be null!");

        List<Player> players = super.renderer.getShownViewersAsPlayer();

        this.hide(players);
        this.npc = this.utils.createNPC(skin, super.getLocation());
        this.armorStand = this.utils.createNameHider(super.getLocation());
        ((Entity) this.npc).at = ImmutableList.<Entity>builder().add(this.armorStand).build();
        HCore.syncScheduler().after(20).run(() -> this.show(players));

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public HNPC setEquipment(@Nonnull EquipmentType equipment, @Nonnull ItemStack itemStack) {
        Objects.requireNonNull(equipment, "equipment type cannot be null!");
        Objects.requireNonNull(itemStack, "itemStack type cannot be null!");

        super.equipments.put(equipment, itemStack);

        List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> equipmentList = new ArrayList<>();
        super.equipments.forEach((key, value) -> equipmentList.add(new Pair<>(EnumItemSlot.fromName(key.getValue()), CraftItemStack.asNMSCopy(value))));
        HCore.sendPacket(super.renderer.getShownViewersAsPlayer(), new PacketPlayOutEntityEquipment(this.npc.getId(), equipmentList));

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public HNPC show(@Nonnull List<Player> players) {
        HCore.sendPacket(Objects.requireNonNull(players, "players cannot be null!"),
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, this.npc),
                new PacketPlayOutNamedEntitySpawn(this.npc),
                new PacketPlayOutEntityMetadata(this.npc.getId(), this.utils.createDataWatcher(this.npc), true),

                new PacketPlayOutSpawnEntityLiving(this.armorStand),
                new PacketPlayOutEntityMetadata(this.armorStand.getId(), this.armorStand.getDataWatcher(), true),
                new PacketPlayOutEntityTeleport(this.armorStand),
                new PacketPlayOutMount(this.npc)
        );

        if (super.equipments.size() > 0) {
            List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> equipmentList = new ArrayList<>();
            super.equipments.forEach((key, value) -> equipmentList.add(new Pair<>(EnumItemSlot.valueOf(key.name()), CraftItemStack.asNMSCopy(value))));
            HCore.sendPacket(players, new PacketPlayOutEntityEquipment(this.npc.getId(), equipmentList));
        }


        HCore.asyncScheduler().after(5)
                .run(() -> HCore.sendPacket(players, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, this.npc)));
        return this.setLocation(super.getLocation());
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public HNPC hide(@Nonnull List<Player> players) {
        Objects.requireNonNull(players, "players cannot be null!");

        HCore.sendPacket(players,
                new PacketPlayOutEntityDestroy(this.npc.getId()),
                new PacketPlayOutEntityDestroy(this.armorStand.getId()));

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public HNPC delete() {
        HNPCHandler.getContent().remove(super.id);

        super.action.onDelete();
        super.hologram.delete();
        super.renderer.delete();
        super.dead = true;
        super.walking = false;
        return this.hide(super.renderer.getShownViewersAsPlayer());
    }
}