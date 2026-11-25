package jaredbgreat.dldungeons.builder;

import jaredbgreat.dldungeons.api.DLDEvent;
import jaredbgreat.dldungeons.util.debug.Logging;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DBlock extends AbstractBlock {
   protected IBlockState block;

   DBlock(IBlockState block) {
      this.block = block;
   }

   /** @deprecated */
   @Deprecated
   public void placeNoMeta(World world, int x, int y, int z) {
      if (!isProtectedBlock(world, x, y, z)) {
         BlockPos pos = new BlockPos(x, y, z);
         if (!MinecraftForge.TERRAIN_GEN_BUS.post(new DLDEvent.PlaceDBlock(world, pos, this))) {
            world.setBlockState(new BlockPos(x, y, z), this.block);
         }
      }
   }

   public void place(World world, int x, int y, int z) {
      if (!isProtectedBlock(world, x, y, z)) {
         BlockPos pos = new BlockPos(x, y, z);
         if (!MinecraftForge.TERRAIN_GEN_BUS.post(new DLDEvent.PlaceDBlock(world, pos, this))) {
            world.setBlockState(pos, this.block);
         }
      }
   }

   public boolean equals(Object other) {
      return !(other instanceof DBlock) ? false : this.block.equals(((DBlock)other).block);
   }

   public int hashCode() {
      return this.block.hashCode();
   }

   public static DBlock makeDBlock(String id) {
      try {
         StringTokenizer nums = new StringTokenizer(id, ":({[]})");
         String modid = nums.nextToken();
         ResourceLocation name = new ResourceLocation(modid + ":" + nums.nextToken());
         Block theBlock = (Block)GameRegistry.findRegistry(Block.class).getValue(name);
         if (theBlock == null) {
            String error = "[DLDUNGEONS] ERROR! Block read as \"" + id + "\" was was not in registry (returned null).";
            Logging.logError(error);
            throw new NoSuchElementException(error);
         } else {
            int meta;
            if (nums.hasMoreElements()) {
               meta = Integer.parseInt(nums.nextToken());
            } else {
               meta = 0;
            }

            return new DBlock(theBlock.getStateFromMeta(meta));
         }
      } catch (NoSuchElementException var7) {
         throw new NoSuchElementException("Something was wrong with " + id + "; could not find all elements.");
      }
   }

   public Object getContents() {
      return this.block;
   }
}
