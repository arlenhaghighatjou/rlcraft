package com.mujmajnkraft.bettersurvival.recipe;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class CrushingRecipe {
   private static final CrushingRecipe CRUSHING_BASE = new CrushingRecipe();
   private final Map<IBlockState, IBlockState> crushingList = Maps.newHashMap();

   public static CrushingRecipe instance() {
      return CRUSHING_BASE;
   }

   public static void Crush(EntityPlayer playerIn, BlockPos pos, boolean particles) {
      World worldIn = playerIn.world;
      if (worldIn instanceof WorldServer && particles) {
         WorldServer worldserver = (WorldServer)worldIn;
         worldserver.spawnParticle(EnumParticleTypes.CRIT, (double)((float)pos.getX() + 0.5F), (double)pos.getY() + 1.0D, (double)((float)pos.getZ() + 0.5F), 1, 0.0D, 0.0D, 0.0D, 0.0D, new int[0]);
      }

      IBlockState result = instance().getCrushingResult(worldIn.getBlockState(pos));
      if (result != null) {
         if (worldIn.getBlockState(pos).getBlock() == Blocks.MONSTER_EGG && !worldIn.isRemote) {
            EntitySilverfish entitysilverfish = new EntitySilverfish(worldIn);
            entitysilverfish.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D, 0.0F, 0.0F);
            worldIn.spawnEntity(entitysilverfish);
            entitysilverfish.attackEntityFrom(DamageSource.causePlayerDamage(playerIn), (float)playerIn.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
         }

         worldIn.setBlockState(pos, result);
      }

   }

   public void addCrushingRecipe(IBlockState target, IBlockState result) {
      if (target != null && result != null && this.getCrushingResult(target) == null) {
         this.crushingList.put(target, result);
      }

   }

   public IBlockState getCrushingResult(IBlockState target) {
      for(Entry<IBlockState, IBlockState> entry : this.crushingList.entrySet()) {
         if (target == entry.getKey()) {
            return (IBlockState)entry.getValue();
         }
      }

      return null;
   }

   private CrushingRecipe() {
      this.addCrushingRecipe(Blocks.STONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState());
      this.addCrushingRecipe(Blocks.SANDSTONE.getDefaultState(), Blocks.SAND.getDefaultState());
      this.addCrushingRecipe(Blocks.STONEBRICK.getDefaultState(), Blocks.STONEBRICK.getStateFromMeta(2));
      this.addCrushingRecipe(Blocks.STONEBRICK.getStateFromMeta(1), Blocks.STONEBRICK.getStateFromMeta(2));
      this.addCrushingRecipe(Blocks.STONEBRICK.getStateFromMeta(3), Blocks.STONEBRICK.getStateFromMeta(2));
      this.addCrushingRecipe(Blocks.STONEBRICK.getStateFromMeta(2), Blocks.COBBLESTONE.getDefaultState());
      this.addCrushingRecipe(Blocks.MOSSY_COBBLESTONE.getDefaultState(), Blocks.GRAVEL.getDefaultState());
      this.addCrushingRecipe(Blocks.COBBLESTONE.getDefaultState(), Blocks.GRAVEL.getDefaultState());
      this.addCrushingRecipe(Blocks.MONSTER_EGG.getDefaultState(), Blocks.COBBLESTONE.getDefaultState());
      this.addCrushingRecipe(Blocks.MONSTER_EGG.getStateFromMeta(1), Blocks.GRAVEL.getDefaultState());
      this.addCrushingRecipe(Blocks.MONSTER_EGG.getStateFromMeta(2), Blocks.STONEBRICK.getStateFromMeta(2));
      this.addCrushingRecipe(Blocks.MONSTER_EGG.getStateFromMeta(3), Blocks.STONEBRICK.getStateFromMeta(2));
      this.addCrushingRecipe(Blocks.MONSTER_EGG.getStateFromMeta(4), Blocks.COBBLESTONE.getDefaultState());
      this.addCrushingRecipe(Blocks.MONSTER_EGG.getStateFromMeta(5), Blocks.STONEBRICK.getStateFromMeta(2));
      this.addCrushingRecipe(Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState());
      this.addCrushingRecipe(Blocks.STONE.getStateFromMeta(2), Blocks.STONE.getStateFromMeta(1));
      this.addCrushingRecipe(Blocks.STONE.getStateFromMeta(4), Blocks.STONE.getStateFromMeta(3));
      this.addCrushingRecipe(Blocks.STONE.getStateFromMeta(6), Blocks.STONE.getStateFromMeta(5));
      this.addCrushingRecipe(Blocks.RED_SANDSTONE.getDefaultState(), Blocks.SAND.getStateFromMeta(1));
      this.addCrushingRecipe(Blocks.RED_SANDSTONE.getStateFromMeta(1), Blocks.RED_SANDSTONE.getDefaultState());
      this.addCrushingRecipe(Blocks.RED_SANDSTONE.getStateFromMeta(2), Blocks.RED_SANDSTONE.getDefaultState());
      this.addCrushingRecipe(Blocks.SANDSTONE.getStateFromMeta(1), Blocks.SANDSTONE.getDefaultState());
      this.addCrushingRecipe(Blocks.SANDSTONE.getStateFromMeta(2), Blocks.SANDSTONE.getDefaultState());
      this.addCrushingRecipe(Blocks.PRISMARINE.getStateFromMeta(1), Blocks.PRISMARINE.getDefaultState());

      for(int i = 0; i < 16; ++i) {
         this.addCrushingRecipe(Blocks.WOOL.getStateFromMeta(i), Blocks.CARPET.getStateFromMeta(i));
      }

   }
}
