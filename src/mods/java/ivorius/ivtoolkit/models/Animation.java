package ivorius.ivtoolkit.models;

import java.util.ArrayList;
import java.util.List;

public class Animation {
   public String id;
   public float duration;
   public final List<NodeAnimation> nodeAnimations = new ArrayList();

   public Animation() {
   }
}
