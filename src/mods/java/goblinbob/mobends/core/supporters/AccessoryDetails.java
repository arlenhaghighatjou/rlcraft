package goblinbob.mobends.core.supporters;

import goblinbob.mobends.core.asset.AssetLocation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AccessoryDetails {
   private String displayName;
   private List<AccessoryPart> parts;

   public AccessoryDetails(String displayName, List<AccessoryPart> parts) {
      this.displayName = displayName;
      this.parts = parts;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public Iterable<AccessoryPart> getParts() {
      return this.parts;
   }

   public Collection<AssetLocation> getAssetLocations() {
      return (Collection)this.parts.stream().flatMap((p) -> {
         return p.getAssetLocations().stream();
      }).collect(Collectors.toCollection(ArrayList::new));
   }
}
