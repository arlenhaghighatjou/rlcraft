package com.ferreusveritas.dynamictrees.cells;

import com.ferreusveritas.dynamictrees.util.SimpleVoxmap;
import net.minecraft.util.math.BlockPos;

public class LeafClusters {
   public static final SimpleVoxmap NULLMAP = new SimpleVoxmap(1, 1, 1, new byte[]{0});
   public static final SimpleVoxmap deciduous = (new SimpleVoxmap(5, 4, 5, new byte[]{0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 3, 4, 3, 1, 1, 4, 0, 4, 1, 1, 3, 4, 3, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 2, 3, 2, 1, 1, 3, 4, 3, 1, 1, 2, 3, 2, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0})).setCenter(new BlockPos(2, 1, 2));
   public static final SimpleVoxmap conifer = (new SimpleVoxmap(5, 2, 5, new byte[]{0, 0, 1, 0, 0, 0, 1, 2, 1, 0, 1, 2, 0, 2, 1, 0, 1, 2, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0})).setCenter(new BlockPos(2, 0, 2));
   public static final SimpleVoxmap acacia = (new SimpleVoxmap(7, 2, 7, new byte[]{0, 0, 1, 1, 1, 0, 0, 0, 1, 2, 2, 2, 1, 0, 1, 2, 3, 4, 3, 2, 1, 1, 2, 4, 0, 4, 2, 1, 1, 2, 3, 4, 3, 2, 1, 0, 1, 2, 2, 2, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 2, 2, 2, 1, 0, 0, 1, 2, 2, 2, 1, 0, 0, 1, 2, 2, 2, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0})).setCenter(new BlockPos(3, 0, 3));
   public static final SimpleVoxmap darkoak = (new SimpleVoxmap(7, 5, 7, new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 2, 2, 2, 1, 0, 1, 2, 3, 4, 3, 2, 1, 1, 2, 4, 0, 4, 2, 1, 1, 2, 3, 4, 3, 2, 1, 0, 1, 2, 2, 2, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 2, 2, 2, 1, 0, 0, 1, 2, 4, 2, 1, 0, 0, 1, 2, 2, 2, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 2, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0})).setCenter(new BlockPos(3, 1, 3));
   public static final SimpleVoxmap bare = new SimpleVoxmap(1, 1, 1, new byte[]{32});
   public static final SimpleVoxmap palm = (new SimpleVoxmap(3, 3, 3, new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 1, 2, 1, 2, 3, 2, 1, 2, 1})).setCenter(new BlockPos(3, 0, 3));
   public static final SimpleVoxmap bush = (new SimpleVoxmap(5, 2, 5, new byte[]{0, 1, 1, 1, 0, 1, 2, 3, 2, 1, 1, 3, 0, 3, 1, 1, 2, 3, 2, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0})).setCenter(new BlockPos(2, 0, 2));

   public LeafClusters() {
   }
}
