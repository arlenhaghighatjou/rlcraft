package moe.plushie.armourers_workshop.api.common.skin.type;

import moe.plushie.armourers_workshop.api.common.IPoint3D;
import moe.plushie.armourers_workshop.api.common.IRectangle3D;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinPart;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISkinPartType {
   String getRegistryName();

   String getPartName();

   IRectangle3D getBuildingSpace();

   IRectangle3D getGuideSpace();

   IPoint3D getOffset();

   @SideOnly(Side.CLIENT)
   void renderBuildingGuide(float var1, ISkinProperties var2, boolean var3);

   int getMinimumMarkersNeeded();

   int getMaximumMarkersNeeded();

   boolean isPartRequired();

   @SideOnly(Side.CLIENT)
   IPoint3D getItemRenderOffset();

   @SideOnly(Side.CLIENT)
   IRectangle3D getItemRenderTextureBounds();

   boolean isModelOverridden(ISkinProperties var1);

   boolean isOverlayOverridden(ISkinProperties var1);

   ISkinPart makeDummyPaintPart(int[] var1);
}
