package c4.corpsecomplex.common.modules.inventory;

import c4.corpsecomplex.CorpseComplex;
import c4.corpsecomplex.common.Module;
import c4.corpsecomplex.common.Submodule;
import c4.corpsecomplex.common.modules.compatibility.advinv.AdvHandler;
import c4.corpsecomplex.common.modules.compatibility.advinv.AdvModule;
import c4.corpsecomplex.common.modules.compatibility.baubles.BaublesHandler;
import c4.corpsecomplex.common.modules.compatibility.baubles.BaublesModule;
import c4.corpsecomplex.common.modules.compatibility.camping.CampingHandler;
import c4.corpsecomplex.common.modules.compatibility.camping.CampingModule;
import c4.corpsecomplex.common.modules.compatibility.cosmeticarmorreworked.CosmeticHandler;
import c4.corpsecomplex.common.modules.compatibility.cosmeticarmorreworked.CosmeticModule;
import c4.corpsecomplex.common.modules.compatibility.galacticraftcore.GalacticraftHandler;
import c4.corpsecomplex.common.modules.compatibility.galacticraftcore.GalacticraftModule;
import c4.corpsecomplex.common.modules.compatibility.powerinventory.OPHandler;
import c4.corpsecomplex.common.modules.compatibility.powerinventory.OPModule;
import c4.corpsecomplex.common.modules.compatibility.rpginventory.RPGHandler;
import c4.corpsecomplex.common.modules.compatibility.rpginventory.RPGModule;
import c4.corpsecomplex.common.modules.compatibility.thebetweenlands.BetweenlandsHandler;
import c4.corpsecomplex.common.modules.compatibility.thebetweenlands.BetweenlandsModule;
import c4.corpsecomplex.common.modules.compatibility.thut_wearables.ThutHandler;
import c4.corpsecomplex.common.modules.compatibility.thut_wearables.ThutModule;
import c4.corpsecomplex.common.modules.compatibility.toolbelt.ToolbeltHandler;
import c4.corpsecomplex.common.modules.compatibility.toolbelt.ToolbeltModule;
import c4.corpsecomplex.common.modules.compatibility.wearablebackpacks.WBHandler;
import c4.corpsecomplex.common.modules.compatibility.wearablebackpacks.WBModule;
import c4.corpsecomplex.common.modules.inventory.capability.DeathInventory;
import c4.corpsecomplex.common.modules.inventory.capability.IDeathInventory;
import c4.corpsecomplex.common.modules.inventory.enchantment.EnchantmentModule;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathInventoryHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class InventoryModule extends Module {
   public static ArrayList<Class<? extends DeathInventoryHandler>> handlerClasses;
   public static boolean destroyCursed;
   public static double randomDrop;
   public static boolean randomDropOnlyMain;
   public static String[] essentialItems;
   public static String[] cursedItems;
   public static double randomDestroy;
   public static double dropLoss;
   public static boolean difficultyDropLoss;
   public static double dropLossEasy;
   public static double dropLossNormal;
   public static double dropLossHard;
   public static double keptLoss;
   public static boolean difficultyKeptLoss;
   public static double keptLossEasy;
   public static double keptLossNormal;
   public static double keptLossHard;
   public static double dropDrain;
   public static double keptDrain;
   public static boolean durabilityLossLimiter;
   static boolean keepArmor;
   static boolean keepHotbar;
   static boolean keepMainhand;
   static boolean keepOffhand;
   static boolean keepMainInventory;
   private static boolean noDropDespawn;
   private static int dropDespawnTimer;
   private static boolean cfgEnabled;

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void storeDeathInventory(LivingDeathEvent e) {
      if (e.getEntityLiving() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)e.getEntityLiving();
         if (!player.world.getGameRules().getBoolean("keepInventory") && !player.world.isRemote) {
            this.storeInventories(player);
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST,
      receiveCanceled = true
   )
   public void canceledDeath(LivingDeathEvent e) {
      if (e.getEntityLiving() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)e.getEntityLiving();
         if (!player.world.getGameRules().getBoolean("keepInventory") && e.isCanceled()) {
            this.retrieveInventories(player, player);
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onPlayerRespawnBegin(Clone e) {
      if (e.isWasDeath() && !e.getEntityPlayer().world.isRemote) {
         EntityPlayer player = e.getEntityPlayer();
         EntityPlayer oldPlayer = e.getOriginal();
         if (!player.world.getGameRules().getBoolean("keepInventory")) {
            this.retrieveInventories(player, oldPlayer);
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onPlayerDrops(PlayerDropsEvent e) {
      if (noDropDespawn) {
         e.getDrops().forEach(EntityItem::setNoDespawn);
      } else if (dropDespawnTimer > 0) {
         e.getDrops().forEach((item) -> {
            item.lifespan = dropDespawnTimer * 20;
         });
      }

   }

   public InventoryModule() {
      super("Inventory", "Customize how your inventory is handled on death and respawn");
      this.submoduleClasses = new ArrayList();
      handlerClasses = new ArrayList();
      this.addSubmodule(EnchantmentModule.class);
      this.addSubmodule("wearablebackpacks", WBModule.class, WBHandler.class);
      this.addSubmodule("thut_wearables", ThutModule.class, ThutHandler.class);
      this.addSubmodule("rpginventory", RPGModule.class, RPGHandler.class);
      this.addSubmodule("powerinventory", OPModule.class, OPHandler.class);
      this.addSubmodule("baubles", BaublesModule.class, BaublesHandler.class);
      this.addSubmodule("galacticraftcore", GalacticraftModule.class, GalacticraftHandler.class);
      this.addSubmodule("cosmeticarmorreworked", CosmeticModule.class, CosmeticHandler.class);
      this.addSubmodule("toolbelt", ToolbeltModule.class, ToolbeltHandler.class);
      this.addSubmodule("advinv", AdvModule.class, AdvHandler.class);
      this.addSubmodule("camping", CampingModule.class, CampingHandler.class);
      this.addSubmodule("thebetweenlands", BetweenlandsModule.class, BetweenlandsHandler.class);
      handlerClasses.add(InventoryHandler.class);
   }

   public void loadModuleConfig() {
      this.setCategoryComment();
      cfgEnabled = this.getBool("Enable Inventory Module", false, "Set to true to enable inventory module", true);
      keepArmor = this.getBool("Keep Armor", false, "Set to true to keep equipped armor on death", false);
      keepHotbar = this.getBool("Keep Hotbar", false, "Set to true to keep non-mainhand hotbar items on death", false);
      keepMainhand = this.getBool("Keep Mainhand", false, "Set to true to keep mainhand item on death", false);
      keepOffhand = this.getBool("Keep Offhand", false, "Set to true to keep offhand item on death", false);
      keepMainInventory = this.getBool("Keep Main Inventory", false, "Set to true to keep main inventory (non-equipped non-hotbar) items on death", false);
      destroyCursed = this.getBool("Destroy Cursed Items", false, "Set to true to destroy cursed items instead of dropping them", false);
      randomDrop = this.getDouble("Random Drop Chance", 0.0D, 0.0F, 1.0F, "Percent chance that items that are kept will still drop", false);
      randomDropOnlyMain = this.getBool("Random Drop Only Main Inventory", false, "Set to true to only apply random drop chance to the main inventory", false);
      randomDestroy = this.getDouble("Random Destroy Chance", 0.0D, 0.0F, 1.0F, "Percent chance that dropped items will be destroyed", false);
      essentialItems = this.getStringList("Essential Items", new String[0], "List of items that are always kept", false);
      cursedItems = this.getStringList("Cursed Items", new String[0], "List of items that are always dropped", false);
      dropLoss = this.getDouble("Durability Loss on Drops", 0.0D, 0.0F, 1.0F, "Percent of durability lost on death for drops", false);
      difficultyDropLoss = this.getBool("Durability Loss Dependent on Difficulty", false, "Set to true to make the durability loss on drops dependent on world difficulty", false);
      dropLossEasy = this.getDouble("Durability Loss on Drops on Easy", 0.0D, 0.0F, 1.0F, "Percent of durability lost on death for drops on easy difficulty", false);
      dropLossNormal = this.getDouble("Durability Loss on Drops on Normal", 0.0D, 0.0F, 1.0F, "Percent of durability lost on death for drops on normal difficulty", false);
      dropLossHard = this.getDouble("Durability Loss on Drops Hard", 0.0D, 0.0F, 1.0F, "Percent of durability lost on death for drops on hard difficulty", false);
      keptLoss = this.getDouble("Durability Loss on Kept Items", 0.0D, 0.0F, 1.0F, "Percent of durability lost on death for kept items", false);
      difficultyKeptLoss = this.getBool("Kept Durability Loss Dependent on Difficulty", false, "Set to true to make the durability loss on kept items dependent on world difficulty", false);
      keptLossEasy = this.getDouble("Durability Loss on Kept Items on Easy", 0.0D, 0.0F, 1.0F, "Percent of durability lost on death for Kept Items on easy difficulty", false);
      keptLossNormal = this.getDouble("Durability Loss on Kept Items on Normal", 0.0D, 0.0F, 1.0F, "Percent of durability lost on death for Kept Items on normal difficulty", false);
      keptLossHard = this.getDouble("Durability Loss on Kept Items Hard", 0.0D, 0.0F, 1.0F, "Percent of durability lost on death for Kept Items on hard difficulty", false);
      dropDrain = this.getDouble("Energy Drain on Drops", 0.0D, 0.0F, 1.0F, "Percent of energy drained on death for drops", false);
      keptDrain = this.getDouble("Energy Drain on Kept Items", 0.0D, 0.0F, 1.0F, "Percent of energy drained on death for kept items", false);
      durabilityLossLimiter = this.getBool("Limit Durability Loss", false, "Set to true to limit durability loss so that items will never break due to death penalities", false);
      noDropDespawn = this.getBool("No Drop Despawn", false, "Set to true to prevent death drops from despawning", false);
      dropDespawnTimer = this.getInt("Drop Despawn Timer", -1, -1, 10000, "Time (in seconds) to set for drop despawn timer, -1 for vanilla", false);
   }

   public void initPropOrder() {
      this.propOrder = new ArrayList(Arrays.asList("Enable Inventory Module", "Keep Armor", "Keep Hotbar", "Keep Mainhand", "Keep Offhand", "Keep Main Inventory", "Durability Loss on Drops", "Durability Loss on Drops on Difficulty", "Durability Loss on Drops on Easy", "Durability Loss on Drops on Normal", "Durability Loss on Drops on Hard", "Durability Loss on Kept Items", "Durability Loss on Drops on Difficulty", "Durability Loss on Kept Items on Easy", "Durability Loss on Kept Items on Normal", "Durability Loss on Kept Items on Hard", "Energy Drain on Drops", "Energy Drain on Kept Items", "Random Drop Chance", "Random Drop Only Main Inventory", "Random Destroy Chance", "Essential Items", "Cursed Items", "Destroy Cursed Items"));
   }

   public void setEnabled() {
      this.enabled = cfgEnabled;
   }

   private void storeInventories(EntityPlayer player) {
      handlerClasses.forEach((handler) -> {
         try {
            ((DeathInventoryHandler)handler.getDeclaredConstructor(EntityPlayer.class).newInstance(player)).storeInventory();
         } catch (Exception var3) {
            CorpseComplex.logger.log(Level.ERROR, "Failed to initialize handler " + handler, var3);
         }

      });
   }

   private void retrieveInventories(EntityPlayer player, EntityPlayer oldPlayer) {
      IDeathInventory oldDeathInventory = (IDeathInventory)oldPlayer.getCapability(DeathInventory.Provider.DEATH_INV_CAP, (EnumFacing)null);
      handlerClasses.forEach((handler) -> {
         try {
            ((DeathInventoryHandler)handler.getDeclaredConstructor(EntityPlayer.class).newInstance(player)).retrieveInventory(oldDeathInventory);
         } catch (Exception var4) {
            CorpseComplex.logger.log(Level.ERROR, "Failed to initialize handler " + handler, var4);
         }

      });
   }

   private void addSubmodule(String modid, Class<? extends Submodule> submodule, Class<? extends DeathInventoryHandler> handler) {
      if (Loader.isModLoaded(modid) && !this.submoduleClasses.contains(submodule)) {
         this.submoduleClasses.add(submodule);
         if (!handlerClasses.contains(handler)) {
            handlerClasses.add(handler);
         }
      }

   }
}
