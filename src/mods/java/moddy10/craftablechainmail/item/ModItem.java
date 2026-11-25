package moddy10.craftablechainmail.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ModItem extends Item {
   public ModItem(String name) {
      this.setRegistryName(name);
      this.setUnlocalizedName("craftablechainmail." + name);
   }

   public void initModel() {
      ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
   }
}
