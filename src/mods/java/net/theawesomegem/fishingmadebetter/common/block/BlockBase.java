package net.theawesomegem.fishingmadebetter.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockBase extends Block {
   public BlockBase(Material material, MapColor mapColor, String name) {
      super(material, mapColor);
      this.setRegistryName(name);
      this.setUnlocalizedName("fishingmadebetter." + name);
   }

   public BlockBase(Material material, String blockName) {
      this(material, material.getMaterialMapColor(), blockName);
   }

   public Item createItemBlock() {
      return (Item)(new ItemBlock(this)).setRegistryName(this.getRegistryName());
   }

   @SideOnly(Side.CLIENT)
   public void registerItemModel(Item item) {
      ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
   }
}
