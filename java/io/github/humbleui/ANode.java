package io.github.humbleui.interfaces;

import java.util.Objects;
import io.github.humbleui.interfaces.IComponent;
import io.github.humbleui.types.IPoint;
import io.github.humbleui.types.IRect;

public abstract class ANode implements IComponent {
    public Object element;
    public ANode parent;
    public IRect bounds;
    public Object containerSize;
    public Object thisSize;
    public Object key;
    public boolean mounted;
    public boolean dirty;

    public Object context(Object ctx) {
        return ctx;
    }

    public void setContainerSize(Object ctx, Object newContainerSize) {
        if (!Objects.equals(containerSize, newContainerSize)) {
            setContainerSizeImpl(ctx, newContainerSize);
            containerSize = newContainerSize;
            thisSize = null;
        }
    }

    public abstract void setContainerSizeImpl(Object ctx, Object containerSize);

    public Object measure(Object ctx, Object cs) {
        ctx = context(ctx);
        setContainerSize(ctx, cs);
        maybeRender(ctx);

        if (thisSize != null) {
            return thisSize;
        } else {
            Object size = measureImpl(ctx, cs);
            thisSize = size;
            return size;
        }
    }

    protected abstract Object measureImpl(Object ctx, Object cs);

    public void maybeRender(Object ctx) {
        // (node instanceof FnNode && node.shouldRender() &&
        //  node.shouldRender.apply(node.getElement().subList(1, node.getElement().size())))
        if (dirty) {
            ctx = context(ctx);
            reconcileChildren(ctx, element);
            reconcileOpts(ctx, element);

            dirty = false;
        }
    }

    // protected abstract void maybeRender(Object ctx);

    public static boolean iRectIntersect(IRect a, IRect b) {
        if (a != null && b != null) {
            if (a.intersect(b) != null) {
                return true;
            }

            return false;
        }
        return false;
    }

    public void draw(Object ctx, IRect bounds, Object containerSize, IRect viewport, Object canvas) {
        this.bounds = bounds;
        setContainerSize(ctx, containerSize);

        if (iRectIntersect(bounds, viewport)) {
            ctx = context(ctx);
            maybeRender(ctx);
            drawImpl(ctx, bounds, containerSize, viewport, canvas);
            if (isDebugMode() && !mounted) {
                drawBorder(ctx, bounds, canvas);
                this.mounted = true;
            }
        }
    }

    public abstract void drawImpl(Object ctx, IRect bounds, Object containerSize, Object viewport, Object canvas);

    // // Handles the event processing
    // public void event(Object ctx, Object event) {
    //     ctx = context(ctx);
    //     if ("window-scale-change".equals(getEventType(event))) {
    //         reconcileOpts(ctx, element);
    //     }
    //     eventImpl(ctx, event);
    // }

    // protected abstract void eventImpl(Object ctx, Object event);

    // public boolean shouldReconcile(Object ctx, Object element) {
    //     return true;
    // }

    // public ANode reconcile(Object ctx, Object newElement) {
    //     if (!Objects.equals(this.element, newElement)) {
    //         ctx = context(ctx);
    //         reconcileChildren(ctx, newElement);
    //         reconcileOpts(ctx, newElement);
    //         this.element = newElement;
    //         invalidateSize();
    //     }
    //     return this;
    // }

    // protected abstract void reconcileChildren(Object ctx, Object newElement);

    // protected void reconcileOpts(Object ctx, Object newElement) {
    //     return "nop";
    // }

    // public void unmount() {
    //     unmountImpl();
    // }

    // protected abstract void unmountImpl();

    // // Helper methods

    // protected void invalidateSize() {
    //     thisSize = null;
    // }

    protected boolean irectIntersect(IRect bounds, Object viewport) {
        // Dummy intersection logic
        return true;
    }

    // protected String getEventType(Object event) {
    //     // Dummy event type handling
    //     return "event-type";
    // }

    public boolean isDebugMode() {
        // Assume a debugging mode flag
        return false;
    }

    // protected void drawBorder(Object ctx, Object bounds, Canvas canvas) {
    //     // Dummy implementation to draw the border
    // }
}
