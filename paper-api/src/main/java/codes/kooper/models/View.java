package codes.kooper.models;

import codes.kooper.utils.Bound;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;

public class View {
    private final String name;
    private final LongSet chunkKeys;
    private boolean breakable;
    private int index;
    private final Stage stage;
    private Bound bound;

    public View(@NotNull Stage stage, @NotNull String name, @NotNull Bound bound) {
        this.name = name;
        this.stage = stage;
        this.chunkKeys = new LongOpenHashSet();
        this.bound = bound;
    }

    public boolean isBreakable() {
        return breakable;
    }

    public void setBreakable(boolean breakable) {
        this.breakable = breakable;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @NotNull
    public Stage getStage() {
        return stage;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public LongSet getChunkKeys() {
        return chunkKeys;
    }

    public void addChunk(long chunkKey) {
        chunkKeys.add(chunkKey);
    }

    public boolean containsChunk(long chunkKey) {
        return chunkKeys.contains(chunkKey);
    }

    // Getter and setter for Bound
    @Nullable
    public Bound getBound() {
        return bound;
    }

    public void setBound(@NotNull Bound bound) {
        this.bound = bound;
    }
}
