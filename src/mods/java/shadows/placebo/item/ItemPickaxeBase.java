package shadows.placebo.item;

import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.Item.ToolMaterial;
import shadows.placebo.client.IHasModel;
import shadows.placebo.registry.RegistryInformation;

public class ItemPickaxeBase extends ItemPickaxe implements IHasModel {
   public ItemPickaxeBase(String name, RegistryInformation info, ToolMaterial mat) {
      super(mat);
      this.setRegistryName(name);
      this.setUnlocalizedName(info.getID() + "." + name);
      this.setCreativeTab(info.getDefaultTab());
      info.getItemList().add(this);
   }
}
