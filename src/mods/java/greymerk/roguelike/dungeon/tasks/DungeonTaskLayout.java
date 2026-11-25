package greymerk.roguelike.dungeon.tasks;

import greymerk.roguelike.dungeon.Dungeon;
import greymerk.roguelike.dungeon.settings.DungeonSettings;
import greymerk.roguelike.worldgen.WorldEditor;

public class DungeonTaskLayout implements IDungeonTask {
   public DungeonTaskLayout() {
   }

   public void execute(WorldEditor editor, Dungeon dungeon, DungeonSettings settings) {
      dungeon.generateLayout(editor);
   }
}
