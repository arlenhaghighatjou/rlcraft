package shadows.placebo.item;

import net.minecraft.item.ItemHoe;
import net.minecraft.item.Item.ToolMaterial;
import shadows.placebo.client.IHasModel;
import shadows.placebo.registry.RegistryInformation;

public class ItemHoeBase extends ItemHoe implements IHasModel {
   public ItemHoeBase(String name, RegistryInformation info, ToolMaterial material) {
      super(material);
      this.setRegistryName(name);
      this.setUnlocalizedName(info.getID() + "." + name);
      this.setCreativeTab(info.getDefaultTab());
      info.getItemList().add(this);
   }
}
