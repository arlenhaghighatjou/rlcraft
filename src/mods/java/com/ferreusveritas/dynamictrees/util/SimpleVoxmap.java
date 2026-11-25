package com.ferreusveritas.dynamictrees.util;

import com.google.common.collect.AbstractIterator;
import java.util.Arrays;
import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

public class SimpleVoxmap {
   private final byte[] data;
   private final boolean[] touched;
   private final int lenX;
   private final int lenY;
   private final int lenZ;
   private final int layerSize;
   BlockPos center;

   public SimpleVoxmap(int lenX, int lenY, int lenZ) {
      this.center = new BlockPos(0, 0, 0);
      this.data = new byte[lenX * lenY * lenZ];
      this.touched = new boolean[lenY];
      this.lenX = lenX;
      this.lenY = lenY;
      this.lenZ = lenZ;
      this.layerSize = lenX * lenZ;
   }

   public SimpleVoxmap(int lenX, int lenY, int lenZ, byte[] extData) {
      this.center = new BlockPos(0, 0, 0);
      this.data = Arrays.copyOf(extData, lenX * lenY * lenZ);
      this.touched = new boolean[lenY];

      for(int y = 0; y < lenY; ++y) {
         this.touched[y] = true;
      }

      this.lenX = lenX;
      this.lenY = lenY;
      this.lenZ = lenZ;
      this.layerSize = lenX * lenZ;
   }

   public SimpleVoxmap(SimpleVoxmap vmp) {
      this(vmp.getLenX(), vmp.getLenY(), vmp.getLenZ(), vmp.data);
      this.center = vmp.center;
   }

   public SimpleVoxmap(BlockBounds bounds) {
      this(bounds.getXSize(), bounds.getYSize(), bounds.getZSize());
      this.setMapAndCenter(bounds.getMin(), new BlockPos(0, 0, 0));
   }

   public SimpleVoxmap setMapAndCenter(BlockPos mapPos, BlockPos centerPos) {
      this.setCenter(centerPos);
      this.center = this.center.subtract(mapPos);
      return this;
   }

   public SimpleVoxmap setMap(BlockPos mapPos) {
      this.center = this.center.subtract(mapPos);
      return this;
   }

   public SimpleVoxmap setCenter(BlockPos centerPos) {
      this.center = centerPos;
      return this;
   }

   public BlockPos getCenter() {
      return this.center;
   }

   public byte[] getData() {
      return this.data;
   }

   public int getLenX() {
      return this.lenX;
   }

   public int getLenY() {
      return this.lenY;
   }

   public int getLenZ() {
      return this.lenZ;
   }

   public BlockBounds getBounds() {
      int minX = this.center.getX() - this.lenX + 1;
      int minY = this.center.getY() - this.lenY + 1;
      int minZ = this.center.getZ() - this.lenZ + 1;
      int maxX = minX + this.lenX - 1;
      int maxY = minY + this.lenY - 1;
      int maxZ = minZ + this.lenZ - 1;
      return new BlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
   }

   public SimpleVoxmap blitOp(BlockPos pos, SimpleVoxmap src, SimpleVoxmap.IBlitOp op) {
      for(int iy = 0; iy < src.getLenY(); ++iy) {
         int srcY = iy - src.center.getY();
         int dstY = pos.getY() + srcY;
         this.setYTouched(dstY);

         for(int iz = 0; iz < src.getLenZ(); ++iz) {
            int srcZ = iz - src.center.getZ();
            int dstZ = pos.getZ() + srcZ;

            for(int ix = 0; ix < src.getLenX(); ++ix) {
               int srcX = ix - src.center.getX();
               int dstX = pos.getX() + srcX;
               byte srcValue = src.getVoxel(srcX, srcY, srcZ);
               byte dstValue = this.getVoxel(dstX, dstY, dstZ);
               this.setVoxel(dstX, dstY, dstZ, op.getOp(srcValue, dstValue));
            }
         }
      }

      return this;
   }

   public SimpleVoxmap blitReplace(BlockPos pos, SimpleVoxmap src) {
      return this.blitOp(pos, src, (s, d) -> {
         return s;
      });
   }

   public SimpleVoxmap blitMax(BlockPos pos, SimpleVoxmap src) {
      return this.blitOp(pos, src, (s, d) -> {
         return s >= d ? s : d;
      });
   }

   public SimpleVoxmap blitClear(BlockPos pos, SimpleVoxmap src) {
      return this.blitOp(pos, src, (s, d) -> {
         return s >= 0 ? 0 : d;
      });
   }

   public SimpleVoxmap filter(SimpleVoxmap.IFilterOp op) {
      for(int i = 0; i < this.touched.length; ++i) {
         this.touched[i] = true;
      }

      for(int i = 0; i < this.data.length; ++i) {
         this.data[i] = op.getOp(this.data[i]);
      }

      return this;
   }

