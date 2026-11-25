package prospector.traverse.shadow.shootingstar;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemCompound {
   private String modid;
   private Item item;
   private ResourceLocation registryName;

   public ItemCompound(String modid, Item item) {
      this.modid = modid;
      this.item = item;
      this.registryName = item.getRegistryName();
   }

   public Item getItem() {
      return this.item;
   }

   public ItemCompound setItem(Item item) {
      this.item = item;
      return this;
   }

   public String getModid() {
      return this.modid;
   }

   public ResourceLocation getRegistryName() {
      return this.registryName;
   }
}
