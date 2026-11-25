package noppes.vc;

import com.google.common.collect.UnmodifiableIterator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import noppes.vc.client.VCSounds;
import noppes.vc.config.ConfigLoader;
import noppes.vc.config.ConfigProp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "variedcommodities",
   version = "1.12.2",
   name = "VariedCommodities",
   acceptedMinecraftVersions = "1.12, 1.12.1, 1.12.2"
)
public class VariedCommodities {
   public static final String MODID = "variedcommodities";
   public static final String VERSION = "1.12.2";
   @SidedProxy(
      clientSide = "noppes.vc.client.ClientProxy",
      serverSide = "noppes.vc.CommonProxy"
   )
   public static CommonProxy proxy;
   @ConfigProp(
      info = "Disables all Varied Commodities recipes"
   )
   public static boolean DisableRecipes = false;
   @ConfigProp(
      info = "Disables all Varied Commodities enchantments"
   )
   public static boolean DisableEnchantments = false;
   @ConfigProp(
      info = "Damage from Machine Gun"
   )
   public static int MachineGunDamage = 4;
   @ConfigProp(
      info = "Damage from Musket"
   )
   public static int MusketDamage = 16;
   @ConfigProp(
      info = "Bonus knockback from Musket"
   )
   public static int MusketKnockback = 0;
   @ConfigProp(
      info = "Holy Hand Grenade Explosion Strength"
   )
   public static float HolyHandGrenadeStrength = 10.0F;
   public static FMLEventChannel Channel;
   public static VariedCommodities Instance;
   private static int NewEntityStartId = 0;
   public static Logger Log = LogManager.getLogger();
   public static ConfigLoader Config;

   public VariedCommodities() {
   }

   @EventHandler
   public void init(FMLPreInitializationEvent event) {
      Instance = this;
      Config = new ConfigLoader(this.getClass(), event.getModConfigurationDirectory(), "VariedCommodities");
      Config.loadConfig();
      Channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("variedcommodities");
      MinecraftForge.EVENT_BUS.register(new VCItems());
      MinecraftForge.EVENT_BUS.register(new VCWeapons());
      MinecraftForge.EVENT_BUS.register(new VCBlocks());
      MinecraftForge.EVENT_BUS.register(new VCArmors());
      if (!DisableRecipes) {
         MinecraftForge.EVENT_BUS.register(new VCRecipes());
      }

      MinecraftForge.EVENT_BUS.register(new VCSounds());
      MinecraftForge.EVENT_BUS.register(this);
      proxy.preinit();
      MinecraftForge.EVENT_BUS.register(new ServerEventsHandler());
      NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init();
      this.registerNewEntity(EntityChairMount.class, "VCChairMount", 64, 10, false);
      this.registerNewEntity(EntityProjectile.class, "VCProjectile", 64, 3, true);
      this.registerNewEntity(EntityMagicProjectile.class, "VCMagicProjectile", 64, 3, true);
      this.registerNewEntity(EntityHolyHandGrenade.class, "VCHolyHandGrenade", 64, 1, true);
   }

   private void registerNewEntity(Class<? extends Entity> cl, String name, int range, int update, boolean velocity) {
      EntityRegistry.registerModEntity(new ResourceLocation("variedcommodities", name), cl, name, NewEntityStartId++, this, range, update, velocity);
   }

   @EventHandler
   public void init(FMLPostInitializationEvent event) {
      proxy.postinit();
   }

   @SubscribeEvent
   public void onMissingMapping(MissingMappings<Item> event) {
      HashMap<String, ResourceLocation> maps = new HashMap();
      this.dealMissingMapping(maps);
      UnmodifiableIterator var3 = event.getAllMappings().iterator();

      while(var3.hasNext()) {
         Mapping<Item> mapping = (Mapping)var3.next();
         String resourcePath = mapping.key.toString().toLowerCase();
         if (maps.containsKey(resourcePath)) {
            Item item = (Item)Item.REGISTRY.getObject(maps.get(resourcePath));
            if (item == null) {
               System.out.println(maps.get(resourcePath));
            }

            mapping.remap(item);
         }
      }

   }

   @SubscribeEvent
   public void onMissingBlockMapping(MissingMappings<Block> event) {
      HashMap<String, ResourceLocation> maps = new HashMap();
      this.dealMissingMapping(maps);
      UnmodifiableIterator var3 = event.getAllMappings().iterator();

      while(var3.hasNext()) {
         Mapping<Block> mapping = (Mapping)var3.next();
         String resourcePath = mapping.key.toString().toLowerCase();
         if (maps.containsKey(resourcePath)) {
            Block b = (Block)Block.REGISTRY.getObject(maps.get(resourcePath));
            mapping.remap(b);
         }
      }

   }

   private void dealMissingMapping(HashMap<String, ResourceLocation> maps) {
      try {
         BufferedReader br = new BufferedReader(new InputStreamReader(MinecraftServer.class.getResourceAsStream("/assets/variedcommodities/mappingfix.cfg")));

         String line;
         while((line = br.readLine()) != null) {
            String[] split = line.toLowerCase().split("=");
            if (split.length == 2) {
               maps.put(split[0], new ResourceLocation(split[1]));
            }
         }

         br.close();
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }
}
