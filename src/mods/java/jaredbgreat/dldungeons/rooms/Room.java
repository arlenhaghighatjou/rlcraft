package jaredbgreat.dldungeons.rooms;

import jaredbgreat.dldungeons.ConfigHandler;
import jaredbgreat.dldungeons.Difficulty;
import jaredbgreat.dldungeons.pieces.Doorway;
import jaredbgreat.dldungeons.pieces.Shapes;
import jaredbgreat.dldungeons.pieces.Spawner;
import jaredbgreat.dldungeons.pieces.chests.BasicChest;
import jaredbgreat.dldungeons.pieces.chests.TreasureChest;
import jaredbgreat.dldungeons.pieces.chests.WeakChest;
import jaredbgreat.dldungeons.pieces.entrances.AbstractEntrance;
import jaredbgreat.dldungeons.pieces.entrances.SimpleEntrance;
import jaredbgreat.dldungeons.pieces.entrances.SpiralStair;
import jaredbgreat.dldungeons.pieces.entrances.TopRoom;
import jaredbgreat.dldungeons.planner.Dungeon;
import jaredbgreat.dldungeons.planner.RoomSeed;
import jaredbgreat.dldungeons.planner.Route;
import jaredbgreat.dldungeons.planner.Symmetry;
import jaredbgreat.dldungeons.planner.astar.DoorQueue;
import jaredbgreat.dldungeons.planner.features.Cutout;
import jaredbgreat.dldungeons.planner.features.Depression;
import jaredbgreat.dldungeons.planner.features.FeatureAdder;
import jaredbgreat.dldungeons.planner.features.IslandPlatform;
import jaredbgreat.dldungeons.planner.features.IslandRoom;
import jaredbgreat.dldungeons.planner.features.Pillar;
import jaredbgreat.dldungeons.planner.features.Pool;
import jaredbgreat.dldungeons.themes.ThemeFlags;
import jaredbgreat.dldungeons.themes.ThemeType;
import java.util.ArrayList;
import java.util.Collections;

public class Room extends AbstractRoom {
   private static int lootBonus = 0;
   public int id;
   public static final Room roomNull = new Room();
   public int beginX;
   public int midX;
   public int endX;
   public int beginZ;
   public int midZ;
   public int endZ;
   public int floorY;
   public int nFloorY;
   public int ceilY;
   public int nCeilY;
   public int y;
   public float realX;
   public float realZ;
   public boolean hasWholePattern;
   public Symmetry sym;
   public int orientation;
   public boolean XFlip;
   public boolean ZFlip;
   public Shapes shape = Shapes.X;
   public ArrayList<RoomSeed> childSeeds;
   public boolean isNode;
   public boolean isSubroom;
   public boolean hasEntrance;
   public boolean hasSpawners;
   public ArrayList<Spawner> spawners;
   public ArrayList<BasicChest> chests;
   public ArrayList<Doorway> doors;
   public ArrayList<DoorQueue> connections;
   public ArrayList<Doorway> topDoors;
   public Doorway midpoint;
   public volatile AbstractEntrance entrance;

   private Room() {
      this.id = 0;
   }

   public void preFinalize() {
      this.childSeeds.clear();
      this.childSeeds = null;
      this.spawners.clear();
      this.chests.clear();
      this.spawners = null;
      this.chests = null;
      this.doors = null;
   }

