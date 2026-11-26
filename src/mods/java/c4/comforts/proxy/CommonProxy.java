package c4.comforts.proxy;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import c4.comforts.common.EventHandlerCommon;
import c4.comforts.common.blocks.BlockHammock;
import c4.comforts.common.blocks.BlockRope;
import c4.comforts.common.blocks.BlockSleepingBag;
import c4.comforts.common.blocks.ComfortsBlocks;
import c4.comforts.common.capability.CapabilitySleepTime;
import c4.comforts.common.capability.CapabilitySleeping;
import c4.comforts.common.capability.CapabilityWellRested;
import c4.comforts.common.entities.EntityRest;
import c4.comforts.common.items.ItemHammock;
import c4.comforts.common.items.ItemSleepingBag;
import c4.comforts.common.tileentities.TileEntityHammock;
import c4.comforts.common.util.ComfortsUtil;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
   public static Configuration config;

   public CommonProxy() {
   }

   public void preInit(FMLPreInitializationEvent evt) {
      ComfortsBlocks.preInit();
      ComfortsUtil.parseDebuffs();
   }

   public void init(FMLInitializationEvent evt) {
      (new EventHandlerCommon()).register(MinecraftForge.EVENT_BUS);
      CapabilitySleepTime.register();
      CapabilityWellRested.register();
      CapabilitySleeping.register();
   }

   public void postInit(FMLPostInitializationEvent evt) {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Block.class, CommonProxy::registerBlocks);
      bus.registerGeneric(Register.class, Item.class, CommonProxy::registerItems);
      bus.registerGeneric(Register.class, EntityEntry.class, CommonProxy::registerEntities);
   }

   public static void registerBlocks(Register<Block> evt) {
      evt.getRegistry().register(new BlockRope());

      for(BlockSleepingBag sleepingBag : ComfortsBlocks.SLEEPING_BAGS) {
         evt.getRegistry().register(sleepingBag);
      }

      for(BlockHammock hammock : ComfortsBlocks.HAMMOCKS) {
         evt.getRegistry().register(hammock);
      }

      GameRegistry.registerTileEntity(TileEntityHammock.class, new ResourceLocation("comforts", "tile_entity_hammock"), TileEntityHammock::new);
   }

   public static void registerItems(Register<Item> evt) {
      evt.getRegistry().register(new ItemSleepingBag());
      evt.getRegistry().register(new ItemHammock());
      evt.getRegistry().register((new ItemBlock(ComfortsBlocks.ROPE)).setRegistryName("rope"));
   }

   public static void registerEntities(Register<EntityEntry> evt) {
      int id = 1;
      EntityEntry entry = EntityEntryBuilder.create().entity(EntityRest.class).factory(EntityRest::new).id(new ResourceLocation("comforts", "_entity_rest"), id++).name("entity_rest").tracker(256, 20, false).build();
      evt.getRegistry().register(entry);
   }
}
