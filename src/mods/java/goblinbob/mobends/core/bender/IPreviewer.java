package goblinbob.mobends.core.bender;

import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.math.vector.IVec3fRead;
import goblinbob.mobends.core.math.vector.Vec3f;
import java.util.Map;

public interface IPreviewer<D extends EntityData<?>> {
   void prePreview(D var1, String var2);

   void postPreview(D var1, String var2);

   default IVec3fRead getAnchorPoint() {
      return Vec3f.ZERO;
   }

   Map<String, BoneMetadata> getBoneMetadata();
}