   public SimpleVoxmap crop(BlockPos from, BlockPos to) {
      for(MutableBlockPos pos : this.getAllNonZero()) {
         if (pos.getX() < from.getX() || pos.getY() < from.getY() || pos.getZ() < from.getZ() || pos.getX() > to.getX() || pos.getY() > to.getY() || pos.getZ() > to.getZ()) {
            this.setVoxel(pos, (byte)0);
         }
      }

      return this;
   }

   public SimpleVoxmap filter(BlockPos from, BlockPos to, SimpleVoxmap.IFilterOp op) {
      for(MutableBlockPos pos : BlockPos.getAllInBoxMutable(from, to)) {
         this.setVoxel(pos, op.getOp(this.getVoxel(pos)));
      }

      return this;
   }

   public SimpleVoxmap fill(byte value) {
      return this.filter((v) -> {
         return value;
      });
   }

   public SimpleVoxmap fill(BlockPos from, BlockPos to, byte value) {
      return this.filter(from, to, (v) -> {
         return value;
      });
   }

   private int calcPos(int x, int y, int z) {
      return y * this.lenX * this.lenZ + z * this.lenX + x;
   }

   public void setVoxel(BlockPos pos, byte value) {
      this.setVoxel(pos.getX(), pos.getY(), pos.getZ(), value);
   }

   public void setVoxelOr(BlockPos pos, byte value) {
      this.setVoxelOr(pos.getX(), pos.getY(), pos.getZ(), value);
   }

   public void setVoxel(int x, int y, int z, byte value) {
      x = x + this.center.getX();
      y = y + this.center.getY();
      z = z + this.center.getZ();
      if (this.testBounds(x, y, z)) {
         if (value != 0) {
            this.setYTouched(y - this.center.getY());
         }

         this.data[this.calcPos(x, y, z)] = value;
      }

   }

   public void setVoxelOr(int x, int y, int z, byte value) {
      x = x + this.center.getX();
      y = y + this.center.getY();
      z = z + this.center.getZ();
      if (this.testBounds(x, y, z)) {
         if (value != 0) {
            this.setYTouched(y - this.center.getY());
         }

         byte[] var10000 = this.data;
         int var10001 = this.calcPos(x, y, z);
         var10000[var10001] |= value;
      }

   }

   public byte getVoxel(BlockPos relPos, BlockPos pos) {
      return this.getVoxel(pos.getX() - relPos.getX(), pos.getY() - relPos.getY(), pos.getZ() - relPos.getZ());
   }

   public byte getVoxel(BlockPos pos) {
      return this.getVoxel(pos.getX(), pos.getY(), pos.getZ());
   }

   public byte getVoxel(int x, int y, int z) {
      if (this.isYTouched(y)) {
         x = x + this.center.getX();
         y = y + this.center.getY();
         z = z + this.center.getZ();
         return this.testBounds(x, y, z) ? this.data[this.calcPos(x, y, z)] : 0;
      } else {
         return 0;
      }
   }

   private boolean testBounds(int x, int y, int z) {
      return x >= 0 && x < this.lenX && y >= 0 && y < this.lenY && z >= 0 && z < this.lenZ;
   }

   public boolean isYTouched(int y) {
      y = y + this.center.getY();
      return y >= 0 && y < this.lenY && this.touched[y];
   }

   public void setYTouched(int y) {
      y = y + this.center.getY();
      if (y >= 0 && y < this.lenY) {
         this.touched[y] = true;
      }

   }

   public Iterable<SimpleVoxmap.Cell> getAllNonZeroCells() {
      return this.getAllNonZeroCells((byte)-1);
   }

   public Iterable<SimpleVoxmap.Cell> getAllNonZeroCells(final byte mask) {
      return new Iterable<SimpleVoxmap.Cell>() {
         public Iterator<SimpleVoxmap.Cell> iterator() {
            return new AbstractIterator<SimpleVoxmap.Cell>() {
               private int x = -1;
               private int y = 0;
               private int z = 0;
               private int dataPos = -1;
               private final SimpleVoxmap.Cell workingCell = SimpleVoxmap.this.new Cell();
               private final MutableBlockPos dPos;

               {
                  this.dPos = this.workingCell.getPos();
               }

               protected SimpleVoxmap.Cell computeNext() {
                  label28:
                  while(true) {
                     if (this.x < SimpleVoxmap.this.lenX - 1) {
                        ++this.x;
                     } else {
                        if (this.z >= SimpleVoxmap.this.lenZ - 1) {
                           this.x = -1;
                           this.z = 0;
                           ++this.y;

                           while(this.y < SimpleVoxmap.this.lenY) {
                              if (SimpleVoxmap.this.touched[this.y]) {
                                 continue label28;
                              }

                              this.dataPos += SimpleVoxmap.this.layerSize;
                              ++this.y;
                           }

                           return (SimpleVoxmap.Cell)this.endOfData();
                        }

                        this.x = 0;
                        ++this.z;
                     }

                     byte value = (byte)(SimpleVoxmap.this.data[++this.dataPos] & mask);
                     if (value > 0) {
                        this.dPos.setPos(this.x - SimpleVoxmap.this.center.getX(), this.y - SimpleVoxmap.this.center.getY(), this.z - SimpleVoxmap.this.center.getZ());
                        return this.workingCell.setValue(value);
                     }
                  }
               }
            };
         }
      };
   }

