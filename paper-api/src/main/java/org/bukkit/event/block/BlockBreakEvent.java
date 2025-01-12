package org.bukkit.event.block;

import codes.kooper.models.Stage;
import codes.kooper.models.View;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a block is broken by a player.
 * <p>
 * If you wish to have the block drop experience, you must set the experience
 * value above 0. By default, experience will be set in the event if:
 * <ol>
 * <li>The player is not in creative or adventure mode
 * <li>The player can loot the block (ie: does not destroy it completely, by
 *     using the correct tool)
 * <li>The player does not have silk touch
 * <li>The block drops experience in vanilla Minecraft
 * </ol>
 * <p>
 * Note:
 * Plugins wanting to simulate a traditional block drop should set the block
 * to air and utilize their own methods for determining what the default drop
 * for the block being broken is and what to do about it, if anything.
 * <p>
 * If a Block Break event is cancelled, the block will not break and
 * experience will not drop.
 */
public class BlockBreakEvent extends BlockExpEvent implements Cancellable {
    private final Player player;
    private boolean dropItems;
    private boolean cancel;
    private boolean clientSided;
    private BlockData blockData;
    private Stage stage;
    private View view;

    public BlockBreakEvent(@NotNull final Block theBlock, @NotNull final Player player) {
        super(theBlock, 0);
        this.clientSided = false;
        this.player = player;
        this.dropItems = true; // Defaults to dropping items as it normally would
    }

    /**
     * The client-sided block's data.
     *
     * @return The client-sided block's data or null if it is a real block.
     */
    @Nullable
    public BlockData getBlockData() {
        return blockData;
    }

    /**
     * Sets the block data for the client-sided block.
     *
     * @param blockData The block data for the client-sided block.
     */
    public void setBlockData(@NotNull BlockData blockData) {
        this.blockData = blockData;
    }

    /**
     * Sets the stage for the client-sided block.
     *
     * @param stage The stage to set.
     */
    public void setStage(@Nullable Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the view for the client-sided block.
     *
     * @param view The view to set.
     */
    public void setView(@Nullable View view) {
        this.view = view;
    }

    /**
     * Gets the stage the client-sided block was broken in.
     *
     * @return The stage the block was broken in, or else null.
     */
    @Nullable
    public Stage getStage() {
        return stage;
    }

    /**
     * Gets the view the client-sided block was broken in.
     *
     * @return The view the block was broken in, or else null.
     */
    @Nullable
    public View getView() {
        return view;
    }

    /**
     * Sets whether the block is client sided.
     *
     * @param clientSided Whether the block is client sided.
     */
    public void setClientSided(boolean clientSided) {
        this.clientSided = clientSided;
    }

    /**
     * Whether the block broken is client sided.
     *
     * @return Whether the block is client sided.
     */
    public boolean isClientSided() {
        return clientSided;
    }

    /**
     * Gets the Player that is breaking the block involved in this event.
     *
     * @return The Player that is breaking the block involved in this event
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets whether or not the block will attempt to drop items as it normally
     * would.
     *
     * If and only if this is false then {@link BlockDropItemEvent} will not be
     * called after this event.
     *
     * @param dropItems Whether or not the block will attempt to drop items
     */
    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    /**
     * Gets whether or not the block will attempt to drop items.
     *
     * If and only if this is false then {@link BlockDropItemEvent} will not be
     * called after this event.
     *
     * @return Whether or not the block will attempt to drop items
     */
    public boolean isDropItems() {
        return this.dropItems;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
