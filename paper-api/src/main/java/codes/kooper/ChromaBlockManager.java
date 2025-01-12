package codes.kooper;

import codes.kooper.models.Stage;
import codes.kooper.models.View;
import io.papermc.paper.math.Position;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChromaBlockManager {
    private final Player player;
    private final Long2ObjectOpenHashMap<Map<Position, BlockData>> blockDataCache;
    private final Long2ObjectOpenHashMap<List<View>> chunkViewMap;
    private final Set<Player> spectators;
    private final Map<String, Stage> stages;

    public ChromaBlockManager(@NotNull Player player) {
        this.player = player;
        this.blockDataCache = new Long2ObjectOpenHashMap<>();
        spectators = new HashSet<>();
        this.chunkViewMap = new Long2ObjectOpenHashMap<>();
        this.stages = new HashMap<>();
    }

    @NotNull
    public Long2ObjectOpenHashMap<Map<Position, BlockData>> getBlockDataCache() {
        return blockDataCache;
    }

    /**
     * Resets all modified blocks for the player by sending "air" block data to their client.
     * <p>
     * This method loops through the cached block data, replaces all modified blocks with air,
     * and sends the updates to the player's client. After updating, it clears the caches
     * to ensure a fresh state for future block modifications.
     * </p>
     */
    public void resetAllBlocks() {
        sendAirBlocks(player);
        spectators.forEach(this::sendAirBlocks);
        blockDataCache.clear();
        chunkViewMap.clear();
    }

    public void sendAirBlocks(@NotNull Player viewer) {
        BlockData airBlockData = Material.AIR.createBlockData();

        for (final Map<Position, BlockData> value : blockDataCache.values()) {
            Map<Position, BlockData> airBlocks = new HashMap<>();

            for (Position position : value.keySet()) {
                airBlocks.put(position, airBlockData);
            }

            viewer.sendMultiBlockChange(airBlocks);
        }
    }
    
    public @NotNull Set<Player> getSpectators() {
        return spectators;
    }

    /**
     * Adds a player to the list of spectators.
     *
     * @param spectator the {@link Player} to add as a spectator
     * @throws NullPointerException if the spectator is null
     */
    public void addSpectator(@NotNull Player spectator) {
        spectators.add(spectator);
        refreshAllBlocks(spectator);
    }

    /**
     * Removes a player from the list of spectators.
     *
     * @param spectator the {@link Player} to remove from the spectators
     * @throws NullPointerException if the spectator is null
     * @throws IllegalArgumentException if the spectator is not in the spectators list
     */
    public void removeSpectator(@NotNull Player spectator) {
        spectators.remove(spectator);
    }

    /**
     * Retrieves the highest position at a given X and Z coordinate across all loaded chunks.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return The highest Y position for the given X and Z, or null if no block is found at that position.
     */
    @Nullable
    public Position getHighestPosAtXAndZ(int x, int z) {
        Position highestPosition = null;

        // Iterate through all chunks in the block data cache
        for (Map<Position, BlockData> chunk : blockDataCache.values()) {
            // Iterate through all positions in the current chunk
            for (Position position : chunk.keySet()) {
                // Check if the position matches the given X and Z
                if (position.blockX() == x && position.blockZ() == z) {
                    // If we have found a higher Y position, update the highestPosition
                    if (highestPosition == null || position.blockY() > highestPosition.blockY()) {
                        highestPosition = position;
                    }
                }
            }
        }

        return highestPosition;
    }

    /**
     * Checks if a block exists at the specified position.
     *
     * @param position The position of the block to check.
     * @return True if the block exists, false otherwise.
     */
    public boolean hasBlock(@NotNull Position position) {
        long chunkKey = Chunk.getChunkKey(position.blockX() >> 4, position.blockZ() >> 4);
        Map<Position, BlockData> chunkData = blockDataCache.get(chunkKey);
        return chunkData != null && chunkData.containsKey(position);
    }

    /**
     * Retrieves all block data within a specific chunk.
     *
     * @param chunkKey The key of the chunk to retrieve block data for.
     * @return A map of positions to block data within the chunk, or an empty map if the chunk is not present.
     */
    @NotNull
    public Map<Position, BlockData> getAllBlockDataInChunk(long chunkKey) {
        return blockDataCache.getOrDefault(chunkKey, new HashMap<>());
    }

    /**
     * Checks if a specific chunk is present in the block data cache.
     *
     * @param chunkKey The key of the chunk to check.
     * @return True if the chunk exists in the cache, false otherwise.
     */
    public boolean hasChunk(long chunkKey) {
        return blockDataCache.containsKey(chunkKey);
    }

    /**
     * Sets a single block in the block data cache.
     *
     * @param position  The position of the block to set.
     * @param blockData The block data to assign to the position.
     */
    public void setBlock(@NotNull Position position, @NotNull BlockData blockData) {
        long chunkKey = Chunk.getChunkKey(position.blockX() >> 4, position.blockZ() >> 4);
        blockDataCache.computeIfAbsent(chunkKey, k -> new HashMap<>()).put(position, blockData);
    }

    /**
     * Sets multiple blocks in the block data cache using a map of positions and block data.
     *
     * @param blocks A map containing positions as keys and block data as values.
     */
    public void setBlocks(@NotNull Map<Position, BlockData> blocks) {
        for (Map.Entry<Position, BlockData> entry : blocks.entrySet()) {
            setBlock(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Sets the same block data for multiple positions in the block data cache.
     *
     * @param positions A list of positions where the block data should be set.
     * @param blockData The block data to assign to all the specified positions.
     */
    public void setBlocks(@NotNull Collection<Position> positions, @NotNull BlockData blockData) {
        for (Position position : positions) {
            setBlock(position, blockData);
        }
    }

    /**
     * Adds a stage to the manager.
     *
     * @param stage The stage to add.
     */
    public void addStage(@NotNull Stage stage) {
        stages.put(stage.getName(), stage);
    }

    /**
     * Retrieves a stage by name.
     *
     * @param stageName The name of the stage.
     * @return The stage, or null if not found.
     */
    @Nullable
    public Stage getStage(@NotNull String stageName) {
        return stages.get(stageName);
    }

    /**
     * Adds a view to a specific stage and updates the chunk-view map for efficient access.
     *
     * @param stageName The name of the stage.
     * @param view The view to add.
     */
    public void addViewToStage(@NotNull String stageName, @NotNull View view) {
        // Get the stage from the stages map
        Stage stage = stages.get(stageName);

        if (stage != null) {
            // Add the view to the stage
            stage.addView(view);

            // Update the chunk-view map for the chunks the view covers
            for (long chunkKey : view.getChunkKeys()) {
                List<View> views = chunkViewMap.computeIfAbsent(chunkKey, k -> new ArrayList<>());
                views.add(view);
                views.sort(Comparator.comparingInt(View::getIndex).reversed()); // Ensure views are sorted by highest index
            }
        }
    }

    /**
     * Removes a view from a specific stage and updates the chunk-view map.
     *
     * @param stageName The name of the stage.
     * @param view The view to remove.
     */
    public void removeViewFromStage(@NotNull String stageName, @NotNull View view) {
        // Get the stage from the stages map
        Stage stage = stages.get(stageName);

        if (stage != null) {
            // Remove the view from the stage
            stage.removeView(view);

            // Update the chunk-view map by removing the view from the chunks it covers
            for (long chunkKey : view.getChunkKeys()) {
                List<View> views = chunkViewMap.get(chunkKey);
                if (views != null) {
                    views.remove(view);
                    if (views.isEmpty()) {
                        chunkViewMap.remove(chunkKey); // Clean up empty lists for chunk keys
                    } else {
                        // Re-sort the list after removing the view
                        views.sort(Comparator.comparingInt(View::getIndex).reversed());
                    }
                }
            }
        }
    }

    /**
     * Retrieves a view from a position, prioritizing the view with the highest index.
     *
     * @param position The position to get the view from.
     * @return The view with the highest index, or null if not found.
     */
    @Nullable
    public View getView(@NotNull Position position) {
        long chunkKey = Chunk.getChunkKey(position.blockX() >> 4, position.blockZ() >> 4);

        List<View> views = chunkViewMap.get(chunkKey);
        if (views != null && !views.isEmpty()) {
            return views.getFirst(); // Return the view with the highest index (first in the sorted list)
        }
        return null;
    }

    /**
     * Retrieves a view from a stage.
     *
     * @param stageName The name of the stage.
     * @param viewName The name of the view.
     * @return The view, or null if not found.
     */
    @Nullable
    public View getView(@NotNull String stageName, @NotNull String viewName) {
        Stage stage = stages.get(stageName);
        return stage != null ? stage.getView(viewName) : null;
    }

    /**
     * Adds block data to the block data cache, optionally associating it with a view.
     *
     * @param position The position of the block.
     * @param blockData The block data.
     * @param viewName The name of the view (optional).
     */
    public void addBlockData(@NotNull Position position, @NotNull BlockData blockData, @Nullable String viewName) {
        long chunkKey = Chunk.getChunkKey(position.blockX() >> 4, position.blockZ() >> 4);

        if (viewName != null) {
            // Loop through stages to find the view that contains this chunk
            for (Stage stage : stages.values()) {
                View view = stage.getView(viewName);
                if (view != null && view.getBound().contains(position)) {
                    // Add the chunk to the view's chunk set
                    view.addChunk(chunkKey);

                    // Cache the block data
                    blockDataCache.computeIfAbsent(chunkKey, k -> new HashMap<>()).put(position, blockData);

                    // Get or create the list of views for the chunk and add the new view
                    List<View> viewsForChunk = chunkViewMap.computeIfAbsent(chunkKey, k -> new ArrayList<>());
                    if (!viewsForChunk.contains(view)) {
                        viewsForChunk.add(view);
                        // Sort the list of views by their index before storing it
                        viewsForChunk.sort(Comparator.comparingInt(View::getIndex));
                    }

                    return;  // Block data added, exit early
                }
            }
        } else {
            // If no view is provided, just cache the block data without view-specific handling
            blockDataCache.computeIfAbsent(chunkKey, k -> new HashMap<>()).put(position, blockData);
        }
    }

    /**
     * Refreshes all blocks in a specific view.
     *
     * @param stageName The name of the stage.
     * @param viewName The name of the view.
     */
    public void refreshBlocksInView(@NotNull String stageName, @NotNull String viewName) {
        View view = getView(stageName, viewName);
        if (view == null) return;

        Map<Position, BlockData> blocksToUpdate = new HashMap<>();
        for (long chunkKey : view.getChunkKeys()) {
            Map<Position, BlockData> chunkBlocks = blockDataCache.get(chunkKey);
            if (chunkBlocks != null) {
                blocksToUpdate.putAll(chunkBlocks);
            }
        }

        if (!blocksToUpdate.isEmpty()) {
            player.sendMultiBlockChange(blocksToUpdate);
            spectators.forEach(spec -> spec.sendMultiBlockChange(blocksToUpdate));
        }
    }

    /**
     * Retrieves block data at a specific position.
     *
     * @param position The position to check.
     * @return The block data, or null if not found.
     */
    @Nullable
    public BlockData getBlockData(@NotNull Position position) {
        long chunkKey = Chunk.getChunkKey(position.blockX() >> 4, position.blockZ() >> 4);
        Map<Position, BlockData> chunkData = blockDataCache.get(chunkKey);
        return chunkData != null ? chunkData.get(position) : null;
    }

    /**
     * Removes block data at a specific position.
     *
     * @param position The position to remove the block data from.
     * @return The removed block data, or null if not found.
     */
    @Nullable
    public BlockData removeBlockData(@NotNull Position position) {
        long chunkKey = Chunk.getChunkKey(position.blockX() >> 4, position.blockZ() >> 4);
        Map<Position, BlockData> chunkData = blockDataCache.get(chunkKey);
        if (chunkData != null) {
            BlockData removed = chunkData.remove(position);
            if (chunkData.isEmpty()) {
                blockDataCache.remove(chunkKey);
            }
            return removed;
        }
        return null;
    }

    /**
     * Refreshes all blocks currently stored in the block data cache.
     */
    public void refreshAllBlocks() {
        Map<Position, BlockData> blocksToUpdate = new HashMap<>();

        for (long chunkKey : blockDataCache.keySet()) {
            Map<Position, BlockData> chunkBlocks = blockDataCache.get(chunkKey);
            blocksToUpdate.putAll(chunkBlocks);
        }

        if (!blocksToUpdate.isEmpty()) {
            player.sendMultiBlockChange(blocksToUpdate);
            spectators.forEach(spec -> spec.sendMultiBlockChange(blocksToUpdate));
        }
    }

    /**
     * Refreshes all blocks currently stored in the block data cache.
     */
    public void refreshAllBlocks(@NotNull Player spectator) {
        Map<Position, BlockData> blocksToUpdate = new HashMap<>();

        for (long chunkKey : blockDataCache.keySet()) {
            Map<Position, BlockData> chunkBlocks = blockDataCache.get(chunkKey);
            blocksToUpdate.putAll(chunkBlocks);
        }

        if (!blocksToUpdate.isEmpty()) {
            spectator.sendMultiBlockChange(blocksToUpdate);
        }
    }

    /**
     * Refreshes specific positions.
     *
     * @param positions The positions to refresh.
     */
    public void refreshBlocks(@NotNull Collection<Position> positions) {
        Map<Position, BlockData> blocksToUpdate = new HashMap<>();

        for (Position position : positions) {
            long chunkKey = Chunk.getChunkKey(position.blockX() >> 4, position.blockZ() >> 4);
            Map<Position, BlockData> chunkBlocks = blockDataCache.get(chunkKey);

            if (chunkBlocks != null && chunkBlocks.containsKey(position)) {
                BlockData blockData = chunkBlocks.get(position);
                blocksToUpdate.put(position, blockData);
            }
        }

        if (!blocksToUpdate.isEmpty()) {
            player.sendMultiBlockChange(blocksToUpdate);
            spectators.forEach(spec -> spec.sendMultiBlockChange(blocksToUpdate));
        }
    }
}
