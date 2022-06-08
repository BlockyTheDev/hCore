package com.hakan.core.message.bossbar;

import com.hakan.core.message.HMessageHandler;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_19_R1.boss.CraftBossBar;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

/**
 * {@inheritDoc}
 */
public final class HBossBar_v1_19_R1 implements HBossBar {

    private final BossBar bossBar;

    /**
     * {@inheritDoc}
     */
    public HBossBar_v1_19_R1(@Nonnull String title, @Nonnull HBarColor color, @Nonnull HBarStyle style, @Nonnull HBarFlag... flags) {
        Objects.requireNonNull(title, "title cannot be null!");
        Objects.requireNonNull(color, "color cannot be null!");
        Objects.requireNonNull(style, "style cannot be null!");
        Objects.requireNonNull(flags, "flags cannot be null!");

        this.bossBar = new CraftBossBar(title, BarColor.valueOf(color.name()), BarStyle.valueOf(style.name()));
        for (HBarFlag hBarFlag : flags)
            this.bossBar.addFlag(BarFlag.valueOf(hBarFlag.name()));
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getTitle() {
        return this.bossBar.getTitle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(@Nonnull String s) {
        this.bossBar.setTitle(Objects.requireNonNull(s, "title cannot be null!"));
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public HBarColor getColor() {
        return HBarColor.valueOf(this.bossBar.getColor().name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColor(@Nonnull HBarColor barColor) {
        this.bossBar.setColor(BarColor.valueOf(Objects.requireNonNull(barColor, "bar color cannot be null!").name()));
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public HBarStyle getStyle() {
        return HBarStyle.valueOf(this.bossBar.getStyle().name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStyle(@Nonnull HBarStyle barStyle) {
        this.bossBar.setStyle(BarStyle.valueOf(Objects.requireNonNull(barStyle, "bar style cannot be null!").name()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFlag(@Nonnull HBarFlag barFlag) {
        this.bossBar.removeFlag(BarFlag.valueOf(Objects.requireNonNull(barFlag, "bar flag cannot be null!").name()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFlag(@Nonnull HBarFlag barFlag) {
        this.bossBar.addFlag(BarFlag.valueOf(Objects.requireNonNull(barFlag, "bar flag cannot be null!").name()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasFlag(@Nonnull HBarFlag barFlag) {
        return this.bossBar.hasFlag(BarFlag.valueOf(Objects.requireNonNull(barFlag, "bar flag cannot be null!").name()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProgress(double progress) {
        this.bossBar.setProgress(progress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getProgress() {
        return this.bossBar.getProgress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPlayer(@Nonnull Player player) {
        this.bossBar.addPlayer(Objects.requireNonNull(player, "player cannot be null!"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePlayer(@Nonnull Player player) {
        this.bossBar.removePlayer(Objects.requireNonNull(player, "player cannot be null!"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAll() {
        this.bossBar.removeAll();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<Player> getPlayers() {
        return this.bossBar.getPlayers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVisible(boolean b) {
        this.bossBar.setVisible(b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible() {
        return this.bossBar.isVisible();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete() {
        HMessageHandler.deleteBossBar(this);
    }
}