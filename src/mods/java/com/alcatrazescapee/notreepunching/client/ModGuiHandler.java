package com.alcatrazescapee.notreepunching.client;

import com.alcatrazescapee.alcatrazcore.client.gui.GuiContainerCore;
import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import com.alcatrazescapee.notreepunching.client.gui.GuiFirePit;
import com.alcatrazescapee.notreepunching.common.blocks.ModBlocks;
import com.alcatrazescapee.notreepunching.common.container.ContainerFirePit;
import com.alcatrazescapee.notreepunching.common.container.ContainerLargeVessel;
import com.alcatrazescapee.notreepunching.common.container.ContainerSmallVessel;
import com.alcatrazescapee.notreepunching.common.items.ItemSmallVessel;
import com.alcatrazescapee.notreepunching.common.tile.TileFirePit;
import com.alcatrazescapee.notreepunching.common.tile.TileLargeVessel;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public final class ModGuiHandler implements IGuiHandler {
   public static final int LARGE_VESSEL = 0;
   public static final int SMALL_VESSEL = 1;
   public static final int FIRE_PIT = 2;
   private static final ResourceLocation LARGE_VESSEL_BACKGROUND = new ResourceLocation("notreepunching", "textures/gui/large_vessel.png");
   private static final ResourceLocation SMALL_VESSEL_BACKGROUND = new ResourceLocation("notreepunching", "textures/gui/small_vessel.png");
   private static final ResourceLocation FIRE_PIT_BACKGROUND = new ResourceLocation("notreepunching", "textures/gui/fire_pit.png");

   public ModGuiHandler() {
   }

   @Nullable
   public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      switch(ID) {
      case 0:
         TileLargeVessel teLargeVessel = (TileLargeVessel)CoreHelpers.getTE(world, new BlockPos(x, y, z), TileLargeVessel.class);
         return teLargeVessel != null ? new ContainerLargeVessel(player.inventory, teLargeVessel) : null;
      case 1:
         ItemStack stack = player.getHeldItemMainhand();
         if (!(stack.getItem() instanceof ItemSmallVessel)) {
            stack = player.getHeldItemOffhand();
         }

         return new ContainerSmallVessel(player.inventory, stack);
      case 2:
         TileFirePit teFirePit = (TileFirePit)CoreHelpers.getTE(world, new BlockPos(x, y, z), TileFirePit.class);
         return teFirePit != null ? new ContainerFirePit(player.inventory, teFirePit) : null;
      default:
         return null;
      }
   }

   @Nullable
   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      Container container = this.getServerGuiElement(ID, player, world, x, y, z);
      switch(ID) {
      case 0:
         return new GuiContainerCore(container, player.inventory, LARGE_VESSEL_BACKGROUND, ModBlocks.CERAMIC_LARGE_VESSEL.getUnlocalizedName());
      case 1:
         ItemStack stack = player.getHeldItemMainhand();
         if (!(stack.getItem() instanceof ItemSmallVessel)) {
            stack = player.getHeldItemOffhand();
         }

         return new GuiContainerCore(container, player.inventory, SMALL_VESSEL_BACKGROUND, stack.getUnlocalizedName());
      case 2:
         TileFirePit teFirePit = (TileFirePit)CoreHelpers.getTE(world, new BlockPos(x, y, z), TileFirePit.class);
         return teFirePit != null ? new GuiFirePit(teFirePit, container, player.inventory, FIRE_PIT_BACKGROUND, ModBlocks.FIRE_PIT.getUnlocalizedName()) : null;
      default:
         return null;
      }
   }
}
