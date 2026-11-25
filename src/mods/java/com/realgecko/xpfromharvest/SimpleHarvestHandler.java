package com.realgecko.xpfromharvest;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SimpleHarvestHandler {
   public SimpleHarvestHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void handleRightClick(RightClickBlock event) {
      if (event.getWorld() instanceof WorldServer) {
         World world = event.getWorld();
         BlockPos pos = event.getPos();
         IBlockState state = world.getBlockState(pos);
         Block block = state.getBlock();
         if (Arrays.asList(ModConfig.crops).contains(state.toString())) {
            this.handleHarvest(block, world, pos, state, world.rand);
         }

      }
   }

   void handleHarvest(Block block, World world, BlockPos pos, IBlockState state, Random rand) {
      NonNullList<ItemStack> drops = NonNullList.create();
      block.getDrops(drops, world, pos, state, 0);
      boolean foundSeed = false;

      for(ItemStack stack : drops) {
         if (stack.getItem() instanceof IPlantable && !foundSeed) {
            foundSeed = true;
         } else {
            EntityItem entityItem = new EntityItem(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), stack);
            world.spawnEntity(entityItem);
         }
      }

      if (rand.nextInt(100) + 1 <= ModConfig.chance) {
         EntityXPOrb xpOrb = new EntityXPOrb(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), ModConfig.xpAmount);
         world.spawnEntity(xpOrb);
      }

      state = block.getDefaultState();
      world.setBlockState(pos, state);
   }
}
