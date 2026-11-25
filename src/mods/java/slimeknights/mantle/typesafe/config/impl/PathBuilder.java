package slimeknights.mantle.typesafe.config.impl;

import java.util.Stack;
import slimeknights.mantle.typesafe.config.ConfigException;

final class PathBuilder {
   private final Stack<String> keys = new Stack();
   private Path result;

   PathBuilder() {
   }

   private void checkCanAppend() {
      if (this.result != null) {
         throw new ConfigException.BugOrBroken("Adding to PathBuilder after getting result");
      }
   }

   void appendKey(String key) {
      this.checkCanAppend();
      this.keys.push(key);
   }

   void appendPath(Path path) {
      this.checkCanAppend();
      String first = path.first();
      Path remainder = path.remainder();

      while(true) {
         this.keys.push(first);
         if (remainder == null) {
            return;
         }

         first = remainder.first();
         remainder = remainder.remainder();
      }
   }

   Path result() {
      if (this.result == null) {
         Path remainder;
         String key;
         for(remainder = null; !this.keys.isEmpty(); remainder = new Path(key, remainder)) {
            key = (String)this.keys.pop();
         }

         this.result = remainder;
      }

      return this.result;
   }
}
