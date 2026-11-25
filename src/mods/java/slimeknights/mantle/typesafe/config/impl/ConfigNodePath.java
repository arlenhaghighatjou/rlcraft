package slimeknights.mantle.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import slimeknights.mantle.typesafe.config.ConfigException;

final class ConfigNodePath extends AbstractConfigNode {
   private final Path path;
   final ArrayList<Token> tokens;

   ConfigNodePath(Path path, Collection<Token> tokens) {
      this.path = path;
      this.tokens = new ArrayList(tokens);
   }

   protected Collection<Token> tokens() {
      return this.tokens;
   }

   protected Path value() {
      return this.path;
   }

   protected ConfigNodePath subPath(int toRemove) {
      int periodCount = 0;
      ArrayList<Token> tokensCopy = new ArrayList(this.tokens);

      for(int i = 0; i < tokensCopy.size(); ++i) {
         if (Tokens.isUnquotedText((Token)tokensCopy.get(i)) && ((Token)tokensCopy.get(i)).tokenText().equals(".")) {
            ++periodCount;
         }

         if (periodCount == toRemove) {
            return new ConfigNodePath(this.path.subPath(toRemove), tokensCopy.subList(i + 1, tokensCopy.size()));
         }
      }

      throw new ConfigException.BugOrBroken("Tried to remove too many elements from a Path node");
   }

   protected ConfigNodePath first() {
      ArrayList<Token> tokensCopy = new ArrayList(this.tokens);

      for(int i = 0; i < tokensCopy.size(); ++i) {
         if (Tokens.isUnquotedText((Token)tokensCopy.get(i)) && ((Token)tokensCopy.get(i)).tokenText().equals(".")) {
            return new ConfigNodePath(this.path.subPath(0, 1), tokensCopy.subList(0, i));
         }
      }

      return this;
   }
}
