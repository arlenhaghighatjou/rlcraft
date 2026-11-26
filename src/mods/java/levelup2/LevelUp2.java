package levelup2;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.List;
import levelup2.api.IProcessor;
import levelup2.capability.PlayerCapability;
import levelup2.config.LevelUpConfig;
import levelup2.event.CapabilityEventHandler;
import levelup2.network.SkillPacketHandler;
import levelup2.player.IPlayerClass;
import levelup2.player.PlayerExtension;
import levelup2.proxy.CommonProxy;
import levelup2.skills.SkillRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(
   modid = "levelup2",
   name = "Level Up! Reloaded",
   version = "${version}",
   acceptedMinecraftVersions = "[1.12]"
)
public class LevelUp2 {
   public static final String ID = "levelup2";
   public static LevelUp2 INSTANCE;
   public static CommonProxy proxy;

   public LevelUp2() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         INSTANCE = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new levelup2.proxy.ClientProxy() : new levelup2.proxy.CommonProxy();
         levelup2.capability.PlayerCapability.registerCapabilities();
         levelup2.LevelUp2.RegistryEventHandler.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent evt) {
      LevelUpConfig.init(evt.getSuggestedConfigurationFile());
      CapabilityManager.INSTANCE.register(IPlayerClass.class, new PlayerCapability.CapabilityPlayerClass(), PlayerExtension.class);
      CapabilityManager.INSTANCE.register(IProcessor.class, new PlayerCapability.CapabilityProcessorClass(), PlayerCapability.CapabilityProcessorDefault.class);
      (new CapabilityEventHandler()).register(MinecraftForge.EVENT_BUS);
      proxy.registerItemMeshes();
   }

   public void init(FMLInitializationEvent evt) {
      proxy.registerGui();
      SkillPacketHandler.init();
      SkillRegistry.loadSkills();
      proxy.registerColors();
   }

   public void postInit(FMLPostInitializationEvent evt) {
      LevelUpConfig.getBlacklistOutputs();
      SkillRegistry.postLoadSkills();
      LevelUpConfig.registerSkillProperties();
      SkillRegistry.registerRecipes();
   }

   public static class RegistryEventHandler {
      public RegistryEventHandler() {
      }

      public static void register(EventBus bus) {
         bus.registerGeneric(Register.class, Item.class, RegistryEventHandler::registerItems);
         bus.registerGeneric(Register.class, IRecipe.class, RegistryEventHandler::registerRecipes);
      }

      public static void registerItems(Register<Item> evt) {
         IForgeRegistry<Item> reg = evt.getRegistry();
         registerItem(reg, SkillRegistry.surfaceOreChunk);
         registerItem(reg, SkillRegistry.netherOreChunk);
         registerItem(reg, SkillRegistry.endOreChunk);
         registerItem(reg, SkillRegistry.respecBook);
      }

      public static void registerRecipes(Register<IRecipe> evt) {
         IForgeRegistry<IRecipe> reg = evt.getRegistry();
         reg.register((new ShapelessOreRecipe(new ResourceLocation("levelup2", "reclaim"), new ItemStack(Blocks.GRAVEL, 4), new Object[]{Items.FLINT, Items.FLINT, Items.FLINT, Items.FLINT})).setRegistryName(new ResourceLocation("levelup2", "gravel")));
         oreLoad(reg);
         SkillRegistry.initPlankCache();
      }

      public static void oreLoad(IForgeRegistry<IRecipe> reg) {
         if (!SkillRegistry.isNullList(LevelUpConfig.oreList)) {
            registerOreRecipes(reg, LevelUpConfig.oreList, SkillRegistry.surfaceOreChunk);
         }

         if (!SkillRegistry.isNullList(LevelUpConfig.netherOreList)) {
            registerOreRecipes(reg, LevelUpConfig.netherOreList, SkillRegistry.netherOreChunk);
         }

         if (!SkillRegistry.isNullList(LevelUpConfig.endOreList)) {
            registerOreRecipes(reg, LevelUpConfig.endOreList, SkillRegistry.endOreChunk);
         }

      }

      public static void registerItem(IForgeRegistry<Item> reg, Item item) {
         reg.register(item);
      }

      public static void registerOreRecipes(IForgeRegistry<IRecipe> reg, List<String> ores, Item item) {
         for(int i = 0; i < ores.size(); ++i) {
            String names = (String)ores.get(i);
            if (OreDictionary.doesOreNameExist(names)) {
               ItemStack ore = SkillRegistry.getOreEntry(names);
               ItemStack chunk = new ItemStack(item, 1, i);
               if (!ore.isEmpty()) {
                  reg.register((new ShapelessOreRecipe(new ResourceLocation("levelup2", "orechunk"), ore.copy(), new Object[]{chunk, chunk})).setRegistryName("levelup2", names.toLowerCase()));
               }

               OreDictionary.registerOre(names, chunk);
            }
         }

      }
   }
}
