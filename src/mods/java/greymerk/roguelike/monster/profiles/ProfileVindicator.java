package greymerk.roguelike.monster.profiles;

import com.github.fnar.minecraft.block.spawner.MobType;
import com.github.fnar.roguelike.loot.special.tools.SpecialAxe;
import greymerk.roguelike.monster.Mob;
import greymerk.roguelike.monster.MonsterProfile;
import greymerk.roguelike.treasure.loot.Equipment;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class ProfileVindicator implements MonsterProfile {
   public ProfileVindicator() {
   }

   public Mob apply(Mob mob, int level, int difficulty, EaglercraftRandom random) {
      mob.setMobType(MobType.VINDICATOR);
      mob.equipMainhand((new SpecialAxe(random, Equipment.rollQuality(random, level))).asStack());
      return mob;
   }
}
