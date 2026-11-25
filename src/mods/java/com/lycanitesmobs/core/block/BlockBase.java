package com.lycanitesmobs.core.block;

import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.info.ModInfo;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBase extends Block {
   public ModInfo group;
   public String blockName = "BlockBase";
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
   public int tickRate = 0;
   public boolean removeOnTick = false;
   public boolean loopTicks = true;
   public boolean canBeCrushed = false;
   public boolean noEntityCollision = false;
   public boolean noBreakCollision = false;
   public boolean isOpaque = true;

   public BlockBase(Material material, ModInfo group, String name) {
      super(material);
      this.group = group;
      this.blockName = name;
      this.fullBlock = this.isOpaque;
      this.setup();
   }

   public BlockBase(Material material) {
      super(material);
   }

   public void setup() {
      this.setRegistryName(this.group.modid, this.blockName.toLowerCase());
      this.setUnlocalizedName(this.blockName);
   }

   public void copyAttributesTo(Block block) {
      block.setHardness(this.blockHardness);
      block.setResistance(this.blockResistance);
   }

   public String getLocalizedName() {
      return LanguageManager.translate(this.getUnlocalizedName() + ".name");
   }

   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
      tooltip.add(this.getDescription(stack, world));
   }

   public String getDescription(ItemStack itemStack, World world) {
      return "\u00a7a" + LanguageManager.translate("tile." + this.blockName + ".description");
   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      if (this.tickRate > 0) {
         world.scheduleUpdate(pos, this, this.tickRate(world));
      }

   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom random, int zero) {
      return super.getItemDropped(state, random, zero);
   }

   public int damageDropped(IBlockState state) {
      return 0;
   }

   public int quantityDropped(EaglercraftRandom random) {
      return 1;
   }

   public BlockStateContainer createBlockState() {
      return super.createBlockState();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState();
   }

   public int getMetaFromState(IBlockState state) {
      return 0;
   }

   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos triggerPos) {
      if (this.canBeCrushed && (block == Blocks.SAND || block == Blocks.GRAVEL)) {
         world.setBlockToAir(pos);
      }

      super.neighborChanged(state, world, pos, block, triggerPos);
   }

   public int tickRate(World world) {
      return this.tickRate;
   }

   public void updateTick(World world, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (!world.isRemote) {
         if (this.removeOnTick && this.canRemove(world, pos, state, rand)) {
            world.setBlockToAir(pos);
         } else if (this.tickRate > 0 && this.loopTicks) {
            world.scheduleBlockUpdate(pos, this, this.tickRate(world), 1);
         }

      }
   }

   public boolean getTickRandomly() {
      return this.tickRate > 0;
   }

   public boolean canRemove(World world, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      return true;
   }

   public boolean isPassable(IBlockAccess world, BlockPos pos) {
      return this.noEntityCollision ? true : super.isPassable(world, pos);
   }

   public boolean isCollidable() {
      return this.noBreakCollision ? false : super.isCollidable();
   }

   public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
      return this.noEntityCollision ? false : super.isSideSolid(base_state, world, pos, side);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      return super.getBoundingBox(state, world, pos);
   }

   public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      return this.noEntityCollision ? Block.NULL_AABB : super.getCollisionBoundingBox(state, world, pos);
   }

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
      return super.getSelectedBoundingBox(state, world, pos);
   }

   public boolean isOpaqueCube(IBlockState state) {
      return this.isOpaque;
   }

   public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
      return this.isOpaqueCube(state) ? 255 : super.getLightOpacity(state, world, pos);
   }

   public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState blockState, Entity entity) {
      super.onEntityCollidedWithBlock(world, pos, blockState, entity);
   }

   public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState blockState, Entity entity, double yToTest, Material materialIn, boolean testingHead) {
      return super.isEntityInsideMaterial(world, blockpos, blockState, entity, yToTest, materialIn, testingHead);
   }

   public BlockBase setBlockStepSound(SoundType soundType) {
      return (BlockBase)this.setSoundType(soundType);
   }

   public static enum RENDER_TYPE {
      NONE(-1),
      NORMAL(0),
      CROSS(1),
      TORCH(2),
      FIRE(3),
      FLUID(4);

      public final int id;

      private RENDER_TYPE(int value) {
         this.id = value;
      }

      public int getValue() {
         return this.id;
      }
   }
}
