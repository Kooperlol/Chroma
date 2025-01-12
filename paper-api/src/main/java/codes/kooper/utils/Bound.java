package codes.kooper.utils;

import io.papermc.paper.math.Position;
import org.jetbrains.annotations.NotNull;

public class Bound {
    private int minX, minY, minZ;
    private int maxX, maxY, maxZ;

    public Bound() {
        // Default constructor: set the bound to the largest possible values
        this.minX = this.minY = this.minZ = Integer.MAX_VALUE;
        this.maxX = this.maxY = this.maxZ = Integer.MIN_VALUE;
    }

    public Bound(@NotNull Position pos1, @NotNull Position pos2) {
        this.minX = Math.min(pos1.blockX(), pos2.blockX());
        this.minY = Math.min(pos1.blockY(), pos2.blockY());
        this.minZ = Math.min(pos1.blockZ(), pos2.blockZ());
        this.maxX = Math.max(pos1.blockX(), pos2.blockX());
        this.maxY = Math.max(pos1.blockY(), pos2.blockY());
        this.maxZ = Math.max(pos1.blockZ(), pos2.blockZ());
    }

    public Bound(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public Bound(@NotNull Bound other) {
        this.minX = other.minX;
        this.minY = other.minY;
        this.minZ = other.minZ;
        this.maxX = other.maxX;
        this.maxY = other.maxY;
        this.maxZ = other.maxZ;
    }

    public void adjustSize(int dx, int dy, int dz) {
        this.minX -= dx;
        this.minY -= dy;
        this.minZ -= dz;
        this.maxX += dx;
        this.maxY += dy;
        this.maxZ += dz;
    }

    public boolean contains(@NotNull Position position) {
        int x = position.blockX();
        int y = position.blockY();
        int z = position.blockZ();

        // Check if the position's X, Y, and Z are within the min and max values of the bound
        return x >= minX && x <= maxX &&
            y >= minY && y <= maxY &&
            z >= minZ && z <= maxZ;
    }

    // Union of two bounds, returns the smallest bound that fits both
    @NotNull
    public Bound union(@NotNull Bound other) {
        int newMinX = Math.min(this.minX, other.minX);
        int newMinY = Math.min(this.minY, other.minY);
        int newMinZ = Math.min(this.minZ, other.minZ);
        int newMaxX = Math.max(this.maxX, other.maxX);
        int newMaxY = Math.max(this.maxY, other.maxY);
        int newMaxZ = Math.max(this.maxZ, other.maxZ);

        return new Bound(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    // Getters and setters for bound coordinates
    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public boolean equals(@NotNull Bound other) {
        return this.minX == other.minX && this.minY == other.minY && this.minZ == other.minZ &&
            this.maxX == other.maxX && this.maxY == other.maxY && this.maxZ == other.maxZ;
    }

    @Override
    public String toString() {
        return "Bound{" +
            "minX=" + minX + ", minY=" + minY + ", minZ=" + minZ +
            ", maxX=" + maxX + ", maxY=" + maxY + ", maxZ=" + maxZ +
            '}';
    }
}
