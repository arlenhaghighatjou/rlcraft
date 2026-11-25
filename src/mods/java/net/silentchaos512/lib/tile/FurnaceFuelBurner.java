package net.silentchaos512.lib.tile;

import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.ForgeEventFactory;
import net.silentchaos512.lib.util.EnumUtils;

public class FurnaceFuelBurner implements IFuelBurner {
   private final FurnaceFuelBurner.BurnCondition burnCondition;
   private int timeRemaining;
   private int currentItemMaxTime;

   public FurnaceFuelBurner(FurnaceFuelBurner.BurnCondition burnCondition) {
      this.burnCondition = burnCondition;
   }

   public boolean feedFuel(ItemStack stack) {
      int value = ForgeEventFactory.getItemBurnTime(stack);
      if (value > 0) {
         this.timeRemaining = this.currentItemMaxTime = value;
         return true;
      } else {
         return false;
      }
   }

   public boolean hasFuel() {
      return this.timeRemaining > 0;
   }

   public boolean tickFuel(boolean isProcessing) {
      if (this.burnCondition.shouldTick(this, isProcessing)) {
         --this.timeRemaining;
         return true;
      } else {
         return false;
      }
   }

   private static FurnaceFuelBurner readFromNBT(NBTTagCompound tags) {
      FurnaceFuelBurner.BurnCondition condition = (FurnaceFuelBurner.BurnCondition)EnumUtils.fromString(FurnaceFuelBurner.BurnCondition.class, tags.getString("Condition")).orElse(FurnaceFuelBurner.BurnCondition.DEFAULT);
      FurnaceFuelBurner result = new FurnaceFuelBurner(condition);
      result.timeRemaining = tags.getInteger("Time");
      result.currentItemMaxTime = tags.getInteger("MaxTime");
      return result;
   }

   private static void writeToNBT(NBTTagCompound tags, FurnaceFuelBurner burner) {
      if (burner.burnCondition != FurnaceFuelBurner.BurnCondition.DEFAULT) {
         tags.setString("Condition", burner.burnCondition.name());
      }

      tags.setInteger("Time", burner.timeRemaining);
      tags.setInteger("MaxTime", burner.currentItemMaxTime);
   }

   public int getTimeRemaining() {
      return this.timeRemaining;
   }

   public int getCurrentItemMaxTime() {
      return this.currentItemMaxTime;
   }

   static {
      SyncVariable.Helper.registerSerializer(FurnaceFuelBurner.class, FurnaceFuelBurner::readFromNBT, FurnaceFuelBurner::writeToNBT);
   }

   public static enum BurnCondition {
      STANDARD {
         boolean shouldTick(FurnaceFuelBurner burner, boolean processing) {
            return burner.timeRemaining > 0;
         }
      },
      ONLY_WHEN_PROCESSING {
         boolean shouldTick(FurnaceFuelBurner burner, boolean processing) {
            return processing;
         }
      },
      ALWAYS {
         boolean shouldTick(FurnaceFuelBurner burner, boolean processing) {
            return true;
         }
      };

      private static final FurnaceFuelBurner.BurnCondition DEFAULT = STANDARD;

      private BurnCondition() {
      }

      abstract boolean shouldTick(FurnaceFuelBurner var1, boolean var2);
   }
}
