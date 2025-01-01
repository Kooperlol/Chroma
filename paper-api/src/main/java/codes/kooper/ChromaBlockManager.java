package codes.kooper;

import io.papermc.paper.math.Position;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ChromaBlockManager {
    private final Long2ObjectOpenHashMap<Map<Position, BlockData>> blockDataCache;

    public ChromaBlockManager() {
        blockDataCache = new Long2ObjectOpenHashMap<>();
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


