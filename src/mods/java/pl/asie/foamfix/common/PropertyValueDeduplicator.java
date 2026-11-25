package pl.asie.foamfix.common;

import gnu.trove.set.hash.TCustomHashSet;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import pl.asie.foamfix.client.IDeduplicatingStorage;
import pl.asie.foamfix.util.DeduplicatingStorageTrove;
import pl.asie.foamfix.util.HashingStrategies;

public class PropertyValueDeduplicator {
   public int successfuls;

   public PropertyValueDeduplicator() {
   }

   public void deduplicate() {
      IDeduplicatingStorage storage = new DeduplicatingStorageTrove(HashingStrategies.GENERIC);
      Set<IProperty> checkedProperties = new TCustomHashSet(HashingStrategies.IDENTITY);

      for(Block b : ForgeRegistries.BLOCKS) {
         if (b.getRegistryName() == null || !"immersiveengineering".equals(b.getRegistryName().getResourceDomain())) {
            for(IProperty property : b.getBlockState().getProperties()) {
               try {
                  if (checkedProperties.add(property)) {
                     Collection allowedValues = property.getAllowedValues();
                     Collection newAllowedValues = (Collection)storage.deduplicate(allowedValues);
                     if (newAllowedValues != allowedValues) {
                        for(Field f : property.getClass().getDeclaredFields()) {
                           f.setAccessible(true);
                           Object o = f.get(property);
                           if (o == allowedValues) {
                              f.set(property, newAllowedValues);
                              ++this.successfuls;
                           }
                        }
                     }
                  }
               } catch (Exception var14) {
                  var14.printStackTrace();
               }
            }
         }
      }

   }
}
