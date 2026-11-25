package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.item.ItemBlockVariant;
import java.util.Random;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockWebBase extends BlockWeb implements IMetaName {
   public static final PropertyEnum<BlockWebBase.EnumType> VARIANT = PropertyEnum.create("variant", BlockWebBase.EnumType.class);

   public BlockWebBase(String name) {
      this.setRegistryName(name);
      this.setUnlocalizedName("srparasites." + name);
      this.setTickRandomly(true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockWebBase.EnumType.ONE));
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote) {
         worldIn.setBlockToAir(pos);
      }

   }

   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      if (!worldIn.isRemote) {
         if (entityIn.ticksExisted % 20 != 0) {
            return;
         }

         if (!(entityIn instanceof EntityParasiteBase) && entityIn instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase)entityIn;
            switch((BlockWebBase.EnumType)state.getValue(VARIANT)) {
            case ONE:
            default:
               break;
            case THREE:
               target.attackEntityFrom(DamageSource.CACTUS, 8.0F);
               break;
            case TWO:
               target.attackEntityFrom(DamageSource.CACTUS, 4.0F);
            }
         }
      }

      super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
      for(BlockWebBase.EnumType variant : BlockWebBase.EnumType.values()) {
         items.add(new ItemStack(this, 1, variant.ordinal()));
      }

   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockWebBase.EnumType.values()[meta]);
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockWebBase.EnumType)state.getValue(VARIANT)).ordinal();
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(world.getBlockState(pos)));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{VARIANT});
   }

   public Enum[] getVariants() {
      return BlockWebBase.EnumType.values();
   }

   public ItemBlock getItemBlock() {
      return new ItemBlockVariant(this);
   }

   public static enum EnumType implements IStringSerializable {
      ONE,
      TWO,
      THREE;

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
