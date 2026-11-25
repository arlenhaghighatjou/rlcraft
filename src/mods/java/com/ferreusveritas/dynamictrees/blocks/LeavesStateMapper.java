package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.client.QuadManipulator;
import com.google.common.collect.UnmodifiableIterator;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;

public class LeavesStateMapper implements IStateMapper {
   public LeavesStateMapper() {
   }

   public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn) {
      Map<IBlockState, ModelResourceLocation> modelMap = new LinkedHashMap();
      if (blockIn instanceof BlockDynamicLeaves) {
         BlockDynamicLeaves leaves = (BlockDynamicLeaves)blockIn;
         UnmodifiableIterator var4 = blockIn.getBlockState().getValidStates().iterator();

         while(var4.hasNext()) {
            IBlockState iblockstate = (IBlockState)var4.next();
            ModelResourceLocation resloc = QuadManipulator.getModelLocation(leaves.getProperties(iblockstate).getPrimitiveLeaves());
            if (resloc != null) {
               modelMap.put(iblockstate, resloc);
            }
         }
      }

      return modelMap;
   }
}
