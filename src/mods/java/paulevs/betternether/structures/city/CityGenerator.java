package paulevs.betternether.structures.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public class CityGenerator {
   private List<StructureCityBuilding> centers = new ArrayList();
   private List<StructureCityBuilding> buildings = new ArrayList();
   private List<StructureCityBuilding> roadEnds = new ArrayList();
   private List<StructureCityBuilding> total = new ArrayList();
   private List<BoundingBox> bounds = new ArrayList();
   private List<BlockPos> ends = new ArrayList();
   private List<BlockPos> add = new ArrayList();
   private List<BlockPos> rem = new ArrayList();

   public CityGenerator() {
      this.addBuildingToList("city_center_01", -10, this.centers);
      this.addBuildingToList("city_center_02", -10, this.centers);
      this.addBuildingToList("city_library_01", this.buildings);
      this.addBuildingToList("city_tower_01", this.buildings);
      this.addBuildingToList("city_tower_02", this.buildings);
      this.addBuildingToList("city_building_01", this.buildings);
      this.addBuildingToList("city_building_02", this.buildings);
      this.addBuildingToList("city_building_03", this.buildings);
      this.addBuildingToList("city_building_04", this.buildings);
      this.addBuildingToList("city_building_05", this.buildings);
      this.addBuildingToList("city_building_06", this.buildings);
      this.addBuildingToList("city_building_07", this.buildings);
      this.addBuildingToList("city_building_08", this.buildings);
      this.addBuildingToList("city_building_09", this.buildings);
      this.addBuildingToList("city_building_10", this.buildings);
      this.addBuildingToList("city_enchanter_01", this.buildings);
      this.addBuildingToList("city_hall", this.buildings);
      this.addBuildingToList("road_end_01", this.roadEnds);
      this.addBuildingToList("road_end_02", -2, this.roadEnds);
      this.total.addAll(this.centers);
      this.total.addAll(this.buildings);
      this.total.addAll(this.roadEnds);
   }

   private void addBuildingToList(String name, List<StructureCityBuilding> buildings) {
      this.addBuildingToList(name, 0, buildings);
   }

   private void addBuildingToList(String name, int offsetY, List<StructureCityBuilding> buildings) {
      StructureCityBuilding building = new StructureCityBuilding("city/" + name, offsetY);
      buildings.add(building);
      buildings.add(building.getRotated(Rotation.CLOCKWISE_90));
      buildings.add(building.getRotated(Rotation.CLOCKWISE_180));
      buildings.add(building.getRotated(Rotation.COUNTERCLOCKWISE_90));
   }

   private void placeCenterBuilding(BlockPos pos, StructureCityBuilding building, ArrayList<BuildingInfo> city) {
      BoundingBox bb = building.getBoungingBox().offset(pos);
      this.bounds.add(bb);
      city.add(new BuildingInfo(building, pos.add(0, building.getYOffset(), 0)));

      for(int i = 0; i < building.getEndsCount(); ++i) {
         this.ends.add(pos.add(building.getOffsettedPos(i).add(0, building.getYOffset(), 0)));
      }

   }

   private void attachBuildings(Random random, ArrayList<BuildingInfo> city) {
      for(BlockPos pos : this.ends) {
         boolean generate = true;

         for(int n = 0; n < 8 && generate; ++n) {
            int b = random.nextInt(this.buildings.size() >> 2) << 2;

            for(int r = 0; r < 4 && generate; ++r) {
               StructureCityBuilding building = (StructureCityBuilding)this.buildings.get(b | r);
               int index = random.nextInt(building.getEndsCount());
               BlockPos offset = building.getPos(index);
               BoundingBox bb = building.getBoungingBox().offset(pos).offsetNegative(offset);
               if (this.noCollisions(bb)) {
                  BlockPos npos = new BlockPos(bb.x1, pos.getY() - offset.getY() + building.getYOffset(), bb.z1);
                  this.bounds.add(bb);
                  this.rem.add(pos);

                  for(int i = 0; i < building.getEndsCount(); ++i) {
                     if (i != index) {
                        this.add.add(npos.add(building.getOffsettedPos(i)));
                     }
                  }

                  city.add(new BuildingInfo(building, npos));
                  generate = false;
               }
            }
         }
      }

      this.ends.removeAll(this.rem);
      this.ends.addAll(this.add);
      this.rem.clear();
      this.add.clear();
   }

   private void closeRoads(ArrayList<BuildingInfo> city) {
      for(BlockPos pos : this.ends) {
         for(int n = 0; n < this.roadEnds.size(); ++n) {
            StructureCityBuilding building = (StructureCityBuilding)this.roadEnds.get(n);
            BlockPos offset = building.getPos(0);
            BoundingBox bb = building.getBoungingBox().offset(pos).offsetNegative(offset);
            if (this.noCollisions(bb)) {
               BlockPos npos = new BlockPos(bb.x1, pos.getY() - offset.getY() + building.getYOffset(), bb.z1);
               this.bounds.add(bb);
               city.add(new BuildingInfo(building, npos));
               break;
            }
         }
      }

      this.ends.clear();
      this.bounds.clear();
      this.rem.clear();
      this.add.clear();
   }

   public ArrayList<BuildingInfo> generate(BlockPos pos, Random random) {
      ArrayList<BuildingInfo> city = new ArrayList();
      this.placeCenterBuilding(pos, (StructureCityBuilding)this.centers.get(random.nextInt(this.centers.size())), city);

      for(int i = 0; i < 2 + random.nextInt(4); ++i) {
         this.attachBuildings(random, city);
      }

      this.closeRoads(city);
      return city;
   }

   private boolean noCollisions(BoundingBox bb) {
      for(BoundingBox b : this.bounds) {
         if (bb.isColliding(b)) {
            return false;
         }
      }

      return true;
   }

   public List<StructureCityBuilding> getBuildings() {
      return this.total;
   }
}
