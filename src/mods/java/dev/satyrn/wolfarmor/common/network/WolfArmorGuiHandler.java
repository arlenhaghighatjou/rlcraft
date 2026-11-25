package dev.satyrn.wolfarmor.common.network;

import dev.satyrn.wolfarmor.api.entity.passive.IArmoredWolf;
import dev.satyrn.wolfarmor.client.gui.GuiWolfInventory;
import dev.satyrn.wolfarmor.common.inventory.ContainerWolfInventory;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class WolfArmorGuiHandler implements IGuiHandler {
   public WolfArmorGuiHandler() {
   }

   @Nullable
   public Object getServerGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, int x, int y, int z) {
      Entity entity = world.getEntityByID(ID);
      return entity != null && entity instanceof EntityWolf ? new ContainerWolfInventory(player.inventory, ((IArmoredWolf)entity).getInventory(), (EntityWolf)entity, player) : null;
   }

   @Nullable
   public Object getClientGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, int x, int y, int z) {
      Entity entity = world.getEntityByID(ID);
      return entity != null && entity instanceof EntityWolf ? new GuiWolfInventory(player.inventory, ((IArmoredWolf)entity).getInventory(), (EntityWolf)entity, player) : null;
   }
}
