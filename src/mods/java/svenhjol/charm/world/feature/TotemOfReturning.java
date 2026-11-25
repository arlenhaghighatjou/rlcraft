package svenhjol.charm.world.feature;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.charm.base.CharmLootTables;
import svenhjol.charm.world.item.ItemTotemOfReturning;
import svenhjol.meson.Feature;
import svenhjol.meson.helper.ItemHelper;
import svenhjol.meson.helper.LootHelper;

public class TotemOfReturning extends Feature {
   public static Item totem;
   public static int numberOfUses;
   public static boolean addToLoot;
   public static boolean addToIllusioner;

   public TotemOfReturning() {
   }

   public String getDescription() {
      return "Right-click with a Totem of Returning to bind it to a location in the world.\nThe next time you use the totem, you will be returned to that location, destroying the totem in the process.";
   }

   public void configure() {
      super.configure();
      addToLoot = this.propBoolean("Add to loot", "Add the totem to mansions, mineshafts and nether fortress loot.", true);
      addToIllusioner = this.propBoolean("Add as Illusioner drop", "If Illusioners are enabled, add the totem as a possible drop.", true);
      numberOfUses = 1;
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      totem = new ItemTotemOfReturning();
      ItemHelper.availableTotems.add(new ItemStack(totem));
   }

   @SubscribeEvent
   public void onLootTableLoad(LootTableLoadEvent event) {
      if (addToLoot) {
         int weight = 0;
         int quality = 0;
         LootFunction[] functions = new LootFunction[0];
         LootCondition[] conditions = new LootCondition[0];
         if (event.getName().equals(LootTableList.CHESTS_WOODLAND_MANSION)) {
            weight = 12;
         }

         if (event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT)) {
            weight = 12;
         }

         if (event.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE)) {
            weight = 8;
         }

         if (event.getName().equals(CharmLootTables.VILLAGE_PRIEST)) {
            weight = 2;
         }

         if (event.getName().equals(CharmLootTables.TREASURE_VALUABLE)) {
            weight = 12;
         }

         if (event.getName().equals(CharmLootTables.TREASURE_RARE)) {
            weight = 16;
         }

         if (weight > 0) {
            LootHelper.addToLootTable(event.getTable(), totem, weight, quality, functions, conditions);
         }

      }
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
