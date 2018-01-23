package com.omar.deathnote;

import dagger.MapKey;

@MapKey
public @interface ComponentKey {
    Class<? extends BaseComponent> value();
}

