package pl.asie.foamfix.ghostbuster;

public class GhostBusterDefinition {
   public final String obfMethodName;
   public final String deobfMethodName;
   public final int accessPos;
   public final int posPos;
   public final int radius;
   public final Object returnValue;

   public GhostBusterDefinition(String obfMethodName, String deobfMethodName, int accessPos, int posPos, int radius) {
      this(obfMethodName, deobfMethodName, accessPos, posPos, radius, (Object)null);
   }

   public GhostBusterDefinition(String obfMethodName, String deobfMethodName, int accessPos, int posPos, int radius, Object returnValue) {
      this.obfMethodName = obfMethodName;
      this.deobfMethodName = deobfMethodName;
      this.accessPos = accessPos;
      this.posPos = posPos;
      this.radius = radius;
      this.returnValue = returnValue;
   }

   public static GhostBusterDefinition updateTick(int radius) {
      return new GhostBusterDefinition("updateTick", "updateTick", 1, 2, radius);
   }

   public static GhostBusterDefinition neighborChanged() {
      return new GhostBusterDefinition("neighborChanged", "neighborChanged", 2, 3, 1);
   }
}
