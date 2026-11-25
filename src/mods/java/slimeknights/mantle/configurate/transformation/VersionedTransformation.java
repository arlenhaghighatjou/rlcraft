package slimeknights.mantle.configurate.transformation;

import java.util.SortedMap;
import java.util.Map.Entry;
import slimeknights.mantle.configurate.ConfigurationNode;

class VersionedTransformation extends ConfigurationTransformation {
   private final Object[] versionPath;
   private final SortedMap<Integer, ConfigurationTransformation> versionTransformations;

   VersionedTransformation(Object[] versionPath, SortedMap<Integer, ConfigurationTransformation> versionTransformations) {
      this.versionPath = versionPath;
      this.versionTransformations = versionTransformations;
   }

   public void apply(ConfigurationNode node) {
      ConfigurationNode versionNode = node.getNode(this.versionPath);
      int currentVersion = versionNode.getInt(-1);

      for(Entry<Integer, ConfigurationTransformation> entry : this.versionTransformations.entrySet()) {
         if (((Integer)entry.getKey()).intValue() > currentVersion) {
            ((ConfigurationTransformation)entry.getValue()).apply(node);
            currentVersion = ((Integer)entry.getKey()).intValue();
         }
      }

      versionNode.setValue(Integer.valueOf(currentVersion));
   }
}
