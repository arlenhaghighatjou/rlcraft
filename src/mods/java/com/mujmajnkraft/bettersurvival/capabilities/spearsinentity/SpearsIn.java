package com.mujmajnkraft.bettersurvival.capabilities.spearsinentity;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class SpearsIn implements ISpearsIn {
   private ArrayList<ItemStack> SpearsIn = new ArrayList();

   public SpearsIn() {
   }

   public void addSpear(ItemStack spear) {
      this.SpearsIn.add(spear);
   }

   public ArrayList<ItemStack> getSpearsIn() {
      return this.SpearsIn;
   }

   public static void Register() {
      CapabilityManager.INSTANCE.register(ISpearsIn.class, new SpearsIn.Storage(), new SpearsIn.Factory());
   }

   private static class Factory implements Callable<ISpearsIn> {
      private Factory() {
      }

      public ISpearsIn call() throws Exception {
         return new SpearsIn();
      }
   }

   private static class Storage implements IStorage<ISpearsIn> {
      private Storage() {
      }

      public NBTTagList writeNBT(Capability<ISpearsIn> capability, ISpearsIn instance, EnumFacing side) {
         NBTTagList taglist = new NBTTagList();
         ArrayList<ItemStack> list = instance.getSpearsIn();
         if (!list.isEmpty()) {
            for(ItemStack spear : list) {
               NBTTagCompound nbt = spear.serializeNBT();
               taglist.appendTag(nbt);
            }
         }

         return taglist;
      }

      public void readNBT(Capability<ISpearsIn> capability, ISpearsIn instance, EnumFacing side, NBTBase nbt) {
         for(int i = 0; i < ((NBTTagList)nbt).tagCount(); ++i) {
            NBTTagCompound compound = ((NBTTagList)nbt).getCompoundTagAt(i);
            if (compound != null) {
               ItemStack spear = new ItemStack(compound);
               instance.addSpear(spear);
            }
         }

      }
   }
}
