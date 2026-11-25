package org.jwaresoftware.mcmods.lib.capability;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.Validate;
import org.jwaresoftware.mcmods.lib.Instructions;
import org.jwaresoftware.mcmods.lib.ItemStacks;
import org.jwaresoftware.mcmods.lib.LibInfo;
import org.jwaresoftware.mcmods.lib.SharedGlue;
import org.jwaresoftware.mcmods.lib.Strings;

@ParametersAreNonnullByDefault
public interface ITemperatureChangeable {
   String NBT_CREATE_HOT = LibInfo.rstring("Heated");
   String NBT_CREATE_COLD = LibInfo.rstring("Cooled");
   String PROPERTY_TEMPERATURE = "temperature";
   String PROPERTY_HEATED = "heated";
   String PROPERTY_COOLED = "cooled";
   String NBT_BONUS_TEMP_TIMELEFT = "BonusTempDuration";
   String NBT_HEATED_FLAG = "Heated";
   String NBT_COOLED_FLAG = "Cooled";
   int DEFAULT_HEATING_DURATION_SECS = 480;
   int DEFAULT_COOLING_DURATION_SECS = 240;
   int DEFAULT_ONGOING_DURATION_SECS = 60;
   int DEFAULT_TEMPERATURE_UPDATE_MODULO = SharedGlue.SECS_TO_TICKS_DURATION_MULTIPLIER();

   ITemperatureChangeable.Category getTemperatureDelivery();

   default int getTemperatureModifier(ItemStack stack) {
      return 0;
   }

   default int getHeatDuration(ItemStack stack) {
      return 480;
   }

   default int getCoolDuration(ItemStack stack) {
      return 240;
   }

   default int getBonusTemperatureTimeLeft(ItemStack stack) {
      int ticks = -1;
      if (Instructions.present(stack)) {
         ticks = Instructions.getInteger(stack, "BonusTempDuration", -1);
      }

      return ticks;
   }

   default int getBonusTemperatureTimeLeftForApply(ItemStack stack, int min) {
      if (min <= 0) {
         min = 60;
      }

      return Math.max(min, this.getBonusTemperatureTimeLeft(stack));
   }

   default boolean hasBonusTemperature(ItemStack stack) {
      return this.getBonusTemperatureTimeLeft(stack) > 0;
   }

   default boolean isHeated(ItemStack stack) {
      return Instructions.getFlag(stack, "Heated") && this.hasBonusTemperature(stack);
   }

   default boolean shouldBeHeated(ItemStack stack) {
      return stack.hasTagCompound() && stack.getTagCompound().getBoolean(NBT_CREATE_HOT);
   }

   default boolean isCooled(ItemStack stack) {
      return Instructions.getFlag(stack, "Cooled") && this.hasBonusTemperature(stack);
   }

   default boolean shouldBeCooled(ItemStack stack) {
      return stack.hasTagCompound() && stack.getTagCompound().getBoolean(NBT_CREATE_COLD);
   }

   default void clearBonusTemperature(ItemStack stack) {
      NBTTagCompound instr = Instructions.get(stack);
      if (instr != null) {
         instr.removeTag("BonusTempDuration");
         instr.removeTag("Cooled");
         instr.removeTag("Heated");
         if (instr.hasNoTags()) {
            Instructions.remove(stack);
         }

         if (stack.getTagCompound().hasNoTags()) {
            stack.setTagCompound((NBTTagCompound)null);
         }
      }

   }

   default int getPercentageBonusTemperatureLeft(ItemStack stack) {
      int percent_left = -1;
      int n = this.getBonusTemperatureTimeLeft(stack);
      if (n > 0) {
         boolean heated = Instructions.getFlag(stack, "Heated");
         float f = (float)n / (heated ? (float)this.getHeatDuration(stack) : (float)this.getCoolDuration(stack));
         percent_left = MathHelper.ceil(f * 100.0F);
      }

      return percent_left;
   }

