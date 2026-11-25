package org.orecruncher.dsurround.client.footsteps.facade;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.lib.compat.ModEnvironment;

@SideOnly(Side.CLIENT)
public final class FacadeHelper {
   private static final Map<Block, IFacadeAccessor> crackers = new Reference2ObjectOpenHashMap();

   private static void addAccessor(@Nonnull List<IFacadeAccessor> accessors, @Nonnull IFacadeAccessor accessor) {
      if (accessor.isValid()) {
         ModBase.log().info("Facade Accessor: %s", new Object[]{accessor.getName()});
         accessors.add(accessor);
      }

   }

   protected FacadeHelper() {
   }

   @Nonnull
   public static IBlockState resolveState(@Nonnull EntityLivingBase entity, @Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull Vec3d pos, @Nullable EnumFacing side) {
      if (crackers.size() > 0 && state != Blocks.AIR.getDefaultState()) {
         IFacadeAccessor accessor = (IFacadeAccessor)crackers.get(state.getBlock());
         if (accessor != null) {
            IBlockState newState = accessor.getBlockState(entity, state, world, pos, side);
            if (newState != null) {
               return newState;
            }
         }
      }

      return state;
   }

   static {
      List<IFacadeAccessor> accessors = new ArrayList();
      if (ModEnvironment.LittleTiles.isLoaded()) {
         addAccessor(accessors, new LittleTilesAccessor());
      }

      if (ModEnvironment.ForgeMultipartCBE.isLoaded()) {
         addAccessor(accessors, new ForgeMultiPartCBE());
      }

      if (ModEnvironment.ConnectedTextures.isLoaded()) {
         addAccessor(accessors, new ConnectedTexturesAccessor());
      }

      addAccessor(accessors, new EnderIOFacadeAccessor());
      addAccessor(accessors, new CoFHCoreCoverAccessor());
      addAccessor(accessors, new ChiselFacadeAccessor());
      if (accessors.size() > 0) {
         label33:
         for(Block b : Block.REGISTRY) {
            Iterator var3 = accessors.iterator();

            IFacadeAccessor accessor;
            while(true) {
               if (!var3.hasNext()) {
                  continue label33;
               }

               accessor = (IFacadeAccessor)var3.next();
               if (accessor.instanceOf(b)) {
                  break;
               }
            }

            crackers.put(b, accessor);
         }
      }

   }
}
