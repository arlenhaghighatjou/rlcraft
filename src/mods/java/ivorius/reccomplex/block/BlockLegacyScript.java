package ivorius.reccomplex.block;

import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.utils.UnstableBlock;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.RunTransformer;
import ivorius.reccomplex.world.gen.script.WorldScript;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockLegacyScript extends Block implements UnstableBlock {
   public BlockLegacyScript() {
      super(Material.IRON);
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote && playerIn instanceof EntityPlayerMP && playerIn.canUseCommand(2, "")) {
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         if (tileEntity instanceof BlockLegacyScript.TileLegacyScript) {
            WorldScript script = ((BlockLegacyScript.TileLegacyScript)tileEntity).script;
            worldIn.setBlockState(pos, RCBlocks.spawnScript.getDefaultState());
            TileEntity newTileEntity = worldIn.getTileEntity(pos);
            if (newTileEntity instanceof TileEntityBlockScript) {
               ((TileEntityBlockScript)newTileEntity).script.scripts.add(script);
            }
         }
      }

      return true;
   }

   public boolean hasTileEntity(IBlockState state) {
      return true;
   }

   public abstract static class TileLegacyScript<T extends WorldScript<I>, I extends NBTStorable> extends TileEntity implements GeneratingTileEntity<I> {
      public T script = (T)this.createScript();

      public TileLegacyScript() {
      }

      public abstract T createScript();

      public void readFromNBT(NBTTagCompound nbtTagCompound) {
         super.readFromNBT(nbtTagCompound);
         if (nbtTagCompound.hasKey("script")) {
            this.script.readFromNBT(nbtTagCompound.getCompoundTag("script"));
         } else {
            this.script.readFromNBT(nbtTagCompound);
         }

      }

      public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
         super.writeToNBT(nbtTagCompound);
         NBTTagCompound scriptCompound = new NBTTagCompound();
         this.script.writeToNBT(scriptCompound);
         nbtTagCompound.setTag("script", scriptCompound);
         return nbtTagCompound;
      }

      public I prepareInstanceData(StructurePrepareContext context) {
         return (I)this.script.prepareInstanceData(context, this.getPos());
      }

      public I loadInstanceData(StructureLoadContext context, NBTBase nbt) {
         return (I)this.script.loadInstanceData(context, nbt);
      }

      public void generate(StructureSpawnContext context, RunTransformer transformer, I instanceData) {
         this.script.generate(context, transformer, instanceData, this.pos);
      }
   }
}