   default boolean setHeated(ItemStack stack) {
      boolean changed = false;
      if (stack.getItem() == this) {
         NBTTagCompound instr = Instructions.createshell(stack);
         instr.removeTag("Cooled");
         instr.setInteger("BonusTempDuration", this.getHeatDuration(stack));
         instr.setBoolean("Heated", true);
         changed = true;
      }

      return changed;
   }

   default boolean setCooled(ItemStack stack) {
      boolean changed = false;
      if (stack.getItem() == this) {
         NBTTagCompound instr = Instructions.createshell(stack);
         instr.removeTag("Heated");
         instr.setInteger("BonusTempDuration", this.getCoolDuration(stack));
         instr.setBoolean("Cooled", true);
         changed = true;
      }

      return changed;
   }

   default ItemStack asOutput(ITemperatureChangeable.UsedEffect effect, ItemStack original) {
      ItemStack output = ItemStacks.copyItemStackSingle(original);
      if (!ITemperatureChangeable.UsedEffect.UNCHANGED.equals(effect)) {
         NBTTagCompound data = ItemStacks.getTagCompoundNonNull(output);
         data.setBoolean(ITemperatureChangeable.UsedEffect.COOLED.equals(effect) ? NBT_CREATE_COLD : NBT_CREATE_HOT, true);
      }

      return output;
   }

   default boolean tickDownBonusTemperature(ItemStack stack, int decrement) {
      boolean changed = false;
      if (stack.getItem() == this && this.hasBonusTemperature(stack)) {
         NBTTagCompound instr = Instructions.get(stack);
         int bonusLeft = instr.getInteger("BonusTempDuration") - decrement;
         if (bonusLeft > 0) {
            instr.setInteger("BonusTempDuration", bonusLeft);
         } else {
            this.clearBonusTemperature(stack);
         }

         changed = true;
      }

      return changed;
   }

   default boolean onUpdateTickBonusTemperature(ItemStack stack, World world, Entity holder, int slotindex, boolean selected) {
      boolean flipped = false;
      if (SharedGlue.isaServerWorld(world) && SharedGlue.isaPlayer(holder) && Instructions.present(stack)) {
         EntityPlayer player = (EntityPlayer)holder;
         if (selected && player.isHandActive() && player.getItemInUseCount() > 0) {
            return false;
         }

         if ((selected || slotindex < 0 || SharedGlue.isAffectingPlayer(stack, player, slotindex)) && player.ticksExisted % DEFAULT_TEMPERATURE_UPDATE_MODULO == 0 && this.tickDownBonusTemperature(stack, 1) && !this.hasBonusTemperature(stack)) {
            SharedGlue.refreshPlayerHUD(player);
            flipped = true;
         }
      }

      return flipped;
   }

   default void addBonusTemperatureInformation(ItemStack stack, World world, List<String> tooltip) {
      if (this.hasBonusTemperature(stack)) {
         int percent_left = this.getPercentageBonusTemperatureLeft(stack);
         int sec = this.getBonusTemperatureTimeLeft(stack);
         int min = sec / 60;
         sec = sec - min * 60;
         String timeLeft = " < " + (min > 0 ? min + "m " + sec + "s" : sec + "s");
         boolean heated = Instructions.getFlag(stack, "Heated");
         String emphasis = "" + (percent_left > 10 ? (heated ? TextFormatting.DARK_RED + "" + TextFormatting.BOLD : TextFormatting.AQUA + "" + TextFormatting.BOLD) : TextFormatting.BLUE);
         tooltip.add(emphasis + Strings.translateTooltip("itemsuffix." + (heated ? "heated" : "cooled")) + TextFormatting.RESET + TextFormatting.DARK_GRAY + timeLeft);
      } else if (stack.hasTagCompound()) {
         String emphasis = null;
         boolean heated = false;
         NBTTagCompound data = stack.getTagCompound();
         if (data.hasKey(NBT_CREATE_HOT)) {
            emphasis = "" + TextFormatting.DARK_RED + TextFormatting.BOLD;
            heated = true;
         } else if (data.hasKey(NBT_CREATE_COLD)) {
            emphasis = "" + TextFormatting.AQUA + TextFormatting.BOLD;
         }

         if (emphasis != null) {
            tooltip.add(emphasis + Strings.translateTooltip("itemsuffix." + (heated ? "heated" : "cooled")) + TextFormatting.RESET);
         }
      }

   }

