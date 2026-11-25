package com.teamacronymcoders.base.multiblocksystem;

import java.util.HashMap;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public final class BlockFacings {
   public static final BlockFacings NONE;
   public static final BlockFacings ALL;
   public static final BlockFacings DOWN;
   public static final BlockFacings UP;
   public static final BlockFacings NORTH;
   public static final BlockFacings SOUTH;
   public static final BlockFacings WEST;
   public static final BlockFacings EAST;
   public static final PropertyBool FACING_DOWN = PropertyBool.create("downFacing");
   public static final PropertyBool FACING_UP = PropertyBool.create("upFacing");
   public static final PropertyBool FACING_WEST = PropertyBool.create("westFacing");
   public static final PropertyBool FACING_EAST = PropertyBool.create("eastFacing");
   public static final PropertyBool FACING_NORTH = PropertyBool.create("northFacing");
   public static final PropertyBool FACING_SOUTH = PropertyBool.create("southFacing");
   private byte _value;
   private static HashMap<Byte, BlockFacings> s_cache = new HashMap(8);

   public boolean isSet(EnumFacing facing) {
      return 0 != (this._value & 1 << facing.getIndex());
   }

   public boolean none() {
      return 0 == this._value;
   }

   public boolean all() {
      return 63 == this._value;
   }

   public boolean down() {
      return this.isSet(EnumFacing.DOWN);
   }

   public boolean up() {
      return this.isSet(EnumFacing.UP);
   }

   public boolean north() {
      return this.isSet(EnumFacing.NORTH);
   }

   public boolean south() {
      return this.isSet(EnumFacing.SOUTH);
   }

   public boolean west() {
      return this.isSet(EnumFacing.WEST);
   }

   public boolean east() {
      return this.isSet(EnumFacing.EAST);
   }

   public IBlockState toBlockState(IBlockState state) {
      return state.withProperty(FACING_DOWN, Boolean.valueOf(this.isSet(EnumFacing.DOWN))).withProperty(FACING_UP, Boolean.valueOf(this.isSet(EnumFacing.UP))).withProperty(FACING_WEST, Boolean.valueOf(this.isSet(EnumFacing.WEST))).withProperty(FACING_EAST, Boolean.valueOf(this.isSet(EnumFacing.EAST))).withProperty(FACING_NORTH, Boolean.valueOf(this.isSet(EnumFacing.NORTH))).withProperty(FACING_SOUTH, Boolean.valueOf(this.isSet(EnumFacing.SOUTH)));
   }

   public BlockFacings set(EnumFacing facing, boolean value) {
      byte newHash = this._value;
      if (value) {
         newHash = (byte)(newHash | 1 << facing.getIndex());
      } else {
         newHash = (byte)(newHash & ~(1 << facing.getIndex()));
      }

      return from(Byte.valueOf(newHash));
   }

   public int countFacesIf(boolean areSet) {
      int checkFor = areSet ? 1 : 0;
      int mask = this._value;
      int faces = 0;

      for(int i = 0; i < 6; mask >>>= 1) {
         if ((mask & 1) == checkFor) {
            ++faces;
         }

         ++i;
      }

      return faces;
   }

   public PropertyBlockFacings toProperty() {
      PropertyBlockFacings[] values = PropertyBlockFacings.values();

      for(PropertyBlockFacings value : values) {
         if (value._hash == this._value) {
            return value;
         }
      }

      return PropertyBlockFacings.None;
   }

   public BlockPos offsetBlockPos(BlockPos originalPosition) {
      int x = 0;
      int y = 0;
      int z = 0;

      for(EnumFacing facing : EnumFacing.VALUES) {
         if (this.isSet(facing)) {
            x += facing.getFrontOffsetX();
            y += facing.getFrontOffsetY();
            z += facing.getFrontOffsetZ();
         }
      }

      return originalPosition.add(x, y, z);
   }

   public EnumFacing firstIf(boolean isSet) {
      for(EnumFacing facing : EnumFacing.VALUES) {
         if (isSet == this.isSet(facing)) {
            return facing;
         }
      }

      return null;
   }

   public static BlockFacings from(boolean down, boolean up, boolean north, boolean south, boolean west, boolean east) {
      return from(computeHash(down, up, north, south, west, east));
   }

   public static BlockFacings from(boolean[] facings) {
      return from(computeHash(facings));
   }

   public String toString() {
      return String.format("Facings: %s%s%s%s%s%s", this.isSet(EnumFacing.DOWN) ? "DOWN " : "", this.isSet(EnumFacing.UP) ? "UP " : "", this.isSet(EnumFacing.NORTH) ? "NORTH " : "", this.isSet(EnumFacing.SOUTH) ? "SOUTH " : "", this.isSet(EnumFacing.WEST) ? "WEST " : "", this.isSet(EnumFacing.EAST) ? "EAST " : "");
   }

   static BlockFacings from(Byte hash) {
      BlockFacings facings = (BlockFacings)s_cache.get(hash);
      if (null == facings) {
         facings = new BlockFacings(hash.byteValue());
         s_cache.put(hash, facings);
      }

      return facings;
   }

   private BlockFacings(byte value) {
      this._value = value;
   }

   static Byte computeHash(boolean down, boolean up, boolean north, boolean south, boolean west, boolean east) {
      byte hash = 0;
      if (down) {
         hash = (byte)(hash | 1 << EnumFacing.DOWN.getIndex());
      }

      if (up) {
         hash = (byte)(hash | 1 << EnumFacing.UP.getIndex());
      }

      if (north) {
         hash = (byte)(hash | 1 << EnumFacing.NORTH.getIndex());
      }

      if (south) {
         hash = (byte)(hash | 1 << EnumFacing.SOUTH.getIndex());
      }

      if (west) {
         hash = (byte)(hash | 1 << EnumFacing.WEST.getIndex());
      }

      if (east) {
         hash = (byte)(hash | 1 << EnumFacing.EAST.getIndex());
      }

      return hash;
   }

   static Byte computeHash(boolean[] facings) {
      byte hash = 0;
      int len = null == facings ? -1 : facings.length;
      if (len >= 0 && len <= EnumFacing.VALUES.length) {
         for(int i = 0; i < len; ++i) {
            if (facings[i]) {
               hash = (byte)(hash | 1 << EnumFacing.VALUES[i].getIndex());
            }
         }

         return hash;
      } else {
         throw new IllegalArgumentException("Invalid length of facings array");
      }
   }

   static {
      Byte hash = computeHash(false, false, false, false, false, false);
      s_cache.put(hash, NONE = new BlockFacings(hash.byteValue()));
      hash = computeHash(true, true, true, true, true, true);
      s_cache.put(hash, ALL = new BlockFacings(hash.byteValue()));
      hash = computeHash(true, false, false, false, false, false);
      s_cache.put(hash, DOWN = new BlockFacings(hash.byteValue()));
      hash = computeHash(false, true, false, false, false, false);
      s_cache.put(hash, UP = new BlockFacings(hash.byteValue()));
      hash = computeHash(false, false, true, false, false, false);
      s_cache.put(hash, NORTH = new BlockFacings(hash.byteValue()));
      hash = computeHash(false, false, false, true, false, false);
      s_cache.put(hash, SOUTH = new BlockFacings(hash.byteValue()));
      hash = computeHash(false, false, false, false, true, false);
      s_cache.put(hash, WEST = new BlockFacings(hash.byteValue()));
      hash = computeHash(false, false, false, false, false, true);
      s_cache.put(hash, EAST = new BlockFacings(hash.byteValue()));
   }
}
