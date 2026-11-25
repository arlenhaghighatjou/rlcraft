package ivorius.reccomplex.world.gen.feature.structure.generic.maze;

import javax.annotation.Nonnull;
import org.apache.commons.lang3.tuple.Pair;

public class SimpleConnectors {
   public SimpleConnectors() {
   }

   public static class Gendered extends Connector {
      private SimpleConnectors.Gendered partner;

      private Gendered(@Nonnull String id) {
         super(id);
      }

      public static Pair<SimpleConnectors.Gendered, SimpleConnectors.Gendered> create(String id1, String id2) {
         SimpleConnectors.Gendered one = new SimpleConnectors.Gendered(id1);
         SimpleConnectors.Gendered two = new SimpleConnectors.Gendered(id2);
         one.partner = two;
         two.partner = one;
         return Pair.of(one, two);
      }

      public SimpleConnectors.Gendered getPartner() {
         return this.partner;
      }

      public boolean accepts(Connector c) {
         return c == null || c == this.partner;
      }
   }

   public static class Hermaphrodite extends Connector {
      public Hermaphrodite(@Nonnull String id) {
         super(id);
      }

      public boolean accepts(Connector c) {
         return c == null || c == this;
      }
   }

   public static class Leaf extends Connector {
      public Leaf(@Nonnull String id) {
         super(id);
      }

      public boolean accepts(Connector c) {
         return c == null;
      }
   }
}
