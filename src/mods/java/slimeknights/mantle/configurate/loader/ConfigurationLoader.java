package slimeknights.mantle.configurate.loader;

import java.io.IOException;
import slimeknights.mantle.configurate.ConfigurationNode;
import slimeknights.mantle.configurate.ConfigurationOptions;

public interface ConfigurationLoader<NodeType extends ConfigurationNode> {
   ConfigurationOptions getDefaultOptions();

   default NodeType load() throws IOException {
      return (NodeType)this.load(this.getDefaultOptions());
   }

   NodeType load(ConfigurationOptions var1) throws IOException;

   void save(ConfigurationNode var1) throws IOException;

   default NodeType createEmptyNode() {
      return (NodeType)this.createEmptyNode(this.getDefaultOptions());
   }

   NodeType createEmptyNode(ConfigurationOptions var1);
}
