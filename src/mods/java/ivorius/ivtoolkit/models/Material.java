package ivorius.ivtoolkit.models;

import java.util.List;

public class Material extends Attributes {
   private static int counter = 0;
   public String id;

   public Material() {
      this("mtl" + ++counter);
   }

   public Material(String id) {
      this.id = id;
   }

   public Material(Attribute... attributes) {
      this();
      this.set(attributes);
   }

   public Material(String id, Attribute... attributes) {
      this(id);
      this.set(attributes);
   }

   public Material(List<Attribute> attributes) {
      this();
      this.set(attributes);
   }

   public Material(String id, List<Attribute> attributes) {
      this(id);
      this.set(attributes);
   }

   public Material(Material copyFrom) {
      this(copyFrom.id, copyFrom);
   }

   public Material(String id, Material copyFrom) {
      this(id);

      for(Attribute attr : copyFrom) {
         this.set(attr.copy());
      }

   }

   public final Material copy() {
      return new Material(this);
   }

   public int hashCode() {
      return super.hashCode() + 3 * this.id.hashCode();
   }
}
