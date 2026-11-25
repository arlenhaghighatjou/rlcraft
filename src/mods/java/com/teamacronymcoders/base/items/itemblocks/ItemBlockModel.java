package com.teamacronymcoders.base.items.itemblocks;

import com.teamacronymcoders.base.client.models.IHasModel;
import com.teamacronymcoders.base.items.IHasSubItems;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockModel<T extends Block & IHasModel> extends ItemBlockGeneric<T> implements IHasModel {
   public ItemBlockModel(T block) {
      super(block);
   }

   public Item getItem() {
      return this;
   }

   public List<ItemStack> getAllSubItems(List<ItemStack> itemStacks) {
      return ((IHasSubItems)this.getActualBlock()).getAllSubItems(itemStacks);
   }

   @SideOnly(Side.CLIENT)
   public List<ModelResourceLocation> getModelResourceLocations(List<ModelResourceLocation> models) {
      return ((IHasModel)this.getActualBlock()).getModelResourceLocations(models);
   }
}
