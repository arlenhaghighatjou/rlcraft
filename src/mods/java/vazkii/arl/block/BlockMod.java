package vazkii.arl.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.arl.interf.IModBlock;
import vazkii.arl.item.ItemModBlock;
import vazkii.arl.util.ProxyRegistry;

public abstract class BlockMod extends Block implements IModBlock {
   private final String[] variants;
   private final String bareName;

   public BlockMod(String name, Material materialIn, String... variants) {
      super(materialIn);
      if (variants.length == 0) {
         variants = new String[]{name};
      }

      this.bareName = name;
      this.variants = variants;
      if (this.registerInConstruction()) {
         this.setUnlocalizedName(name);
      }

   }

   public Block setUnlocalizedName(String name) {
      super.setUnlocalizedName(name);
      this.setRegistryName(this.getPrefix() + name);
      ProxyRegistry.register(this);
      ProxyRegistry.register(this.createItemBlock(new ResourceLocation(this.getPrefix() + name)));
      return this;
   }

   public ItemBlock createItemBlock(ResourceLocation res) {
      return new ItemModBlock(this, res);
   }

   public boolean registerInConstruction() {
      return true;
   }

   public String getBareName() {
      return this.bareName;
   }

   public String[] getVariants() {
      return this.variants;
   }

   public EnumRarity getBlockRarity(ItemStack stack) {
      return EnumRarity.COMMON;
   }

   public IProperty[] getIgnoredProperties() {
      return new IProperty[0];
   }

   public IProperty getVariantProp() {
      return null;
   }

   public Class getVariantEnum() {
      return null;
   }
}
