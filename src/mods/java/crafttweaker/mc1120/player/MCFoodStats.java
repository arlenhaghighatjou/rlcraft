package crafttweaker.mc1120.player;

import crafttweaker.api.data.IData;
import crafttweaker.api.player.IFoodStats;
import crafttweaker.api.player.IPlayer;
import crafttweaker.mc1120.data.NBTConverter;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;

public class MCFoodStats implements IFoodStats {
   private final FoodStats foodStats;

   public MCFoodStats(FoodStats foodStats) {
      this.foodStats = foodStats;
   }

   public void addStats(int foodValue, float saturationLevel) {
      this.foodStats.addStats(foodValue, saturationLevel);
   }

   public void onUpdate(IPlayer player) {
      this.foodStats.onUpdate((EntityPlayer)player.getInternal());
   }

   public IData asNBT() {
      NBTTagCompound tag = new NBTTagCompound();
      this.foodStats.writeNBT(tag);
      return NBTConverter.from(tag, true);
   }

   public int getFoodLevel() {
      return this.foodStats.getFoodLevel();
   }

   public void setFootLevel(int foodLevel) {
      this.foodStats.setFoodLevel(foodLevel);
   }

   public boolean needFood() {
      return this.foodStats.needFood();
   }

   public void addExhaustion(float exhaustion) {
      this.foodStats.addExhaustion(exhaustion);
   }

   public float getSaturationLevel() {
      return this.foodStats.getSaturationLevel();
   }

   public void setSaturationLevel(float saturationLevel) {
      this.foodStats.addExhaustion(saturationLevel - this.getSaturationLevel());
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCFoodStats that = (MCFoodStats)o;
         return Objects.equals(this.foodStats, that.foodStats);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.foodStats});
   }
}
