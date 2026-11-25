package ivorius.ivtoolkit.models.loaders.g3d;

import java.awt.Color;

public class G3DMaterial {
   public String id;
   public String type;
   public Color ambient;
   public Color diffuse;
   public Color specular;
   public Color emissive;
   public Color reflection;
   public float shininess;
   public float opacity = 1.0F;
   public G3DTexture[] textures;

   public G3DMaterial() {
   }
}
