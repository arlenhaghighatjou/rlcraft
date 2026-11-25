package net.silentchaos512.scalinghealth.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.silentchaos512.scalinghealth.config.Config;

public final class ScalingHealthSavedData extends WorldSavedData {
   private static final String DATA_NAME = "ScalingHealth_Difficulty";
   private static final String NBT_DIFFICULTY = "Difficulty";
   public double difficulty = -1.0D;

   public ScalingHealthSavedData(String name) {
      super(name);
   }

   private ScalingHealthSavedData() {
      super("ScalingHealth_Difficulty");
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.difficulty = nbt.getDouble("Difficulty");
      this.difficulty = MathHelper.clamp(this.difficulty, (double)Config.Difficulty.minValue, (double)Config.Difficulty.maxValue);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      this.difficulty = MathHelper.clamp(this.difficulty, (double)Config.Difficulty.minValue, (double)Config.Difficulty.maxValue);
      nbt.setDouble("Difficulty", this.difficulty);
      return nbt;
   }

   public static ScalingHealthSavedData get(World world) {
      MapStorage storage = world.getPerWorldStorage();
      ScalingHealthSavedData instance = (ScalingHealthSavedData)storage.getOrLoadData(ScalingHealthSavedData.class, "ScalingHealth_Difficulty");
      if (instance == null) {
         instance = new ScalingHealthSavedData();
         storage.setData("ScalingHealth_Difficulty", instance);
      }

      return instance;
   }
}
