package svenhjol.meson;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import svenhjol.meson.iface.IMesonBlock;
import svenhjol.meson.iface.IMesonItemBlock;

public class MesonItemBlock extends ItemBlock implements IMesonItemBlock {
   public IMesonBlock block;
   public List<ResourceLocation> variants = new ArrayList();

   public MesonItemBlock(Block block, String name) {
      super(block);
      this.block = (IMesonBlock)block;
      if (this.block.getVariants().length > 0) {
         this.setHasSubtypes(true);
      }

      this.setRegistryName(this.getModId() + ":" + name);
   }

   public String getModId() {
      return this.block.getModId();
   }

   public int getMetadata(int damage) {
      return damage;
   }

   public String getUnlocalizedNameInefficiently(ItemStack stack) {
      int meta = stack.getItemDamage();
      String[] variants = this.block.getVariants();
      String name;
      if (variants.length > 1) {
         name = this.block.getName() + "_" + variants[meta];
      } else {
         name = this.block.getName();
      }

      return "tile." + this.block.getModId() + ":" + name;
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      String[] variants = this.block.getVariants();
      if (this.isInCreativeTab(tab)) {
         for(int i = 0; i < variants.length; ++i) {
            items.add(new ItemStack(this, 1, i));
         }
      }

   }

   public ItemMeshDefinition getCustomMeshDefinition() {
      return null;
   }
}
