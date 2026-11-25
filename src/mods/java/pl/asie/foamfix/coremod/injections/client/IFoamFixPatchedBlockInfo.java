package pl.asie.foamfix.coremod.injections.client;

public interface IFoamFixPatchedBlockInfo {
   int[][][] getRawS();

   int[][][] getRawB();

   void updateAO();
}