   public Room(int beginX, int endX, int beginZ, int endZ, int floorY, int ceilY, Dungeon dungeon, Room parent, Room previous) {
      super(dungeon, previous);
      dungeon.rooms.add(this);
      this.id = dungeon.rooms.realSize();
      this.childSeeds = new ArrayList();
      this.spawners = new ArrayList();
      this.chests = new ArrayList();
      this.doors = new ArrayList();
      this.connections = new ArrayList();
      dungeon.planter.add(this);
      this.isNode = previous == null;
      this.isSubroom = parent != null;
      this.hasEntrance = this.isNode && !ConfigHandler.singleEntrance && dungeon.entrances.use(dungeon.random);
      if (this.hasEntrance) {
         ++dungeon.numEntrances;
      }

      this.hasSpawners = false;
      this.sym = Symmetry.getSymmetry(dungeon);
      this.orientation = dungeon.random.nextInt(4);
      this.XFlip = dungeon.random.nextBoolean();
      this.ZFlip = dungeon.random.nextBoolean();
      this.beginX = beginX;
      this.endX = endX;
      this.beginZ = beginZ;
      this.endZ = endZ;
      this.floorY = floorY;
      this.ceilY = ceilY;
      this.midX = beginX + (endX - beginX) / 2;
      this.midZ = beginZ + (endZ - beginZ) / 2;
      this.realX = (float)(endX - beginX) / 2.0F + (float)beginX + 1.0F;
      this.realZ = (float)(endZ - beginZ) / 2.0F + (float)beginZ + 1.0F;
      dungeon.spawners.addRoom((endX - beginX) * (endZ - beginZ));
      if (this.isNode) {
         this.degenerateFloors = false;
         this.doors.add(new Doorway(this.midX, this.midZ, dungeon.random.nextBoolean()));
      }

      if (this.hasEntrance) {
         this.addEntrance(dungeon);
      }

      if (this.isSubroom && parent.sky) {
         this.sky = this.sky && !dungeon.outside.use(dungeon.random);
      }

      for(int i = beginX + 1; i < endX; ++i) {
         for(int j = beginZ + 1; j < endZ; ++j) {
            if (dungeon.map.room[i][j] == 0) {
               dungeon.map.room[i][j] = this.id;
            }

            dungeon.map.ceilY[i][j] = dungeon.map.nCeilY[i][j] = (byte)ceilY;
            dungeon.map.floorY[i][j] = dungeon.map.nFloorY[i][j] = (byte)floorY;
            if (!this.sky) {
               dungeon.map.ceiling[i][j] = this.cielingBlock;
            }

            dungeon.map.floor[i][j] = this.floorBlock;
            dungeon.map.wall[i][j] = this.wallBlock1;
            dungeon.map.hasLiquid[i][j] = false;
            dungeon.map.isWall[i][j] = false;
         }
      }

      for(int i = beginX; i <= endX; ++i) {
         this.assignEdge(dungeon, i, beginZ);
         this.assignEdge(dungeon, i, endZ);
      }

      for(int i = beginZ; i <= endZ; ++i) {
         this.assignEdge(dungeon, beginX, i);
         this.assignEdge(dungeon, endX, i);
      }

      this.doorways(dungeon);
      this.midpoint = new Doorway(this.midX, this.midZ, dungeon.random.nextBoolean());
   }

   public Room plan(Dungeon dungeon, Room parent) {
      if (!dungeon.complexity.use(dungeon.random) && !this.isNode) {
         this.hasWholePattern = true;
         if (dungeon.liquids.use(dungeon.random)) {
            this.walkway(dungeon);
         } else if (!this.sky && (dungeon.complexity.use(dungeon.random) || dungeon.symmetry.use(dungeon.random))) {
            this.cutin(dungeon);
         }
      } else {
         this.addFeatures(dungeon);
      }

      if (this.hasEntrance) {
         for(int i = (int)this.realX - 2; i < (int)this.realX + 2; ++i) {
            for(int j = (int)this.realZ - 2; j < (int)this.realZ + 2; ++j) {
               dungeon.map.floorY[i][j] = (byte)this.floorY;
               dungeon.map.hasLiquid[i][j] = false;
               dungeon.map.isWall[i][j] = false;
            }
         }
      }

      if (parent == null) {
         this.addSpawners(dungeon);
      }

      return this;
   }

   public void addEntrance(Dungeon dungeon) {
      int type;
      if (dungeon.variability.use(dungeon.random)) {
         type = dungeon.random.nextInt(3);
      } else {
         type = dungeon.entrancePref;
      }

      if (ConfigHandler.easyFind) {
         type = 1;
      }

      switch(type) {
      case 0:
         this.entrance = new SpiralStair((int)this.realX, (int)this.realZ);
         break;
      case 1:
         this.entrance = new TopRoom((int)this.realX, (int)this.realZ);
         break;
      case 2:
      default:
         this.entrance = new SimpleEntrance((int)this.realX, (int)this.realZ);
      }

   }

