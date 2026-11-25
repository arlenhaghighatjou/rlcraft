package tschipp.carryon.common.capabilities.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Close;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tschipp.carryon.common.capabilities.IPosition;
import tschipp.carryon.common.capabilities.PositionProvider;

public class PositionClientEvents {
   public PositionClientEvents() {
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void onGui(DrawScreenEvent event) {
      if (event.getGui() != null) {
         EntityPlayer player = Minecraft.getMinecraft().player;
         boolean inventory = event.getGui() instanceof GuiContainer;
         if (player != null && inventory && player.hasCapability(PositionProvider.POSITION_CAPABILITY, (EnumFacing)null)) {
            IPosition cap = (IPosition)player.getCapability(PositionProvider.POSITION_CAPABILITY, (EnumFacing)null);
            if (cap.isBlockActivated()) {
               World world = player.world;
               BlockPos pos = cap.getPos();
               if (world != null) {
                  TileEntity te = world.getTileEntity(pos);
                  if (te == null) {
                     Minecraft.getMinecraft().currentScreen = null;
                     Minecraft.getMinecraft().setIngameFocus();
                     cap.setBlockActivated(false);
                     cap.setPos(new BlockPos(0, 0, 0));
                  }
               }
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void onGuiClose(Close event) {
      EntityPlayer player = event.getEntityPlayer();
      if (player.hasCapability(PositionProvider.POSITION_CAPABILITY, (EnumFacing)null)) {
         IPosition cap = (IPosition)player.getCapability(PositionProvider.POSITION_CAPABILITY, (EnumFacing)null);
         cap.setBlockActivated(false);
         cap.setPos(new BlockPos(0, 0, 0));
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent event) {
      if (event.side == Side.CLIENT) {
         EntityPlayer player = event.player;
         if (player.hasCapability(PositionProvider.POSITION_CAPABILITY, (EnumFacing)null)) {
            IPosition cap = (IPosition)player.getCapability(PositionProvider.POSITION_CAPABILITY, (EnumFacing)null);
            if (cap.isBlockActivated() && Minecraft.getMinecraft().currentScreen == null) {
               cap.setBlockActivated(false);
               cap.setPos(new BlockPos(0, 0, 0));
            }
         }
      }

   }
}
