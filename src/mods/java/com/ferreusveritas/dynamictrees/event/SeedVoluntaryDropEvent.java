package com.ferreusveritas.dynamictrees.event;

import com.ferreusveritas.dynamictrees.trees.Species;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class SeedVoluntaryDropEvent extends Event {
   private final World world;
   private final BlockPos rootPos;
   private final Species species;
   private final List<ItemStack> dropList;

   public SeedVoluntaryDropEvent(World world, BlockPos rootPos, Species species, List<ItemStack> dropList) {
      this.world = world;
      this.rootPos = rootPos;
      this.species = species;
      this.dropList = dropList;
   }

   public World getWorld() {
      return this.world;
   }

   public BlockPos getRootPos() {
      return this.rootPos;
   }

   public Species getSpecies() {
      return this.species;
   }

   public List<ItemStack> getDropList() {
      return this.dropList;
   }
}
