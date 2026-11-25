package ivorius.reccomplex.world.gen.feature.structure.generic.maze;

public class SavedConnector {
   public String id;

   public SavedConnector(String id) {
      this.id = id;
   }

   public Connector toConnector(ConnectorFactory factory) {
      return factory.get(this.id);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         boolean var10000;
         label35: {
            SavedConnector that = (SavedConnector)o;
            if (this.id != null) {
               if (this.id.equals(that.id)) {
                  break label35;
               }
            } else if (that.id == null) {
               break label35;
            }

            var10000 = false;
            return var10000;
         }

         var10000 = true;
         return var10000;
      } else {
         return false;
      }
   }

   public SavedConnector copy() {
      return new SavedConnector(this.id);
   }

   public int hashCode() {
      return this.id != null ? this.id.hashCode() : 0;
   }

   public String toString() {
      return "SavedConnector{id='" + this.id + '\'' + '}';
   }
}
