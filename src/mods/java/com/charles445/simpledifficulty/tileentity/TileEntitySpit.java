package com.charles445.simpledifficulty.tileentity;

import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.block.BlockCampfire;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.SoundUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntitySpit extends TileEntity implements ITickable {
   private static final String NBT_INT_PROGRESS = "progress";
   private static final String NBT_DOUBLE_EXPERIENCE = "experience";
   private static final String NBT_TAG_ITEMS = "items";
   public TileEntitySpit.ItemHandler items;
   private int progress = 0;
   private double experience = 0.0D;
   private int timer = 0;

   public TileEntitySpit() {
      this.items = new TileEntitySpit.ItemHandler(ModConfig.server.miscellaneous.campfireSpitSize);
   }

   public void update() {
      if (this.timer % 20 == 0) {
         this.timer = 0;
         this.secondUpdate();
      }

      ++this.timer;
   }

   private void secondUpdate() {
      if (this.shouldCook()) {
         ++this.progress;
         if (this.progress >= ModConfig.server.miscellaneous.campfireSpitDelay) {
            this.cookFood();
            this.progress = 0;
         }
      }

   }

   private void cookFood() {
      for(int i = 0; i < this.items.getSlots(); ++i) {
         ItemStack stack = this.items.getStackInSlot(i);
         if (this.isCookable(stack)) {
            ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack).copy();
            if (ModConfig.server.miscellaneous.campfireSpitExperience) {
               this.experience += (double)FurnaceRecipes.instance().getSmeltingExperience(result);
            }

            this.items.setStackInSlot(i, result);
         }
      }

   }

   private boolean playWorldSound(World world, BlockPos pos, boolean deposit) {
      SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, 0.4F, 0.9F);
      return true;
   }

   public void handleRightClick(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack heldItemStack = player.getHeldItem(hand);
      boolean rawWithdraw = heldItemStack.isEmpty();
      boolean playedSound = false;
      boolean found = false;
      boolean withdrewToHand = false;

      for(int i = 0; i < this.items.getSlots(); ++i) {
         if (this.isCooked(this.items.getStackInSlot(i))) {
            withdrewToHand = this.withdrawFromSlot(player, hand, i);
            this.dumpExperience(world, pos);
            if (!playedSound) {
               playedSound = this.playWorldSound(world, pos, false);
            }

            found = true;
            break;
         }
      }

      if (!withdrewToHand && this.isCookable(heldItemStack)) {
         String heldItemName = heldItemStack.getItem().getRegistryName().toString();
         boolean isBlacklisted = false;
         String[] spitBlacklist = ModConfig.server.miscellaneous.campfireSpitBlacklist;

         for(int i = 0; i < spitBlacklist.length; ++i) {
            if (spitBlacklist[i].equals(heldItemName)) {
               isBlacklisted = true;
               break;
            }
         }

         if (isBlacklisted == ModConfig.server.miscellaneous.campfireSpitBlacklistIsWhitelist) {
            for(int i = 0; i < this.items.getSlots(); ++i) {
               if (this.items.getStackInSlot(i).isEmpty()) {
                  this.items.insertItem(i, new ItemStack(heldItemStack.getItem(), 1, heldItemStack.getItemDamage()), false);
                  heldItemStack.shrink(1);
                  this.progress = 0;
                  if (!playedSound) {
                     playedSound = this.playWorldSound(world, pos, true);
                  }

                  found = true;
                  break;
               }
            }
         }
      }

      if (!found && rawWithdraw && player.isSneaking()) {
         for(int i = 0; i < this.items.getSlots(); ++i) {
            if (!this.items.getStackInSlot(i).isEmpty()) {
               this.withdrawFromSlot(player, hand, i);
               if (!playedSound) {
                  this.playWorldSound(world, pos, false);
               }
               break;
            }
         }
      }

   }

   private boolean withdrawFromSlot(EntityPlayer player, EnumHand hand, int slot) {
      ItemStack stack = this.items.extractItem(slot, 1, false);
      if (player.getHeldItem(hand).isEmpty()) {
         player.setHeldItem(hand, stack);
         return true;
      } else if (!player.inventory.addItemStackToInventory(stack)) {
         player.dropItem(stack, false);
         return false;
      } else {
         if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
         }

         return false;
      }
   }

   private boolean isCookable(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else {
         ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
         return !result.isEmpty() && result.getItem() instanceof ItemFood;
      }
   }

   private boolean isCooked(ItemStack stack) {
      return !stack.isEmpty() && !this.isCookable(stack);
   }

   private boolean shouldCook() {
      boolean hasItem = false;

      for(int i = 0; i < this.items.getSlots(); ++i) {
         if (this.isCookable(this.items.getStackInSlot(i))) {
            hasItem = true;
            break;
         }
      }

      if (!hasItem) {
         return false;
      } else {
         IBlockState state = this.world.getBlockState(this.pos.down());
         Block block = state.getBlock();
         return block == SDBlocks.campfire ? ((Boolean)state.getValue(BlockCampfire.BURNING)).booleanValue() : false;
      }
   }

   public void dumpItems(World world, BlockPos pos) {
      for(int i = 0; i < this.items.getSlots(); ++i) {
         ItemStack itemstack = this.items.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            InventoryHelper.spawnItemStack(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemstack);
         }

         this.items.setStackInSlot(i, ItemStack.EMPTY);
      }

   }

   public void dumpExperience(World world, BlockPos pos) {
      int convExp = (int)this.experience;
      this.experience -= (double)convExp;

      while(convExp > 0) {
         int val = EntityXPOrb.getXPSplit(convExp);
         convExp -= val;
         this.world.spawnEntity(new EntityXPOrb(this.world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, val));
      }

   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.progress = compound.getInteger("progress");
      this.items.deserializeNBT(compound.getCompoundTag("items"));
      this.experience = compound.getDouble("experience");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound = super.writeToNBT(compound);
      compound.setInteger("progress", this.progress);
      compound.setTag("items", this.items.serializeNBT());
      compound.setDouble("experience", this.experience);
      return compound;
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
   }

   public void onDataPacket(INetworkManager net, SPacketUpdateTileEntity pkt) {
      super.onDataPacket(net, pkt);
      this.readFromNBT(pkt.getNbtCompound());
      this.updateClients();
   }

   public void updateClients() {
      IBlockState state = this.world.getBlockState(this.pos);
      this.world.notifyBlockUpdate(this.pos, state, state, 2);
   }

   public class ItemHandler extends ItemStackHandler {
      public ItemHandler(int slots) {
         super(slots);
      }

      protected void onContentsChanged(int slot) {
         super.onContentsChanged(slot);
         TileEntitySpit.this.markDirty();
         TileEntitySpit.this.updateClients();
      }

      public int getSlotLimit(int slot) {
         return 1;
      }
   }
}
