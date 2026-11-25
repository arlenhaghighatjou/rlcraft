package com.teamacronymcoders.base.guisystem;

import com.teamacronymcoders.base.IBaseMod;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GuiHandler implements IGuiHandler {
   public GuiHandler(IBaseMod instance) {
      NetworkRegistry.INSTANCE.registerGuiHandler(instance, this);
   }

   public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      BlockPos blockPos = new BlockPos(x, y, z);
      return this.getHasGUI(id, player, world, blockPos).map((openableGUI) -> {
         return openableGUI.getContainer(player, world, blockPos);
      }).orElse((Object)null);
   }

   public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      BlockPos blockPos = new BlockPos(x, y, z);
      return this.getHasGUI(id, player, world, blockPos).map((openableGUI) -> {
         return openableGUI.getGui(player, world, blockPos);
      }).orElse((Object)null);
   }

   private Optional<IHasGui> getHasGUI(int id, EntityPlayer player, World world, BlockPos blockPos) {
      return Optional.ofNullable(GuiCarrier.values()[id].getHasGUI(player, world, blockPos));
   }
}
