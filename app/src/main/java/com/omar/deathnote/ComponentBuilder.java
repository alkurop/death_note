package com.omar.deathnote;

public interface ComponentBuilder<T extends BaseComponent> {
    T build();
}
