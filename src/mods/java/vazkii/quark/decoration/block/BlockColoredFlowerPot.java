package vazkii.quark.decoration.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.arl.interf.IRecipeGrouped;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.decoration.client.state.ColoredFlowerPotStateMapper;

public class BlockColoredFlowerPot extends BlockCustomFlowerPot implements IQuarkBlock, IBlockColorProvider, IRecipeGrouped {
   private final String[] variants;
   private final String bareName;

   public BlockColoredFlowerPot(EnumDyeColor color) {
      String name = "colored_flowerpot_" + color.getName();
      this.variants = new String[]{name};
      this.bareName = name;
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.setUnlocalizedName(name);
   }

   @Nonnull
   public Block setUnlocalizedName(@Nonnull String name) {
      super.setUnlocalizedName(name);
      this.register(name);
      return this;
   }

   @Nonnull
   public ItemStack getItem(World worldIn, @Nonnull BlockPos pos, IBlockState state) {
      ItemStack stack = super.getItem(worldIn, pos, state);
      if (stack.getItem() == Items.FLOWER_POT) {
         stack = new ItemStack(Item.getItemFromBlock(this));
      }

      return stack;
   }

   @Nonnull
   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Item.getItemFromBlock(this);
   }

   @Nonnull
   public String getLocalizedName() {
      return I18n.translateToLocal(this.getUnlocalizedName() + ".name");
   }

   public String getBareName() {
      return this.bareName;
   }

   public String[] getVariants() {
      return this.variants;
   }

   @SideOnly(Side.CLIENT)
   public ItemMeshDefinition getCustomMeshDefinition() {
      return null;
   }

   public EnumRarity getBlockRarity(ItemStack stack) {
      return EnumRarity.COMMON;
   }

   @SideOnly(Side.CLIENT)
   public IProperty[] getIgnoredProperties() {
      return null;
   }

   @SideOnly(Side.CLIENT)
   public IStateMapper getStateMapper() {
      return new ColoredFlowerPotStateMapper();
   }

   public IProperty getVariantProp() {
      return null;
   }

   public Class getVariantEnum() {
      return null;
   }

   @SideOnly(Side.CLIENT)
   public IItemColor getItemColor() {
      return (stack, i) -> {
         return 16777215;
      };
   }

   @SideOnly(Side.CLIENT)
   public IBlockColor getBlockColor() {
      return (state, world, pos, i) -> {
         return Minecraft.getMinecraft().getBlockColors().colorMultiplier(Blocks.FLOWER_POT.getDefaultState(), world, pos, i);
      };
   }

   public String getRecipeGroup() {
      return "colored_flower_pot";
   }
}
