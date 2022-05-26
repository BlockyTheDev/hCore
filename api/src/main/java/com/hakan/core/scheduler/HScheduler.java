package com.hakan.core.scheduler;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * HScheduler class.
 */
public final class HScheduler {

    private final JavaPlugin plugin;
    private boolean async;
    private long after;
    private Long every;

    /**
     * Creates new instance of this class.
     *
     * @param plugin Plugin.
     * @param async  Async.
     */
    public HScheduler(@Nonnull JavaPlugin plugin, boolean async) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null!");
        this.async = async;
    }

    /**
     * Checks will scheduler run as async?
     *
     * @return If scheduler run as async, returns true.
     */
    public boolean isAsync() {
        return this.async;
    }

    /**
     * Sets async mode of scheduler.
     *
     * @param async Async mode.
     * @return This class.
     */
    @Nonnull
    public HScheduler async(boolean async) {
        this.async = async;
        return this;
    }

    /**
     * Runs how many ticks later.
     *
     * @param after Ticks.
     * @return This class.
     */
    @Nonnull
    public HScheduler after(long after) {
        this.after = after;
        return this;
    }

    /**
     * Runs every how many ticks.
     *
     * @param every Ticks.
     * @return This class.
     */
    @Nonnull
    public HScheduler every(long every) {
        this.every = every;
        return this;
    }

    /**
     * Runs how many ticks later.
     *
     * @param after    Ticks.
     * @param timeUnit Time unit.
     * @return This class.
     */
    @Nonnull
    public HScheduler after(int after, @Nonnull TimeUnit timeUnit) {
        this.after = timeUnit.toSeconds(after) * 20;
        return this;
    }

    /**
     * Runs every how many ticks.
     *
     * @param every    Ticks.
     * @param timeUnit Time unit.
     * @return This class.
     */
    @Nonnull
    public HScheduler every(int every, @Nonnull TimeUnit timeUnit) {
        this.every = timeUnit.toSeconds(every) * 20;
        return this;
    }

    /**
     * Starts to scheduler.
     *
     * @param runnable Callback.
     * @return Bukkit task id.
     */
    public synchronized int run(@Nonnull Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable cannot be null!");
        return this.run(consumer -> runnable.run());
    }

    /**
     * Starts to scheduler.
     *
     * @param taskConsumer Callback.
     * @return Bukkit task id.
     */
    public synchronized int run(@Nonnull Consumer<BukkitRunnable> taskConsumer) {
        Objects.requireNonNull(taskConsumer, "task consumer cannot be null!");

        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                taskConsumer.accept(this);
            }
        };

        if (this.async) {
            if (this.every == null)
                return bukkitRunnable.runTaskLaterAsynchronously(this.plugin, this.after).getTaskId();
            else
                return bukkitRunnable.runTaskTimerAsynchronously(this.plugin, this.after, this.every).getTaskId();
        } else {
            if (this.every == null)
                return bukkitRunnable.runTaskLater(this.plugin, this.after).getTaskId();
            else
                return bukkitRunnable.runTaskTimer(this.plugin, this.after, this.every).getTaskId();
        }
    }
}