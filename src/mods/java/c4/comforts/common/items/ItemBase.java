package c4.comforts.common.items;

import c4.comforts.common.util.ComfortsUtil;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBase extends Item {
   public ItemBase(String name) {
      this.setCreativeTab(ComfortsUtil.comfortsTab);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
      this.setRegistryName(name);
      this.setUnlocalizedName("comforts." + name);
   }

   @Nonnull
   public String getUnlocalizedName(ItemStack stack) {
      return super.getUnlocalizedName() + "." + EnumDyeColor.byMetadata(stack.getMetadata()).getUnlocalizedName();
   }

   public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         for(int i = 0; i < 16; ++i) {
            items.add(new ItemStack(this, 1, i));
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void initModel() {
      for(int i = 0; i < 16; ++i) {
         ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(this.getRegistryName(), "inventory"));
      }

   }

   @SideOnly(Side.CLIENT)
   public IItemColor getColorFromItemstack() {
      return (stack, tintIndex) -> {
         return tintIndex == 1 ? ComfortsUtil.getColor(stack.getMetadata()) : 16777215;
      };
   }
}
