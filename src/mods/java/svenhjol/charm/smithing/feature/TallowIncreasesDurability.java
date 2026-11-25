package svenhjol.charm.smithing.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.charm.smithing.compat.QuarkTallow;
import svenhjol.meson.Feature;
import svenhjol.meson.Meson;
import svenhjol.meson.helper.ForgeHelper;

public class TallowIncreasesDurability extends Feature {
   public static List<Class<? extends Item>> repairable = new ArrayList();
   public static double amount;
   public static double chance;
   public static int xpCost;
   private QuarkTallow quarkTallow;

   public TallowIncreasesDurability() {
   }

   public String getDescription() {
      return "Tallow can be combined on an anvil with a tool or armor to give a small durability boost.\nRepairing using tallow has a chance to increase future repair costs of the tool or armor.\nNOTE: Quark must be installed for this feature to be enabled.";
   }

   public void configure() {
      super.configure();
      amount = this.propDouble("Amount repaired", "Percentage (out of 1.0) of the tool or armor repaired by a piece of tallow.", 0.02D);
      chance = this.propDouble("Chance of repair cost increase", "Chance (out of 1.0) of tallow increasing the future repair cost of the tool.", 0.05D);
      xpCost = this.propInt("XP cost", "Amount of XP (levels) required to repair.", 0);
      repairable = Arrays.asList(ItemTool.class, ItemSword.class, ItemHoe.class, ItemShield.class, ItemArmor.class, ItemElytra.class, ItemShears.class, ItemFlintAndSteel.class, ItemFishingRod.class);

      try {
         if (ForgeHelper.areModsLoaded("quark")) {
            this.quarkTallow = (QuarkTallow)QuarkTallow.class.newInstance();
         }
      } catch (Exception var2) {
         Meson.runtimeException("Error loading QuarkTallow");
      }

   }

   public boolean isEnabled() {
      return this.enabled && ForgeHelper.areModsLoaded("quark");
   }

   @SubscribeEvent
   public void onAnvilUpdate(AnvilUpdateEvent event) {
      if (this.quarkTallow != null) {
         this.quarkTallow.onAnvilUpdate(event);
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
