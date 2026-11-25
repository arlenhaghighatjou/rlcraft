package com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo;

import java.util.concurrent.Callable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class NunchakuCombo implements INunchakuCombo {
   private float comboPower;
   private int comboTime;
   private boolean isSpinning;

   public NunchakuCombo() {
   }

   public float getComboPower() {
      return this.comboPower;
   }

   public int getComboTime() {
      return this.comboTime;
   }

   public void setComboPower(float power) {
      this.comboPower = power < 1.0F ? power : 1.0F;
      this.comboTime = 30;
   }

   public void countDown() {
      --this.comboTime;
      if (this.comboTime <= 0) {
         this.comboTime = 0;
         this.comboPower = 0.0F;
      }

   }

   public void setComboTime(int time) {
      this.comboTime = time;
   }

   public boolean isSpinning() {
      return this.isSpinning;
   }

   public void setSpinning(boolean b) {
      this.isSpinning = b;
   }

   public static void Register() {
      CapabilityManager.INSTANCE.register(INunchakuCombo.class, new NunchakuCombo.Storage(), new NunchakuCombo.Factory());
   }

   private static class Factory implements Callable<INunchakuCombo> {
      private Factory() {
      }

      public INunchakuCombo call() throws Exception {
         return new NunchakuCombo();
      }
   }

   private static class Storage implements IStorage<INunchakuCombo> {
      private Storage() {
      }

      public NBTBase writeNBT(Capability<INunchakuCombo> capability, INunchakuCombo instance, EnumFacing side) {
         NBTTagCompound compound = new NBTTagCompound();
         compound.setFloat("Power", instance.getComboPower());
         compound.setInteger("Time", instance.getComboTime());
         return compound;
      }

      public void readNBT(Capability<INunchakuCombo> capability, INunchakuCombo instance, EnumFacing side, NBTBase nbt) {
         instance.setComboPower(((NBTTagCompound)nbt).getFloat("Power"));
         instance.setComboTime(((NBTTagCompound)nbt).getInteger("Time"));
      }
   }
}
