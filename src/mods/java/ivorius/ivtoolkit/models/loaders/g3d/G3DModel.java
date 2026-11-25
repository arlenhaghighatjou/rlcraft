package ivorius.ivtoolkit.models.loaders.g3d;

public class G3DModel {
   public short[] version = new short[2];
   public String id;
   public G3DMesh[] meshes;
   public G3DMaterial[] materials;
   public G3DNode[] nodes;
   public G3DAnimation[] animations;

   public G3DModel() {
   }
}
