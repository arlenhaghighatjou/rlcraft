package net.ilexiconn.llibrary.server.structure;

import java.util.HashMap;

public enum StructureHandler {
   INSTANCE;

   private HashMap<String, StructureGenerator> map = new HashMap();

   private StructureHandler() {
   }

   public void registerStructure(String name, StructureGenerator generator) {
      this.map.put(name, generator);
   }

   public StructureBuilder createStructure(String name) {
      StructureBuilder builder = new StructureBuilder();
      this.registerStructure(name, builder);
      return builder;
   }

   public StructureGenerator getStructure(String name) {
      return (StructureGenerator)this.map.get(name);
   }
}
