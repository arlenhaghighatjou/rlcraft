package cursedflames.bountifulbaubles.block;

import java.util.ArrayList;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;

public abstract class GenericTileBlock extends GenericBlock implements ITileEntityProvider {
   public final Class<? extends TileEntity> tileEntity;

   public GenericTileBlock(String modId, String unlocalizedName, Class<? extends TileEntity> te, CreativeTabs tab) {
      super(modId, unlocalizedName, tab);
      this.tileEntity = te;
   }

   public GenericTileBlock(String modId, String unlocalizedName, Class<? extends TileEntity> te, CreativeTabs tab, Material mat) {
      super(modId, unlocalizedName, tab, mat);
      this.tileEntity = te;
   }

   public GenericTileBlock(String modId, String unlocalizedName, Class<? extends TileEntity> te, CreativeTabs tab, Material mat, float hardness, float resistance) {
      super(modId, unlocalizedName, tab, mat, hardness, resistance);
      this.tileEntity = te;
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      try {
         return (TileEntity)this.tileEntity.newInstance();
      } catch (IllegalAccessException | InstantiationException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      return new ArrayList();
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      if (!world.isRemote) {
         TileEntity te = world.getTileEntity(pos);
         if (te instanceof GenericTileEntity) {
            ItemStack item = new ItemStack(state.getBlock());
            NBTTagCompound blockData = ((GenericTileEntity)te).getBlockBreakNBT();
            if (blockData != null) {
               item.setTagInfo("blockData", blockData);
            }

            world.spawnEntity(new EntityItem(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), item));
         }
      }

      super.breakBlock(world, pos, state);
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof GenericTileEntity) {
         NBTTagCompound blockData = stack.getSubCompound("blockData");
         if (blockData != null) {
            ((GenericTileEntity)te).loadBlockPlaceNBT(stack.getOrCreateSubCompound("blockData"));
         }
      }

   }

   public TileEntity safelyGetTileEntity(IBlockAccess world, BlockPos pos) {
      return world instanceof ChunkCache ? ((ChunkCache)world).getTileEntity(pos, EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
   }
}
