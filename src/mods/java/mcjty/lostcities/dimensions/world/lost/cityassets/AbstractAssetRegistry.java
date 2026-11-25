package mcjty.lostcities.dimensions.world.lost.cityassets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mcjty.lostcities.api.ILostCityAsset;
import mcjty.lostcities.api.ILostCityAssetRegistry;

public class AbstractAssetRegistry<T extends ILostCityAsset> implements ILostCityAssetRegistry<T> {
   private final Map<String, T> assets = new HashMap();
   private final List<String> assetNames = new ArrayList();

   public AbstractAssetRegistry() {
   }

   public void register(T building) {
      this.assets.put(building.getName(), building);
      this.assetNames.add(building.getName());
   }

   public <S extends ILostCityAsset> ILostCityAssetRegistry<S> cast() {
      return this;
   }

   public T get(String name) {
      if (name == null) {
         return (T)null;
      } else {
         T t = this.assets.get(name);
         if (t != null) {
            t.init();
         }

         return t;
      }
   }

   public T get(int i) {
      T t = this.assets.get(this.assetNames.get(i));
      if (t != null) {
         t.init();
      }

      return t;
   }

   public Iterable<T> getIterable() {
      return this.assets.values();
   }

   public int getCount() {
      return this.assets.size();
   }

   public String getName(int i) {
      return (String)this.assetNames.get(i);
   }

   public void reset() {
      this.assets.clear();
   }
}
