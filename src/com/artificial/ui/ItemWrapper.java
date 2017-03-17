package com.artificial.ui;

public class ItemWrapper {
    private final Object obj;

    public ItemWrapper(final Object obj) {
        this.obj = obj;
    }

    public Object getObject() {
        return obj;
    }

    @Override
    public String toString() {
        return obj.toString();
    }
}
