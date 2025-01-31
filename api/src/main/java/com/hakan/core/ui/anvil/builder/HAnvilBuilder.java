package com.hakan.core.ui.anvil.builder;

import com.hakan.core.HCore;
import com.hakan.core.ui.anvil.HAnvil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * HAnvilBuilder class to build HAnvil.
 */
public final class HAnvilBuilder {

    private Player player;
    private String title;
    private String text;
    private ItemStack leftItem;
    private ItemStack rightItem;
    private boolean closable;

    private Runnable openRunnable;
    private Runnable closeRunnable;
    private Consumer<String> inputConsumer;

    /**
     * Constructor.
     *
     * @param player Player.
     */
    public HAnvilBuilder(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.title = "";
        this.text = "";
        this.leftItem = new ItemStack(Material.DIAMOND);
        this.rightItem = null;
        this.closable = true;
    }

    /**
     * Sets the owner of this anvil.
     *
     * @param player Player.
     * @return This class.
     */
    public HAnvilBuilder player(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        return this;
    }

    /**
     * Sets title.
     *
     * @param title Title.
     * @return This class.
     */
    public HAnvilBuilder title(@Nonnull String title) {
        this.title = Objects.requireNonNull(title, "title cannot be null!");
        return this;
    }

    /**
     * Sets text at input area.
     *
     * @param text Text.
     * @return This class.
     */
    public HAnvilBuilder text(@Nonnull String text) {
        this.text = Objects.requireNonNull(text, "text cannot be null!");
        return this;
    }

    /**
     * Sets item stack at left side of anvil.
     *
     * @param leftItem Item stack.
     * @return This class.
     */
    public HAnvilBuilder leftItem(@Nonnull ItemStack leftItem) {
        this.leftItem = Objects.requireNonNull(leftItem, "leftItem cannot be null!");
        return this;
    }

    /**
     * Sets item stack at right side of anvil.
     *
     * @param rightItem Item stack.
     * @return This class.
     */
    public HAnvilBuilder rightItem(@Nullable ItemStack rightItem) {
        this.rightItem = rightItem;
        return this;
    }

    /**
     * Sets closable.
     *
     * @param closable Closable.
     * @return This class.
     */
    public HAnvilBuilder closable(boolean closable) {
        this.closable = closable;
        return this;
    }

    /**
     * Runs when GUI is opened.
     *
     * @param openRunnable Callback.
     * @return This class.
     */
    public HAnvilBuilder whenOpened(@Nonnull Runnable openRunnable) {
        this.openRunnable = Objects.requireNonNull(openRunnable, "open callback cannot be null!");
        return this;
    }

    /**
     * Runs when input is received.
     * (When the player clicks the second slot on the Anvil GUI.)
     *
     * @param inputConsumer Callback.
     * @return This class.
     */
    public HAnvilBuilder whenInputReceived(@Nonnull Consumer<String> inputConsumer) {
        this.inputConsumer = Objects.requireNonNull(inputConsumer, "input callback cannot be null!");
        return this;
    }

    /**
     * Runs when GUI is closed.
     *
     * @param closeRunnable Callback.
     * @return This class.
     */
    public HAnvilBuilder whenClosed(@Nonnull Runnable closeRunnable) {
        this.closeRunnable = Objects.requireNonNull(closeRunnable, "close callback cannot be null!");
        return this;
    }

    /**
     * Builds HAnvil.
     *
     * @return HAnvil.
     */
    public HAnvil build() {
        try {
            HAnvil anvil = (HAnvil) Class.forName("com.hakan.core.ui.anvil.wrapper.HAnvil_" + HCore.getVersionString())
                    .getConstructor(Player.class, String.class, String.class, ItemStack.class, ItemStack.class)
                    .newInstance(this.player, this.title, this.text, this.leftItem, this.rightItem);

            if (this.openRunnable != null)
                anvil.whenOpened(this.openRunnable);
            if (this.inputConsumer != null)
                anvil.whenInputReceived(this.inputConsumer);
            if (this.closeRunnable != null)
                anvil.whenClosed(this.closeRunnable);
            return anvil.setClosable(this.closable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}