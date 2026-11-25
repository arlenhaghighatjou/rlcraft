package shadows.placebo.item;

import net.minecraft.item.ItemAxe;
import net.minecraft.item.Item.ToolMaterial;
import shadows.placebo.client.IHasModel;
import shadows.placebo.registry.RegistryInformation;

public class ItemAxeBase extends ItemAxe implements IHasModel {
   public ItemAxeBase(String name, RegistryInformation info, ToolMaterial mat) {
      super(mat, 8.0F, -3.0F);
      this.setRegistryName(name);
      this.setUnlocalizedName(info.getID() + "." + name);
      this.setCreativeTab(info.getDefaultTab());
      info.getItemList().add(this);
   }
}
