package com.creativemd.creativecore.client.rendering.face;

import com.creativemd.creativecore.common.utils.math.vec.VectorFan;
import java.util.List;

public interface IFaceRenderType {
   boolean shouldRender();

   boolean isOutside();

   boolean hasCachedFans();

   List<VectorFan> getCachedFans();

   float getScale();
}
