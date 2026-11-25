package moe.plushie.armourers_workshop.api.common.skin.type;

import java.awt.Point;
import moe.plushie.armourers_workshop.api.common.IPoint3D;

public interface ISkinPartTypeTextured extends ISkinPartType {
   Point getTextureSkinPos();

   boolean isTextureMirrored();

   Point getTextureBasePos();

   Point getTextureOverlayPos();

   IPoint3D getTextureModelSize();
}
