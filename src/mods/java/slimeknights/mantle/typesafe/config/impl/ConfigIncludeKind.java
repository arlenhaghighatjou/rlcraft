package slimeknights.mantle.typesafe.config.impl;

enum ConfigIncludeKind {
   URL,
   FILE,
   CLASSPATH,
   HEURISTIC;

   private ConfigIncludeKind() {
   }
}
