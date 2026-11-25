package moe.plushie.armourers_workshop.api.common.skin.data;

import moe.plushie.armourers_workshop.api.common.skin.Point3D;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import net.minecraft.util.EnumFacing;

public interface ISkinPart {
   ISkinPartType getPartType();

   int getMarkerCount();

   Point3D getMarker(int var1);

   EnumFacing getMarkerSide(int var1);
}