   public Iterable<MutableBlockPos> getAllNonZero() {
      return this.getAllNonZero((byte)-1);
   }

   public Iterable<MutableBlockPos> getAllNonZero(final byte mask) {
      return new Iterable<MutableBlockPos>() {
         public Iterator<MutableBlockPos> iterator() {
            return new AbstractIterator<MutableBlockPos>() {
               private int x = -1;
               private int y = 0;
               private int z = 0;
               private int dataPos = -1;
               private boolean yclean;
               private final MutableBlockPos dPos = new MutableBlockPos();

               protected MutableBlockPos computeNext() {
                  label33:
                  while(true) {
                     if (this.x < SimpleVoxmap.this.lenX - 1) {
                        ++this.x;
                     } else {
                        if (this.z >= SimpleVoxmap.this.lenZ - 1) {
                           this.x = -1;
                           this.z = 0;
                           SimpleVoxmap.this.touched[this.y] = !this.yclean;
                           ++this.y;

                           for(this.yclean = true; this.y < SimpleVoxmap.this.lenY; this.yclean = true) {
                              if (SimpleVoxmap.this.touched[this.y]) {
                                 continue label33;
                              }

                              this.dataPos += SimpleVoxmap.this.layerSize;
                              ++this.y;
                           }

                           return (MutableBlockPos)this.endOfData();
                        }

                        this.x = 0;
                        ++this.z;
                     }

                     if ((SimpleVoxmap.this.data[++this.dataPos] & mask) > 0) {
                        this.yclean = false;
                        return this.dPos.setPos(this.x - SimpleVoxmap.this.center.getX(), this.y - SimpleVoxmap.this.center.getY(), this.z - SimpleVoxmap.this.center.getZ());
                     }
                  }
               }
            };
         }
      };
   }

   public Iterable<MutableBlockPos> getTops() {
      return new Iterable<MutableBlockPos>() {
         public Iterator<MutableBlockPos> iterator() {
            return new AbstractIterator<MutableBlockPos>() {
               private int x = -1;
               private int y = 0;
               private int z = 0;
               private final int yStart = this.getStartY();
               private final MutableBlockPos dPos = new MutableBlockPos();

               protected int getStartY() {
                  int yi;
                  for(yi = SimpleVoxmap.this.lenY - 1; yi >= 0 && !SimpleVoxmap.this.touched[yi]; --yi) {
                     ;
                  }

                  return yi;
               }

               protected MutableBlockPos computeNext() {
                  while(true) {
                     if (this.x < SimpleVoxmap.this.lenX - 1) {
                        ++this.x;
                     } else {
                        if (this.z >= SimpleVoxmap.this.lenZ - 1) {
                           return (MutableBlockPos)this.endOfData();
                        }

                        this.x = 0;
                        ++this.z;
                     }

                     this.y = this.yStart;

                     for(int dataPos = SimpleVoxmap.this.calcPos(this.x, this.y, this.z); this.y >= 0; --this.y) {
                        if (SimpleVoxmap.this.data[dataPos] != 0) {
                           return this.dPos.setPos(this.x - SimpleVoxmap.this.center.getX(), this.y - SimpleVoxmap.this.center.getY(), this.z - SimpleVoxmap.this.center.getZ());
                        }

                        dataPos -= SimpleVoxmap.this.layerSize;
                     }
                  }
               }
            };
         }
      };
   }

   public void print() {
      for(int y = 0; y < this.lenY; ++y) {
         System.out.println("Touched: " + this.touched[y]);

         for(int z = 0; z < this.lenZ; ++z) {
            String buffer = "";

            for(int x = 0; x < this.lenX; ++x) {
               byte b = this.getVoxel(x - this.center.getX(), y - this.center.getY(), z - this.center.getZ());
               if ((b & 32) != 0) {
                  buffer = buffer + "B";
               } else if ((b & 16) != 0) {
                  buffer = buffer + "T";
               } else {
                  buffer = buffer + Integer.toHexString(b & 15);
               }
            }

            System.out.println(buffer);
         }

         String buffer = "";

         for(int k = 0; k < this.lenX; ++k) {
            buffer = buffer + "-";
         }

         System.out.println(buffer);
      }

   }

   public class Cell {
      private byte value;
      private final MutableBlockPos pos = new MutableBlockPos();

      public Cell() {
      }

      public SimpleVoxmap.Cell setValue(byte value) {
         this.value = value;
         return this;
      }

      public byte getValue() {
         return this.value;
      }

      public MutableBlockPos getPos() {
         return this.pos;
      }
   }

   public interface IBlitOp {
      byte getOp(byte var1, byte var2);
   }

   public interface IFilterOp {
      byte getOp(byte var1);
   }
}
