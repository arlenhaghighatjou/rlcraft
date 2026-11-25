package com.github.alexthe666.iceandfire.client;

import com.github.alexthe666.iceandfire.client.gui.GuiDragon;
import com.github.alexthe666.iceandfire.client.gui.GuiHippocampus;
import com.github.alexthe666.iceandfire.client.gui.GuiHippogryph;
import com.github.alexthe666.iceandfire.client.gui.GuiLectern;
import com.github.alexthe666.iceandfire.client.gui.GuiMyrmexCocoon;
import com.github.alexthe666.iceandfire.client.gui.GuiPodium;
import com.github.alexthe666.iceandfire.client.gui.bestiary.GuiBestiary;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityLectern;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityMyrmexCocoon;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPodium;
import com.github.alexthe666.iceandfire.inventory.ContainerDragon;
import com.github.alexthe666.iceandfire.inventory.ContainerHippocampus;
import com.github.alexthe666.iceandfire.inventory.ContainerHippogryph;
import com.github.alexthe666.iceandfire.inventory.ContainerLectern;
import com.github.alexthe666.iceandfire.inventory.ContainerMyrmexCocoon;
import com.github.alexthe666.iceandfire.inventory.ContainerPodium;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler {
   public GuiHandler() {
   }

   public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      Entity entity = world.getEntityByID(x);
      TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
      switch(id) {
      case 0:
         if (entity != null && entity instanceof EntityDragonBase) {
            return new ContainerDragon((EntityDragonBase)entity, player);
         }
         break;
      case 1:
         if (tile != null && tile instanceof TileEntityPodium) {
            return new ContainerPodium(player.inventory, (TileEntityPodium)tile, player);
         }
         break;
      case 2:
         if (tile != null && tile instanceof TileEntityLectern) {
            return new ContainerLectern(player.inventory, (TileEntityLectern)tile);
         }
      case 3:
      default:
         break;
      case 4:
         if (entity != null && entity instanceof EntityHippogryph) {
            return new ContainerHippogryph((EntityHippogryph)entity, player);
         }
         break;
      case 5:
         if (entity != null && entity instanceof EntityHippocampus) {
            return new ContainerHippocampus((EntityHippocampus)entity, player);
         }
         break;
      case 6:
         if (tile != null && tile instanceof TileEntityMyrmexCocoon) {
            return new ContainerMyrmexCocoon(player.inventory, (TileEntityMyrmexCocoon)tile, player);
         }
      }

      return null;
   }

   @SideOnly(Side.CLIENT)
   public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      Entity entity = world.getEntityByID(x);
      TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
      switch(id) {
      case 0:
         if (entity != null && entity instanceof EntityDragonBase) {
            return new GuiDragon(player.inventory, (EntityDragonBase)entity);
         }
         break;
      case 1:
         if (tile != null && tile instanceof TileEntityPodium) {
            return new GuiPodium(player.inventory, (TileEntityPodium)tile);
         }
         break;
      case 2:
         if (tile != null && tile instanceof TileEntityLectern) {
            return new GuiLectern(player.inventory, (TileEntityLectern)tile);
         }
         break;
      case 3:
         return new GuiBestiary(player.getActiveItemStack());
      case 4:
         if (entity != null && entity instanceof EntityHippogryph) {
            return new GuiHippogryph(player.inventory, (EntityHippogryph)entity);
         }
         break;
      case 5:
         if (entity != null && entity instanceof EntityHippocampus) {
            return new GuiHippocampus(player.inventory, (EntityHippocampus)entity);
         }
         break;
      case 6:
         if (tile != null && tile instanceof TileEntityMyrmexCocoon) {
            return new GuiMyrmexCocoon(player.inventory, (TileEntityMyrmexCocoon)tile);
         }
      }

      return entity;
   }
}
