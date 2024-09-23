package io.github.humbleui.interfaces;

import io.github.humbleui.types.IPoint;
import io.github.humbleui.types.IRect;

public interface IComponent {
    Object context(Object ctx);
    void setContainerSize(Object ctx, Object containerSize);
    void setContainerSizeImpl(Object ctx, Object containerSize);
    IPoint measure(Object ctx, IPoint cs);
    IPoint measureImpl(Object ctx, IPoint cs);
    void draw(Object ctx, IRect bounds, Object containerSize, Object viewport, Object canvas);
    void drawImpl(Object ctx, IRect bounds, Object containerSize, Object viewport, Object canvas);
    void event(Object ctx, Object event);
    void eventImpl(Object ctx, Object event);
    void iterate(Object ctx, Object cb);
    boolean shouldReconcile(Object ctx, Object newEl);
    void reconcile(Object ctx, Object newEl);
    Object childElements(Object ctx, Object newEl);
    void reconcileChildren(Object ctx, Object newEl);
    Object reconcileOpts(Object ctx, Object newEl);
    void unmount();
    void unmountImpl();
    void maybeRender(Object ctx);
}
