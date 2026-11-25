package noppes.vc.containers;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import noppes.vc.Server;
import noppes.vc.blocks.tiles.TileTrading;
import noppes.vc.constants.PacketClient;

public class ContainerTradingBlock extends ContainerBasic {
   public InventoryCrafting craftMatrix = new InventoryCrafting(this, 9, 1);
   public TileTrading tile;
   private EntityPlayer player;
   private BlockPos pos;
   public int state = 0;

   public ContainerTradingBlock(EntityPlayer player, BlockPos pos) {
      super(player);
      this.pos = pos;
      this.tile = (TileTrading)player.world.getTileEntity(pos);
      this.player = player;

      for(int j = 0; j < 4; ++j) {
         for(int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(player.inventory, k + j * 9, 8 + k * 18, 150 + j * 18));
         }
      }

      for(int j = 0; j < 9; ++j) {
         this.addSlotToContainer(new ContainerTradingBlock.SlotTrader(this.craftMatrix, j, 8 + j * 18, 125));
      }

   }

   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int i) {
      return ItemStack.EMPTY;
   }

   public void onContainerClosed(EntityPlayer player) {
      super.onContainerClosed(player);
      if (!player.world.isRemote) {
         EntityPlayer other = this.tile.other(player);
         if (this.tile.trader1 == player) {
            this.tile.trader1 = null;
         } else {
            this.tile.trader2 = null;
         }

         if (this.state != 3 && other != null) {
            ((ContainerTradingBlock)other.openContainer).setState(4);
         }

         for(int i = 0; i < this.craftMatrix.getSizeInventory(); ++i) {
            ItemStack itemstack = this.craftMatrix.removeStackFromSlot(i);
            if (!itemstack.isEmpty() && !player.inventory.addItemStackToInventory(itemstack)) {
               player.dropItem(itemstack, true, false);
            }
         }

      }
   }

   public boolean canInteractWith(EntityPlayer player) {
      TileEntity tile = player.world.getTileEntity(this.pos);
      if (tile != null && tile instanceof TileTrading) {
         this.tile = (TileTrading)tile;
         return !player.isDead && player.getPosition().distanceSq(this.pos) < 16.0D;
      } else {
         return false;
      }
   }

   public NBTTagCompound itemsToComp() {
      NBTTagCompound compound = new NBTTagCompound();
      NBTTagList list = new NBTTagList();

      for(int i = 0; i < this.craftMatrix.getSizeInventory(); ++i) {
         ItemStack item = this.craftMatrix.getStackInSlot(i);
         if (!item.isEmpty()) {
            NBTTagCompound c = new NBTTagCompound();
            c.setInteger("Slot", i);
            item.writeToNBT(c);
            list.appendTag(c);
         }
      }

      compound.setTag("Items", list);
      return compound;
   }

   public static Map<Integer, ItemStack> CompToItem(NBTTagCompound compound) {
      Map<Integer, ItemStack> items = new HashMap();
      NBTTagList list = compound.getTagList("Items", 10);

      for(int i = 0; i < list.tagCount(); ++i) {
         NBTTagCompound c = list.getCompoundTagAt(i);
         int slot = c.getInteger("Slot");
         ItemStack item = new ItemStack(c);
         if (!item.isEmpty()) {
            items.put(Integer.valueOf(slot), new ItemStack(c));
         } else {
            items.put(Integer.valueOf(slot), ItemStack.EMPTY);
         }
      }

      return items;
   }

   public void setState(int i) {
      if (this.state != i) {
         this.state = i;
         if (!this.player.world.isRemote) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("State", i);
            Server.sendData((EntityPlayerMP)this.player, PacketClient.GUI_DATA, compound);
         }

      }
   }

   public class SlotTrader extends Slot {
      public SlotTrader(IInventory par1iInventory, int par2, int par3, int par4) {
         super(par1iInventory, par2, par3, par4);
      }

      public boolean isItemValid(ItemStack par1ItemStack) {
         if (ContainerTradingBlock.this.player.world.isRemote) {
            return ContainerTradingBlock.this.tile.trader1 != null && ContainerTradingBlock.this.tile.trader2 != null && ContainerTradingBlock.this.state < 3;
         } else {
            return ContainerTradingBlock.this.tile.isFull() && ContainerTradingBlock.this.state < 3;
         }
      }

      public void onSlotChanged() {
         super.onSlotChanged();
         if (!ContainerTradingBlock.this.player.world.isRemote && ContainerTradingBlock.this.tile.isFull()) {
            EntityPlayer other = ContainerTradingBlock.this.tile.other(ContainerTradingBlock.this.player);
            if (other != null) {
               if (ContainerTradingBlock.this.state == 2 || ContainerTradingBlock.this.state == 1) {
                  ContainerTradingBlock.this.setState(0);
                  ((ContainerTradingBlock)other.openContainer).setState(0);
               }

               Server.sendData((EntityPlayerMP)other, PacketClient.GUI_DATA, ContainerTradingBlock.this.itemsToComp());
            }
         }
      }
   }
}