   private void assignEdge(Dungeon dungeon, int x, int z) {
      if (dungeon.map.room[x][z] == 0 || ((Room)dungeon.rooms.get(dungeon.map.room[x][z])).sky && !this.sky || this.isSubroom) {
         dungeon.map.room[x][z] = this.id;
         if (!this.sky) {
            dungeon.map.ceiling[x][z] = this.cielingBlock;
         }

         dungeon.map.floor[x][z] = this.floorBlock;
         dungeon.map.wall[x][z] = this.wallBlock1;
         dungeon.map.hasLiquid[x][z] = false;
         dungeon.map.isWall[x][z] = !this.sky;
         dungeon.map.isFence[x][z] = this.fenced;
      }

      if (dungeon.map.ceilY[x][z] < (byte)this.ceilY) {
         dungeon.map.ceilY[x][z] = (byte)this.ceilY;
      }

      if (dungeon.map.nCeilY[x][z] < (byte)this.ceilY) {
         dungeon.map.nCeilY[x][z] = (byte)this.ceilY;
      }

      if (dungeon.map.floorY[x][z] < (byte)this.floorY) {
         dungeon.map.floorY[x][z] = (byte)this.floorY;
      }

      if (dungeon.map.nFloorY[x][z] > (byte)this.nFloorY || dungeon.map.nFloorY[x][z] == 0) {
         dungeon.map.nFloorY[x][z] = (byte)this.floorY;
      }

   }

   public void addFeatures(Dungeon dungeon) {
      ArrayList<FeatureAdder> features = new ArrayList();
      features.add(new IslandPlatform(dungeon.verticle));
      features.add(new Depression(dungeon.verticle));
      features.add(new Pool(dungeon.liquids));
      if (!this.sky) {
         features.add(new Cutout(dungeon.complexity));
         features.add(new Pillar(dungeon.pillars));
      }

      features.add(new IslandRoom(dungeon.islands));
      features.trimToSize();
      dungeon.random.shuffle(features);
      if (dungeon.complexity.value > 0) {
         int tries = (this.endX - this.beginX + this.endZ - this.beginZ) / (4 + this.sym.level);

         for(int i = 0; i <= tries; ++i) {
            for(FeatureAdder feat : features) {
               if (feat.addFeature(dungeon, this)) {
                  break;
               }
            }
         }

      }
   }

   protected void addSpawners(Dungeon dungeon) {
      if (ConfigHandler.difficulty != Difficulty.NONE && (this.isNode || ConfigHandler.difficulty.addmob(dungeon.random)) && (!this.hasEntrance || ConfigHandler.difficulty.entrancemobs)) {
         boolean multibonus = false;
         if (!this.hasEntrance && (ConfigHandler.difficulty.multimob(dungeon.random) || this.isNode)) {
            int tmp = this.endX - this.beginX > this.endZ - this.beginZ ? this.endX - this.beginX : this.endZ - this.beginZ;
            int num = dungeon.random.nextInt(2 + tmp / 8) + 1;

            for(int i = 0; i < num; ++i) {
               tmp = this.endX - this.beginX - 3;
               int x = dungeon.random.nextInt(tmp) + this.beginX + 2;
               tmp = this.endZ - this.beginZ - 3;
               int z = dungeon.random.nextInt(tmp) + this.beginZ + 2;
               int y;
               if (dungeon.random.nextInt(4) == 0) {
                  y = dungeon.map.ceilY[x][z];
               } else {
                  y = dungeon.map.floorY[x][z];
               }

               int lev = this.levAdjust(ConfigHandler.difficulty.moblevel(dungeon.random), dungeon);
               if (lev >= 0) {
                  String mob = (String)dungeon.theme.allMobs[lev].get(dungeon.random.nextInt(dungeon.theme.allMobs[lev].size()));
                  Spawner s = new Spawner(x, y, z, this.id, lev, mob);
                  this.spawners.add(s);
                  dungeon.spawners.addSpawner(s);
               }
            }
         } else {
            int tmp = (this.endX - this.beginX) / 2;
            int x = dungeon.random.nextInt(tmp) + this.beginX + tmp / 2;
            tmp = (this.endZ - this.beginZ) / 2;
            int z = dungeon.random.nextInt(tmp) + this.beginZ + tmp / 2;
            int y;
            if (dungeon.random.nextInt(4) == 0) {
               y = dungeon.map.floorY[x][z];
            } else {
               y = dungeon.map.ceilY[x][z];
            }

            int lev = this.levAdjust(ConfigHandler.difficulty.moblevel(dungeon.random), dungeon);
            if (lev >= 0) {
               String mob = (String)dungeon.theme.allMobs[lev].get(dungeon.random.nextInt(dungeon.theme.allMobs[lev].size()));
               Spawner s = new Spawner(x, y, z, this.id, lev, mob);
               this.spawners.add(s);
               dungeon.spawners.addSpawner(s);
            }
         }

         if (this.isNode && !this.hasEntrance) {
            int x = (int)this.realX;
            int z = (int)this.realZ;
            int y;
            if (dungeon.map.hasLiquid[x][z]) {
               y = dungeon.map.ceilY[x][z];
            } else {
               y = dungeon.map.floorY[x][z] - 1;
            }

            int lev = this.levAdjust(ConfigHandler.difficulty.nodelevel(dungeon.random), dungeon);
            if (lev >= 0) {
               String mob = (String)dungeon.theme.allMobs[lev].get(dungeon.random.nextInt(dungeon.theme.allMobs[lev].size()));
               this.spawners.add(new Spawner(x, y, z, this.id, lev, mob));
            }
         }

      }
   }

