package ivorius.ivtoolkit.models;

import ivorius.ivtoolkit.models.utils.MatrixMathUtils;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class Node {
   public String id;
   public Node parent;
   public final List<Node> children = new ArrayList(2);
   public boolean isAnimated;
   public final Vector3f translation = new Vector3f();
   public final Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   public final Vector3f scale = new Vector3f(1.0F, 1.0F, 1.0F);
   public final Matrix4f localTransform = new Matrix4f();
   public final Matrix4f globalTransform = new Matrix4f();
   public List<NodePart> parts = new ArrayList(2);

   public Node() {
   }

   public void calculateLocalTransform() {
      MatrixMathUtils.setTRS(this.localTransform, this.translation, this.rotation, this.scale);
   }

   public void calculateGlobalTransform() {
      this.globalTransform.load(this.localTransform);
      if (this.parent != null) {
         Matrix4f.mul(this.parent.globalTransform, this.globalTransform, this.globalTransform);
      }

   }

   public void calculateTransforms(boolean recursive) {
      this.calculateLocalTransform();
      this.calculateGlobalTransform();
      if (recursive) {
         for(Node child : this.children) {
            child.calculateTransforms(true);
         }
      }

   }

   public void calculateBoneTransforms(boolean recursive) {
      for(NodePart part : this.parts) {
         if (part.invBoneBindTransforms != null && part.bones != null && part.invBoneBindTransforms.size == part.bones.length) {
            int n = part.invBoneBindTransforms.size;

            for(int i = 0; i < n; ++i) {
               part.bones[i].load(((Node[])part.invBoneBindTransforms.keys)[i].globalTransform);
               Matrix4f.mul(part.bones[i], ((Matrix4f[])part.invBoneBindTransforms.values)[i], part.bones[i]);
            }
         }
      }

      if (recursive) {
         for(Node child : this.children) {
            child.calculateBoneTransforms(true);
         }
      }

   }
}
