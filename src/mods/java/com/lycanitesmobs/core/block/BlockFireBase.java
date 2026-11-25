package com.lycanitesmobs.core.block;

import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.info.ModInfo;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFireBase extends BlockBase {
   public static final PropertyBool PERMANENT = PropertyBool.create("permanent");
   public static final PropertyBool STATIC = PropertyBool.create("static");
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool EAST = PropertyBool.create("east");
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool WEST = PropertyBool.create("west");
   public static final PropertyBool UPPER = PropertyBool.create("up");
   public static final DamageSource COLD_FIRE = (new DamageSource("cold_fire")).setDamageBypassesArmor().setMagicDamage();
   public boolean dieInRain = true;
   public boolean triggerTNT = true;
   public boolean tickRandomly = true;
   public int agingRate = 3;
   public float spreadChance = 1.0F;
   public boolean removeOnNoFireTick = false;

   public BlockFireBase(Material material, ModInfo group, String name) {
      super(material, group, name);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)).withProperty(PERMANENT, Boolean.valueOf(false)).withProperty(STATIC, Boolean.valueOf(false)).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)).withProperty(UPPER, Boolean.valueOf(false)));
      this.removeOnTick = false;
      this.removeOnNoFireTick = false;
      this.loopTicks = true;
      this.canBeCrushed = true;
      this.noEntityCollision = true;
      this.noBreakCollision = false;
      this.isOpaque = false;
      this.tickRate = 30;
      this.setLightOpacity(1);
      this.setTickRandomly(this.tickRandomly);
      this.setSoundType(SoundType.CLOTH);
      this.disableStats();
   }

   public BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{AGE, PERMANENT, STATIC, NORTH, EAST, SOUTH, WEST, UPPER});
   }

   public String getLocalizedName() {
      return LanguageManager.translate(this.getUnlocalizedName() + ".name");
   }

   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
      tooltip.add(this.getDescription(stack, world));
   }

   public String getDescription(ItemStack itemStack, World world) {
      return "§a" + LanguageManager.translate(this.getUnlocalizedName() + ".description");
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return !worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP) && !Blocks.FIRE.canCatchFire(worldIn, pos.down(), EnumFacing.UP) ? state.withProperty(NORTH, Boolean.valueOf(this.canCatchFire(worldIn, pos.north(), EnumFacing.SOUTH))).withProperty(EAST, Boolean.valueOf(this.canCatchFire(worldIn, pos.east(), EnumFacing.WEST))).withProperty(SOUTH, Boolean.valueOf(this.canCatchFire(worldIn, pos.south(), EnumFacing.NORTH))).withProperty(WEST, Boolean.valueOf(this.canCatchFire(worldIn, pos.west(), EnumFacing.EAST))).withProperty(UPPER, Boolean.valueOf(this.canCatchFire(worldIn, pos.up(), EnumFacing.DOWN))).withProperty(PERMANENT, Boolean.valueOf(false)).withProperty(STATIC, Boolean.valueOf(false)) : this.getDefaultState();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(AGE)).intValue();
   }

   public int tickRate(World world) {
      return this.tickRate;
   }

   public void updateTick(World world, BlockPos pos, IBlockState blockState, EaglercraftRandom rand) {
      if (!((Boolean)blockState.getValue(STATIC)).booleanValue() && world.isAreaLoaded(pos, 2)) {
         boolean permanent = ((Boolean)blockState.getValue(PERMANENT)).booleanValue();
         if (!world.getGameRules().getBoolean("doFireTick")) {
            if (this.removeOnNoFireTick && !permanent) {
               world.setBlockToAir(pos);
            }

         } else {
            if (!this.canPlaceBlockAt(world, pos) || this.removeOnTick) {
               world.setBlockToAir(pos);
            }

            Block blockBelow = world.getBlockState(pos.down()).getBlock();
            boolean isOnFireSource = permanent || this.isBlockFireSource(blockBelow, world, pos.down(), EnumFacing.UP);
            int age = ((Integer)blockState.getValue(AGE)).intValue();
            if (!isOnFireSource && this.canDie(world, pos) && rand.nextFloat() < 0.2F + (float)age * 0.03F) {
               world.setBlockToAir(pos);
            } else {
               if (age < 15) {
                  blockState = blockState.withProperty(AGE, Integer.valueOf(Math.max(age + Math.round((float)rand.nextInt(this.agingRate) / 2.0F), 15)));
                  world.setBlockState(pos, blockState, 4);
               }

               if (this.loopTicks) {
                  world.scheduleUpdate(pos, this, this.tickRate(world) + rand.nextInt(10));
               }

               if (!isOnFireSource) {
                  if (!this.canNeighborCatchFire(world, pos)) {
                     if (age > 3) {
                        world.setBlockToAir(pos);
                     }

                     return;
                  }

                  if (!this.canCatchFire(world, pos.down(), EnumFacing.UP) && age == 15 && rand.nextInt(4) == 0) {
                     world.setBlockToAir(pos);
                     return;
                  }
               }

               if (this.spreadChance > 0.0F && !permanent) {
                  boolean highHumidity = world.isBlockinHighHumidity(pos);
                  int humidityChance = 0;
                  if (highHumidity) {
                     humidityChance = -50;
                  }

                  this.tryCatchFire(world, pos.east(), 300 + humidityChance, rand, age, EnumFacing.WEST);
                  this.tryCatchFire(world, pos.west(), 300 + humidityChance, rand, age, EnumFacing.EAST);
                  this.tryCatchFire(world, pos.down(), 250 + humidityChance, rand, age, EnumFacing.UP);
                  this.tryCatchFire(world, pos.up(), 250 + humidityChance, rand, age, EnumFacing.DOWN);
                  this.tryCatchFire(world, pos.north(), 300 + humidityChance, rand, age, EnumFacing.SOUTH);
                  this.tryCatchFire(world, pos.south(), 300 + humidityChance, rand, age, EnumFacing.NORTH);

                  for(int offsetX = -1; offsetX <= 1; ++offsetX) {
                     for(int offsetZ = -1; offsetZ <= 1; ++offsetZ) {
                        for(int offsetY = -1; offsetY <= 4; ++offsetY) {
                           if (offsetX != 0 || offsetY != 0 || offsetZ != 0) {
                              int chance = 100;
                              if (offsetY > 1) {
                                 chance += (offsetY - 1) * 100;
                              }

                              BlockPos spreadPos = pos.add(offsetX, offsetY, offsetZ);
                              int spreadEncouragement = this.getNeighborEncouragement(world, spreadPos);
                              if (spreadEncouragement > 0) {
                                 int spreadFlammability = (spreadEncouragement + 40 + world.getDifficulty().getDifficultyId() * 7) / (age + 30);
                                 if (highHumidity) {
                                    spreadFlammability /= 2;
                                 }

                                 if (spreadFlammability > 0 && rand.nextInt(chance) <= spreadFlammability && (!world.isRaining() || !this.canDie(world, spreadPos))) {
                                    int spreadAge = age + rand.nextInt(5) / 4;
                                    if (spreadAge > 15) {
                                       spreadAge = 15;
                                    }

                                    world.setBlockState(spreadPos, blockState.withProperty(AGE, Integer.valueOf(spreadAge)).withProperty(PERMANENT, Boolean.valueOf(false)), 3);
                                 }
                              }
                           }
                        }
                     }
                  }

               }
            }
         }
      }
   }

   public boolean requiresUpdates() {
      return !this.tickRandomly;
   }

   public boolean canPlaceBlockAt(World world, BlockPos pos) {
      try {
         return world.getBlockState(pos.down()).isSideSolid(world, pos, EnumFacing.UP) || this.canNeighborCatchFire(world, pos);
      } catch (Exception var4) {
         return false;
      }
   }

   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos triggerPos) {
   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      super.onBlockAdded(world, pos, state);
   }

   protected boolean canNeighborCatchFire(World worldIn, BlockPos pos) {
      for(EnumFacing enumfacing : EnumFacing.values()) {
         if (this.canCatchFire(worldIn, pos.offset(enumfacing), enumfacing.getOpposite())) {
            return true;
         }
      }

      return false;
   }

   protected int getNeighborEncouragement(World worldIn, BlockPos pos) {
      if (!worldIn.isAirBlock(pos)) {
         return 0;
      } else {
         int i = 0;

         for(EnumFacing enumfacing : EnumFacing.values()) {
            i = Math.max(worldIn.getBlockState(pos.offset(enumfacing)).getBlock().getFlammability(worldIn, pos.offset(enumfacing), enumfacing.getOpposite()), i);
         }

         return i;
      }
   }

   private void tryCatchFire(World world, BlockPos pos, int chance, EaglercraftRandom random, int age, EnumFacing face) {
      int flammability = this.getBlockFlammability(world, pos, face);
      if (Math.round((float)random.nextInt(chance) / this.spreadChance) < flammability) {
         IBlockState blockState = world.getBlockState(pos);
         if (random.nextInt(age + 10) < 5 && !world.isRainingAt(pos)) {
            int newFireAge = age + random.nextInt(5) / 4;
            if (newFireAge > 15) {
               newFireAge = 15;
            }

            this.burnBlockReplace(world, pos, newFireAge);
         } else {
            this.burnBlockDestroy(world, pos);
         }

         if (this.triggerTNT && blockState.getBlock() == Blocks.TNT) {
            Blocks.TNT.onBlockDestroyedByPlayer(world, pos, blockState.withProperty(BlockTNT.EXPLODE, Boolean.valueOf(true)));
         }
      }

   }

   public void burnBlockReplace(World world, BlockPos pos, int newFireAge) {
      world.setBlockState(pos, this.getDefaultState().withProperty(AGE, Integer.valueOf(newFireAge)), 3);
   }

   public void burnBlockDestroy(World world, BlockPos pos) {
      world.setBlockToAir(pos);
   }

   public boolean canCatchFire(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return world.getBlockState(pos).getBlock().isFlammable(world, pos, face);
   }

   public boolean isBlockFireSource(Block block, World world, BlockPos pos, EnumFacing side) {
      return block.isFireSource(world, pos, side);
   }

   public int getBlockFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return world.getBlockState(pos).getBlock().getFlammability(world, pos, face);
   }

   protected boolean canDie(World world, BlockPos pos) {
      return world.isRainingAt(pos) || world.isRainingAt(pos.west()) || world.isRainingAt(pos.east()) || world.isRainingAt(pos.north()) || world.isRainingAt(pos.south());
   }

   public int quantityDropped(EaglercraftRandom random) {
      return 0;
   }

   protected ItemStack getSilkTouchDrop(IBlockState state) {
      return new ItemStack(this.getItemDropped(state, (EaglercraftRandom)null, 0), 1, 0);
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }
}