   private int levAdjust(int lev, Dungeon dungeon) {
      while(true) {
         if (dungeon.theme.allMobs[lev].isEmpty()) {
            --lev;
            if (lev >= 0) {
               continue;
            }

            return -1;
         }

         return lev;
      }
   }

   public void addChests(Dungeon dungeon) {
      if (ConfigHandler.difficulty != Difficulty.NONE && !this.hasEntrance) {
         this.hasSpawners = this.spawners.size() > 0;
         if (this.hasSpawners || dungeon.random.nextInt(5) <= 0) {
            int lev = 0;
            int n = this.spawners.size();

            for(int i = 0; i < n; ++i) {
               lev = Math.max(lev, ((Spawner)this.spawners.get(i)).getLevel());
            }

            boolean trueBoss = lev > 3;
            if (n > 1) {
               ++lev;
            }

            if (this.isNode && !this.hasEntrance) {
               ++lev;
            }

            if (dungeon.theme.flags.contains(ThemeFlags.HARD)) {
               ++lev;
            } else if (dungeon.theme.flags.contains(ThemeFlags.EASY)) {
               --lev;
            }

            lev = lev + lootBonus + dungeon.random.nextInt(1);
            if (!this.hasSpawners) {
               int tmp = this.endX - this.beginX - 3;
               int x = dungeon.random.nextInt(tmp) + this.beginX + 2;
               tmp = this.endZ - this.beginZ - 3;
               int z = dungeon.random.nextInt(tmp) + this.beginZ + 2;
               int y = dungeon.map.floorY[x][z];
               this.chests.add(new WeakChest(x, y, z, dungeon.lootCat));
            } else if (dungeon.random.nextBoolean() && !this.isNode) {
               int tmp = this.endX - this.beginX - 3;
               int x = dungeon.random.nextInt(tmp) + this.beginX + 2;
               tmp = this.endZ - this.beginZ - 3;
               int z = dungeon.random.nextInt(tmp) + this.beginZ + 2;
               int y = dungeon.map.floorY[x][z];
               this.chests.add(new BasicChest(x, y, z, lev, dungeon.lootCat));
            } else {
               int ms = Math.max(n, 2);
               int num;
               if (this.isNode) {
                  num = Math.min(ms, dungeon.random.nextInt(2 + ms / 2) + 2);
               } else {
                  num = dungeon.random.nextInt(1 + ms / 2) + 1;
               }

               for(int i = 0; i < num; ++i) {
                  int tmp = this.endX - this.beginX - 3;
                  int x = dungeon.random.nextInt(tmp) + this.beginX + 2;
                  tmp = this.endZ - this.beginZ - 3;
                  int z = dungeon.random.nextInt(tmp) + this.beginZ + 2;
                  int y = dungeon.map.floorY[x][z];
                  this.chests.add(new BasicChest(x, y, z, lev, dungeon.lootCat));
               }
            }

            if (this.isNode && !this.hasEntrance) {
               int x = (int)this.realX;
               int z = (int)this.realZ;
               int y = dungeon.map.floorY[x][z];
               this.chests.add((new TreasureChest(x, y, z, lev, dungeon.lootCat)).setWithBoss(trueBoss));
            }

         }
      }
   }

   protected void doorways(Dungeon dungeon) {
      int num = dungeon.random.nextInt(2 + (this.endX - this.beginX + this.endZ - this.beginZ) / (this.sym.level * 8 + 8) + dungeon.subrooms.value / (2 + this.sym.level)) + 1;

      for(int i = 0; i < num; ++i) {
         this.doorway(dungeon);
      }

   }

