package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.block.BlockFluidBasic;
import com.charles445.simpledifficulty.fluid.FluidBasic;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RegisterFluids {
   public RegisterFluids() {
   }

   @EventBusSubscriber(
      modid = "simpledifficulty"
   )
   public static class Registrar {
      public Registrar() {
      }

      @SubscribeEvent
      public static void registerFluidsAndFluidBlocks(Register<Block> event) {
         IForgeRegistry<Block> registry = event.getRegistry();
         SDFluids.purifiedWater = new FluidBasic("purifiedwater", "purifiedwater_still", "purifiedwater_flow", -1);
         SDFluids.blockPurifiedWater = new BlockFluidBasic(SDFluids.purifiedWater, Material.WATER);

         for(String key : SDFluids.fluidBlocks.keySet()) {
            registry.register((IForgeRegistryEntry)SDFluids.fluidBlocks.get(key));
         }

      }

      @SubscribeEvent
      public static void registerFluidItems(Register<Item> event) {
         IForgeRegistry<Item> registry = event.getRegistry();

         for(String key : SDFluids.fluidBlocks.keySet()) {
            registry.register(makeItemFromBlock((Block)SDFluids.fluidBlocks.get(key)));
         }

      }

      private static ItemBlock makeItemFromBlock(Block block) {
         ItemBlock itemblock = new ItemBlock(block);
         itemblock.setRegistryName(block.getRegistryName());
         itemblock.setCreativeTab(ModCreativeTab.instance);
         return itemblock;
      }
   }
}
