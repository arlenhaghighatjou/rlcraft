package paulevs.betternether.structures.city;

import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.Template;
import paulevs.betternether.structures.StructureNBT;

public class StructureCityBuilding extends StructureNBT {
   protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
   private BoundingBox bb;
   private BlockPos[] ends;
   private EnumFacing[] dirs;
   private BlockPos rotationOffset;
   private Rotation rotation;
   private int offsetY;

   public StructureCityBuilding(String structure) {
      super(structure);
      this.offsetY = 0;
      this.init();
   }

   public StructureCityBuilding(String structure, int offsetY) {
      super(structure);
      this.offsetY = offsetY;
      this.init();
   }

   protected StructureCityBuilding(ResourceLocation location, Template template) {
      super(location, template);
      this.init();
   }

   private void init() {
      BlockPos size = this.template.getSize();
      this.bb = new BoundingBox(size);
      Map<BlockPos, String> map = this.template.getDataBlocks(BlockPos.ORIGIN, DEFAULT_SETTINGS);
      this.ends = new BlockPos[map.size()];
      this.dirs = new EnumFacing[map.size()];
      int i = 0;
      BlockPos center = new BlockPos(size.getX() >> 1, size.getY(), size.getZ() >> 1);

      for(BlockPos pos : map.keySet()) {
         this.ends[i] = pos;
         this.dirs[i++] = this.getDir(pos.add(-center.getX(), 0, -center.getZ()));
      }

      this.rotationOffset = new BlockPos(0, 0, 0);
      this.rotation = Rotation.NONE;
   }

   private EnumFacing getDir(BlockPos pos) {
      int ax = Math.abs(pos.getX());
      int az = Math.abs(pos.getZ());
      int mx = Math.max(ax, az);
      if (mx == ax) {
         return pos.getX() > 0 ? EnumFacing.EAST : EnumFacing.WEST;
      } else {
         return pos.getZ() > 0 ? EnumFacing.SOUTH : EnumFacing.NORTH;
      }
   }

   public BoundingBox getBoungingBox() {
      return this.bb;
   }

   public void place(World world, BlockPos pos) {
      BlockPos p = pos.add(this.rotationOffset);
      this.template.addBlocksToWorld(world, p, DEFAULT_SETTINGS.copy().setRotation(this.rotation));

      for(BlockPos rep : this.ends) {
         world.setBlockState(rep.add(pos), AIR);
      }

      for(int x = 0; x < this.bb.x2; ++x) {
         for(int z = 0; z < this.bb.z2; ++z) {
            p = pos.add(x, 0, z);
            IBlockState state = world.getBlockState(p);
            if (state.isFullBlock()) {
               int d;
               for(d = 1; d < pos.getY() - 5 && !world.getBlockState(p.down(d)).isFullBlock(); ++d) {
                  ;
               }

               for(int y = 1; y < d; ++y) {
                  world.setBlockState(p.down(y), state);
               }
            }
         }
      }

   }

   protected Rotation mirrorRotation(Rotation r) {
      switch(r) {
      case CLOCKWISE_90:
         return Rotation.COUNTERCLOCKWISE_90;
      default:
         return r;
      }
   }

   public void placeInChunk(World world, BlockPos pos, StructureBoundingBox boundingBox) {
      BlockPos p = pos.add(this.rotationOffset);
      this.template.addBlocksToWorld(world, p, DEFAULT_SETTINGS.copy().setRotation(this.rotation).setBoundingBox(boundingBox));

      for(BlockPos rep : this.ends) {
         BlockPos repPos = rep.add(pos);
         if (repPos.getX() >= boundingBox.minX && repPos.getX() <= boundingBox.maxX && repPos.getZ() >= boundingBox.minZ && repPos.getZ() <= boundingBox.maxZ) {
            world.setBlockState(repPos, AIR);
         }
      }

      for(int x = 0; x < this.bb.x2; ++x) {
         for(int z = 0; z < this.bb.z2; ++z) {
            p = pos.add(x, 0, z);
            if (p.getX() >= boundingBox.minX && p.getX() <= boundingBox.maxX && p.getZ() >= boundingBox.minZ && p.getZ() <= boundingBox.maxZ) {
               IBlockState state = world.getBlockState(p);
               if (state.isFullBlock()) {
                  int d;
                  for(d = 1; d < pos.getY() - 5 && !world.getBlockState(p.down(d)).isFullBlock(); ++d) {
                     ;
                  }

                  for(int y = 1; y < d; ++y) {
                     world.setBlockState(p.down(y), state);
                  }
               }
            }
         }
      }

   }

   public BlockPos[] getEnds() {
      return this.ends;
   }

   public int getEndsCount() {
      return this.ends.length;
   }

   public BlockPos getOffsettedPos(int index) {
      return this.ends[index].offset(this.dirs[index]);
   }

   public BlockPos getPos(int index) {
      return this.ends[index];
   }

   public StructureCityBuilding getRotated(Rotation rotation) {
      StructureCityBuilding building = this.clone();
      building.rotation = rotation;
      building.rotationOffset = building.template.getSize().rotate(rotation);
      int x = building.rotationOffset.getX();
      int z = building.rotationOffset.getZ();
      if (x < 0) {
         x = -x - 1;
      } else {
         x = 0;
      }

      if (z < 0) {
         z = -z - 1;
      } else {
         z = 0;
      }

      building.rotationOffset = new BlockPos(x, 0, z);

      for(int i = 0; i < building.dirs.length; ++i) {
         building.dirs[i] = this.rotated(building.dirs[i], rotation);
         building.ends[i] = building.ends[i].rotate(rotation).add(building.rotationOffset);
      }

      building.bb.rotate(rotation);
      building.offsetY = this.offsetY;
      return building;
   }

   public StructureCityBuilding getRandomRotated(EaglercraftRandom random) {
      return this.getRotated(Rotation.values()[random.nextInt(Rotation.values().length)]);
   }

   public StructureCityBuilding clone() {
      return new StructureCityBuilding(this.location, this.template);
   }

   private EnumFacing rotated(EnumFacing dir, Rotation rotation) {
      EnumFacing f;
      switch(rotation) {
      case CLOCKWISE_90:
         f = dir.rotateY();
         break;
      case CLOCKWISE_180:
         f = dir.getOpposite();
         break;
      case COUNTERCLOCKWISE_90:
         f = dir.rotateYCCW();
         break;
      default:
         f = dir;
      }

      return f;
   }

   public int getYOffset() {
      return this.offsetY;
   }

   public Rotation getRotation() {
      return this.rotation;
   }
}
