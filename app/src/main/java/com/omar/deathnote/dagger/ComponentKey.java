package com.omar.deathnote.dagger;

import dagger.MapKey;

@MapKey
public @interface ComponentKey {
    Class<? extends BaseComponent> value();
}

