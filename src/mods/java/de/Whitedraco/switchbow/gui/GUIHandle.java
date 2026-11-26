package de.Whitedraco.switchbow.gui;

import de.Whitedraco.switchbow.item.ItemMagicQuiver;
import de.Whitedraco.switchbow.item.ItemQuiver;
import de.Whitedraco.switchbow.tileentity.TileEntityArrowDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GUIHandle implements IGuiHandler {
   public GUIHandle() {
   }

   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      switch(ID) {
      case 0:
         ItemStack itemstack = ItemStack.EMPTY;
         if (player.getHeldItemMainhand() != ItemStack.EMPTY && player.getHeldItemMainhand().getItem() instanceof ItemQuiver) {
            itemstack = player.getHeldItemMainhand();
            return new ContainerQuiver(player, itemstack);
         } else {
            if (player.getHeldItemOffhand() != ItemStack.EMPTY && player.getHeldItemOffhand().getItem() instanceof ItemQuiver) {
               itemstack = player.getHeldItemOffhand();
               return new ContainerQuiver(player, itemstack);
            }

            return null;
         }
      case 1:
         itemstack = ItemStack.EMPTY;
         if (player.getHeldItemMainhand() != ItemStack.EMPTY && player.getHeldItemMainhand().getItem() instanceof ItemMagicQuiver) {
            itemstack = player.getHeldItemMainhand();
            return new ContainerMagicQuiver(player, itemstack);
         } else {
            if (player.getHeldItemOffhand() != ItemStack.EMPTY && player.getHeldItemOffhand().getItem() instanceof ItemMagicQuiver) {
               itemstack = player.getHeldItemOffhand();
               return new ContainerMagicQuiver(player, itemstack);
            }

            return null;
         }
      case 2:
         TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
         if (tileEntity instanceof TileEntityArrowDispenser) {
            return new ContainerArrowDispenser(player.inventory, (TileEntityArrowDispenser)tileEntity);
         }

         return null;
      case 3:
         return new ContainerArrowOverlay();
      default:
         return null;
      }
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      switch(ID) {
      case 0:
         ItemStack item = ItemStack.EMPTY;
         if (player.getHeldItemMainhand() != ItemStack.EMPTY && player.getHeldItemMainhand().getItem() instanceof ItemQuiver) {
            item = player.getHeldItemMainhand();
            return new GUIQuiver(player, item);
         } else {
            if (player.getHeldItemOffhand() != ItemStack.EMPTY && player.getHeldItemOffhand().getItem() instanceof ItemQuiver) {
               item = player.getHeldItemOffhand();
               return new GUIQuiver(player, item);
            }

            return null;
         }
      case 1:
         item = ItemStack.EMPTY;
         if (player.getHeldItemMainhand() != ItemStack.EMPTY && player.getHeldItemMainhand().getItem() instanceof ItemMagicQuiver) {
            item = player.getHeldItemMainhand();
            return new GUIMagicQuiver(player, item);
         } else {
            if (player.getHeldItemOffhand() != ItemStack.EMPTY && player.getHeldItemOffhand().getItem() instanceof ItemMagicQuiver) {
               item = player.getHeldItemOffhand();
               return new GUIMagicQuiver(player, item);
            }

            return null;
         }
      case 2:
         TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
         if (tileEntity instanceof TileEntityArrowDispenser) {
            return new GUIArrowDispenser(player.inventory, (TileEntityArrowDispenser)tileEntity);
         }

         return null;
      case 3:
         return new GUIArrowOverlay();
      default:
         return null;
      }
   }
}
