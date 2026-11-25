package com.charles445.simpledifficulty.capability;

import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.temperature.TemporaryModifier;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class TemperatureStorage implements IStorage<ITemperatureCapability> {
   private static final String temperatureLevel = "temperatureLevel";
   private static final String temperatureTickTimer = "temperatureTickTimer";
   private static final String temporaryModifiers = "temporaryModifiers";
   private static final String temperatureDamageCounter = "temperatureDamageCounter";
   private static final String NBT_name = "name";
   private static final String NBT_temperature = "temperature";
   private static final String NBT_duration = "duration";

   public TemperatureStorage() {
   }

   public NBTBase writeNBT(Capability<ITemperatureCapability> capability, ITemperatureCapability instance, EnumFacing side) {
      NBTTagCompound compound = new NBTTagCompound();
      NBTTagList temporaryList = new NBTTagList();
      Map<String, TemporaryModifier> tempModMap = instance.getTemporaryModifiers();

      for(Entry<String, TemporaryModifier> entry : tempModMap.entrySet()) {
         TemporaryModifier tempMod = (TemporaryModifier)entry.getValue();
         NBTTagCompound tempModCompound = new NBTTagCompound();
         tempModCompound.setString("name", (String)entry.getKey());
         tempModCompound.setFloat("temperature", tempMod.temperature);
         tempModCompound.setInteger("duration", tempMod.duration);
         temporaryList.appendTag(tempModCompound);
      }

      compound.setInteger("temperatureLevel", instance.getTemperatureLevel());
      compound.setInteger("temperatureTickTimer", instance.getTemperatureTickTimer());
      compound.setInteger("temperatureDamageCounter", instance.getTemperatureDamageCounter());
      compound.setTag("temporaryModifiers", temporaryList);
      return compound;
   }

   public void readNBT(Capability<ITemperatureCapability> capability, ITemperatureCapability instance, EnumFacing side, NBTBase nbt) {
      if (nbt instanceof NBTTagCompound) {
         NBTTagCompound compound = (NBTTagCompound)nbt;
         if (compound.hasKey("temperatureLevel")) {
            instance.setTemperatureLevel(compound.getInteger("temperatureLevel"));
         }

         if (compound.hasKey("temperatureTickTimer")) {
            instance.setTemperatureTickTimer(compound.getInteger("temperatureTickTimer"));
         }

         if (compound.hasKey("temperatureDamageCounter")) {
            instance.setTemperatureDamageCounter(compound.getInteger("temperatureDamageCounter"));
         }

         if (compound.hasKey("temporaryModifiers")) {
            instance.clearTemporaryModifiers();

            for(NBTTagCompound tempComp : compound.getTagList("temporaryModifiers", 10)) {
               if (tempComp.hasKey("name") && tempComp.hasKey("temperature") && tempComp.hasKey("duration")) {
                  instance.setTemporaryModifier(tempComp.getString("name"), tempComp.getFloat("temperature"), tempComp.getInteger("duration"));
               }
            }
         }
      }

   }
}
