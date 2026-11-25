package greymerk.roguelike.util;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public interface IWeighted<T> {
   int getWeight();

   T get(EaglercraftRandom var1);
}
