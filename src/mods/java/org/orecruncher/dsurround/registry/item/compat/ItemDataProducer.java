package org.orecruncher.dsurround.registry.item.compat;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.lib.compat.ModEnvironment;
import org.orecruncher.dsurround.registry.item.IItemData;
import org.orecruncher.dsurround.registry.item.ItemClass;
import org.orecruncher.dsurround.registry.item.SimpleItemData;

@SideOnly(Side.CLIENT)
public final class ItemDataProducer {
   private static final List<IItemDataProducer> PRODUCERS = new ArrayList();

   public ItemDataProducer() {
   }

   @Nonnull
   public static IItemData create(@Nonnull Item item, @Nonnull ItemClass ic) {
      for(IItemDataProducer producer : PRODUCERS) {
         IItemData data = producer.create(item, ic);
         if (data != null) {
            return data;
         }
      }

      return (IItemData)SimpleItemData.CACHE.get(ItemClass.NONE);
   }

   static {
      if (ModEnvironment.ConstructArmory.isLoaded()) {
         PRODUCERS.add(new ConstructArmoryProducer());
      }

      PRODUCERS.add((item, ic) -> {
         return (SimpleItemData)SimpleItemData.CACHE.get(ic);
      });
   }
}
