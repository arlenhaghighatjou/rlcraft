package org.orecruncher.dsurround.registry.item;

import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.registry.IDataAccessor;
import org.orecruncher.dsurround.registry.RegistryManager;

@SideOnly(Side.CLIENT)
public final class ItemUtils {
   public ItemUtils() {
   }

   @Nonnull
   public static IItemData getItemData(@Nonnull Item item) {
      IDataAccessor<IItemData> accessor = (IDataAccessor)item;
      IItemData result = accessor.getData();
      if (result == null) {
         RegistryManager.ITEMS.reload();
         result = accessor.getData();
         if (result == null) {
            ModBase.log().warn("Unable to find IItemData for item [%s]", new Object[]{item.toString()});
            result = (IItemData)SimpleItemData.CACHE.get(ItemClass.NONE);
            accessor.setData(result);
         }
      }

      return result;
   }

   public static void setItemData(@Nonnull Item item, @Nonnull IItemData data) {
      ((IDataAccessor)item).setData(data);
   }
}
