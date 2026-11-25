package mcjty.lostcities.api;

public interface ILostCityAssetRegistry<T extends ILostCityAsset> {
   T get(String var1);

   Iterable<T> getIterable();
}