   protected void addDoor(Dungeon dungeon, int x, int z, boolean xOriented) {
      this.doors.add(new Doorway(x, z, xOriented));
      dungeon.map.isDoor[x][z] = true;
   }

   protected void doorway(Dungeon dungeon) {
      int xExtend = 0;
      int zExtend = 0;
      int xSeedDir = 0;
      int zSeedDir = 0;
      int wall = dungeon.random.nextInt(4);
      int z;
      int oppX;
      int oppZ;
      int x;
      switch(wall) {
      case 0:
         x = this.beginX;
         oppX = this.endX;
         z = dungeon.random.nextInt(this.endZ - this.beginZ - 3) + 2;
         oppZ = this.endZ - z;
         z = z + this.beginZ;
         xSeedDir = -1;
         zSeedDir = 0;
         zExtend = dungeon.random.nextInt(3) - 1;
         break;
      case 1:
         z = this.beginZ;
         oppZ = this.endZ;
         x = dungeon.random.nextInt(this.endX - this.beginX - 3) + 2;
         oppX = this.endX - x;
         x = x + this.beginX;
         xSeedDir = 0;
         zSeedDir = -1;
         xExtend = dungeon.random.nextInt(3) - 1;
         break;
      case 2:
         x = this.endX;
         oppX = this.beginX;
         z = dungeon.random.nextInt(this.endZ - this.beginZ - 3) + 2;
         oppZ = this.endZ - z;
         z = z + this.beginZ;
         xSeedDir = 1;
         zSeedDir = 0;
         zExtend = dungeon.random.nextInt(3) - 1;
         break;
      case 3:
         z = this.endZ;
         oppZ = this.beginZ;
         x = dungeon.random.nextInt(this.endX - this.beginX - 3) + 2;
         oppX = this.endX - x;
         x = x + this.beginX;
         xSeedDir = 0;
         zSeedDir = 1;
         xExtend = dungeon.random.nextInt(3) - 1;
         break;
      default:
         x = this.beginX;
         oppX = this.endX;
         z = dungeon.random.nextInt(this.endZ - this.beginZ - 3) + 2;
         oppZ = this.endZ - z;
         z = z + this.beginZ;
         xSeedDir = -1;
         zSeedDir = 0;
         zExtend = dungeon.random.nextInt(3) - 1;
      }

      this.addDoor(dungeon, x, z, xSeedDir != 0);
      this.addDoor(dungeon, x + xExtend, z + zExtend, xSeedDir != 0);
      this.childSeeds.add(new RoomSeed(x + xSeedDir, this.floorY, z + zSeedDir));
      switch(this.sym) {
      case NONE:
      default:
         break;
      case TR1:
         oppX = (int)(this.realX + ((float)z - this.realZ) / (float)(this.endZ - this.beginZ) * (float)(this.endX - this.beginX));
         if (oppX < 1) {
            oppX = 1;
         }

         if (oppX > dungeon.size.width - 2) {
            oppX = dungeon.size.width - 2;
         }

         oppZ = (int)(this.realZ + ((float)x - this.realX) / (float)(this.endX - this.beginX) * (float)(this.endZ - this.beginZ));
         if (oppZ < 1) {
            oppZ = 1;
         }

         if (oppZ > dungeon.size.width - 2) {
            oppZ = dungeon.size.width - 2;
         }

         this.addDoor(dungeon, oppX, oppZ, zSeedDir != 0);
         this.addDoor(dungeon, oppX + xExtend, oppZ + zExtend, zSeedDir != 0);
         this.childSeeds.add(new RoomSeed(oppX + zSeedDir, this.floorY, oppZ + xSeedDir));
         break;
      case TR2:
         oppX = (int)(this.realX + ((float)z - this.realZ) / (float)(this.endZ - this.beginZ) * (float)(this.endX - this.beginX));
         if (oppX < 1) {
            oppX = 1;
         }

         if (oppX > dungeon.size.width - 2) {
            oppX = dungeon.size.width - 2;
         }

         oppZ = (int)(this.realZ + ((float)x - this.realX) / (float)(this.endX - this.beginX) * (float)(this.endZ - this.beginZ));
         oppZ = this.endZ - (oppZ - this.beginZ);
         if (oppZ < 1) {
            oppZ = 1;
         }

         if (oppZ > dungeon.size.width - 2) {
            oppZ = dungeon.size.width - 2;
         }

         this.addDoor(dungeon, oppX, oppZ, zSeedDir != 0);
         this.addDoor(dungeon, oppX + xExtend, oppZ + zExtend, zSeedDir != 0);
         this.childSeeds.add(new RoomSeed(oppX + zSeedDir, this.floorY, oppZ - xSeedDir));
         break;
      case X:
         this.addDoor(dungeon, oppX, z, xSeedDir != 0);
         this.addDoor(dungeon, oppX - xExtend, z + zExtend, xSeedDir != 0);
         this.childSeeds.add(new RoomSeed(oppX - xSeedDir, this.floorY, z + zSeedDir));
         break;
      case Z:
         this.addDoor(dungeon, x, oppZ, xSeedDir != 0);
         this.addDoor(dungeon, x + xExtend, oppZ - zExtend, xSeedDir != 0);
         this.childSeeds.add(new RoomSeed(x + xSeedDir, this.floorY, oppZ - zSeedDir));
         break;
      case XZ:
         this.addDoor(dungeon, oppX, z, xSeedDir != 0);
         this.addDoor(dungeon, oppX - xExtend, z + zExtend, xSeedDir != 0);
         this.childSeeds.add(new RoomSeed(oppX - xSeedDir, this.floorY, z + zSeedDir));
         this.addDoor(dungeon, x, oppZ, xSeedDir != 0);
         this.addDoor(dungeon, x + xExtend, oppZ - zExtend, xSeedDir != 0);
         this.childSeeds.add(new RoomSeed(x + xSeedDir, this.floorY, oppZ - zSeedDir));
         this.addDoor(dungeon, oppX, oppZ, xSeedDir != 0);
         this.addDoor(dungeon, oppX - xExtend, oppZ - zExtend, xSeedDir != 0);
         this.childSeeds.add(new RoomSeed(oppX - xSeedDir, this.floorY, oppZ - zSeedDir));
         break;
      case R:
         this.addDoor(dungeon, oppX, oppZ, xSeedDir != 0);
         this.addDoor(dungeon, oppX - xExtend, oppZ - zExtend, xSeedDir != 0);
         this.childSeeds.add(new RoomSeed(oppX - xSeedDir, this.floorY, oppZ - zSeedDir));
         break;
      case SW:
         this.addDoor(dungeon, oppX, z, xSeedDir != 0);
         this.addDoor(dungeon, oppX - xExtend, z + zExtend, xSeedDir != 0);
         this.childSeeds.add(new RoomSeed(oppX - xSeedDir, this.floorY, z + zSeedDir));
         this.addDoor(dungeon, x, oppZ, xSeedDir != 0);
         this.addDoor(dungeon, x + xExtend, oppZ - zExtend, xSeedDir != 0);
         this.childSeeds.add(new RoomSeed(x + xSeedDir, this.floorY, oppZ - zSeedDir));
         this.addDoor(dungeon, oppX, oppZ, xSeedDir != 0);
         this.addDoor(dungeon, oppX - xExtend, oppZ - zExtend, xSeedDir != 0);
         this.childSeeds.add(new RoomSeed(oppX - xSeedDir, this.floorY, oppZ - zSeedDir));
      }

   }

