package tschipp.carryon.common.capabilities.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tschipp.carryon.common.capabilities.IPosition;
import tschipp.carryon.common.capabilities.PositionProvider;

public class PositionCommonEvents {
   public PositionCommonEvents() {
   }

   @SubscribeEvent
   public void onAttachCaps(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof EntityPlayer) {
         event.addCapability(new ResourceLocation("carryon", "position"), new PositionProvider());
      }

   }

   @SubscribeEvent
   public void onBlockRight(RightClickBlock event) {
      BlockPos pos = event.getPos();
      World world = event.getWorld();
      EntityPlayer player = event.getEntityPlayer();
      if (!event.isCanceled()) {
         if (player != null) {
            if (!(player instanceof FakePlayer)) {
               TileEntity te = world.getTileEntity(pos);
               if (te != null && player.hasCapability(PositionProvider.POSITION_CAPABILITY, (EnumFacing)null)) {
                  IPosition cap = (IPosition)player.getCapability(PositionProvider.POSITION_CAPABILITY, (EnumFacing)null);
                  cap.setBlockActivated(true);
                  cap.setPos(pos);
               }

            }
         }
      }
   }
}
