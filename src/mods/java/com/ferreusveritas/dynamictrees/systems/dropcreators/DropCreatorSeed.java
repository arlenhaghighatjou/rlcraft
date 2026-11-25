package com.ferreusveritas.dynamictrees.systems.dropcreators;

import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.treedata.IDropCreator;
import com.ferreusveritas.dynamictrees.event.SeedVoluntaryDropEvent;
import com.ferreusveritas.dynamictrees.trees.Species;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class DropCreatorSeed implements IDropCreator {
   protected final float rarity;

   public DropCreatorSeed() {
      this(1.0F);
   }

   public DropCreatorSeed(float rarity) {
      this.rarity = rarity;
   }

   protected float getHarvestRarity() {
      return this.rarity;
   }

   protected float getVoluntaryRarity() {
      return this.rarity;
   }

   protected float getLeavesRarity() {
      return this.rarity;
   }

   public ResourceLocation getName() {
      return new ResourceLocation("dynamictrees", "seed");
   }

   public List<ItemStack> getHarvestDrop(World world, Species species, BlockPos leafPos, EaglercraftRandom random, List<ItemStack> dropList, int soilLife, int fortune) {
      float rarity = this.getHarvestRarity();
      rarity = rarity * ((float)(fortune + 1) / 64.0F);
      rarity = (float)((double)rarity * Math.min((double)(species.seasonalSeedDropFactor(world, leafPos) + 0.15F), 1.0D));
      if (rarity > random.nextFloat()) {
         dropList.add(species.getSeedStack(1));
      }

      return dropList;
   }

   public List<ItemStack> getVoluntaryDrop(World world, Species species, BlockPos rootPos, EaglercraftRandom random, List<ItemStack> dropList, int soilLife) {
      if (this.getVoluntaryRarity() * ModConfigs.seedDropRate * species.seasonalSeedDropFactor(world, rootPos) > random.nextFloat()) {
         dropList.add(species.getSeedStack(1));
         SeedVoluntaryDropEvent seedDropEvent = new SeedVoluntaryDropEvent(world, rootPos, species, dropList);
         MinecraftForge.EVENT_BUS.post(seedDropEvent);
         if (seedDropEvent.isCanceled()) {
            dropList.clear();
         }
      }

      return dropList;
   }

   public List<ItemStack> getLeavesDrop(IBlockAccess access, Species species, BlockPos breakPos, EaglercraftRandom random, List<ItemStack> dropList, int fortune) {
      int chance = 20;
      if (fortune > 0) {
         chance -= 2 << fortune;
         if (chance < 10) {
            chance = 10;
         }
      }

      float seasonFactor = 1.0F;
      if (access instanceof World) {
         World world = (World)access;
         if (!world.isRemote) {
            seasonFactor = species.seasonalSeedDropFactor(world, breakPos);
         }
      }

      if (random.nextInt((int)((float)chance / this.getLeavesRarity())) == 0 && seasonFactor > random.nextFloat()) {
         dropList.add(species.getSeedStack(1));
      }

      return dropList;
   }

   public List<ItemStack> getLogsDrop(World world, Species species, BlockPos breakPos, EaglercraftRandom random, List<ItemStack> dropList, float volume) {
      return dropList;
   }
}
