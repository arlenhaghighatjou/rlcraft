package noppes.vc.client.gui;

import net.minecraft.util.math.BlockPos;
import noppes.vc.blocks.tiles.TileBigSign;
import noppes.vc.client.Client;
import noppes.vc.constants.PacketServer;
import noppes.vc.shared.gui.GuiTextAreaScreen;

public class GuiBigSign extends GuiTextAreaScreen {
   public TileBigSign tile;

   public GuiBigSign(int x, int y, int z) {
      super("");
      this.tile = (TileBigSign)this.player.world.getTileEntity(new BlockPos(x, y, z));
      this.text = this.tile.getText();
   }

   public void save() {
      super.save();
      Client.sendData(PacketServer.SAVE_SIGN, this.tile.getPos().getX(), this.tile.getPos().getY(), this.tile.getPos().getZ(), this.text);
   }
}
