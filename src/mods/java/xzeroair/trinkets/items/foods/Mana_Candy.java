package xzeroair.trinkets.items.foods;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.items.base.FoodBase;

public class Mana_Candy extends FoodBase {
   public Mana_Candy(String name) {
      super(name, 2, 1.0F);
      this.setAlwaysEdible();
      this.setUUID("892f3d8b-cd9a-4efd-9c90-c06e9930eff2");
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 16;
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.EAT;
   }

   @SideOnly(Side.CLIENT)
   public void registerModels() {
      ModelResourceLocation full_stack = new ModelResourceLocation(this.getRegistryName().toString(), "inventory");
      ModelResourceLocation three_forths = new ModelResourceLocation(this.getRegistryName().toString() + "2", "inventory");
      ModelResourceLocation one_half = new ModelResourceLocation(this.getRegistryName().toString() + "3", "inventory");
      ModelResourceLocation one_forth = new ModelResourceLocation(this.getRegistryName().toString() + "4", "inventory");
      ModelBakery.registerItemVariants(this, new ResourceLocation[]{full_stack, three_forths, one_half, one_forth});
      ModelLoader.setCustomMeshDefinition(this, (stack) -> {
         if ((double)stack.getCount() >= (double)stack.getMaxStackSize() * 0.75D) {
            return one_forth;
         } else if ((double)stack.getCount() >= (double)stack.getMaxStackSize() * 0.5D) {
            return one_half;
         } else {
            return (double)stack.getCount() >= (double)stack.getMaxStackSize() * 0.25D ? three_forths : full_stack;
         }
      });
   }
}
