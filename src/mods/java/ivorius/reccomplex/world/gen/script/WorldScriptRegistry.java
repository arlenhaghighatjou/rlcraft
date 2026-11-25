package ivorius.reccomplex.world.gen.script;

import ivorius.reccomplex.nbt.NBTStringTypeRegistry;

public class WorldScriptRegistry extends NBTStringTypeRegistry<WorldScript> {
   public static final WorldScriptRegistry INSTANCE = new WorldScriptRegistry("script", "id");

   public WorldScriptRegistry(String objectKey, String typeKey) {
      super(objectKey, typeKey);
   }

   public WorldScriptRegistry() {
   }
}
