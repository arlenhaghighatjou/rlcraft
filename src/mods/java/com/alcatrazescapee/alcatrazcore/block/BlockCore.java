package com.alcatrazescapee.alcatrazcore.block;

import com.alcatrazescapee.alcatrazcore.client.IModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCore extends Block implements IModelProvider {
   public BlockCore(Material material) {
      super(material);
   }

   public BlockCore(Material material, MapColor mapColor) {
      super(material, mapColor);
   }

   @SideOnly(Side.CLIENT)
   public void registerModel() {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "normal"));
   }
}
