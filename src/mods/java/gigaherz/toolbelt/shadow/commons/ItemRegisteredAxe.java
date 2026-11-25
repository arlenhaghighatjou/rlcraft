package gigaherz.toolbelt.shadow.commons;

import net.minecraft.item.ItemAxe;
import net.minecraft.item.Item.ToolMaterial;

public class ItemRegisteredAxe extends ItemAxe {
   public ItemRegisteredAxe(String name, ToolMaterial material, float damage, float speed) {
      super(material, damage, speed);
      this.setRegistryName(name);
      this.setUnlocalizedName(this.getRegistryName().getResourceDomain() + "." + name);
   }
}
