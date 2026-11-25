package ivorius.ivtoolkit.models.loaders.g3d;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class G3DNode {
   public String id;
   public Vector3f translation;
   public Quaternion rotation;
   public Vector3f scale;
   public G3DNodePart[] parts;
   public G3DNode[] children;

   public G3DNode() {
   }
}
