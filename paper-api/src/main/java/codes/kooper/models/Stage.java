package codes.kooper.models;

import codes.kooper.utils.Bound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Stage {
    private final String name;
    private final Map<String, View> views;

    public Stage(@NotNull String name) {
        this.name = name;
        this.views = new HashMap<>();
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void addView(@NotNull View view) {
        views.put(view.getName(), view);
    }

    public void removeView(@NotNull View view) {
        views.remove(view.getName());
    }

    public void removeView(@NotNull String viewName) {
        views.remove(viewName);
    }

    @Nullable
    public View getView(@NotNull String viewName) {
        return views.get(viewName);
    }

    @NotNull
    public Map<String, View> getViews() {
        return views;
    }

    /**
     * Calculates the smallest bounding box that fits all of the views in this stage.
     *
     * @return The smallest bound that fits all the views, or null if no views exist.
     */
    @Nullable
    public Bound getSmallestBound() {
        if (views.isEmpty()) {
            return null; // No views, so no bound
        }

        Bound smallestBound = null;

        // Iterate over all views and calculate the union of their bounds
        for (View view : views.values()) {
            Bound viewBound = view.getBound();
            if (viewBound != null) {
                if (smallestBound == null) {
                    smallestBound = new Bound(viewBound); // Initialize with the first view's bound
                } else {
                    smallestBound = smallestBound.union(viewBound); // Combine bounds
                }
            }
        }

        return smallestBound;
    }
}
