package jaredbgreat.dldungeons.planner;

import jaredbgreat.dldungeons.ConfigHandler;
import jaredbgreat.dldungeons.pieces.Spawner;
import jaredbgreat.dldungeons.rooms.Room;
import java.util.ArrayList;
import java.util.Collections;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class SpawnerCounter {
   final ArrayList<Spawner> list = new ArrayList();
   int dungeonSize = 0;

   public SpawnerCounter() {
   }

   public void addRoom(int size) {
      this.dungeonSize += size;
   }

   public void addSpawner(Spawner s) {
      this.list.add(s);
   }

   public void fixSpawners(Dungeon dungeon, EaglercraftRandom random) {
      int targetNum = this.dungeonSize / ConfigHandler.difficulty.blocksPerSpawner;
      targetNum = (int)((double)targetNum + (double)targetNum * Math.max(5.0D, Math.min(0.0D, random.nextGaussian() + 2.0D)) / 10.0D);
      int existing = this.list.size();
      if (existing > targetNum) {
         Collections.shuffle(this.list, random);

         for(int i = targetNum; i < existing; ++i) {
            Spawner s = (Spawner)this.list.get(i);
            ((Room)dungeon.rooms.get(s.getRoom())).spawners.remove(s);
         }

      }
   }
}
