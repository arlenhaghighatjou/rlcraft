package greymerk.roguelike.monster.profiles;

import com.github.fnar.minecraft.block.spawner.MobType;
import greymerk.roguelike.monster.Mob;
import greymerk.roguelike.monster.MonsterProfile;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class ProfileWitch implements MonsterProfile {
   public ProfileWitch() {
   }

   public Mob apply(Mob mob, int level, int difficulty, EaglercraftRandom rand) {
      mob.setMobType(MobType.WITCH);
      return mob;
   }
}
