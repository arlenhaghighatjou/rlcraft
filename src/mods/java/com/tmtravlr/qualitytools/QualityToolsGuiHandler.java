package com.tmtravlr.qualitytools;

import com.tmtravlr.qualitytools.reforging.ContainerReforgingStation;
import com.tmtravlr.qualitytools.reforging.GuiReforgingStation;
import com.tmtravlr.qualitytools.reforging.TileEntityReforgingStation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class QualityToolsGuiHandler implements IGuiHandler {
   public static final int REFORGING_STATION_ID = 0;

   public QualityToolsGuiHandler() {
   }

   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
      return ID == 0 && tileEntity instanceof TileEntityReforgingStation ? new ContainerReforgingStation(player.inventory, (TileEntityReforgingStation)tileEntity) : null;
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
      return ID == 0 && tileEntity instanceof TileEntityReforgingStation ? new GuiReforgingStation(player.inventory, (TileEntityReforgingStation)tileEntity) : null;
   }
}
