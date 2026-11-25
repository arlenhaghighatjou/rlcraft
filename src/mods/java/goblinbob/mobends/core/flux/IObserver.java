package goblinbob.mobends.core.flux;

@FunctionalInterface
public interface IObserver<T> {
   void onChanged(T var1);
}
