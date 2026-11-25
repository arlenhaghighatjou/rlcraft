package rustic.common.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rustic.core.Rustic;

public class ItemFoodBase extends ItemFood {
   public ItemFoodBase(String name, int hunger, float saturation, boolean wolfFood) {
      super(hunger, saturation, wolfFood);
      this.setRegistryName(name);
      this.setUnlocalizedName("rustic." + name);
      this.setCreativeTab(Rustic.farmingTab);
      GameRegistry.findRegistry(Item.class).register(this);
      this.initFood();
   }

   public void initFood() {
   }

   public void initModel() {
      ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName().toString()));
   }
}
