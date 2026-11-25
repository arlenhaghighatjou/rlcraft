package ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules;

import ivorius.reccomplex.nbt.NBTStringTypeRegistry;

public class MazeRuleRegistry extends NBTStringTypeRegistry<MazeRule> {
   public static final MazeRuleRegistry INSTANCE = new MazeRuleRegistry("rule", "type");

   public MazeRuleRegistry(String objectKey, String typeKey) {
      super(objectKey, typeKey);
   }

   public MazeRuleRegistry() {
   }
}
