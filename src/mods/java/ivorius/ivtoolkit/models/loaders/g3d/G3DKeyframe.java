package ivorius.ivtoolkit.models.loaders.g3d;

import org.lwjgl.util.vector.Quaternion;
import net.lax1dude.eaglercraft.v1_8.vector.Vector3f;

public class G3DKeyframe {
   public float keytime;
   public Quaternion rotation;
   public Vector3f translation;
   public Vector3f scale;

   public G3DKeyframe() {
   }
}
