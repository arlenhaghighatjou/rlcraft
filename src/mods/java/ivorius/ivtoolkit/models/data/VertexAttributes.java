package ivorius.ivtoolkit.models.data;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class VertexAttributes implements Iterable<VertexAttribute> {
   private final VertexAttribute[] attributes;
   public final int vertexSize;
   private long mask = -1L;
   private VertexAttributes.ReadonlyIterable<VertexAttribute> iterable;

   public VertexAttributes(VertexAttribute... attributes) {
      if (attributes.length == 0) {
         throw new IllegalArgumentException("attributes must be >= 1");
      } else {
         VertexAttribute[] list = new VertexAttribute[attributes.length];
         System.arraycopy(attributes, 0, list, 0, attributes.length);
         this.attributes = list;
         this.vertexSize = this.calculateOffsets();
      }
   }

   public int getOffset(int usage) {
      VertexAttribute vertexAttribute = this.findByUsage(usage);
      return vertexAttribute == null ? 0 : vertexAttribute.offset / 4;
   }

   public VertexAttribute findByUsage(int usage) {
      int len = this.size();

      for(int i = 0; i < len; ++i) {
         if (this.get(i).usage == usage) {
            return this.get(i);
         }
      }

      return null;
   }

   public VertexAttribute findByUsageAndUnit(int usage, int unit) {
      int len = this.size();

      for(int i = 0; i < len; ++i) {
         if (this.get(i).usage == usage && this.get(i).unit == unit) {
            return this.get(i);
         }
      }

      return null;
   }

   private int calculateOffsets() {
      int count = 0;

      for(VertexAttribute attribute : this.attributes) {
         attribute.offset = count;
         if (attribute.usage == 4) {
            count += 4;
         } else {
            count += 4 * attribute.numComponents;
         }
      }

      return count;
   }

   public int size() {
      return this.attributes.length;
   }

   public VertexAttribute get(int index) {
      return this.attributes[index];
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("[");

      for(VertexAttribute attribute : this.attributes) {
         builder.append("(");
         builder.append(attribute.usage);
         builder.append(", ");
         builder.append(attribute.numComponents);
         builder.append(", ");
         builder.append(attribute.offset);
         builder.append(")");
         builder.append("\n");
      }

      builder.append("]");
      return builder.toString();
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof VertexAttributes)) {
         return false;
      } else {
         VertexAttributes other = (VertexAttributes)obj;
         if (this.attributes.length != other.size()) {
            return false;
         } else {
            for(int i = 0; i < this.attributes.length; ++i) {
               if (!this.attributes[i].equals(other.attributes[i])) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public long getMask() {
      if (this.mask == -1L) {
         long result = 0L;

         for(VertexAttribute attribute : this.attributes) {
            result |= (long)attribute.usage;
         }

         this.mask = result;
      }

      return this.mask;
   }

   public Iterator<VertexAttribute> iterator() {
      if (this.iterable == null) {
         this.iterable = new VertexAttributes.ReadonlyIterable<VertexAttribute>(this.attributes);
      }

      return this.iterable.iterator();
   }

   private static class ReadonlyIterable<T> implements Iterable<T> {
      private final T[] array;
      private VertexAttributes.ReadonlyIterator iterator1;
      private VertexAttributes.ReadonlyIterator iterator2;

      public ReadonlyIterable(T[] array) {
         this.array = array;
      }

      public Iterator<T> iterator() {
         if (this.iterator1 == null) {
            this.iterator1 = new VertexAttributes.ReadonlyIterator(this.array);
            this.iterator2 = new VertexAttributes.ReadonlyIterator(this.array);
         }

         if (!this.iterator1.valid) {
            this.iterator1.index = 0;
            this.iterator1.valid = true;
            this.iterator2.valid = false;
            return this.iterator1;
         } else {
            this.iterator2.index = 0;
            this.iterator2.valid = true;
            this.iterator1.valid = false;
            return this.iterator2;
         }
      }
   }

   private static class ReadonlyIterator<T> implements Iterator<T>, Iterable<T> {
      private final T[] array;
      int index;
      boolean valid = true;

      public ReadonlyIterator(T[] array) {
         this.array = array;
      }

      public boolean hasNext() {
         if (!this.valid) {
            throw new RuntimeException("#iterator() cannot be used nested.");
         } else {
            return this.index < this.array.length;
         }
      }

      public T next() {
         if (this.index >= this.array.length) {
            throw new NoSuchElementException(String.valueOf(this.index));
         } else if (!this.valid) {
            throw new RuntimeException("#iterator() cannot be used nested.");
         } else {
            return (T)this.array[this.index++];
         }
      }

      public void remove() {
         throw new RuntimeException("Remove not allowed.");
      }

      public void reset() {
         this.index = 0;
      }

      public Iterator<T> iterator() {
         return this;
      }
   }

   public static final class Usage {
      public static final int Position = 1;
      /** @deprecated */
      @Deprecated
      public static final int Color = 2;
      public static final int ColorUnpacked = 2;
      public static final int ColorPacked = 4;
      public static final int Normal = 8;
      public static final int TextureCoordinates = 16;
      public static final int Generic = 32;
      public static final int BoneWeight = 64;
      public static final int Tangent = 128;
      public static final int BiNormal = 256;

      public Usage() {
      }
   }
}
