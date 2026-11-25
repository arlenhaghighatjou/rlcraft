package rustic.common.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rustic.core.Rustic;

public class ItemBase extends Item {
   public ItemBase(String name) {
      this.setRegistryName(name);
      this.setUnlocalizedName("rustic." + name);
      this.setCreativeTab(Rustic.farmingTab);
      GameRegistry.findRegistry(Item.class).register(this);
   }

   public void initModel() {
      ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName().toString()));
   }
}
