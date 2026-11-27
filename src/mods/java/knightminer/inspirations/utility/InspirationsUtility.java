package knightminer.inspirations.utility;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.utility.block.BlockBricksButton;
import knightminer.inspirations.utility.block.BlockCarpetedPressurePlate;
import knightminer.inspirations.utility.block.BlockCarpetedTrapdoor;
import knightminer.inspirations.utility.block.BlockCollector;
import knightminer.inspirations.utility.block.BlockPipe;
import knightminer.inspirations.utility.block.BlockRedstoneBarrel;
import knightminer.inspirations.utility.block.BlockRedstoneTorchLever;
import knightminer.inspirations.utility.block.BlockTorchLever;
import knightminer.inspirations.utility.dispenser.DispenseFluidTank;
import knightminer.inspirations.utility.tileentity.TileCollector;
import knightminer.inspirations.utility.tileentity.TilePipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class InspirationsUtility extends PulseBase {
   public void handlePulseEvent(Object evt) {
      if (evt instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)evt);
      }

      if (evt instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)evt);
      }

      if (evt instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)evt);
      }

   }

   public static final String pulseID = "InspirationsUtility";
   public static CommonProxy proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new knightminer.inspirations.utility.UtilityClientProxy() : new knightminer.inspirations.common.CommonProxy();
   public static Block torchLever;
   public static Block redstoneBarrel;
   public static BlockBricksButton bricksButton;
   public static Block redstoneTorchLever;
   public static Block redstoneTorchLeverPowered;
   public static Block[] carpetedTrapdoors;
   public static Block carpetedPressurePlate1;
   public static Block carpetedPressurePlate2;
   public static Block collector;
   public static Block pipe;
   public static Item pipeItem;

   public InspirationsUtility() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit();
   }

   public void register(EventBus bus) {
      bus.registerGeneric(Register.class, Block.class, this::registerBlocks);
      bus.registerGeneric(Register.class, Item.class, this::registerItems);
   }

   public void registerBlocks(Register<Block> event) {
      IForgeRegistry<Block> r = event.getRegistry();
      if (Config.enableTorchLever) {
         torchLever = registerBlock(r, new BlockTorchLever(), "torch_lever");
      }

      if (Config.enableBricksButton) {
         bricksButton = (BlockBricksButton)registerBlock(r, new BlockBricksButton(), "bricks_button");
      }

      if (Config.enableRedstoneBarrel) {
         redstoneBarrel = registerBlock(r, new BlockRedstoneBarrel(), "redstone_barrel");
      }

      if (Config.enableRedstoneTorchLever) {
         redstoneTorchLever = registerBlock(r, new BlockRedstoneTorchLever(false), "redstone_torch_lever");
         redstoneTorchLeverPowered = registerBlock(r, new BlockRedstoneTorchLever(true), "redstone_torch_lever_powered");
      }

      if (Config.enableCarpetedTrapdoor) {
         carpetedTrapdoors = new Block[16];

         for(EnumDyeColor color : EnumDyeColor.values()) {
            carpetedTrapdoors[color.getMetadata()] = registerBlock(r, new BlockCarpetedTrapdoor(), "carpeted_trapdoor_" + color.getName());
         }
      }

      if (Config.enableCarpetedPressurePlate) {
         carpetedPressurePlate1 = registerBlock(r, new BlockCarpetedPressurePlate(false), "carpeted_pressure_plate_1");
         carpetedPressurePlate2 = registerBlock(r, new BlockCarpetedPressurePlate.BlockCarpetedPressurePlate2(), "carpeted_pressure_plate_2");
      }

      if (Config.enableCollector) {
         collector = registerBlock(r, new BlockCollector(), "collector");
         registerTE(TileCollector.class, "collector", TileCollector::new);
      }

      if (Config.enablePipe) {
         pipe = registerBlock(r, new BlockPipe(), "pipe");
         registerTE(TilePipe.class, "pipe", TilePipe::new);
      }

   }

   public void registerItems(Register<Item> event) {
      IForgeRegistry<Item> r = event.getRegistry();
      if (torchLever != null) {
         registerItemBlock(r, torchLever);
      }

      if (bricksButton != null) {
         registerEnumItemBlock(r, bricksButton);
      }

      if (redstoneBarrel != null) {
         registerItemBlock(r, redstoneBarrel);
      }

      if (redstoneTorchLever != null) {
         registerItemBlock(r, redstoneTorchLever);
      }

      if (carpetedTrapdoors != null) {
         for(Block trapdoor : carpetedTrapdoors) {
            registerItemBlock(r, trapdoor);
         }
      }

      if (collector != null) {
         registerItemBlock(r, collector);
      }

      if (pipe != null) {
         pipeItem = registerItemBlock(r, pipe);
      }

   }

   public void init(FMLInitializationEvent event) {
      proxy.init();
      this.registerDispenserBehavior();
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit();
      UtilityEvents.register(MinecraftForge.EVENT_BUS);
   }

   private void registerDispenserBehavior() {
      if (Config.enableDispenserFluidTanks) {
         for(String container : Config.fluidContainers) {
            Item item = (Item)GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(container));
            if (item != null && item != Items.AIR) {
               registerDispenseTankLogic(item);
            }
         }
      }

   }

   private static void registerDispenseTankLogic(Item item) {
      registerDispenserBehavior(item, new DispenseFluidTank((IBehaviorDispenseItem)BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(item)));
   }
}
