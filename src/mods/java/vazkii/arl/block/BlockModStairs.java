package vazkii.arl.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.arl.interf.IModBlock;
import vazkii.arl.item.ItemModBlock;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;

public abstract class BlockModStairs extends BlockStairs implements IModBlock {
   private final String[] variants;
   private final String bareName;

   public BlockModStairs(String name, IBlockState state) {
      super(state);
      this.variants = new String[]{name};
      this.bareName = name;
      this.setUnlocalizedName(name);
      this.useNeighborBrightness = true;
   }

   public ItemBlock createItemBlock(ResourceLocation res) {
      return new ItemModBlock(this, res);
   }

   @Nonnull
   public Block setUnlocalizedName(@Nonnull String name) {
      super.setUnlocalizedName(name);
      this.setRegistryName(this.getPrefix() + name);
      ProxyRegistry.register(this);
      ProxyRegistry.register(this.createItemBlock(this.getRegistryName()));
      return this;
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

   public static void initStairs(Block base, int meta, BlockStairs block) {
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(block, 4), "B  ", "BB ", "BBB", 'B', ProxyRegistry.newStack(base, 1, meta));
   }
}
