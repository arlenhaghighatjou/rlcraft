package com.ferreusveritas.dynamictrees.api.worldgen;

import com.ferreusveritas.dynamictrees.systems.poissondisc.PoissonDisc;
import java.util.List;

public interface IPoissonDiscProvider {
   List<PoissonDisc> getPoissonDiscs(int var1, int var2, int var3);

   byte[] getChunkPoissonData(int var1, int var2, int var3);

   void setChunkPoissonData(int var1, int var2, int var3, byte[] var4);

   void unloadChunkPoissonData(int var1, int var2, int var3);
}
