package greymerk.roguelike.monster.profiles;

import com.github.fnar.minecraft.Difficulty;
import com.github.fnar.minecraft.item.Arrow;
import com.github.fnar.util.Color;
import greymerk.roguelike.monster.Mob;
import greymerk.roguelike.monster.MonsterProfile;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class ProfileMagicArcher implements MonsterProfile {
   public ProfileMagicArcher() {
   }

   public Mob apply(Mob mob, int level, int difficulty, EaglercraftRandom random) {
      mob.equipBow(random, level, Difficulty.fromInt(difficulty));
      mob.equipArrows(Arrow.newRandomHarmful(random));
      mob.equipArmor(random, level, Color.DEEP_VIOLET, difficulty);
      return mob;
   }
}
