package slimeknights.mantle.typesafe.config;

public enum ConfigValueType {
   OBJECT,
   LIST,
   NUMBER,
   BOOLEAN,
   NULL,
   STRING;

   private ConfigValueType() {
   }
}
