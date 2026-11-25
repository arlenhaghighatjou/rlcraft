package rustic.client.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.ItemStack;

public class ItemColorCache implements IResourceManagerReloadListener {
   public static final ItemColorCache INSTANCE = new ItemColorCache();
   protected final Map<String, Integer> itemColors = new HashMap();

   public ItemColorCache() {
   }

   public int getColor(ItemStack stack) {
      String itemKey = stack.getItem().getUnlocalizedName() + "@" + stack.getItemDamage();
      if (!this.itemColors.containsKey(itemKey)) {
         this.itemColors.put(itemKey, Integer.valueOf(ClientUtils.getItemColor(stack)));
      }

      return ((Integer)this.itemColors.get(itemKey)).intValue();
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.itemColors.clear();
   }
}
