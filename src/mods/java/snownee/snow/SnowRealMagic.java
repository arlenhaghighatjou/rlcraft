package snownee.snow;

import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "snowrealmagic",
   name = "Snow! Real Magic!",
   version = "0.7.1",
   acceptedMinecraftVersions = "[1.12, 1.13)"
)
public class SnowRealMagic {
   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         snownee.snow.ModConfig.register();
         snownee.snow.ModConfig.register(MinecraftForge.EVENT_BUS);
         snownee.snow.SnowRealMagic.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public static final String MODID = "snowrealmagic";
   public static final String NAME = "Snow! Real Magic!";
   public static Logger logger;
   public static Block BLOCK = Blocks.SNOW_LAYER;

   public SnowRealMagic() {
      SnowGenerator.register(MinecraftForge.TERRAIN_GEN_BUS);

   }

   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
   }

   public void init(FMLInitializationEvent event) {
      GameRegistry.registerWorldGenerator(new SnowGenerator(), ModConfig.snowWorldGenPriority);
   }

   public void postInit(FMLPostInitializationEvent event) {
      ModConfig.postInit();
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Block.class, SnowRealMagic::onBlockRegister);
      bus.registerGeneric(Register.class, EntityEntry.class, SnowRealMagic::onEntityRegister);
      bus.register(ModelRegistryEvent.class, SnowRealMagic::onModelRegister);
   }

   public static void onBlockRegister(Register<Block> event) {
      BLOCK = new ModSnowBlock();
      event.getRegistry().register(BLOCK);
      Item item = Item.getItemFromBlock(BLOCK);
      if (item instanceof ItemBlock) {
         ((ItemBlock)item).setBlock(BLOCK);
      }

      if (ModConfig.placeSnowInBlock) {
         GameRegistry.registerTileEntity(SnowTile.class, new ResourceLocation("snowrealmagic", "snow"), SnowTile::new);
      }

   }

   public static void onEntityRegister(Register<EntityEntry> event) {
      event.getRegistry().register(EntityEntryBuilder.<FallingSnowEntity>create().entity(FallingSnowEntity.class).factory(FallingSnowEntity::new).id(new ResourceLocation("snowrealmagic", "snow"), 0).name("snowrealmagic.snow").tracker(160, 20, true).build());
   }

   @SideOnly(Side.CLIENT)
   public static void onModelRegister(ModelRegistryEvent event) {
      RenderingRegistry.registerEntityRenderingHandler(FallingSnowEntity.class, RenderFallingSnow::new);
      if (ModConfig.placeSnowInBlock) {
         ModelLoader.setCustomStateMapper(BLOCK, (new Builder()).ignore(new IProperty[]{ModSnowBlock.TILE}).build());
      }

   }
}
