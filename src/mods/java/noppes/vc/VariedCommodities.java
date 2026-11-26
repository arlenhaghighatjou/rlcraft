package noppes.vc;

import net.minecraft.world.World;
import java.util.function.Function;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
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
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import noppes.vc.client.VCSounds;
import noppes.vc.config.ConfigLoader;
import noppes.vc.config.ConfigProp;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "variedcommodities",
   version = "1.12.2",
   name = "VariedCommodities",
   acceptedMinecraftVersions = "1.12, 1.12.1, 1.12.2"
)
public class VariedCommodities {
   public static final String MODID = "variedcommodities";
   public static final String VERSION = "1.12.2";
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

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new noppes.vc.client.ClientProxy() : new noppes.vc.CommonProxy();
         noppes.vc.VCArmors.registerHolders();
         noppes.vc.VCBlocks.registerHolders();
         noppes.vc.VCItems.registerHolders();
         noppes.vc.VCWeapons.registerHolders();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.init((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.init((FMLPostInitializationEvent)event);
      }

   }

   public void init(FMLPreInitializationEvent event) {
      Instance = this;
      Config = new ConfigLoader(event.getModConfigurationDirectory(), "VariedCommodities",
         new ConfigLoader.Prop("DisableRecipes", "Disables all Varied Commodities recipes", boolean.class, () -> DisableRecipes, v -> DisableRecipes = (Boolean)v),
         new ConfigLoader.Prop("DisableEnchantments", "Disables all Varied Commodities enchantments", boolean.class, () -> DisableEnchantments, v -> DisableEnchantments = (Boolean)v),
         new ConfigLoader.Prop("MachineGunDamage", "Damage from Machine Gun", int.class, () -> MachineGunDamage, v -> MachineGunDamage = (Integer)v),
         new ConfigLoader.Prop("MusketDamage", "Damage from Musket", int.class, () -> MusketDamage, v -> MusketDamage = (Integer)v),
         new ConfigLoader.Prop("MusketKnockback", "Bonus knockback from Musket", int.class, () -> MusketKnockback, v -> MusketKnockback = (Integer)v),
         new ConfigLoader.Prop("HolyHandGrenadeStrength", "Holy Hand Grenade Explosion Strength", float.class, () -> HolyHandGrenadeStrength, v -> HolyHandGrenadeStrength = (Float)v));
      Config.loadConfig();
      Channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("variedcommodities");
      (new VCItems()).register(MinecraftForge.EVENT_BUS);
      (new VCWeapons()).register(MinecraftForge.EVENT_BUS);
      (new VCBlocks()).register(MinecraftForge.EVENT_BUS);
      (new VCArmors()).register(MinecraftForge.EVENT_BUS);
      if (!DisableRecipes) {
         (new VCRecipes()).register(MinecraftForge.EVENT_BUS);
      }

      (new VCSounds()).register(MinecraftForge.EVENT_BUS);
      this.register(MinecraftForge.EVENT_BUS);
      proxy.preinit();
      (new ServerEventsHandler()).register(MinecraftForge.EVENT_BUS);
      NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
   }

   public void init(FMLInitializationEvent event) {
      proxy.init();
      this.registerNewEntity(EntityChairMount.class, "VCChairMount", 64, 10, false, EntityChairMount::new);
      this.registerNewEntity(EntityProjectile.class, "VCProjectile", 64, 3, true, EntityProjectile::new);
      this.registerNewEntity(EntityMagicProjectile.class, "VCMagicProjectile", 64, 3, true, EntityMagicProjectile::new);
      this.registerNewEntity(EntityHolyHandGrenade.class, "VCHolyHandGrenade", 64, 1, true, EntityHolyHandGrenade::new);
   }

   private <T extends Entity> void registerNewEntity(Class<T> cl, String name, int range, int update, boolean velocity, Function<World, T> factory) {
      EntityRegistry.registerModEntity(new ResourceLocation("variedcommodities", name), cl, name, NewEntityStartId++, this, range, update, velocity, factory);
   }

   public void init(FMLPostInitializationEvent event) {
      proxy.postinit();
   }

   public void register(EventBus bus) {
      bus.registerGeneric(MissingMappings.class, Item.class, this::onMissingMapping);
      bus.registerGeneric(MissingMappings.class, Block.class, this::onMissingBlockMapping);
   }

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
