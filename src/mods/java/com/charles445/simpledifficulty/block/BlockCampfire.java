package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.OreDictUtil;
import com.charles445.simpledifficulty.util.SoundUtil;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCampfire extends Block implements IBlockStateIgnore {
   private static final int AGE_MIN = 0;
   private static final int AGE_MAX = 7;
   private static final int LOG_REFUEL = 3;
   private static final int TICK_RATE = 10;
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);
   public static final PropertyBool BURNING = PropertyBool.create("burning");
   private static final IProperty[] ignoredProperties = new IProperty[]{BURNING};
   private static final AxisAlignedBB HITBOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.4D, 1.0D);

   public BlockCampfire() {
      super(Material.CIRCUITS, MapColor.WOOD);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)).withProperty(BURNING, Boolean.valueOf(false)));
      this.setHardness(0.5F);
      this.setSoundType(SoundType.WOOD);
      this.setTickRandomly(true);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack heldItemStack = player.getHeldItem(hand);
      if (heldItemStack.isEmpty()) {
         return true;
      } else {
         Item heldItem = heldItemStack.getItem();
         if (Block.getBlockFromItem(heldItem).equals(SDBlocks.spit)) {
            return false;
         } else if (world.isRemote) {
            if (heldItem == Items.FLINT_AND_STEEL) {
               int age = ((Integer)state.getValue(AGE)).intValue();
               boolean burning = ((Boolean)state.getValue(BURNING)).booleanValue();
               if (!burning && age < 7 && !world.isRainingAt(pos.up())) {
                  world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
               }
            }

            return true;
         } else {
            int age = ((Integer)state.getValue(AGE)).intValue();
            boolean burning = ((Boolean)state.getValue(BURNING)).booleanValue();
            if (OreDictUtil.isOre(OreDictUtil.logWood, heldItemStack)) {
               if (age > 0) {
                  if (!player.capabilities.isCreativeMode) {
                     heldItemStack.shrink(1);
                  }

                  int refuelAmount = 3 + (age == 7 ? 1 : 0);
                  world.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(Math.max(0, age - refuelAmount))), 2);
               }

               return true;
            } else {
               if (!burning && age < 7 && !world.isRainingAt(pos.up())) {
                  if (OreDictUtil.isOre(OreDictUtil.stick, heldItemStack) || heldItem == Items.STICK) {
                     if (!player.capabilities.isCreativeMode) {
                        heldItemStack.shrink(1);
                     }

                     if (world.rand.nextInt(ModConfig.server.miscellaneous.campfireStickIgniteChance) == 0) {
                        world.setBlockState(pos, state.withProperty(BURNING, Boolean.valueOf(true)), 2);
                     }

                     return true;
                  }

                  if (heldItem == Items.FLINT_AND_STEEL) {
                     world.setBlockState(pos, state.withProperty(BURNING, Boolean.valueOf(true)), 2);
                     heldItemStack.damageItem(1, player);
                     return true;
                  }
               }

               return true;
            }
         }
      }
   }

   public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
      if (!world.isRemote) {
         int age = ((Integer)state.getValue(AGE)).intValue();
         boolean burning = ((Boolean)state.getValue(BURNING)).booleanValue();
         if (burning) {
            if (world.isRainingAt(pos.up())) {
               world.setBlockState(pos, state.withProperty(BURNING, Boolean.valueOf(false)), 2);
               this.effectExtinguish(world, pos);
            } else if (rand.nextInt(ModConfig.server.miscellaneous.campfireDecayChance) == 0) {
               ++age;
               if (age >= 7) {
                  world.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(7)).withProperty(BURNING, Boolean.valueOf(false)), 2);
                  this.effectExtinguish(world, pos);
               } else {
                  world.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(age)), 2);
               }
            }
         }

      }
   }

   public int tickRate(World world) {
      return 10;
   }

   public IBlockState getStateFromMeta(int meta) {
      boolean burning = (meta & 1) != 0;
      int age = meta >> 1;
      return this.getDefaultState().withProperty(AGE, Integer.valueOf(age)).withProperty(BURNING, Boolean.valueOf(burning));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(AGE)).intValue() << 1 | (((Boolean)state.getValue(BURNING)).booleanValue() ? 1 : 0);
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{AGE, BURNING});
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return HITBOX;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos) {
      return Block.NULL_AABB;
   }

   public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (!world.isRemote && ((Boolean)state.getValue(BURNING)).booleanValue() && entity instanceof EntityLivingBase) {
         entity.setFire(1);
      }

   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
      return ((Boolean)state.getValue(BURNING)).booleanValue() ? 15 : 0;
   }

   public int quantityDropped(IBlockState state, int fortune, Random random) {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
      super.randomDisplayTick(state, world, pos, rand);
      if (((Boolean)state.getValue(BURNING)).booleanValue()) {
         int age = ((Integer)state.getValue(AGE)).intValue();
         float strength = 1.0F - (float)age / 7.0F;
         if (rand.nextFloat() < strength) {
            int loop = rand.nextInt(6) + 1;

            for(int i = 0; i < loop; ++i) {
               this.createFlameParticle(world, pos, rand);
            }
         }

         if (rand.nextInt(30) == 0) {
            world.playSound(0.5D + (double)pos.getX(), 0.5D + (double)pos.getY(), 0.5D + (double)pos.getZ(), SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 0.5F, 1.0F, false);
         }
      }

   }

   private void effectExtinguish(World world, BlockPos pos) {
      SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH);
      world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, 0.5D + (double)pos.getX(), 0.25D + (double)pos.getY(), 0.5D + (double)pos.getZ(), 0.0D, 0.1D, 0.0D, new int[0]);
      world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, 0.75D + (double)pos.getX(), 0.25D + (double)pos.getY(), 0.75D + (double)pos.getZ(), 0.0D, 0.05D, 0.0D, new int[0]);
      world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, 0.75D + (double)pos.getX(), 0.25D + (double)pos.getY(), 0.25D + (double)pos.getZ(), 0.0D, 0.05D, 0.0D, new int[0]);
      world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, 0.25D + (double)pos.getX(), 0.25D + (double)pos.getY(), 0.75D + (double)pos.getZ(), 0.0D, 0.05D, 0.0D, new int[0]);
      world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, 0.25D + (double)pos.getX(), 0.25D + (double)pos.getY(), 0.25D + (double)pos.getZ(), 0.0D, 0.05D, 0.0D, new int[0]);
   }

   private void createFlameParticle(World world, BlockPos pos, Random rand) {
      double yOffset = rand.nextDouble() * 0.35D + 0.35D;
      double offAdj = (0.7D - yOffset) * 2.28571428D;
      double xOffset = (rand.nextDouble() - 0.5D) * offAdj + 0.5D;
      double zOffset = (rand.nextDouble() - 0.5D) * offAdj + 0.5D;
      world.spawnParticle(EnumParticleTypes.FLAME, xOffset + (double)pos.getX(), yOffset + (double)pos.getY(), zOffset + (double)pos.getZ(), 0.0D, rand.nextDouble() * 0.015D + 0.005D, 0.0D, new int[0]);
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public IProperty[] getIgnoredProperties() {
      return ignoredProperties;
   }
}
