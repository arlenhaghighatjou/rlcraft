package c4.corpsecomplex.common.modules;

import c4.corpsecomplex.CorpseComplex;
import c4.corpsecomplex.common.Module;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class HungerModule extends Module {
   private static final Field FOOD_SATURATION_LEVEL = ReflectionHelper.findField(FoodStats.class, "foodSaturationLevel", "foodSaturationLevel");
   private static boolean keepFood;
   private static int minFood;
   private static int maxFood;
   private static boolean cfgEnabled;
   private static boolean keepSaturation;

   @SubscribeEvent
   public void onPlayerRespawnBegin(Clone e) {
      if (e.isWasDeath() && !e.getEntityPlayer().world.isRemote) {
         restoreHunger(e.getEntityPlayer(), e.getOriginal());
      }
   }

   public HungerModule() {
      super("Hunger", "Customize hunger and saturation values on respawn");
   }

   public void loadModuleConfig() {
      this.setCategoryComment();
      cfgEnabled = this.getBool("Enable Hunger Module", false, "Set to true to enable hunger module", false);
      keepFood = this.getBool("Keep Food Level", false, "Set to true to retain food level on death", false);
      minFood = this.getInt("Minimum Food Level", 6, 0, 20, "Lowest amount of food level you can respawn with", false);
      maxFood = this.getInt("Maximum Food Level", 20, minFood, 20, "Highest amount of food level you can respawn with", false);
      keepSaturation = this.getBool("Keep Saturation", false, "Set to true to retain saturation on death", false);
   }

   public void initPropOrder() {
      this.propOrder = new ArrayList(Arrays.asList("Enable Hunger Module", "Keep Food Level", "Maximum Food Level", "Minimum Food Level"));
   }

   public void setEnabled() {
      this.enabled = cfgEnabled;
   }

   private static void restoreHunger(EntityPlayer player, EntityPlayer oldPlayer) {
      FoodStats oldFood = oldPlayer.getFoodStats();
      FoodStats newFood = player.getFoodStats();
      if (keepFood) {
         newFood.setFoodLevel(Math.max(minFood, Math.min(maxFood, oldFood.getFoodLevel())));
      } else {
         newFood.setFoodLevel(Math.max(minFood, Math.min(maxFood, 20)));
      }

      if (keepSaturation) {
         try {
            FOOD_SATURATION_LEVEL.setFloat(newFood, oldFood.getSaturationLevel());
         } catch (IllegalAccessException var5) {
            CorpseComplex.logger.error("Error syncing food saturation level in hunger module!");
         }
      }

   }
}
