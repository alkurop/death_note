package com.omar.deathnote.dagger;

public interface ComponentBuilder<T extends BaseComponent> {
    T build();
}
