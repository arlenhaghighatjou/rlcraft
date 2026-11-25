package ivorius.ivtoolkit.models.data;

public final class VertexAttribute {
   public final int usage;
   public final int numComponents;
   public final boolean normalized;
   public final int type;
   public int offset;
   public int unit;
   private final int usageIndex;

   public VertexAttribute(int usage, int numComponents) {
      this(usage, numComponents, 0);
   }

   public VertexAttribute(int usage, int numComponents, int index) {
      this(usage, numComponents, usage == 4 ? 5121 : 5126, usage == 4, index);
   }

   private VertexAttribute(int usage, int numComponents, int type, boolean normalized) {
      this(usage, numComponents, type, normalized, 0);
   }

   private VertexAttribute(int usage, int numComponents, int type, boolean normalized, int index) {
      this.usage = usage;
      this.numComponents = numComponents;
      this.type = type;
      this.normalized = normalized;
      this.unit = index;
      this.usageIndex = Integer.numberOfTrailingZeros(usage);
   }

   public static VertexAttribute Position() {
      return new VertexAttribute(1, 3);
   }

   public static VertexAttribute TexCoords(int unit) {
      return new VertexAttribute(16, 2, unit);
   }

   public static VertexAttribute Normal() {
      return new VertexAttribute(8, 3);
   }

   public static VertexAttribute ColorPacked() {
      return new VertexAttribute(4, 4, 5121, true);
   }

   public static VertexAttribute ColorUnpacked() {
      return new VertexAttribute(2, 4, 5126, false);
   }

   public static VertexAttribute Tangent() {
      return new VertexAttribute(128, 3);
   }

   public static VertexAttribute Binormal() {
      return new VertexAttribute(256, 3);
   }

   public static VertexAttribute BoneWeight(int unit) {
      return new VertexAttribute(64, 2, unit);
   }

   public int getKey() {
      return (this.usageIndex << 8) + (this.unit & 255);
   }
}
