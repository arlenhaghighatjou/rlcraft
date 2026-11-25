package ivorius.ivtoolkit.models.loaders.g3d;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class G3DBone {
   public String node;
   public Vector3f translation;
   public Quaternion rotation;
   public Vector3f scale;

   public G3DBone() {
   }
}
