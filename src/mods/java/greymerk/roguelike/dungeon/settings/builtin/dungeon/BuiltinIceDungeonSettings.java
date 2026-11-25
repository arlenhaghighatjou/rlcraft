package greymerk.roguelike.dungeon.settings.builtin.dungeon;

import com.google.common.collect.Lists;
import greymerk.roguelike.dungeon.settings.DungeonSettings;
import greymerk.roguelike.dungeon.settings.SettingIdentifier;
import greymerk.roguelike.dungeon.settings.TowerSettings;
import greymerk.roguelike.dungeon.settings.builtin.BuiltinBaseSettings;
import greymerk.roguelike.dungeon.towers.TowerType;
import greymerk.roguelike.theme.Themes;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BuiltinIceDungeonSettings extends DungeonSettings {
   public static final SettingIdentifier ID = new SettingIdentifier("builtin", "ice");

   public BuiltinIceDungeonSettings() {
      super(ID);
      this.setExclusive(true);
      this.getInherit().add(BuiltinBaseSettings.ID);
      this.getCriteria().setBiomeTypes(Lists.newArrayList(new Type[]{Type.SNOWY}));
      this.setTowerSettings(new TowerSettings(TowerType.PYRAMID, Themes.ICE));
      IntStream.range(0, 5).mapToObj(this::getLevelSettings).forEach((levelSettings) -> {
         levelSettings.setTheme(Themes.ICE);
      });
   }
}
