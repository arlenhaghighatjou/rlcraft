package ivorius.reccomplex.world.gen.feature.structure.generic.maze;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Connector {
   @Nonnull
   public final String id;

   public Connector(@Nonnull String id) {
      this.id = id;
   }

   public abstract boolean accepts(@Nullable Connector var1);

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Connector connector = (Connector)o;
         return this.id.equals(connector.id);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public String toString() {
      return this.id;
   }
}
