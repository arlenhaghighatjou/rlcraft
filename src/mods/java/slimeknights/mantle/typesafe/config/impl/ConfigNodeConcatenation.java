package slimeknights.mantle.typesafe.config.impl;

import java.util.Collection;

final class ConfigNodeConcatenation extends ConfigNodeComplexValue {
   ConfigNodeConcatenation(Collection<AbstractConfigNode> children) {
      super(children);
   }

   protected ConfigNodeConcatenation newNode(Collection<AbstractConfigNode> nodes) {
      return new ConfigNodeConcatenation(nodes);
   }
}
