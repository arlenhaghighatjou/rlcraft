package reborncore.client.multiblock;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import reborncore.client.multiblock.component.MultiblockComponent;

public class Multiblock {
   public List<MultiblockComponent> components = new ArrayList();
   public int minX;
   public int minY;
   public int minZ;
   public int maxX;
   public int maxY;
   public int maxZ;
   public int offX;
   public int offY;
   public int offZ;

   public Multiblock() {
   }

   public void addComponent(MultiblockComponent component) {
      this.components.add(component);
      this.changeAxisForNewComponent(component.relPos.getX(), component.relPos.getY(), component.relPos.getZ());
   }

   public void addComponent(BlockPos pos, IBlockState state) {
      this.addComponent(new MultiblockComponent(pos, state));
   }

   private void changeAxisForNewComponent(int x, int y, int z) {
      if (x < this.minX) {
         this.minX = x;
      } else if (x > this.maxX) {
         this.maxX = x;
      }

      if (y < this.minY) {
         this.minY = y;
      } else if (y > this.maxY) {
         this.maxY = y;
      }

      if (z < this.minZ) {
         this.minZ = z;
      } else if (z > this.maxZ) {
         this.maxZ = z;
      }

   }

   public void setRenderOffset(int x, int y, int z) {
      this.offX = x;
      this.offY = y;
      this.offZ = z;
   }

   public List<MultiblockComponent> getComponents() {
      return this.components;
   }

   public void rotate(double angle) {
      for(MultiblockComponent comp : this.getComponents()) {
         comp.rotate(angle);
      }

   }

   public Multiblock copy() {
      Multiblock mb = new Multiblock();

      for(MultiblockComponent comp : this.getComponents()) {
         mb.addComponent(comp.copy());
      }

      return mb;
   }

   public Multiblock[] createRotations() {
      Multiblock[] blocks = new Multiblock[4];
      blocks[0] = this;
      blocks[1] = blocks[0].copy();
      blocks[1].rotate(1.5707963267948966D);
      blocks[2] = blocks[1].copy();
      blocks[2].rotate(1.5707963267948966D);
      blocks[3] = blocks[2].copy();
      blocks[3].rotate(1.5707963267948966D);
      return blocks;
   }

   public MultiblockSet makeSet() {
      return new MultiblockSet(this);
   }

   public int getXSize() {
      return Math.abs(this.minX) + Math.abs(this.maxX) + 1;
   }

   public int getYSize() {
      return Math.abs(this.minY) + Math.abs(this.maxY) + 1;
   }

   public int getZSize() {
      return Math.abs(this.minZ) + Math.abs(this.maxZ) + 1;
   }
}
