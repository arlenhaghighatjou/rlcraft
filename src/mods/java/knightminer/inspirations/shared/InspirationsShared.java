package knightminer.inspirations.shared;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.shared.item.ItemMaterials;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.item.ItemEdible;

public class InspirationsShared extends PulseBase {
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

   public static final String pulseID = "InspirationsShared";
   public static CommonProxy proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new knightminer.inspirations.shared.SharedClientProxy() : new knightminer.inspirations.common.CommonProxy();
   public static ItemMaterials materials;
   public static ItemEdible edibles;
   public static ItemStack lock;
   public static ItemStack key;
   public static ItemStack splashBottle;
   public static ItemStack lingeringBottle;
   public static ItemStack mushrooms;
   public static ItemStack rabbitStewMix;
   public static ItemStack silverfishPowder;
   public static ItemStack witherBone;
   public static ItemStack stoneRod;
   public static ItemStack heartbeet;
   public static ItemStack boiledEgg;
   private static boolean witherBoneDrop = false;
   public static boolean milkCooldownCow = false;
   public static boolean milkCooldownSquid = false;
   private static final ResourceLocation WITHER_SKELETON_TABLE = new ResourceLocation("entities/wither_skeleton");

   public InspirationsShared() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit();
   }

   public void register(EventBus bus) {
      bus.registerGeneric(Register.class, Item.class, this::registerItems);
      bus.register(LootTableLoadEvent.class, this::onLootTableLoad);
   }

   public void registerItems(Register<Item> event) {
      IForgeRegistry<Item> r = event.getRegistry();
      materials = (ItemMaterials)registerItem(r, new ItemMaterials(), "materials");
      materials.setCreativeTab(CreativeTabs.MATERIALS);
      edibles = (ItemEdible)registerItem(r, new ItemEdible(), "edibles");
      edibles.setCreativeTab(CreativeTabs.FOOD);
      if (isToolsLoaded() && Config.enableLock) {
         lock = materials.addMeta(0, "lock");
         key = materials.addMeta(1, "key");
      }

      if (isTweaksLoaded()) {
         if (Config.enableHeartbeet) {
            heartbeet = edibles.addFood(0, 2, 2.4F, "heartbeet", new PotionEffect[]{new PotionEffect(MobEffects.REGENERATION, 100)});
         }

         if (Config.brewMissingPotions) {
            silverfishPowder = materials.addMeta(6, "silverfish_powder", CreativeTabs.BREWING);
         }
      }

      if (isTweaksLoaded() && Config.brewMissingPotions || isToolsLoaded() && Config.netherCrooks) {
         witherBone = materials.addMeta(7, "wither_bone", CreativeTabs.BREWING);
         witherBoneDrop = Config.witherBoneDrop;
         milkCooldownCow = Config.milkCooldown;
      }

      if (isRecipesLoaded()) {
         if (Config.enableCauldronPotions) {
            splashBottle = materials.addMeta(2, "splash_bottle", CreativeTabs.BREWING);
            lingeringBottle = materials.addMeta(3, "lingering_bottle", CreativeTabs.BREWING);
         }

         if (Config.cauldronStew) {
            mushrooms = materials.addMeta(4, "mushrooms", (CreativeTabs)null);
            rabbitStewMix = materials.addMeta(5, "rabbit_stew_mix", (CreativeTabs)null);
         }

         milkCooldownSquid = Config.milkSquids;
      }

      if (isToolsLoaded() && Config.separateCrook) {
         stoneRod = materials.addMeta(8, "stone_rod", CreativeTabs.MATERIALS);
      }

   }

   public void init(FMLInitializationEvent event) {
      proxy.init();
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit();
      if (milkCooldownCow || milkCooldownSquid) {
         SharedEvents.register(MinecraftForge.EVENT_BUS);
      }

   }

   public void onLootTableLoad(LootTableLoadEvent event) {
      if (witherBoneDrop && WITHER_SKELETON_TABLE.equals(event.getName())) {
         addToVanillaLoot(event, "entities/wither_skeleton");
      }

   }
}
