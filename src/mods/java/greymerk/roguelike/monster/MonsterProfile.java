package greymerk.roguelike.monster;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public interface MonsterProfile {
   Mob apply(Mob var1, int var2, int var3, EaglercraftRandom var4);
}
