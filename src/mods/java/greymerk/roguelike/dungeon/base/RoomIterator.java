package greymerk.roguelike.dungeon.base;

import greymerk.roguelike.dungeon.rooms.RoomSetting;
import greymerk.roguelike.dungeon.settings.LevelSettings;
import greymerk.roguelike.util.WeightedRandomizer;
import greymerk.roguelike.worldgen.WorldEditor;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RoomIterator implements Iterator<BaseRoom> {
   private final LinkedList<BaseRoom> singleRooms;
   private final WeightedRandomizer<RoomSetting> randomRooms;
   private final LevelSettings levelSettings;
   private final WorldEditor worldEditor;

   public RoomIterator(LevelSettings levelSettings, WorldEditor worldEditor) {
      this.singleRooms = (LinkedList)levelSettings.getRooms().getSingleRoomSettings().stream().map((roomSetting) -> {
         return roomSetting.instantiate(levelSettings, worldEditor);
      }).collect(Collectors.toCollection(LinkedList::new));
      this.randomRooms = levelSettings.getRooms().getRandomRooms();
      this.levelSettings = levelSettings;
      this.worldEditor = worldEditor;
   }

   public BaseRoom getDungeonRoom() {
      if (this.hasNext()) {
         return this.next();
      } else {
         return this.randomRooms.isEmpty() ? RoomType.CORNER.newSingleRoomSetting().instantiate(this.levelSettings, this.worldEditor) : ((RoomSetting)this.randomRooms.get(this.worldEditor.getRandom())).instantiate(this.levelSettings, this.worldEditor);
      }
   }

   public boolean hasNext() {
      return !this.singleRooms.isEmpty();
   }

   public BaseRoom next() {
      return (BaseRoom)this.singleRooms.poll();
   }
}
