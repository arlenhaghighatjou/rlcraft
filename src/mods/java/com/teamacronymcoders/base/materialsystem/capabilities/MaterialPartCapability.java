package com.teamacronymcoders.base.materialsystem.capabilities;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class MaterialPartCapability {
   @CapabilityInject(IMaterialPartHolder.class)
   public static Capability<IMaterialPartHolder> MATERIAL_PART_HOLDER;

   public MaterialPartCapability() {
   }

   public static void register() {
      CapabilityManager.INSTANCE.register(IMaterialPartHolder.class, new IStorage<IMaterialPartHolder>() {
         @Nullable
         public NBTBase writeNBT(Capability<IMaterialPartHolder> capability, IMaterialPartHolder instance, EnumFacing side) {
            return instance.serializeNBT();
         }

         public void readNBT(Capability<IMaterialPartHolder> capability, IMaterialPartHolder instance, EnumFacing side, NBTBase nbt) {
            instance.deserializeNBT((NBTTagCompound)nbt);
         }
      }, MaterialPartHolder.class);
   }
}
