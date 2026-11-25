package gigaherz.toolbelt.shadow.commons;

import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.Item.ToolMaterial;

public class ItemRegisteredPick extends ItemPickaxe {
   public ItemRegisteredPick(String name, ToolMaterial material) {
      super(material);
      this.setRegistryName(name);
      this.setUnlocalizedName(this.getRegistryName().getResourceDomain() + "." + name);
   }
}
