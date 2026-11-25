package slimeknights.mantle.typesafe.config.impl;

import java.util.Collection;
import slimeknights.mantle.typesafe.config.parser.ConfigNode;

abstract class AbstractConfigNode implements ConfigNode {
   AbstractConfigNode() {
   }

   abstract Collection<Token> tokens();

   public final String render() {
      StringBuilder origText = new StringBuilder();

      for(Token t : this.tokens()) {
         origText.append(t.tokenText());
      }

      return origText.toString();
   }

   public final boolean equals(Object other) {
      return other instanceof AbstractConfigNode && this.render().equals(((AbstractConfigNode)other).render());
   }

   public final int hashCode() {
      return this.render().hashCode();
   }
}
