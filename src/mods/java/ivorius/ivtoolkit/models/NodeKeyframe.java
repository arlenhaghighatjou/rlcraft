package ivorius.ivtoolkit.models;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class NodeKeyframe {
   public float keytime;
   public Vector3f translation;
   public Vector3f scale;
   public Quaternion rotation;

   public NodeKeyframe() {
   }
}
