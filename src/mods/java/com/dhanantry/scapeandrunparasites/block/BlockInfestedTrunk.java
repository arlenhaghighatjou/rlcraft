package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasite;
import java.util.Random;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockInfestedTrunk extends BlockRotatedPillar {
   public BlockInfestedTrunk(Material material, String name, float hardness, boolean creative, boolean tickRandom) {
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

      this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, Axis.Y));
   }

   public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
      boolean flag = super.removedByPlayer(state, world, pos, player, willHarvest);
      if (world.isRemote) {
         return flag;
      } else {
         if (SRPConfigSystems.useEvolution && this.getMetaFromState(state) > 0) {
            SRPWorldData data = SRPWorldData.get(world);
            data.setTotalKills(-SRPConfigSystems.valueLossBlockTrunk, true, world, false);
         }

         return flag;
      }
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (worldIn.isAreaLoaded(pos, 3)) {
         if (rand.nextDouble() < 0.5D) {
            if (worldIn.getBiome(pos) instanceof BiomeParasite) {
               worldIn.setBlockState(pos, SRPBlocks.ParasiteStain.getDefaultState());
            } else {
               int heart = ParasiteEventWorld.canBiomeStillExist(worldIn, pos, true);
               if (heart > 0) {
                  ParasiteEventWorld.SpreadBiome(worldIn, pos, heart);
               }
            }
         } else if (rand.nextDouble() <= 0.05D) {
            if (!worldIn.isUpdateScheduled(pos, state.getBlock())) {
               this.updateTick(worldIn, pos, state, rand);
            }
         } else if (!worldIn.isUpdateScheduled(pos, state.getBlock())) {
            worldIn.updateBlockTick(pos, state.getBlock(), ParasiteEventWorld.getTimer(this.getMetaFromState(state)), 5);
         }

      }
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (worldIn.isAreaLoaded(pos, 3)) {
         if (!worldIn.isRemote) {
            this.getMetaFromState(state);
            ParasiteEventWorld.canInfestBlock(worldIn, pos, rand, 2, true);
         }

      }
   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState state = this.getDefaultState();
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
      return new BlockStateContainer(this, new IProperty[]{AXIS});
   }

   protected ItemStack getSilkTouchDrop(IBlockState state) {
      return super.getSilkTouchDrop(state);
   }
}