   public Room connector(Dungeon dungeon, int dir, int xdim, int zdim, int height, Route source) {
      int xExtend = 0;
      int zExtend = 0;
      int xSeedDir = 0;
      int zSeedDir = 0;
      int x;
      int z;
      switch(dir) {
      case 0:
         x = this.endX;
         int oppX = this.beginX;
         z = (int)this.realZ;
         xSeedDir = 1;
         zSeedDir = 0;
         zExtend = dungeon.random.nextInt(3) - 1;
         break;
      case 1:
         z = this.endZ;
         int oppZ = this.beginZ;
         x = (int)this.realX;
         xSeedDir = 0;
         zSeedDir = 1;
         xExtend = dungeon.random.nextInt(3) - 1;
         break;
      case 2:
         x = this.beginX;
         oppX = this.endX;
         z = (int)this.realZ;
         xSeedDir = -1;
         zSeedDir = 0;
         zExtend = dungeon.random.nextInt(3) - 1;
         break;
      case 3:
         z = this.beginZ;
         oppZ = this.endZ;
         x = (int)this.realX;
         xSeedDir = 0;
         zSeedDir = -1;
         xExtend = dungeon.random.nextInt(3) - 1;
         break;
      default:
         x = this.beginX;
         oppX = this.endX;
         z = (int)this.realZ;
         z = z + this.beginZ;
         xSeedDir = -1;
         zSeedDir = 0;
         zExtend = dungeon.random.nextInt(3) - 1;
      }

      this.addDoor(dungeon, x, z, xSeedDir != 0);
      this.addDoor(dungeon, x + xExtend, z + zExtend, xSeedDir != 0);
      if (x + xSeedDir < dungeon.size.width && x + xSeedDir >= 0 && z + zSeedDir < dungeon.size.width && z + zSeedDir >= 0) {
         if (dungeon.map.room[x + xSeedDir][z + zSeedDir] != 0) {
            return (Room)dungeon.rooms.get(dungeon.map.room[x + xSeedDir][z + zSeedDir]);
         } else {
            return dir % 2 == 0 ? (new RoomSeed(x + xSeedDir, this.floorY, z + zSeedDir)).growRoomZ(xdim, zdim, height, dungeon, (Room)null, this) : (new RoomSeed(x + xSeedDir, this.floorY, z + zSeedDir)).growRoomX(xdim, zdim, height, dungeon, (Room)null, this);
         }
      } else {
         return null;
      }
   }

