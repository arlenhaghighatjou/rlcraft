package ivorius.ivtoolkit.models;

import ivorius.ivtoolkit.models.utils.ArrayMap;
import net.lax1dude.eaglercraft.v1_8.vector.Matrix4f;

public class NodePart {
   public MeshPart meshPart;
   public Material material;
   public ArrayMap<Node, Matrix4f> invBoneBindTransforms;
   public Matrix4f[] bones;
   public boolean enabled = true;

   public NodePart() {
   }
}
