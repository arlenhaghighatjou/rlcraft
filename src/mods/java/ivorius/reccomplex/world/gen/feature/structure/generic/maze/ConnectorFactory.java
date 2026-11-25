package ivorius.reccomplex.world.gen.feature.structure.generic.maze;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

public class ConnectorFactory {
   public static final String MALE_PREFIX = "Male:";
   public static final String FEMALE_PREFIX = "Female:";
   private final Map<String, Connector> connectors = new HashMap();

   public ConnectorFactory() {
   }

   public Connector get(String id) {
      Connector connector = (Connector)this.connectors.get(id);
      return connector != null ? connector : this.register(id);
   }

   public Connector register(String id) {
      Connector gendered = this.tryCreateGendered(id);
      if (gendered != null) {
         return gendered;
      } else {
         SimpleConnectors.Hermaphrodite hermaphrodite = new SimpleConnectors.Hermaphrodite(id);
         this.connectors.put(id, hermaphrodite);
         return hermaphrodite;
      }
   }

   public void put(String id, Connector connector) {
      this.connectors.put(id, connector);
   }

   protected Connector tryCreateGendered(String id) {
      String baseID = fromPrefix("Male:", id);
      if (baseID != null) {
         return this.createGendered(true, baseID);
      } else {
         baseID = fromPrefix("Female:", id);
         return baseID != null ? this.createGendered(false, baseID) : null;
      }
   }

   protected Connector createGendered(boolean returnMale, String baseID) {
      String male = "Male:" + baseID;
      String female = "Female:" + baseID;
      Pair<SimpleConnectors.Gendered, SimpleConnectors.Gendered> gendered = SimpleConnectors.Gendered.create(male, female);
      this.connectors.put(male, gendered.getLeft());
      this.connectors.put(female, gendered.getRight());
      return returnMale ? (Connector)gendered.getLeft() : (Connector)gendered.getRight();
   }

   private static String fromPrefix(String prefix, String id) {
      return id.startsWith(prefix) ? id.substring(prefix.length()) : null;
   }
}
