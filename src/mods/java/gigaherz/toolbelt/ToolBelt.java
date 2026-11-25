package gigaherz.toolbelt;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import gigaherz.toolbelt.belt.ItemToolBelt;
import gigaherz.toolbelt.common.GuiHandler;
import gigaherz.toolbelt.customslots.CapabilityExtensionSlotItem;
import gigaherz.toolbelt.network.BeltContentsChange;
import gigaherz.toolbelt.network.OpenBeltSlotInventory;
import gigaherz.toolbelt.network.SwapItems;
import gigaherz.toolbelt.network.SyncBeltSlotContents;
import gigaherz.toolbelt.shadow.commons.ItemRegistered;
import gigaherz.toolbelt.slot.ExtensionSlotBelt;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "toolbelt",
   version = "1.9.14",
   acceptedMinecraftVersions = "[1.12.0,1.13.0)",
   dependencies = "after:baubles;after:jei@[4.6,)",
   guiFactory = "gigaherz.toolbelt.client.config.ConfigGuiFactory"
)
public class ToolBelt {
   public static final String MODID = "toolbelt";
   public static final String VERSION = "1.9.14";
   public static final String CHANNEL = "toolbelt";
   public static ItemToolBelt belt;
   public static Item pouch;
   public static ToolBelt instance;
   public static ISideProxy proxy;
   public static Logger logger;
   public static GuiHandler guiHandler;
   public static SimpleNetworkWrapper channel;

   public ToolBelt() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new gigaherz.toolbelt.client.ClientProxy() : new gigaherz.toolbelt.server.ServerProxy();
         gigaherz.toolbelt.belt.ItemToolBelt.registerCapabilities();
         gigaherz.toolbelt.customslots.CapabilityExtensionSlotItem.registerCapabilities();
         gigaherz.toolbelt.customslots.example.RpgEquipment.registerCapabilities();
         gigaherz.toolbelt.slot.ExtensionSlotBelt.registerCapabilities();
         gigaherz.toolbelt.BeltFinderBaubles.registerCapabilities();
         gigaherz.toolbelt.BeltFinderBeltSlot.registerCapabilities();
         gigaherz.toolbelt.Config.register(MinecraftForge.EVENT_BUS);
         gigaherz.toolbelt.ToolBelt.register(MinecraftForge.EVENT_BUS);
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            gigaherz.toolbelt.client.ClientProxy.register(MinecraftForge.EVENT_BUS);
         }
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            gigaherz.toolbelt.client.GuiRadialMenu.register(MinecraftForge.EVENT_BUS);
         }
         gigaherz.toolbelt.common.BeltEvents.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Block.class, ToolBelt::registerBlocks);
      bus.registerGeneric(Register.class, Item.class, ToolBelt::registerItems);
      bus.register(AnvilUpdateEvent.class, ToolBelt::anvilChange);
   }

   public static void registerBlocks(Register<Block> event) {
   }

   public static void registerItems(Register<Item> event) {
      event.getRegistry().registerAll(new Item[]{belt = new ItemToolBelt("belt"), pouch = (new ItemRegistered("pouch")).setCreativeTab(CreativeTabs.TOOLS)});
   }

   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
      channel = NetworkRegistry.INSTANCE.newSimpleChannel("toolbelt");
      int messageNumber = 0;
      channel.registerMessage(new SwapItems.Handler(), SwapItems.class, SwapItems::new, messageNumber++, Side.SERVER);
      channel.registerMessage(new BeltContentsChange.Handler(), BeltContentsChange.class, BeltContentsChange::new, messageNumber++, Side.CLIENT);
      channel.registerMessage(new OpenBeltSlotInventory.Handler(), OpenBeltSlotInventory.class, OpenBeltSlotInventory::new, messageNumber++, Side.SERVER);
      channel.registerMessage(new SyncBeltSlotContents.Handler(), SyncBeltSlotContents.class, SyncBeltSlotContents::new, messageNumber++, Side.CLIENT);
      logger.debug("Final message number: " + messageNumber);
      VFile2 configurationFile = event.getSuggestedConfigurationFile();
      Config.loadConfig(configurationFile);
      CapabilityExtensionSlotItem.register();
      ExtensionSlotBelt.register();
   }

   public void init(FMLInitializationEvent event) {
      proxy.init();
      NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler = new GuiHandler());
   }

   public void postInit(FMLPostInitializationEvent event) {
      Config.postInit();
   }

   public static void anvilChange(AnvilUpdateEvent ev) {
      if (!Config.disableAnvilUpgrading) {
         ItemStack left = ev.getLeft();
         ItemStack right = ev.getRight();
         if (left.getCount() > 0 && left.getItem() == belt) {
            if (right.getCount() > 0 && right.getItem() == pouch) {
               int cost = ItemToolBelt.getUpgradeXP(left);
               if (cost < 0) {
                  ev.setCanceled(true);
               } else {
                  ev.setCost(cost);
                  ev.setMaterialCost(1);
                  ev.setOutput(ItemToolBelt.upgrade(left));
               }
            }
         }
      }
   }

   public static ResourceLocation location(String path) {
      return new ResourceLocation("toolbelt", path);
   }
}
