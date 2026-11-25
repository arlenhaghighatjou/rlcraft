package de.impelon.disenchanter.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.impelon.disenchanter.DisenchanterConfig;
import de.impelon.disenchanter.item.ItemExperienceJar;
import de.impelon.disenchanter.proxy.CommonProxy;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;

public class ExperienceJarUpgrade {
   public final ExperienceJarUpgrade.ModeChange overloadChange;
   public final ExperienceJarUpgrade.CapacityChange capacityChange;
   public static final ExperienceJarUpgrade RESET_UPGRADE = new ExperienceJarUpgrade(ExperienceJarUpgrade.ModeChange.RESET, ExperienceJarUpgrade.CapacityChange.RESET);

   public static ExperienceJarUpgrade parseRecipe(JsonObject json) {
      ExperienceJarUpgrade upgrade;
      if (json.get("upgrade").isJsonObject()) {
         upgrade = parse(JsonUtils.getJsonObject(json, "upgrade"));
      } else {
         if (!JsonUtils.getString(json, "upgrade").equalsIgnoreCase("reset_all")) {
            throw new JsonSyntaxException("Invalid upgrade specification.");
         }

         upgrade = RESET_UPGRADE;
      }

      return upgrade;
   }

   public static ExperienceJarUpgrade parse(JsonObject json) {
      ExperienceJarUpgrade.ModeChange overloadChange = ExperienceJarUpgrade.ModeChange.UNCHANGED;
      ExperienceJarUpgrade.CapacityChange capacityChange = ExperienceJarUpgrade.CapacityChange.UNCHANGED;
      String overload = JsonUtils.getString(json, "overload", (String)null);
      if (overload != null) {
         overloadChange = ExperienceJarUpgrade.ModeChange.valueOf(overload.toUpperCase());
      }

      String capacity = JsonUtils.getString(json, "capacity", (String)null);
      if (capacity != null) {
         capacityChange = ExperienceJarUpgrade.CapacityChange.valueOf(capacity.toUpperCase());
      }

      return new ExperienceJarUpgrade(overloadChange, capacityChange);
   }

   public ExperienceJarUpgrade(ExperienceJarUpgrade.ModeChange overloadChange, ExperienceJarUpgrade.CapacityChange capacityChange) {
      this.overloadChange = overloadChange;
      this.capacityChange = capacityChange;
   }

   public ItemStack apply(ItemStack stack) {
      if (!stack.getItem().equals(CommonProxy.itemExperienceJar)) {
         return ItemStack.EMPTY;
      } else {
         ItemExperienceJar.ensureValidTag(stack);
         switch(this.overloadChange) {
         case RESET:
         case DISABLE:
            ItemExperienceJar.setOverload(stack, false);
            break;
         case ENABLE:
            ItemExperienceJar.setOverload(stack, true);
         case UNCHANGED:
         }

         int changeMultiplier = 1;
         switch(this.capacityChange) {
         case RESET:
            ItemExperienceJar.resetExperienceCapacity(stack);
            break;
         case DECREASE:
            changeMultiplier = -1;
         case INCREASE:
            int currentCapacity = ItemExperienceJar.getExperienceCapacity(stack);
            int capacityChange = DisenchanterConfig.experienceJar.jarUpgradeCapacityChange * changeMultiplier;
            ItemExperienceJar.setExperienceCapacity(stack, MathHelper.clamp(currentCapacity + capacityChange, 0, DisenchanterConfig.experienceJar.jarUpgradeMaxCapacity));
         case UNCHANGED:
         }

         ItemExperienceJar.ensureValidTag(stack);
         return stack;
      }
   }

   public static enum CapacityChange {
      INCREASE,
      DECREASE,
      UNCHANGED,
      RESET;

      private CapacityChange() {
      }
   }

   public static enum ModeChange {
      ENABLE,
      DISABLE,
      UNCHANGED,
      RESET;

      private ModeChange() {
      }
   }
}
