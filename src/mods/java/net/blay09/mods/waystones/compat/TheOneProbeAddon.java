package net.blay09.mods.waystones.compat;

import com.google.common.base.Function;
import javax.annotation.Nullable;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class TheOneProbeAddon implements Function<ITheOneProbe, Void> {
   public TheOneProbeAddon() {
   }

   @Nullable
   public Void apply(@Nullable ITheOneProbe top) {
      if (top != null) {
         top.registerProvider(new TheOneProbeAddon.ProbeInfoProvider());
      }

      return null;
   }

   @Nullable
   private static <T extends TileEntity> T tryGetTileEntity(World world, BlockPos pos, Class<T> tileClass) {
      TileEntity tileEntity = world.getTileEntity(pos);
      return (T)(tileEntity != null && tileClass.isAssignableFrom(tileEntity.getClass()) ? tileEntity : null);
   }

   public static class ProbeInfoProvider implements IProbeInfoProvider {
      public ProbeInfoProvider() {
      }

      public String getID() {
         return "waystones";
      }

      public void addProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
         if (state.getBlock() instanceof BlockWaystone) {
            TileWaystone tileEntity = (TileWaystone)TheOneProbeAddon.tryGetTileEntity(world, data.getPos(), TileWaystone.class);
            if (tileEntity != null) {
               info.text(TextFormatting.DARK_AQUA + tileEntity.getParent().getWaystoneName());
            }
         }

      }
   }
}
