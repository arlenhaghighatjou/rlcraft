package gigaherz.toolbelt.shadow.commons;

import net.minecraft.item.Item;

public class ItemRegistered extends Item {
   public ItemRegistered(String name) {
      this.setRegistryName(name);
      this.setUnlocalizedName(this.getRegistryName().getResourceDomain() + "." + name);
   }
}
