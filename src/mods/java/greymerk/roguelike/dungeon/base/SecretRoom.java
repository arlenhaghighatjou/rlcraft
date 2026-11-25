package greymerk.roguelike.dungeon.base;

import com.github.fnar.minecraft.block.SingleBlockBrush;
import com.google.common.collect.Lists;
import greymerk.roguelike.dungeon.rooms.RoomSetting;
import greymerk.roguelike.dungeon.settings.LevelSettings;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.Direction;
import greymerk.roguelike.worldgen.WorldEditor;
import greymerk.roguelike.worldgen.shapes.RectSolid;
import java.util.List;

public class SecretRoom extends BaseRoom {
   private final BaseRoom prototype;

   public SecretRoom(RoomSetting roomSetting, LevelSettings levelSettings, WorldEditor worldEditor) {
      super(roomSetting, levelSettings, worldEditor);
      this.prototype = this.getRoomSetting().instantiate(this.levelSettings, this.worldEditor);
      this.wallDist = this.prototype.getWallDist();
   }

   public boolean isValidLocation(Coord at, Direction facing) {
      if (this.getRoomSetting().getCount() <= 0) {
         return false;
      } else {
         int connectionLength = 5;
         Coord cursor = at.copy().translate(facing, this.prototype.getWallDist() + connectionLength);
         return this.prototype.isValidLocation(cursor, facing.reverse());
      }
   }

   public BaseRoom generate(Coord at, List<Direction> entrances) {
      Direction entrance = this.getEntrance(entrances);
      int inset = this.prototype.getWallDist() + 5;
      Coord secretRoomOrigin = at.copy().translate(entrance, inset);
      RectSolid hallwayWallsRect = RectSolid.newRect(at.copy().translate(entrance.left()).translate(entrance, 2).down(), at.copy().translate(entrance.right()).translate(entrance, inset).up(2));
      this.primaryWallBrush().fill(this.worldEditor, hallwayWallsRect, false, true);
      RectSolid hallwayHollowRect = RectSolid.newRect(at, secretRoomOrigin.copy().up());
      SingleBlockBrush.AIR.fill(this.worldEditor, hallwayHollowRect);
      return this.prototype.generate(secretRoomOrigin, Lists.newArrayList(new Direction[]{entrance.reverse()}));
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SecretRoom)) {
         return false;
      } else {
         SecretRoom other = (SecretRoom)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (!super.equals(o)) {
            return false;
         } else {
            Object this$prototype = this.prototype;
            Object other$prototype = other.prototype;
            if (this$prototype == null) {
               if (other$prototype != null) {
                  return false;
               }
            } else if (!this$prototype.equals(other$prototype)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof SecretRoom;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = super.hashCode();
      Object $prototype = this.prototype;
      result = result * 59 + ($prototype == null ? 43 : $prototype.hashCode());
      return result;
   }

   public String toString() {
      return "SecretRoom(prototype=" + this.prototype + ")";
   }
}
