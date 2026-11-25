package org.jwaresoftware.mcmods.lib.integration;

import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.Method;
import org.apache.commons.lang3.Validate;
import org.jwaresoftware.mcmods.lib.Armory;
import org.jwaresoftware.mcmods.lib.ItemStacks;
import org.jwaresoftware.mcmods.lib.ModIntegration;
import org.jwaresoftware.mcmods.lib.Potions;
import org.jwaresoftware.mcmods.lib.SharedGlue;
import org.jwaresoftware.mcmods.lib.Strings;
import org.jwaresoftware.mcmods.lib.capability.ITemperatureChangeable;
import toughasnails.api.TANPotions;
import toughasnails.api.config.GameplayOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.temperature.IModifierMonitor;
import toughasnails.api.temperature.ITemperatureModifier;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureScale;
import toughasnails.api.temperature.IModifierMonitor.Context;
import toughasnails.api.temperature.TemperatureScale.TemperatureRange;
import toughasnails.api.thirst.ThirstHelper;

@ParametersAreNonnullByDefault
public final class TANHelper {
   private static final String _TAN = "toughasnails";
   private static final String _TAN_TEMPMODIFIER_SPI = "toughasnails.api.temperature.ITemperatureModifier";
   private static final int _MAX_TEMP_SHIFT = 9;
   public static ItemStack ICE_CUBE = null;
   static boolean INITED = false;
   static boolean INITED_FOODMGMT = false;
   static boolean INITED_LINING = false;
   static boolean INITED_PORTPAKS = false;
   static boolean ENABLED = false;
   static Potion FOOD_WARMING = Potions.NO_POTION;
   static Potion FOOD_COOLING = Potions.NO_POTION;

   public static final boolean isEnabled() {
      return ENABLED;
   }

   public static final void initFinal() {
      if (!INITED) {
         INITED = true;
         if (ModIntegration.TOUGH_AS_NAILS.isLoaded()) {
            ICE_CUBE = ModIntegration.TOUGH_AS_NAILS.itemstack("ice_cube");
            ENABLED = !ICE_CUBE.isEmpty();
         }

      }
   }

   public static final void initFoodManagement(@Nonnull Potion warming, @Nonnull Potion cooling) {
      initFinal();
      if (!INITED_FOODMGMT) {
         INITED_FOODMGMT = true;
         Validate.isTrue(!Potions.isEmpty(warming), "Food warming indicator potion is required", new Object[0]);
         Validate.isTrue(!Potions.isEmpty(cooling), "Food cooling indicator potion is required", new Object[0]);
         Validate.isTrue(warming != cooling, "Temperature indicator potions MUST be different from each other", new Object[0]);
         if (ENABLED) {
            FOOD_WARMING = warming;
            FOOD_COOLING = cooling;
            TemperatureHelper.registerTemperatureModifier(new TANHelper.WarmingFoodModifier());
            TemperatureHelper.registerTemperatureModifier(new TANHelper.CoolingFoodModifier());
         }

      }
   }

   public static final void initArmorLining() {
      initFinal();
      if (!INITED_LINING) {
         INITED_LINING = true;
         if (ENABLED) {
            TemperatureHelper.registerTemperatureModifier(new TANHelper.LinedArmorModifier());
            TemperatureHelper.registerTemperatureModifier(new TANHelper.AutoRegulatingArmorModifier());
         }

      }
   }

   public static final void initPortablePaks() {
      initFinal();
      if (!INITED_PORTPAKS) {
         INITED_PORTPAKS = true;
         if (ENABLED) {
            TemperatureHelper.registerTemperatureModifier(new TANHelper.PortablePaksModifier());
         }

      }
   }

   @Method(
      modid = "toughasnails"
   )
   public static final boolean manageTemperature() {
      return ENABLED && SyncedConfig.getBooleanValue(GameplayOption.ENABLE_TEMPERATURE);
   }

