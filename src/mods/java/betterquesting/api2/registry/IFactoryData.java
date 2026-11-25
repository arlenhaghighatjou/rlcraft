package betterquesting.api2.registry;

/** @deprecated */
@Deprecated
public interface IFactoryData<T, E> extends IFactory<T> {
   T loadFromData(E var1);
}
