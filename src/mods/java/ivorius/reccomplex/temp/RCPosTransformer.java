package ivorius.reccomplex.temp;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.math.MinecraftTransforms;
import ivorius.ivtoolkit.transform.AreaTransformable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import org.apache.commons.lang3.tuple.Pair;

public class RCPosTransformer {
   public RCPosTransformer() {
   }

   public static void transformAdditionalData(TileEntity tileEntity, AxisAlignedTransform2D transform, int[] size) {
      if (tileEntity instanceof AreaTransformable) {
         ((AreaTransformable)tileEntity).transform(transform.getRotation(), transform.isMirrorX(), size);
      } else {
         Pair<Rotation, Mirror> mct = MinecraftTransforms.to(transform);
         tileEntity.mirror((Mirror)mct.getRight());
         tileEntity.rotate((Rotation)mct.getLeft());
      }

   }
}
