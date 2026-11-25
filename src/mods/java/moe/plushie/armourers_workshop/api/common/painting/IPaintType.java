package moe.plushie.armourers_workshop.api.common.painting;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IPaintType {
   IExtraColours.ExtraColourType getColourType();

   float getU();

   float getV();

   int getId();

   int getMarkerIndex();

   boolean hasAverageColourChannel();

   int getChannelIndex();

   void setColourChannelIndex(int var1);

   String getName();

   String getUnlocalizedName();

   @SideOnly(Side.CLIENT)
   String getLocalizedName();
}
