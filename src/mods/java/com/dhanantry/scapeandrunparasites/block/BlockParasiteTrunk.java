package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.item.ItemBlockVariant;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasite;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockParasiteTrunk extends BlockRotatedPillar implements IMetaName {
   public static final PropertyEnum<BlockParasiteTrunk.EnumType> VARIANT = PropertyEnum.create("variant", BlockParasiteTrunk.EnumType.class);

   public BlockParasiteTrunk(Material material, String name, float hardness, boolean creative, boolean tickRandom) {
      super(material);
      this.setHarvestLevel("axe", 0);
      this.setRegistryName(name);
      this.setUnlocalizedName("srparasites." + name);
      this.setHardness(hardness);
      this.setTickRandomly(tickRandom);
      this.setSoundType(SoundType.WOOD);
      if (creative) {
         this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
      }

      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockParasiteTrunk.EnumType.TREE).withProperty(AXIS, Axis.Y));
   }

   public int damageDropped(IBlockState state) {
      return ((BlockParasiteTrunk.EnumType)state.getValue(VARIANT)).ordinal();
   }

   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
      for(BlockParasiteTrunk.EnumType variant : BlockParasiteTrunk.EnumType.values()) {
         items.add(new ItemStack(this, 1, variant.ordinal()));
      }

   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState state = this.getDefaultState().withProperty(VARIANT, BlockParasiteTrunk.EnumType.values()[(meta & 3) % BlockParasiteTrunk.EnumType.values().length]);
      int i = meta & 12;
      Axis enumfacing$axis = Axis.Y;
      if (i == 4) {
         enumfacing$axis = Axis.X;
      } else if (i == 8) {
         enumfacing$axis = Axis.Z;
      }

      return state.withProperty(AXIS, enumfacing$axis);
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i = i | ((BlockParasiteTrunk.EnumType)state.getValue(VARIANT)).ordinal();
      Axis enumfacing$axis = (Axis)state.getValue(AXIS);
      if (enumfacing$axis == Axis.X) {
         i |= 4;
      } else if (enumfacing$axis == Axis.Z) {
         i |= 8;
      }

      return i;
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(world.getBlockState(pos)));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{VARIANT, AXIS});
   }

   protected ItemStack getSilkTouchDrop(IBlockState state) {
      return super.getSilkTouchDrop(state);
   }

   public Enum[] getVariants() {
      return BlockParasiteTrunk.EnumType.values();
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (worldIn.isAreaLoaded(pos, 3)) {
         if (worldIn.getBiome(pos) instanceof BiomeParasite) {
            ParasiteEventWorld.spreadBiomeBlockTrunk(worldIn, pos, rand);
         }

      }
   }

   public ItemBlock getItemBlock() {
      return new ItemBlockVariant(this);
   }

   public static enum EnumType implements IStringSerializable {
      BALL,
      TREE,
      PLANT;

      private EnumType() {
      }

      public String getName() {
         return this.name().toLowerCase();
      }

      public String toString() {
         return this.getName();
      }
   }
}