   @Method(
      modid = "toughasnails"
   )
   public static final boolean manageThirst() {
      return ENABLED && SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST);
   }

   @Method(
      modid = "toughasnails"
   )
   private static final boolean isThirsty(IThirst cap) {
      return cap != null && cap.getThirst() < 20;
   }

   public static final boolean isThirsty(@Nullable World world, EntityPlayer player) {
      boolean is = false;
      if (ModIntegration.TOUGH_AS_NAILS.isLoaded() && manageThirst()) {
         IThirst cap = ThirstHelper.getThirstData(player);
         is = isThirsty(cap);
      }

      return is;
   }

   public static final boolean quenchThirst(EntityPlayer player, int thirstAmount, float hydration, float impurityChance) {
      boolean quenched = false;
      if (ModIntegration.TOUGH_AS_NAILS.isLoaded() && manageThirst()) {
         IThirst cap = ThirstHelper.getThirstData(player);
         quenched = isThirsty(cap);
         cap.setExhaustion(0.0F);
         cap.addStats(thirstAmount, hydration);
         if (impurityChance > 0.0F && TANPotions.thirst != null && player.getRNG().nextFloat() < impurityChance) {
            Potions.addPotionEffect(player, TANPotions.thirst, 30, 0);
         }
      }

      return quenched;
   }

   public static final void hydrateFor(int thirstAmount, EntityPlayer player) {
      if (ModIntegration.TOUGH_AS_NAILS.isLoaded() && manageThirst()) {
         IThirst cap = ThirstHelper.getThirstData(player);
         if (cap != null && TANPotions.hydration != null) {
            player.addPotionEffect(Potions.newStubbornPotionEffect(TANPotions.hydration, 1 + thirstAmount, 0, false, false));
         }
      }

   }

   public static final boolean isPlayerAtExtremeTemperature(EntityPlayer player, boolean hot) {
      boolean is = false;
      if (ModIntegration.TOUGH_AS_NAILS.isLoaded() && manageTemperature()) {
         ITemperature temp = TemperatureHelper.getTemperatureData(player);
         if (temp != null) {
            is = TemperatureScale.isScalePosInRange(temp.getTemperature().getRawValue(), hot ? TemperatureRange.HOT : TemperatureRange.ICY);
         }
      }

      return is;
   }

   public static final boolean coolIfHot(EntityPlayer player, int instantCooldown, int ongoingTimesecs, int fortifyTimesecs) {
      boolean cooled = false;
      if (ModIntegration.TOUGH_AS_NAILS.isLoaded() && manageTemperature()) {
         ITemperature temp = TemperatureHelper.getTemperatureData(player);
         if (temp != null) {
            int scaleValue = temp.getTemperature().getRawValue();
            if (TemperatureScale.isScalePosInRange(scaleValue, TemperatureRange.WARM, TemperatureRange.HOT)) {
               cooled = instantCooldown > 0;
               if (cooled) {
                  temp.setTemperature(new Temperature(scaleValue - instantCooldown));
                  if (instantCooldown > 1) {
                     player.removePotionEffect(TANPotions.hyperthermia);
                  }

                  if (ongoingTimesecs > 0) {
                     Potions.addPotionEffect(player, FOOD_COOLING, ongoingTimesecs, instantCooldown - 1);
                  }
               }

               if (fortifyTimesecs > 0 && TANPotions.heat_resistance != null) {
                  Potions.addPotionEffect(player, TANPotions.heat_resistance, fortifyTimesecs, 0, false, false);
               }
            }
         }
      }

      return cooled;
   }

   public static final boolean coolIfHot(EntityPlayer player, int instantCooldown, int ongoingTimesecs) {
      return coolIfHot(player, instantCooldown, ongoingTimesecs, -1);
   }

   public static final boolean warmIfCold(EntityPlayer player, int instantWarmup, int ongoingTimesecs, int fortifyTimesecs) {
      boolean warmed = false;
      if (ModIntegration.TOUGH_AS_NAILS.isLoaded() && manageTemperature()) {
         ITemperature temp = TemperatureHelper.getTemperatureData(player);
         if (temp != null) {
            int scaleValue = temp.getTemperature().getRawValue();
            if (TemperatureScale.isScalePosInRange(scaleValue, TemperatureRange.ICY, TemperatureRange.COOL)) {
               warmed = instantWarmup > 0;
               if (warmed) {
                  temp.setTemperature(new Temperature(scaleValue + instantWarmup));
                  if (instantWarmup > 1) {
                     player.removePotionEffect(TANPotions.hypothermia);
                  }

                  if (ongoingTimesecs > 0) {
                     Potions.addPotionEffect(player, FOOD_WARMING, ongoingTimesecs, instantWarmup - 1);
                  }
               }

               if (fortifyTimesecs > 0 && TANPotions.cold_resistance != null) {
                  Potions.addPotionEffect(player, TANPotions.cold_resistance, fortifyTimesecs, 0, false, false);
               }
            }
         }
      }

      return warmed;
   }

   public static final boolean warmIfCold(EntityPlayer player, int instantWarmup, int ongoingTimesecs) {
      return warmIfCold(player, instantWarmup, ongoingTimesecs, -1);
   }

   private TANHelper() {
   }

   @Interface(
      iface = "toughasnails.api.temperature.ITemperatureModifier",
      modid = "toughasnails"
   )
   static final class AutoRegulatingArmorModifier extends TANHelper.PlayerModifierImpl {
      AutoRegulatingArmorModifier() {
      }

      @Method(
         modid = "toughasnails"
      )
      public String getId() {
         return "carrots:regulating_armor";
      }

      @Method(
         modid = "toughasnails"
      )
      public Temperature applyPlayerModifiers(EntityPlayer player, Temperature iniT, IModifierMonitor m) {
         TemperatureRange _HAPPY_PLACE = TemperatureRange.MILD;
         Temperature outT = iniT;
         if (TANHelper.manageTemperature()) {
            TemperatureRange adjust = null;
            int found_antifreeze = 0;
            boolean have_large_piece = false;

            for(ItemStack armor : player.getArmorInventoryList()) {
               Armory.XLining.Def liningd = Armory.getXLining(armor);
               if (adjust == null && liningd.name == Armory.XLining.TEMPERATURE_REGULATOR) {
                  int i = liningd.value;
                  adjust = i == 0 ? _HAPPY_PLACE : (i < 0 ? TemperatureRange.COOL : TemperatureRange.WARM);
               }

               if (liningd.name == Armory.XLining.ANTIFREEZE_SHIELD) {
                  ++found_antifreeze;
                  if (armor == player.getItemStackFromSlot(EntityEquipmentSlot.CHEST) || armor == player.getItemStackFromSlot(EntityEquipmentSlot.LEGS)) {
                     have_large_piece = true;
                  }
               }
            }

            if (found_antifreeze > 0) {
               if (have_large_piece) {
                  adjust = adjust == _HAPPY_PLACE ? TemperatureRange.COOL : (adjust == null ? _HAPPY_PLACE : adjust);
               }

               if (found_antifreeze > 3 && SharedGlue.isaServerWorld(player.getEntityWorld())) {
                  player.removePotionEffect(TANPotions.cold_resistance);
                  Potions.addPotionEffect(player, TANPotions.cold_resistance, 20, 0, true, false);
               }
            }

            if (adjust != null) {
               int haveT = iniT.getRawValue();
               int wantT = TemperatureScale.getRangeStart(_HAPPY_PLACE) + _HAPPY_PLACE.getRangeSize() / 2;
               int shift = wantT - haveT;
               int max_shift = this.getMaxShift(adjust, iniT);
               shift = MathHelper.clamp(shift, -max_shift, max_shift);
               if (shift != 0) {
                  int newT = iniT.getRawValue() + shift;
                  m.addEntry(new Context(this.getId(), Strings.translate("carrots.tooltip.tan.xlining.auto.name"), iniT, new Temperature(newT)));
                  outT = new Temperature(newT);
               }
            }
         }

         return outT;
      }

      @Method(
         modid = "toughasnails"
      )
      private int getMidpoint(TemperatureRange range) {
         return TemperatureScale.getRangeStart(range) + range.getRangeSize() / 2;
      }

      @Method(
         modid = "toughasnails"
      )
      private boolean isExtreme(Temperature iniT) {
         int at = iniT.getRawValue();
         return at >= this.getMidpoint(TemperatureRange.HOT) || at <= this.getMidpoint(TemperatureRange.ICY);
      }

      @Method(
         modid = "toughasnails"
      )
      private int getMaxShift(TemperatureRange adjust, Temperature iniT) {
         int max_shift = 6;
         if (this.isExtreme(iniT)) {
            max_shift = 9;
         }

         if (adjust == TemperatureRange.WARM) {
            max_shift += TemperatureRange.ICY.getRangeSize();
         } else if (adjust == TemperatureRange.COOL) {
            max_shift += TemperatureRange.HOT.getRangeSize();
         }

         return max_shift;
      }
   }

   @Interface(
      iface = "toughasnails.api.temperature.ITemperatureModifier",
      modid = "toughasnails"
   )
   static final class CoolingFoodModifier extends TANHelper.PlayerModifierImpl {
      CoolingFoodModifier() {
      }

      @Method(
         modid = "toughasnails"
      )
      public String getId() {
         return "carrots:coldfood";
      }

      @Method(
         modid = "toughasnails"
      )
      public Temperature applyPlayerModifiers(EntityPlayer player, Temperature iniT, IModifierMonitor m) {
         Temperature outT = iniT;
         Potion p = TANHelper.FOOD_COOLING;
         if (TANHelper.manageTemperature() && player.isPotionActive(p)) {
            int newT = iniT.getRawValue();
            newT = newT - MathHelper.clamp(1 + player.getActivePotionEffect(p).getAmplifier(), 1, 4);
            m.addEntry(new Context(this.getId(), Strings.translate("carrots.tooltip.tan.colddrink.name"), iniT, new Temperature(newT)));
            outT = new Temperature(newT);
         }

         return outT;
      }
   }

   @Interface(
      iface = "toughasnails.api.temperature.ITemperatureModifier",
      modid = "toughasnails"
   )
   static final class LinedArmorModifier extends TANHelper.PlayerModifierImpl {
      LinedArmorModifier() {
      }

      @Method(
         modid = "toughasnails"
      )
      public String getId() {
         return "carrots:lined_armor";
      }

      @Method(
         modid = "toughasnails"
      )
      public Temperature applyPlayerModifiers(EntityPlayer player, Temperature iniT, IModifierMonitor m) {
         Temperature outT = iniT;
         if (TANHelper.manageTemperature()) {
            int modifier = 0;

            for(ItemStack armor : player.getArmorInventoryList()) {
               modifier += Armory.getTLining(armor).getModifier();
            }

            if (modifier != 0) {
               int newT = iniT.getRawValue() + modifier;
               m.addEntry(new Context(this.getId(), Strings.translate("carrots.tooltip.tan.xlining.temp.name"), iniT, new Temperature(newT)));
               outT = new Temperature(newT);
            }
         }

         return outT;
      }
   }

   @Interface(
      iface = "toughasnails.api.temperature.ITemperatureModifier",
      modid = "toughasnails"
   )
   abstract static class PlayerModifierImpl implements ITemperatureModifier {
      PlayerModifierImpl() {
      }

      @Method(
         modid = "toughasnails"
      )
      public final boolean isPlayerSpecific() {
         return true;
      }

      @Method(
         modid = "toughasnails"
      )
      public final Temperature applyEnvironmentModifiers(World w, BlockPos pos, Temperature iniT, IModifierMonitor m) {
         return iniT;
      }
   }

   @Interface(
      iface = "toughasnails.api.temperature.ITemperatureModifier",
      modid = "toughasnails"
   )
   static final class PortablePaksModifier extends TANHelper.PlayerModifierImpl {
      PortablePaksModifier() {
      }

      @Method(
         modid = "toughasnails"
      )
      public String getId() {
         return "carrots:portable_temp_pak";
      }

      @Method(
         modid = "toughasnails"
      )
      public Temperature applyPlayerModifiers(EntityPlayer player, Temperature iniT, IModifierMonitor m) {
         Temperature outT = iniT;
         if (TANHelper.manageTemperature()) {
            List<ItemStack> sources = SharedGlue.getAffectingEntity(TANHelper.PortablePaksModifier.IsPortableTemperatureModifier.INSTANCE, player, false, true);
            if (!sources.isEmpty()) {
               int modifier = 0;

               for(ItemStack pak : sources) {
                  ITemperatureChangeable t_cap = (ITemperatureChangeable)pak.getItem();
                  int delta = t_cap.getTemperatureModifier(pak);
                  if (t_cap.isCooled(pak)) {
                     delta *= -1;
                  }

                  modifier += delta;
                  if (Math.abs(modifier) >= 9) {
                     break;
                  }
               }

               if (modifier != 0) {
                  int newT = iniT.getRawValue() + modifier;
                  m.addEntry(new Context(this.getId(), Strings.translate("carrots.tooltip.tan.temppak.name"), iniT, new Temperature(newT)));
                  outT = new Temperature(newT);
               }
            }
         }

         return outT;
      }

      static final class IsPortableTemperatureModifier implements Predicate<ItemStack> {
         static final TANHelper.PortablePaksModifier.IsPortableTemperatureModifier INSTANCE = new TANHelper.PortablePaksModifier.IsPortableTemperatureModifier();

         private IsPortableTemperatureModifier() {
         }

         public boolean apply(ItemStack check) {
            if (ItemStacks.isEmpty(check)) {
               return false;
            } else {
               Item item = check.getItem();
               if (!(item instanceof ITemperatureChangeable)) {
                  return false;
               } else {
                  ITemperatureChangeable t_cap = (ITemperatureChangeable)item;
                  return t_cap.getTemperatureDelivery() == ITemperatureChangeable.Category.CARRIED && t_cap.hasBonusTemperature(check);
               }
            }
         }
      }
   }

   @Interface(
      iface = "toughasnails.api.temperature.ITemperatureModifier",
      modid = "toughasnails"
   )
   static final class WarmingFoodModifier extends TANHelper.PlayerModifierImpl {
      WarmingFoodModifier() {
      }

      @Method(
         modid = "toughasnails"
      )
      public String getId() {
         return "carrots:warmfood";
      }

      @Method(
         modid = "toughasnails"
      )
      public Temperature applyPlayerModifiers(EntityPlayer player, Temperature iniT, IModifierMonitor m) {
         Temperature outT = iniT;
         Potion p = TANHelper.FOOD_WARMING;
         if (TANHelper.manageTemperature() && player.isPotionActive(p)) {
            int newT = iniT.getRawValue();
            newT = newT + MathHelper.clamp(1 + player.getActivePotionEffect(p).getAmplifier(), 1, 4);
            m.addEntry(new Context(this.getId(), Strings.translate("carrots.tooltip.tan.warmfood.name"), iniT, new Temperature(newT)));
            outT = new Temperature(newT);
         }

         return outT;
      }
   }
}
