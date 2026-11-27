package maxhyper.dynamictreesdefiledlands;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import maxhyper.dynamictreesdefiledlands.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   modid = "dynamictreesdefiledlands",
   name = "Dynamic Trees for Defiled Lands",
   dependencies = "required-after:dynamictrees@[1.12.2-0.9.21,);required-after:defiledlands",
   updateJSON = "https://github.com/supermassimo/DynamicTrees-ExC/blob/1.12.2/.DONE/DynamicTrees-DefiledLands/version_info.json?raw=true",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class DynamicTreesDefiledLands {
   public static final String MODID = "dynamictreesdefiledlands";
   public static final String NAME = "Dynamic Trees for Defiled Lands";
   public static final String DEPENDENCIES = "required-after:dynamictrees@[1.12.2-0.9.21,);required-after:defiledlands";
   public static DynamicTreesDefiledLands instance;
   public static CommonProxy proxy;

   public DynamicTreesDefiledLands() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new maxhyper.dynamictreesdefiledlands.proxy.ClientProxy() : new maxhyper.dynamictreesdefiledlands.proxy.CommonProxy();
         maxhyper.dynamictreesdefiledlands.ModContent.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit();
   }

   public void init(FMLInitializationEvent event) {
      proxy.init();
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit();
   }
}
