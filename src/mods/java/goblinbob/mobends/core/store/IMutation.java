package goblinbob.mobends.core.store;

@FunctionalInterface
public interface IMutation<T, P> {
   default T mutate(T state) {
      return (T)this.mutate(state, (Object)null);
   }

   T mutate(T var1, P var2);
}
