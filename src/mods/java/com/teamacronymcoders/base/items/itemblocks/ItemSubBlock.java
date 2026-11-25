package com.teamacronymcoders.base.items.itemblocks;

import com.teamacronymcoders.base.client.models.IHasModel;
import com.teamacronymcoders.base.items.IHasSubItems;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSubBlock<BLOCK extends Block & IHasModel> extends ItemBlockGeneric<BLOCK> implements IHasModel {
   private String[] names;

   public ItemSubBlock(BLOCK block, String[] names) {
      super(block);
      this.names = names;
      this.setHasSubtypes(true);
   }

   public int getMetadata(int metadata) {
      return metadata;
   }

   @Nonnull
   public String getUnlocalizedName(ItemStack stack) {
      return super.getUnlocalizedName() + "." + this.names[stack.getItemDamage()];
   }

   public List<ItemStack> getAllSubItems(List<ItemStack> itemStacks) {
      return ((IHasSubItems)this.getActualBlock()).getAllSubItems(itemStacks);
   }

   @SideOnly(Side.CLIENT)
   public List<ModelResourceLocation> getModelResourceLocations(List<ModelResourceLocation> models) {
      return ((IHasModel)this.getActualBlock()).getModelResourceLocations(models);
   }
}
