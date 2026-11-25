package ivorius.ivtoolkit.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NodeAnimation implements Comparator<NodeKeyframe> {
   public Node node;
   public List<NodeKeyframe> keyframes = new ArrayList();

   public NodeAnimation() {
   }

   public void addKeyframe(NodeKeyframe keyframe) {
      this.keyframes.add(keyframe);
      Collections.sort(this.keyframes, this);
   }

   public int compare(NodeKeyframe o1, NodeKeyframe o2) {
      return Float.compare(o1.keytime, o2.keytime);
   }
}
