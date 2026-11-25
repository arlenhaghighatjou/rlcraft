package svenhjol.charm.crafting.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.charm.base.CharmLootTables;
import svenhjol.charm.crafting.item.ItemSuspiciousSoup;
import svenhjol.charm.crafting.potion.SuspiciousEffects;
import svenhjol.meson.Feature;
import svenhjol.meson.handler.RecipeHandler;
import svenhjol.meson.helper.ForgeHelper;
import svenhjol.meson.helper.LootHelper;
import svenhjol.meson.registry.ProxyRegistry;

public class SuspiciousSoup extends Feature {
   public static Item suspiciousSoup;
   public static List<SuspiciousEffects.CustomEffect> effects = new ArrayList();
   public static long lastSeed;
   public static int duration;
   public static int amplifier;
   public static int heal;
   public static double saturation;
   public static boolean addToLoot;
   public static int maxTypes;
   public static int maxStackSize;

   public SuspiciousSoup() {
   }

   public String getDescription() {
      return "Craft (or find) beetroot soup with a flower to make a suspicious soup.\nDifferent flowers provide positive effects that are different for each world.\nBe warned: these unstable concoctions might occasionally have negative side effects.";
   }

   public boolean isEnabled() {
      return this.enabled && !ForgeHelper.areModsLoaded("sm");
   }

   public void configure() {
      super.configure();
      duration = this.propInt("Suspicious Soup duration", "Minimum duration (in seconds) of the effect(s) of the soup when consumed.", 20);
      heal = this.propInt("Health restored", "Amount of half-hearts of health restored by consuming any soup.", 10);
      addToLoot = this.propBoolean("Add to loot", "Add soup to mansions, igloos and village blacksmith loot.", true);
      SuspiciousEffects.init();
      maxTypes = effects.size();
      maxStackSize = 1;
      amplifier = 1;
      saturation = 0.2D;
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      suspiciousSoup = new ItemSuspiciousSoup();
      List<ItemStack> flowers = new ArrayList();
      flowers.add(new ItemStack(Blocks.YELLOW_FLOWER, 1));

      for(int i = 0; i <= 8; ++i) {
         flowers.add(new ItemStack(Blocks.RED_FLOWER, 1, i));
      }

      for(int i = 0; i <= 5; ++i) {
         flowers.add(new ItemStack(Blocks.DOUBLE_PLANT, 1, i));
      }

      int i = 0;

      for(ItemStack flower : flowers) {
         RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(suspiciousSoup, 1, i), "BBB", "BBB", "FWF", 'B', Items.BEETROOT, 'W', Items.BOWL, 'F', flower);
         ++i;
         if (i == maxTypes) {
            i = 0;
         }
      }

   }

   @SubscribeEvent
   public void onWorldLoad(Load event) {
      World world = event.getWorld();
      if (!world.isRemote) {
         if (effects.isEmpty() || lastSeed != world.getSeed()) {
            lastSeed = world.getSeed();
            this.assignEffects();
         }

      }
   }

   @SubscribeEvent
   public void onLootTableLoad(LootTableLoadEvent event) {
      if (addToLoot) {
         int weight = 0;
         int quality = 0;
         LootCondition[] conditions = new LootCondition[0];
         LootFunction[] functions = new LootFunction[]{new SetMetadata(new LootCondition[0], new RandomValueRange(0.0F, (float)(maxTypes - 1)))};
         if (event.getName().equals(LootTableList.CHESTS_WOODLAND_MANSION)) {
            weight = 14;
         }

         if (event.getName().equals(LootTableList.CHESTS_IGLOO_CHEST)) {
            weight = 14;
         }

         if (event.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH)) {
            weight = 14;
         }

         if (event.getName().equals(CharmLootTables.VILLAGE_FARMER)) {
            weight = 14;
         }

         if (event.getName().equals(CharmLootTables.VILLAGE_BUTCHER)) {
            weight = 14;
         }

         if (weight > 0) {
            LootHelper.addToLootTable(event.getTable(), suspiciousSoup, weight, quality, functions, conditions);
         }

      }
   }

   public boolean hasSubscriptions() {
      return true;
   }

   private void assignEffects() {
      Collections.shuffle(effects, new EaglercraftRandom(lastSeed));
   }
}
