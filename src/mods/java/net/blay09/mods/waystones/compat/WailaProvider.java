package net.blay09.mods.waystones.compat;

import java.util.List;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

@WailaPlugin
public class WailaProvider implements IWailaPlugin {
   public WailaProvider() {
   }

   public void register(IWailaRegistrar registrar) {
      registrar.registerBodyProvider(new WailaProvider.WaystoneDataProvider(), BlockWaystone.class);
   }

   private static class WaystoneDataProvider implements IWailaDataProvider {
      private WaystoneDataProvider() {
      }

      public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
         return ItemStack.EMPTY;
      }

      public List<String> getWailaHead(ItemStack itemStack, List<String> list, IWailaDataAccessor accessor, IWailaConfigHandler config) {
         return list;
      }

      public List<String> getWailaBody(ItemStack itemStack, List<String> list, IWailaDataAccessor accessor, IWailaConfigHandler config) {
         TileEntity tileEntity = accessor.getTileEntity();
         if (tileEntity instanceof TileWaystone) {
            list.add(TextFormatting.DARK_AQUA + ((TileWaystone)tileEntity).getParent().getWaystoneName());
         }

         return list;
      }

      public List<String> getWailaTail(ItemStack itemStack, List<String> list, IWailaDataAccessor accessor, IWailaConfigHandler config) {
         return list;
      }

      public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tileEntity, NBTTagCompound tagCompound, World world, BlockPos pos) {
         if (tileEntity != null) {
            tileEntity.writeToNBT(tagCompound);
         }

         return tagCompound;
      }
   }
}