   default ITemperatureChangeable.UsedEffect onCreateCheckBonusTemperature(ItemStack stack) {
      ITemperatureChangeable.UsedEffect how = ITemperatureChangeable.UsedEffect.UNCHANGED;
      if (stack.hasTagCompound()) {
         NBTTagCompound data = stack.getTagCompound();
         if (data.hasKey(NBT_CREATE_HOT)) {
            if (data.getBoolean(NBT_CREATE_HOT)) {
               how = ITemperatureChangeable.UsedEffect.HEATED;
            }

            data.removeTag(NBT_CREATE_HOT);
         }

         if (data.hasKey(NBT_CREATE_COLD)) {
            if (how == ITemperatureChangeable.UsedEffect.UNCHANGED && data.getBoolean(NBT_CREATE_COLD)) {
               how = ITemperatureChangeable.UsedEffect.COOLED;
            }

            data.removeTag(NBT_CREATE_COLD);
         }

         if (data.hasNoTags()) {
            stack.setTagCompound((NBTTagCompound)null);
         }
      }

      return how;
   }

   public static enum Category {
      CONSUMED,
      WORN,
      CARRIED;

      private Category() {
      }
   }

   public static enum SupportedEffect {
      HEATING,
      COOLING,
      BOTH;

      private SupportedEffect() {
      }
   }

   public static final class TemperaturePropertyGetter implements IItemPropertyGetter {
      private final ITemperatureChangeable.SupportedEffect _whatCheck;

      public static final IItemPropertyGetter forEither() {
         return new ITemperatureChangeable.TemperaturePropertyGetter(ITemperatureChangeable.SupportedEffect.BOTH);
      }

      public static final IItemPropertyGetter forHeatable() {
         return new ITemperatureChangeable.TemperaturePropertyGetter(ITemperatureChangeable.SupportedEffect.HEATING);
      }

      public static final IItemPropertyGetter forCoolable() {
         return new ITemperatureChangeable.TemperaturePropertyGetter(ITemperatureChangeable.SupportedEffect.COOLING);
      }

      private TemperaturePropertyGetter(@Nonnull ITemperatureChangeable.SupportedEffect support) {
         Validate.notNull(support);
         this._whatCheck = support;
      }

      @SideOnly(Side.CLIENT)
      public float apply(ItemStack stack, World world, EntityLivingBase entity) {
         if (ITemperatureChangeable.SupportedEffect.BOTH == this._whatCheck) {
            return this._applyIfEither(stack);
         } else {
            ITemperatureChangeable cap = (ITemperatureChangeable)stack.getItem();
            return ITemperatureChangeable.SupportedEffect.HEATING == this._whatCheck ? (!cap.isHeated(stack) && !cap.shouldBeHeated(stack) ? 0.0F : 1.0F) : (!cap.isCooled(stack) && !cap.shouldBeCooled(stack) ? 0.0F : 1.0F);
         }
      }

      @SideOnly(Side.CLIENT)
      private final float _applyIfEither(ItemStack stack) {
         float out = 0.0F;
         ITemperatureChangeable cap = (ITemperatureChangeable)stack.getItem();
         if (cap.hasBonusTemperature(stack)) {
            out = !cap.isHeated(stack) && !cap.shouldBeHeated(stack) ? 2.0F : 1.0F;
         }

         return out;
      }
   }

   public static enum UsedEffect {
      UNCHANGED,
      HEATED,
      COOLED;

      private UsedEffect() {
      }
   }
}
