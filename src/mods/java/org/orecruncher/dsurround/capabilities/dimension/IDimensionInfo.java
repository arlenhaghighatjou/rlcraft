package org.orecruncher.dsurround.capabilities.dimension;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IDimensionInfo extends INBTSerializable<NBTTagCompound> {
   int getId();

   int getSeaLevel();

   int getSkyHeight();

   int getCloudHeight();

   int getSpaceHeight();

   boolean hasHaze();

   boolean hasAuroras();

   boolean hasWeather();

   boolean hasFog();

   boolean playBiomeSounds();

   boolean alwaysOutside();
}
