package maxhyper.dynamictreesdefiledlands;

import maxhyper.dynamictreesdefiledlands.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
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
   @Instance
   public static DynamicTreesDefiledLands instance;
   @SidedProxy(
      clientSide = "maxhyper.dynamictreesdefiledlands.proxy.ClientProxy",
      serverSide = "maxhyper.dynamictreesdefiledlands.proxy.CommonProxy"
   )
   public static CommonProxy proxy;

   public DynamicTreesDefiledLands() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init();
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
      proxy.postInit();
   }
}
