package com.google.common.collect;

import java.util.Map;
import java.util.HashMap;

public final class MapMaker
{
    public MapMaker weakKeys()
    {
        return this;
    }

    public <K, V> Map<K, V> makeMap()
    {
        return new HashMap<K, V>();
    }
}
