package greymerk.roguelike.dungeon.settings;

import greymerk.roguelike.dungeon.base.RoomType;
import greymerk.roguelike.dungeon.base.RoomsSetting;
import greymerk.roguelike.dungeon.base.SecretsSetting;
import greymerk.roguelike.dungeon.layout.LayoutGenerator;
import greymerk.roguelike.dungeon.segment.Segment;
import greymerk.roguelike.dungeon.segment.SegmentGenerator;
import greymerk.roguelike.dungeon.towers.TowerType;
import greymerk.roguelike.theme.Themes;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestDungeonSettings extends DungeonSettings {
   public static final SettingIdentifier ID = new SettingIdentifier("test", "dungeon");
   private final Random random;

   public TestDungeonSettings(Random random) {
      super(ID);
      this.random = random;
      this.setExclusive(true);
      this.setTowerSettings(new TowerSettings(TowerType.random(random), Themes.random(random)));
      IntStream.range(0, 5).forEach(this::generateLevelSettings);
   }

   private void generateLevelSettings(int i) {
      LevelSettings levelSettings = this.getLevelSettings(i);
      levelSettings.setGenerator(i % 2 == 0 ? LayoutGenerator.Type.CLASSIC : LayoutGenerator.Type.MST);
      levelSettings.setRange(60);
      levelSettings.setScatter(15);
      levelSettings.setTheme(Themes.random(this.random));
      Stream var10000 = Arrays.stream(RoomType.values()).map(RoomType::newSingleRoomSetting);
      RoomsSetting var10001 = levelSettings.getRooms();
      var10000.forEach(var10001::add);
      var10000 = Arrays.stream(RoomType.values()).map(RoomType::newSingleRoomSetting);
      SecretsSetting var5 = levelSettings.getSecrets();
      var10000.forEach(var5::add);
      var10000 = Arrays.stream(Segment.values()).map((segment) -> {
         return (new SegmentGenerator()).with(segment, 1);
      });
      SegmentGenerator var6 = levelSettings.getSegments();
      var10000.forEach(var6::add);
   }
}
