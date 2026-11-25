package pw.prok.realbench.asm;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import pw.prok.realbench.WorkbenchRenderer;
import pw.prok.realbench.WorkbenchTile;

public class ASMHooks {
   public ASMHooks() {
   }

   public static WorkbenchTile createNewTileEntity() {
      return new WorkbenchTile();
   }

   public static void breakBlock(World world, BlockPos pos) {
      TileEntity tile = world.getTileEntity(pos);
      if (tile != null && tile instanceof WorkbenchTile) {
         for(ItemStack item : ((WorkbenchTile)tile).getCraftMatrixItems()) {
            if (item != ItemStack.EMPTY) {
               dropBlockAsItem(world, pos.getX(), pos.getY(), pos.getZ(), item);
            }
         }

      }
   }

   protected static void dropBlockAsItem(World world, int x, int y, int z, ItemStack item) {
      if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops") && !world.restoringBlockSnapshots) {
         float f = 0.7F;
         double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
         double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
         double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
         EntityItem entityitem = new EntityItem(world, (double)x + d0, (double)y + d1, (double)z + d2, item);
         entityitem.setDefaultPickupDelay();
         world.spawnEntity(entityitem);
      }

   }

   public static void staticInit() {
      GameRegistry.registerTileEntity(WorkbenchTile.class, "realbench:Workbench");
      if (FMLLaunchHandler.side() == Side.CLIENT) {
         ASMHooks.Client.init();
      }

   }

   public static native WorkbenchTile getTile(ContainerWorkbench var0);

   public static WorkbenchTile getTile(World world, BlockPos pos) {
      TileEntity tile = world.getTileEntity(pos);
      return tile instanceof WorkbenchTile ? (WorkbenchTile)tile : null;
   }

   static class Client {
      Client() {
      }

      static void init() {
         ClientRegistry.bindTileEntitySpecialRenderer(WorkbenchTile.class, new WorkbenchRenderer());
      }
   }
}
