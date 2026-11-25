package pl.asie.foamfix.client.deduplicator;

@FunctionalInterface
public interface DeduplicatorFunction {
   Object deduplicate(Object var1, int var2) throws Throwable;
}
