package knightminer.inspirations.building.tileentity;

import javax.annotation.Nonnull;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.building.client.GuiBookshelf;
import knightminer.inspirations.building.inventory.ContainerBookshelf;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.common.network.InventorySlotSyncPacket;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.client.ClientUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.common.IInventoryGui;
import slimeknights.mantle.tileentity.TileInventory;

public class TileBookshelf extends TileInventory implements IInventoryGui {
   private float enchantBonus = Float.NaN;

   public TileBookshelf() {
      super("gui.inspirations.bookshelf.name", 14, 1);
   }

   public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
      return oldState.getBlock() != newSate.getBlock();
   }

   public void setInventorySlotContents(int slot, ItemStack itemstack) {
      ItemStack oldStack = this.getStackInSlot(slot);
      if (this.getWorld() != null && this.getWorld() instanceof WorldServer && !this.getWorld().isRemote && !ItemStack.areItemStacksEqual(itemstack, this.getStackInSlot(slot))) {
         InspirationsNetwork.sendToClients((WorldServer)this.getWorld(), this.pos, new InventorySlotSyncPacket(itemstack, slot, this.pos));
      }

      super.setInventorySlotContents(slot, itemstack);
      if (this.getWorld() != null) {
         if (this.getWorld().isRemote) {
            Minecraft.getMinecraft().renderGlobal.notifyBlockUpdate((World)null, this.pos, (IBlockState)null, (IBlockState)null, 0);
         }

         if (InspirationsBuilding.redstoneBook != null && InspirationsBuilding.redstoneBook.isItemEqual(oldStack) ^ InspirationsBuilding.redstoneBook.isItemEqual(itemstack)) {
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
            IBlockState state = this.world.getBlockState(this.pos);
            if (state.getBlock() == this.getBlockType()) {
               this.world.notifyNeighborsOfStateChange(this.pos.offset(((EnumFacing)state.getValue(BlockBookshelf.FACING)).getOpposite()), this.getBlockType(), false);
            }
         }
      }

      this.enchantBonus = Float.NaN;
   }

   public boolean interact(EntityPlayer player, EnumHand hand, int bookClicked) {
      if (this.isStackInSlot(bookClicked)) {
         if (!this.world.isRemote) {
            ItemHandlerHelper.giveItemToPlayer(player, this.getStackInSlot(bookClicked), player.inventory.currentItem);
            this.setInventorySlotContents(bookClicked, ItemStack.EMPTY);
         }

         return true;
      } else {
         ItemStack stack = player.getHeldItem(hand);
         if (InspirationsRegistry.isBook(stack)) {
            if (!this.world.isRemote) {
               this.setInventorySlotContents(bookClicked, stack.splitStack(1));
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public ContainerBookshelf createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
      return new ContainerBookshelf(inventoryplayer, this);
   }

   @SideOnly(Side.CLIENT)
   public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
      return new GuiBookshelf(this.createContainer(inventoryplayer, world, pos));
   }

   public int getPower() {
      if (InspirationsBuilding.redstoneBook == null) {
         return 0;
      } else {
         for(int i = 0; i < 14; ++i) {
            if (InspirationsBuilding.redstoneBook.isItemEqual(this.getStackInSlot(i))) {
               return i + 2;
            }
         }

         return 0;
      }
   }

   public float getEnchantPower() {
      if (!Float.isNaN(this.enchantBonus)) {
         return this.enchantBonus;
      } else {
         float books = 0.0F;

         for(int i = 0; i < this.getSizeInventory(); ++i) {
            if (this.isStackInSlot(i)) {
               float power = InspirationsRegistry.getBookEnchantingPower(this.getStackInSlot(i));
               if (power >= 0.0F) {
                  books += power;
               }
            }
         }

         this.enchantBonus = books / 14.0F;
         return this.enchantBonus;
      }
   }

   public IBlockState writeExtendedBlockState(IExtendedBlockState state) {
      int books = 0;

      for(int i = 0; i < 14; ++i) {
         if (this.isStackInSlot(i)) {
            books |= 1 << i;
         }
      }

      state = state.withProperty(BlockBookshelf.BOOKS, Integer.valueOf(books));
      String texture = ClientUtil.getTexturePath(this);
      if (!texture.isEmpty()) {
         state = state.withProperty(BlockBookshelf.TEXTURE, texture);
      }

      return state;
   }

   @Nonnull
   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound tag = this.getTileData().copy();
      this.writeToNBT(tag);
      return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
   }

   public void onDataPacket(INetworkManager net, SPacketUpdateTileEntity pkt) {
      NBTTagCompound tag = pkt.getNbtCompound();
      NBTBase texture = tag.getTag("texture");
      if (texture != null) {
         this.getTileData().setTag("texture", texture);
      }

      this.readFromNBT(tag);
   }

   public void readFromNBT(NBTTagCompound tags) {
      super.readFromNBT(tags);
      NBTTagCompound forgeData = tags.getCompoundTag("ForgeData");
      if (forgeData.hasKey("texture", 8)) {
         forgeData.setString("texture_path", forgeData.getString("texture"));
         forgeData.removeTag("texture");
      }

   }
}
