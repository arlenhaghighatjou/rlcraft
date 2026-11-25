package greymerk.roguelike.monster.profiles;

import com.github.fnar.minecraft.Difficulty;
import com.github.fnar.util.Color;
import greymerk.roguelike.monster.Mob;
import greymerk.roguelike.monster.MonsterProfile;
import greymerk.roguelike.treasure.loot.provider.ItemNovelty;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class ProfileSwordsman implements MonsterProfile {
   public ProfileSwordsman() {
   }

   public Mob apply(Mob mob, int level, int difficulty, EaglercraftRandom random) {
      if (random.nextDouble() < 0.05D) {
         mob.equipMainhand(ItemNovelty.valandrahsKiss());
      } else {
         mob.equipSword(random, level, Difficulty.fromInt(difficulty));
      }

      mob.equipShield(random);
      mob.equipArmor(random, level, Color.random(random), difficulty);
      return mob;
   }
}
