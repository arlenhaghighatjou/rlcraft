package gigaherz.toolbelt.shadow.commons;

import net.minecraft.item.ItemSpade;
import net.minecraft.item.Item.ToolMaterial;

public class ItemRegisteredSpade extends ItemSpade {
   public ItemRegisteredSpade(String name, ToolMaterial material) {
      super(material);
      this.setRegistryName(name);
      this.setUnlocalizedName(this.getRegistryName().getResourceDomain() + "." + name);
   }
}
