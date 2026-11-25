package shadows.placebo.item;

import net.minecraft.item.Item;
import shadows.placebo.client.IHasModel;
import shadows.placebo.registry.RegistryInformation;

public abstract class ItemBase extends Item implements IHasModel {
   public ItemBase(String name, RegistryInformation info) {
      this.setRegistryName(name);
      this.setUnlocalizedName(info.getID() + "." + name);
      this.setCreativeTab(info.getDefaultTab());
      info.getItemList().add(this);
   }
}
