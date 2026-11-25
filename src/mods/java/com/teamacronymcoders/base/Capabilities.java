package com.teamacronymcoders.base;

import com.teamacronymcoders.base.api.ITool;
import com.teamacronymcoders.base.api.ToolImpl;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class Capabilities {
   @CapabilityInject(ITool.class)
   public static Capability<ITool> TOOL;

   public Capabilities() {
   }

   public static void register() {
      CapabilityManager.INSTANCE.register(ITool.class, new IStorage<ITool>() {
         public NBTTagCompound writeNBT(Capability<ITool> capability, ITool instance, EnumFacing side) {
            return new NBTTagCompound();
         }

         public void readNBT(Capability<ITool> capability, ITool instance, EnumFacing side, NBTBase nbt) {
         }
      }, ToolImpl.class);
   }
}
