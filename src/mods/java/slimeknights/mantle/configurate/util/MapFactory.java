package slimeknights.mantle.configurate.util;

import java.util.concurrent.ConcurrentMap;

@FunctionalInterface
public interface MapFactory {
   <K, V> ConcurrentMap<K, V> create();
}
