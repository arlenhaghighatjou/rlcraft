package com.ferreusveritas.dynamictrees.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicSapling;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.ItemUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class VanillaSaplingEventHandler {
   public VanillaSaplingEventHandler() {
   }

   public void register(EventBus bus) {
      bus.register(PlaceEvent.class, this::onPlayerPlaceBlock);
      bus.register(SaplingGrowTreeEvent.class, this::onSaplingGrowTree);
   }

   public void onPlayerPlaceBlock(PlaceEvent event) {
      IBlockState blockState = event.getPlacedBlock();
      if (TreeRegistry.saplingReplacers.containsKey(blockState)) {
         Species species = (Species)TreeRegistry.saplingReplacers.get(blockState);
         World world = event.getWorld();
         BlockPos pos = event.getPos();
         world.setBlockToAir(pos);
         if (!species.plantSapling(world, pos)) {
            ItemUtils.spawnItemStack(world, pos, species.getSeedStack(1));
         }

      }
   }

   public void onSaplingGrowTree(SaplingGrowTreeEvent event) {
      World world = event.getWorld();
      BlockPos pos = event.getPos();
      IBlockState blockState = world.getBlockState(pos);
      if (TreeRegistry.saplingReplacers.containsKey(blockState)) {
         Species species = (Species)TreeRegistry.saplingReplacers.get(blockState);
         world.setBlockToAir(pos);
         event.setResult(Result.DENY);
         if (species.isValid() && BlockDynamicSapling.canSaplingStay(world, species, pos)) {
            species.transitionToTree(world, pos);
         }

      }
   }
}
