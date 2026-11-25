package ivorius.ivtoolkit.models;

import ivorius.ivtoolkit.models.data.Disposable;
import java.util.ArrayList;
import java.util.List;

public class Model {
   public final List<Material> materials = new ArrayList();
   public final List<Node> nodes = new ArrayList();
   public final List<Animation> animations = new ArrayList();
   public final List<Mesh> meshes = new ArrayList();
   public final List<MeshPart> meshParts = new ArrayList();
   public final List<Disposable> disposables = new ArrayList();

   public Model() {
   }

   public Animation animationForID(String id) {
      for(Animation animation : this.animations) {
         if (animation.id.equals(id)) {
            return animation;
         }
      }

      return null;
   }

   public void calculateTransforms() {
      for(Node node : this.nodes) {
         node.calculateTransforms(true);
      }

      for(Node node : this.nodes) {
         node.calculateBoneTransforms(true);
      }

   }
}
