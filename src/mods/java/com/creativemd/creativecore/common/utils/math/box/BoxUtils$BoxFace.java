package com.creativemd.creativecore.common.utils.math.box;

import javax.vecmath.Vector3d;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

public enum BoxUtils$BoxFace {
   EAST(EnumFacing.EAST, new BoxUtils$BoxCorner[]{BoxUtils$BoxCorner.EUS, BoxUtils$BoxCorner.EDS, BoxUtils$BoxCorner.EDN, BoxUtils$BoxCorner.EUN}),
   WEST(EnumFacing.WEST, new BoxUtils$BoxCorner[]{BoxUtils$BoxCorner.WUN, BoxUtils$BoxCorner.WDN, BoxUtils$BoxCorner.WDS, BoxUtils$BoxCorner.WUS}),
   UP(EnumFacing.UP, new BoxUtils$BoxCorner[]{BoxUtils$BoxCorner.WUN, BoxUtils$BoxCorner.WUS, BoxUtils$BoxCorner.EUS, BoxUtils$BoxCorner.EUN}),
   DOWN(EnumFacing.DOWN, new BoxUtils$BoxCorner[]{BoxUtils$BoxCorner.WDS, BoxUtils$BoxCorner.WDN, BoxUtils$BoxCorner.EDN, BoxUtils$BoxCorner.EDS}),
   SOUTH(EnumFacing.SOUTH, new BoxUtils$BoxCorner[]{BoxUtils$BoxCorner.WUS, BoxUtils$BoxCorner.WDS, BoxUtils$BoxCorner.EDS, BoxUtils$BoxCorner.EUS}),
   NORTH(EnumFacing.NORTH, new BoxUtils$BoxCorner[]{BoxUtils$BoxCorner.EUN, BoxUtils$BoxCorner.EDN, BoxUtils$BoxCorner.WDN, BoxUtils$BoxCorner.WUN});

   public final EnumFacing facing;
   public final BoxUtils$BoxCorner[] corners;

   private BoxUtils$BoxFace(EnumFacing facing, BoxUtils$BoxCorner[] corners) {
      this.facing = facing;
      this.corners = corners;
   }

   public Vector3d first(Vector3d[] corners) {
      return corners[this.corners[0].ordinal()];
   }

   public Vector3d normal(Vector3d[] corners) {
      Vector3d origin = this.first(corners);
      Vector3d first = new Vector3d(corners[this.corners[1].ordinal()]);
      Vector3d second = new Vector3d(corners[this.corners[2].ordinal()]);
      first.sub(origin);
      second.sub(origin);
      return new Vector3d(first.y * second.z - first.z * second.y, first.z * second.x - first.x * second.z, first.x * second.y - first.y * second.x);
   }

   public static BoxUtils$BoxFace getFace(EnumFacing facing) {
      // $FF: Couldn't be decompiled
   }

   public static BoxUtils$BoxFace getFace(Axis axis, boolean direction) {
      // $FF: Couldn't be decompiled
   }
}
