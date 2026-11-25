package noppes.vc.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.vc.CommonUtils;
import noppes.vc.Server;
import noppes.vc.blocks.tiles.TileBook;
import noppes.vc.constants.PacketClient;

public class BlockBook extends BlockBasicRotated {
   public static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.20000000298023224D, 1.0D);

   public BlockBook() {
      super(Blocks.PLANKS);
      this.setSoundType(SoundType.WOOD);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      return AABB;
   }

   public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (par1World.isRemote) {
         return true;
      } else {
         TileEntity tile = par1World.getTileEntity(pos);
         if (!(tile instanceof TileBook)) {
            return false;
         } else {
            ItemStack currentItem = player.inventory.getCurrentItem();
            if (currentItem.getItem().getUnlocalizedName().equals("item.npcwand")) {
               ((TileBook)tile).book = CommonUtils.ChangeItemStack(((TileBook)tile).book, Items.WRITABLE_BOOK);
            }

            Server.sendData((EntityPlayerMP)player, PacketClient.OPEN_BOOK, pos.getX(), pos.getY(), pos.getZ(), ((TileBook)tile).book.writeToNBT(new NBTTagCompound()));
            return true;
         }
      }
   }

   public String getUnlocalizedName() {
      return "item.book";
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileBook();
   }
}
