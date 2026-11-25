package com.github.alexthe666.iceandfire.structures;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.BlockGoldPile;
import com.github.alexthe666.iceandfire.core.ModBlocks;
import com.github.alexthe666.iceandfire.entity.EntityFireDragon;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockChest;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootTableList;

public class WorldGenFireDragonCave extends WorldGenerator {
   public static final ResourceLocation FIREDRAGON_CHEST = LootTableList.register(new ResourceLocation("iceandfire", "fire_dragon_cave"));

   public WorldGenFireDragonCave() {
   }

   public static void setGoldPile(World world, BlockPos pos) {
      int chance = (new EaglercraftRandom()).nextInt(99) + 1;
      if (!(world.getBlockState(pos).getBlock() instanceof BlockChest)) {
         if (chance < 60) {
            boolean generateGold = IceAndFire.CONFIG.dragonDenGoldAmount > 1 ? (new EaglercraftRandom()).nextInt(IceAndFire.CONFIG.dragonDenGoldAmount) == 0 : true;
            world.setBlockState(pos, generateGold ? ModBlocks.goldPile.getDefaultState().withProperty(BlockGoldPile.LAYERS, Integer.valueOf(1 + (new EaglercraftRandom()).nextInt(7))) : Blocks.AIR.getDefaultState(), 3);
         } else if (chance > 60 && chance < 62) {
            world.setBlockState(pos, Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.HORIZONTALS[(new EaglercraftRandom()).nextInt(3)]), 3);
            if (world.getBlockState(pos).getBlock() instanceof BlockChest) {
               TileEntity tileentity1 = world.getTileEntity(pos);
               if (tileentity1 instanceof TileEntityChest && !((TileEntityChest)tileentity1).isInvalid()) {
                  ((TileEntityChest)tileentity1).setLootTable(FIREDRAGON_CHEST, (new EaglercraftRandom()).nextLong());
               }
            }
         }

      }
   }

   public static void setOres(World world, BlockPos pos) {
      float hardness = world.getBlockState(pos).getBlock().getBlockHardness(world.getBlockState(pos), world, pos);
      if (hardness != -1.0F && !world.isAirBlock(pos)) {
         boolean vien_chance = (new EaglercraftRandom()).nextInt(IceAndFire.CONFIG.oreToStoneRatioForDragonCaves + 1) == 0;
         if (vien_chance) {
            int chance = (new EaglercraftRandom()).nextInt(199) + 1;
            if (chance < 30) {
               world.setBlockState(pos, Blocks.IRON_ORE.getDefaultState(), 3);
            }

            if (chance > 30 && chance < 40) {
               world.setBlockState(pos, Blocks.GOLD_ORE.getDefaultState(), 3);
            }

            if (chance > 40 && chance < 50) {
               world.setBlockState(pos, IceAndFire.CONFIG.generateSilverOre ? ModBlocks.silverOre.getDefaultState() : ModBlocks.charedStone.getDefaultState(), 3);
            }

            if (chance > 50 && chance < 60) {
               world.setBlockState(pos, Blocks.COAL_ORE.getDefaultState(), 3);
            }

            if (chance > 60 && chance < 70) {
               world.setBlockState(pos, Blocks.REDSTONE_ORE.getDefaultState(), 3);
            }

            if (chance > 70 && chance < 80) {
               world.setBlockState(pos, Blocks.LAPIS_ORE.getDefaultState(), 3);
            }

            if (chance > 80 && chance < 90) {
               world.setBlockState(pos, Blocks.DIAMOND_ORE.getDefaultState(), 3);
            }

            if (chance > 90 && chance < 1000) {
               world.setBlockState(pos, Blocks.EMERALD_ORE.getDefaultState(), 3);
            }
         } else {
            int chance = (new EaglercraftRandom()).nextInt(2);
            if (chance == 0) {
               world.setBlockState(pos, ModBlocks.charedStone.getDefaultState(), 3);
            } else {
               world.setBlockState(pos, ModBlocks.charedCobblestone.getDefaultState(), 3);
            }
         }

      }
   }

   public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos position) {
      int dragonAge = 75 + rand.nextInt(50);
      int i1 = dragonAge / 4;
      int i2 = i1 - 2;
      int ySize = rand.nextInt(2);

      for(int i = 0; i1 >= 0 && i < 3; ++i) {
         int j = i1 + rand.nextInt(2);
         int k = i1 / 2 + ySize;
         int l = i1 + rand.nextInt(2);
         float f = (float)(j + k + l) * 0.333F + 0.5F;

         for(BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l))) {
            if (blockpos.distanceSq(position) <= (double)(f * f) && !(worldIn.getBlockState(position).getBlock() instanceof BlockChest) && worldIn.getBlockState(position).getBlock().getBlockHardness(worldIn.getBlockState(position), worldIn, position) >= 0.0F) {
               worldIn.setBlockState(blockpos, Blocks.STONE.getDefaultState(), 3);
            }
         }
      }

      for(int i = 0; i2 >= 0 && i < 3; ++i) {
         int j = i2 + rand.nextInt(2);
         int k = i2 / 2 + ySize;
         int l = i2 + rand.nextInt(2);
         float f = (float)(j + k + l) * 0.333F + 0.5F;

         for(BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l))) {
            if (blockpos.distanceSq(position) <= (double)(f * f) && !(worldIn.getBlockState(position).getBlock() instanceof BlockChest)) {
               worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 3);
            }
         }
      }

      for(int i = 0; i2 >= 0 && i < 3; ++i) {
         int j = i2 + rand.nextInt(2);
         int k = i2 + rand.nextInt(2);
         int l = i2 + rand.nextInt(2);
         float f = (float)(j + k + l) * 0.333F + 0.5F;

         for(BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l))) {
            float hardness = worldIn.getBlockState(position).getBlock().getBlockHardness(worldIn.getBlockState(position), worldIn, position);
            if (blockpos.distanceSq(position) <= (double)(f * f) && hardness >= 0.0F && hardness != -1.0F) {
               setOres(worldIn, blockpos);
            }
         }
      }

      for(int i = 0; i2 >= 0 && i < 3; ++i) {
         int j = i2 + rand.nextInt(2);
         int k = (i2 + rand.nextInt(2)) / 2;
         int l = i2 + rand.nextInt(2);
         float f = (float)(j + k + l) * 0.333F + 0.5F;

         for(BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l))) {
            if (blockpos.distanceSq(position) <= (double)(f * f) && worldIn.getBlockState(blockpos.down()).getMaterial() == Material.ROCK && worldIn.getBlockState(blockpos).getMaterial() != Material.ROCK) {
               setGoldPile(worldIn, blockpos);
            }
         }
      }

      EntityFireDragon dragon = new EntityFireDragon(worldIn);
      dragon.setGender(dragon.getRNG().nextBoolean());
      dragon.growDragon(dragonAge);
      dragon.setAgingDisabled(true);
      dragon.setHealth(dragon.getMaxHealth());
      dragon.setVariant((new EaglercraftRandom()).nextInt(4));
      dragon.setPositionAndRotation((double)position.getX() + 0.5D, (double)position.getY() + 0.5D, (double)position.getZ() + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
      dragon.setSleeping(true);
      dragon.homePos = position;
      dragon.setHunger(50);
      worldIn.spawnEntity(dragon);
      return true;
   }
}
