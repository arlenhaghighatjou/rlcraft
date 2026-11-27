package com.charles445.simpledifficulty.register;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.block.BlockCampfire;
import com.charles445.simpledifficulty.block.BlockRainCollector;
import com.charles445.simpledifficulty.block.BlockSpit;
import com.charles445.simpledifficulty.block.BlockTemperature;
import com.charles445.simpledifficulty.tileentity.TileEntitySpit;
import com.charles445.simpledifficulty.tileentity.TileEntityTemperature;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class RegisterBlocks {
   public RegisterBlocks() {
   }

   public static class Registrar {
      public Registrar() {
      }

      public static void register(EventBus bus) {
         bus.registerGeneric(Register.class, Block.class, Registrar::registerBlocks);
         bus.registerGeneric(Register.class, Item.class, Registrar::registerItemBlocks);
      }

      public static void registerBlocks(Register<Block> event) {
         IForgeRegistry<Block> registry = event.getRegistry();
         SDBlocks.campfire = registerAs("campfire", new BlockCampfire(), registry);
         SDBlocks.rainCollector = registerAs("rain_collector", new BlockRainCollector(), registry);
         SDBlocks.heater = registerAs("heater", new BlockTemperature(1.0F), registry);
         SDBlocks.chiller = registerAs("chiller", new BlockTemperature(-1.0F), registry);
         SDBlocks.spit = registerAs("spit", new BlockSpit(), registry);
         GameRegistry.registerTileEntity(TileEntitySpit.class, new ResourceLocation("simpledifficulty", "campfirespit"), TileEntitySpit::new);
         GameRegistry.registerTileEntity(TileEntityTemperature.class, new ResourceLocation("simpledifficulty", "temperatureChanged"), TileEntityTemperature::new);
      }

      public static void registerItemBlocks(Register<Item> event) {
         IForgeRegistry<Item> registry = event.getRegistry();
         registerItemBlock(SDBlocks.campfire, registry);
         registerItemBlock(SDBlocks.rainCollector, registry);
         registerItemBlock(SDBlocks.heater, registry);
         registerItemBlock(SDBlocks.chiller, registry);
         registerItemBlock(SDBlocks.spit, registry);
      }

      private static void registerItemBlock(Block block, IForgeRegistry<Item> registry) {
         registry.register((new ItemBlock(block)).setRegistryName(block.getRegistryName()));
      }

      private static Block registerAs(String name, Block newBlock, IForgeRegistry<Block> registry) {
         newBlock.setRegistryName("simpledifficulty", name);
         newBlock.setUnlocalizedName(newBlock.getRegistryName().toString());
         newBlock.setCreativeTab(ModCreativeTab.instance);
         registry.register(newBlock);
         SDBlocks.blocks.put(name, newBlock);
         return newBlock;
      }
   }
}