   private void walkway(Dungeon dungeon) {
      int drop;
      if (dungeon.theme.type.contains(ThemeType.SWAMP)) {
         drop = 1;
      } else {
         drop = 2;
      }

      this.shape = Shapes.wholeShape(this.sym, dungeon.random);

      for(int i = this.beginX; i <= this.endX; ++i) {
         for(int j = this.beginZ; j <= this.endZ; ++j) {
            dungeon.map.floorY[i][j] = (byte)(dungeon.map.floorY[i][j] - drop);
            dungeon.map.hasLiquid[i][j] = true;
         }
      }

      this.shape.family[this.orientation].drawWalkway(dungeon, this, this.realX, this.realZ, (byte)(this.endX - this.beginX + 1), (byte)(this.endZ - this.beginZ + 1), this.XFlip, this.ZFlip);
   }

   private void cutin(Dungeon dungeon) {
      this.shape = Shapes.wholeShape(this.sym, dungeon.random);

      for(int i = this.beginX; i <= this.endX; ++i) {
         for(int j = this.beginZ; j <= this.endZ; ++j) {
            dungeon.map.isWall[i][j] = true;
         }
      }

      this.shape.family[this.orientation].drawCutin(dungeon, this, this.realX, this.realZ, (byte)(this.endX - this.beginX - 1), (byte)(this.endZ - this.beginZ - 1), this.XFlip, this.ZFlip);
   }

   public boolean plantChildren(Dungeon dungeon) {
      boolean result = false;

      for(RoomSeed planted : this.childSeeds) {
         if (dungeon.rooms.realSize() >= dungeon.size.maxRooms) {
            return false;
         }

         int height = dungeon.baseHeight;
         int x = dungeon.random.nextInt(dungeon.size.width);
         int z = dungeon.random.nextInt(dungeon.size.width);
         if (height > dungeon.theme.maxY) {
            height = dungeon.baseHeight;
         }

         if (height < dungeon.theme.minY) {
            height = dungeon.baseHeight;
         }

         int xdim = dungeon.random.nextInt(dungeon.size.maxRoomSize - 5) + 6;
         int zdim = dungeon.random.nextInt(dungeon.size.maxRoomSize - 5) + 6;
         int ymod = xdim <= zdim ? (int)Math.sqrt((double)xdim) : (int)Math.sqrt((double)zdim);
         int roomHeight = dungeon.random.nextInt(dungeon.verticle.value / 2 + ymod + 1) + 2;
         if (planted.growRoom(xdim, zdim, roomHeight, dungeon, (Room)null, this) != null) {
            result = true;
         }
      }

      return result;
   }

   public void addToConnections(Doorway door) {
      if (this.id >= 1) {
         if (this.connections.isEmpty()) {
            DoorQueue pq = new DoorQueue(door.otherside);
            pq.add(door);
            this.connections.add(pq);
         } else {
            boolean added = false;

            for(DoorQueue pq : this.connections) {
               if (pq.isRoom(door.otherside)) {
                  pq.add(door);
                  added = true;
                  break;
               }
            }

            if (!added) {
               DoorQueue pq = new DoorQueue(door.otherside);
               pq.add(door);
               this.connections.add(pq);
            }
         }

      }
   }

   public static void setLootBonus(int bonus) {
      lootBonus = bonus;
   }
}
