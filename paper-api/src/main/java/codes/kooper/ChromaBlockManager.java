package codes.kooper;

import io.papermc.paper.math.Position;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.bukkit.Chunk;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChromaBlockManager {
    private final Long2ObjectOpenHashMap<Map<Position, BlockData>> blockDataCache;

    public ChromaBlockManager() {
        blockDataCache = new Long2ObjectOpenHashMap<>();
    }

    /**
     * Retrieves the {@link BlockData} at the specified position within a chunk.
     *
     * @param position The {@link Position} within the chunk to retrieve the block data for. Must not be null.
     * @return The {@link BlockData} at the specified position within the chunk, or {@code null} if the position is
     *         invalid or not found.
     */
    public BlockData getBlockData(@NotNull Position position) {
        try {
            return blockDataCache.get(Chunk.getChunkKey(position.blockX() >> 4, position.blockZ() >> 4)).get(position);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Sends a multi-block change packet to refresh all blocks currently
     * stored in the block data cache for the specified player.
     * <p>
     * This method retrieves all block data from all cached chunks and updates
     * the player's client with the latest block states.
     *
     * @param player The player to send the block updates to. Must not be null.
     */
    public void refreshAllBlocks(@NotNull Player player) {
        Map<Position, BlockData> blocksToUpdate = new HashMap<>();

        for (long chunkKey : blockDataCache.keySet()) {
            Map<Position, BlockData> chunkBlocks = blockDataCache.get(chunkKey);
            blocksToUpdate.putAll(chunkBlocks);
        }

        if (!blocksToUpdate.isEmpty()) {
            player.sendMultiBlockChange(blocksToUpdate);
        }
    }

    /**
     * Refreshes blocks for the specified player at the given positions.
     * Sends a multi-block change packet to update the block data.
     *
     * @param player    The player to send the block changes to.
     * @param positions The collection of positions to refresh.
     */
    public void refreshBlocks(@NotNull Player player, @NotNull Collection<Position> positions) {
        Map<Position, BlockData> blocksToUpdate = new HashMap<>();

        for (Position position : positions) {
            long chunkKey = Chunk.getChunkKey(position.blockX(), position.blockZ());
            Map<Position, BlockData> chunkBlocks = blockDataCache.get(chunkKey);

            if (chunkBlocks != null && chunkBlocks.containsKey(position)) {
                BlockData blockData = chunkBlocks.get(position);
                blocksToUpdate.put(position, blockData);
            }
        }

        if (!blocksToUpdate.isEmpty()) {
            player.sendMultiBlockChange(blocksToUpdate);
        }
    }

    /**
     * Sets multiple blocks in the block data cache using the specified map of positions and block data.
     *
     * @param entries A map containing {@link Position} objects as keys and {@link BlockData} as values.
     *                Each entry represents the position of the block and the corresponding block data to set.
     */
    public void setMultipleBlocks(@NotNull Map<Position, BlockData> entries) {
        entries.forEach(this::setBlock);
    }

    /**
     * Sets a block in the block data cache at the specified position with the given block data.
     *
     * @param position  The {@link Position} of the block to set.
     * @param blockData The {@link BlockData} to assign to the block at the specified position.
     *                  If the chunk key derived from the position is not present in the cache, the block is not set.
     */
    public void setBlock(@NotNull Position position, @NotNull BlockData blockData) {
        System.out.println(blockDataCache.containsKey(Chunk.getChunkKey(position.blockX() >> 4, position.blockZ() >> 4)) + " < chroma lol");
        if (!blockDataCache.containsKey(Chunk.getChunkKey(position.blockX(), position.blockZ()))) return;
        blockDataCache.get(Chunk.getChunkKey(position.blockX(), position.blockZ())).put(position, blockData);
    }

    /**
     * Whether blockDataCache contains the chunkKey.
     *
     * @param chunkKey The key of the chunk.
     * @return Whether blockDataCache contains the chunkKey.
     */
    public boolean hasChunk(long chunkKey) {
        return blockDataCache.containsKey(chunkKey);
    }

    /**
     * Adds a block data to a specific chunk.
     *
     * @param chunkKey  the key of the chunk
     * @param position  the position of the block
     * @param blockData the block data to add
     */
    public void addBlockData(long chunkKey, @NotNull Position position, @NotNull BlockData blockData) {
        blockDataCache.computeIfAbsent(chunkKey, k -> new HashMap<>()).put(position, blockData);
    }

    /**
     * Removes a block data from a specific chunk.
     *
     * @param chunkKey the key of the chunk
     * @param position the position of the block to remove
     * @return the removed BlockData, or null if not present
     */
    @Nullable
    public BlockData removeBlockData(long chunkKey, @NotNull Position position) {
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
     * Checks if a block data is present at a specific position in a chunk.
     *
     * @param chunkKey the key of the chunk
     * @param position the position of the block
     * @return true if the block data is present, false otherwise
     */
    public boolean hasBlockData(long chunkKey, @NotNull Position position) {
        Map<Position, BlockData> chunkData = blockDataCache.get(chunkKey);
        return chunkData != null && chunkData.containsKey(position);
    }

    /**
     * Gets all block data for a specific chunk.
     *
     * @param chunkKey the key of the chunk
     * @return a map of positions to block data for the chunk, or an empty map if none exist
     */
    @NotNull
    public Map<Position, BlockData> getAllBlockData(long chunkKey) {
        return blockDataCache.getOrDefault(chunkKey, new HashMap<>());
    }
}


