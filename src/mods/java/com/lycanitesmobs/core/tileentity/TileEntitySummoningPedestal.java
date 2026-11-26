package com.lycanitesmobs.core.tileentity;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.SummoningPedestalScreen;
import com.lycanitesmobs.core.block.BlockSummoningPedestal;
import com.lycanitesmobs.core.container.SummoningPedestalContainer;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.PortalEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.network.MessageSummoningPedestalStats;
import com.lycanitesmobs.core.network.MessageSummoningPedestalSummonSet;
import com.lycanitesmobs.core.pets.SummonSet;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class TileEntitySummoningPedestal extends TileEntityBase {
   public long updateTick = 0L;
   public PortalEntity summoningPortal;
   public EaglercraftUUID ownerUUID;
   public String ownerName = "";
   public SummonSet summonSet;
   public int summonAmount = 1;
   public int capacityCharge = 100;
   public int capacity = 0;
   public int capacityMax;
   public int summonProgress;
   public int summonProgressMax;
   public String inventoryName;
   public NonNullList<ItemStack> itemStacks;
   public int summoningFuel;
   public int summoningFuelMax;
   public int summoningFuelAmount;
   public List<BaseCreatureEntity> minions;
   protected String[] loadMinionIDs;
   protected boolean blockStateSet;

   public TileEntitySummoningPedestal() {
      this.capacityMax = this.capacityCharge * 10;
      this.summonProgress = 0;
      this.summonProgressMax = 180;
      this.inventoryName = "";
      this.itemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
      this.summoningFuel = 0;
      this.summoningFuelMax = 12000;
      this.summoningFuelAmount = 12000;
      this.minions = new ArrayList();
      this.blockStateSet = false;
      this.summoningFuelAmount = LycanitesMobs.config.getInt("Player", "Summoning Pedestal Redstone Time", this.summoningFuelAmount, "How much summoning time (in ticks) 1 redstone dust provides. 20 ticks = 1 second, default is 12000 (10 minutes).");
   }

   public void onRemove() {
      if (this.summoningPortal != null && !this.summoningPortal.isDead) {
         this.summoningPortal.setDead();
      }

      for(ItemStack itemStack : this.itemStacks) {
         EntityItemCustom entityItem = new EntityItemCustom(this.getWorld(), (double)this.getPos().getX(), (double)this.getPos().getY() + 0.5D, (double)this.getPos().getZ(), itemStack);
         this.getWorld().spawnEntity(entityItem);
      }

      this.clear();
      super.onRemove();
   }

   public void update() {
      if (this.getWorld().isRemote) {
         if (this.summoningFuel > 0) {
            if (this.summonProgress >= this.summonProgressMax) {
               this.summonProgress = 0;
            } else if (this.summonProgress > 0) {
               ++this.summonProgress;
            }
         }

      } else {
         if (this.loadMinionIDs != null) {
            int range = 20;

            for(BaseCreatureEntity possibleEntity : this.getWorld().getEntitiesWithinAABB(BaseCreatureEntity.class, new AxisAlignedBB((double)(this.getPos().getX() - range), (double)(this.getPos().getY() - range), (double)(this.getPos().getZ() - range), (double)(this.getPos().getX() + range), (double)(this.getPos().getY() + range), (double)(this.getPos().getZ() + range)))) {
               for(String loadMinionID : this.loadMinionIDs) {
                  EaglercraftUUID uuid = null;

                  try {
                     uuid = EaglercraftUUID.fromString(loadMinionID);
                  } catch (Exception var11) {
                     ;
                  }

                  if (possibleEntity.getUniqueID().equals(uuid)) {
                     this.minions.add(possibleEntity);
                     break;
                  }
               }
            }

            this.loadMinionIDs = null;
         }

         if (this.summonSet != null && this.summonSet.getCreatureInfo() != null) {
            if (this.summonSet.getFollowing()) {
               this.summonSet.following = false;
            }

            if (this.updateTick % 100L == 0L) {
               this.capacity = 0;

               for(BaseCreatureEntity minion : (BaseCreatureEntity[])this.minions.toArray(new BaseCreatureEntity[this.minions.size()])) {
                  if (minion != null && !minion.isDead) {
                     this.capacity += minion.creatureInfo.summonCost * this.capacityCharge;
                  } else {
                     this.minions.remove(minion);
                  }
               }
            }

            if (this.capacity + this.summonSet.getCreatureInfo().summonCost > this.capacityMax) {
               this.summonProgress = 0;
            } else {
               if (this.summoningFuel <= 0) {
                  ItemStack fuelStack = this.getStackInSlot(0);
                  if (!fuelStack.isEmpty()) {
                     int refuel = this.summoningFuelAmount;
                     if (fuelStack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)) {
                        refuel = this.summoningFuelAmount * 9;
                     }

                     fuelStack.shrink(1);
                     this.summoningFuel = refuel;
                     this.summoningFuelMax = refuel;
                  }
               }

               if (this.summoningFuel > 0) {
                  --this.summoningFuel;
                  if (this.summoningPortal == null || this.summoningPortal.isDead) {
                     this.summoningPortal = new PortalEntity(this.getWorld(), this);
                     this.summoningPortal.setProjectileScale(4.0F);
                     this.getWorld().spawnEntity(this.summoningPortal);
                  }

                  if (this.summonProgress++ >= this.summonProgressMax) {
                     this.summoningPortal.summonCreatures();
                     this.summonProgress = 0;
                     this.capacity = Math.min(this.capacity + this.capacityCharge * this.summonSet.getCreatureInfo().summonCost, this.capacityMax);
                  }
               }
            }
         }

         if (this.summoningFuel <= 0 && this.summoningPortal != null && !this.summoningPortal.isDead) {
            this.summoningPortal.setDead();
         }

         if (!this.blockStateSet) {
            if (!"".equals(this.getOwnerName())) {
               BlockSummoningPedestal.setState(BlockSummoningPedestal.EnumSummoningPedestal.PLAYER, this.getWorld(), this.getPos());
            } else {
               BlockSummoningPedestal.setState(BlockSummoningPedestal.EnumSummoningPedestal.NONE, this.getWorld(), this.getPos());
            }

            this.blockStateSet = true;
         }

         if (this.updateTick % 20L == 0L) {
            LycanitesMobs.packetHandler.sendToAllAround(new MessageSummoningPedestalStats(this.capacity, this.summonProgress, this.summoningFuel, this.summoningFuelMax, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()), new TargetPoint(this.getWorld().provider.getDimension(), (double)this.getPos().getX(), (double)this.getPos().getY(), (double)this.getPos().getZ(), 5.0D));
         }

         ++this.updateTick;
      }
   }

   public void setOwner(EntityLivingBase entity) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer entityPlayer = (EntityPlayer)entity;
         this.ownerUUID = entityPlayer.getUniqueID();
         this.ownerName = entity.getName();
      }

   }

   public EaglercraftUUID getOwnerUUID() {
      return this.ownerUUID;
   }

   public String getOwnerName() {
      return this.ownerName;
   }

   public EntityPlayer getPlayer() {
      return this.ownerUUID == null ? null : this.getWorld().getPlayerEntityByUUID(this.ownerUUID);
   }

   public Class getSummonClass() {
      return this.summonSet == null ? null : this.summonSet.getCreatureClass();
   }

   public void setSummonSet(SummonSet summonSet) {
      if (this.getPlayer() == null || summonSet.isUseable()) {
         this.summonSet = new SummonSet((ExtendedPlayer)null);
         this.summonSet.setSummonType(summonSet.summonType);
         this.summonSet.sitting = summonSet.getSitting();
         this.summonSet.following = false;
         this.summonSet.passive = summonSet.getPassive();
         this.summonSet.aggressive = summonSet.getAggressive();
         this.summonSet.pvp = summonSet.getPVP();
      }
   }

   public void applyMinionBehaviour(TameableCreatureEntity minion) {
      minion.fromSummoningPedestal = true;
      if (this.summonSet != null) {
         this.summonSet.applyBehaviour(minion);
         minion.setSubspecies(this.summonSet.subspecies);
         minion.applyVariant(this.summonSet.variant);
      }

      this.minions.add(minion);
      minion.setHome(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), 20.0F);
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.itemStacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getStackInSlot(int index) {
      return (ItemStack)this.itemStacks.get(index);
   }

   public ItemStack decrStackSize(int index, int count) {
      return ItemStackHelper.getAndSplit(this.itemStacks, index, count);
   }

   public ItemStack removeStackFromSlot(int index) {
      return ItemStackHelper.getAndRemove(this.itemStacks, index);
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      this.itemStacks.set(index, stack);
      if (stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

   }

   public int getSizeInventory() {
      return this.itemStacks.size();
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void openInventory(EntityPlayer player) {
   }

   public void closeInventory(EntityPlayer player) {
   }

   public boolean isItemValidForSlot(int index, ItemStack itemStack) {
      return itemStack.getItem() == Items.REDSTONE || itemStack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK);
   }

   public int getField(int id) {
      return 0;
   }

   public void setField(int id, int value) {
   }

   public int getFieldCount() {
      return 0;
   }

   public void clear() {
      this.itemStacks.clear();
   }

   public String getName() {
      return this.inventoryName;
   }

   public boolean hasCustomName() {
      return !"".equals(this.inventoryName);
   }

   public boolean receiveClientEvent(int eventID, int eventArg) {
      return false;
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound syncData = new NBTTagCompound();
      if (this.summonSet != null) {
         NBTTagCompound summonSetNBT = new NBTTagCompound();
         this.summonSet.writeToNBT(summonSetNBT);
         syncData.setTag("SummonSet", summonSetNBT);
      }

      if (!this.getWorld().isRemote && this.getOwnerUUID() != null && this.getOwnerName() != null) {
         syncData.setString("OwnerUUID", this.getOwnerUUID().toString());
         syncData.setString("OwnerName", this.getOwnerName());
         syncData.setString("InventoryName", this.getName());
      }

      return new SPacketUpdateTileEntity(this.getPos(), 1, syncData);
   }

   public void onDataPacket(INetworkManager net, SPacketUpdateTileEntity packet) {
      if (this.getWorld().isRemote) {
         NBTTagCompound syncData = packet.getNbtCompound();
         if (syncData.hasKey("OwnerUUID")) {
            this.ownerUUID = EaglercraftUUID.fromString(syncData.getString("OwnerUUID"));
         }

         if (syncData.hasKey("OwnerName")) {
            this.ownerName = syncData.getString("OwnerName");
         }

         if (syncData.hasKey("InventoryName")) {
            this.inventoryName = syncData.getString("InventoryName");
         }

         if (syncData.hasKey("SummonSet")) {
            SummonSet summonSet = new SummonSet((ExtendedPlayer)null);
            summonSet.readFromNBT(syncData.getCompoundTag("SummonSet"));
            this.summonSet = summonSet;
         }

      }
   }

   public void sendSummonSetToServer(SummonSet summonSet) {
      LycanitesMobs.packetHandler.sendToServer(new MessageSummoningPedestalSummonSet(summonSet, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()));
   }

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      super.readFromNBT(nbtTagCompound);
      if (nbtTagCompound.hasKey("OwnerUUID")) {
         String uuidString = nbtTagCompound.getString("OwnerUUID");
         if (!"".equals(uuidString)) {
            this.ownerUUID = EaglercraftUUID.fromString(uuidString);
         } else {
            this.ownerUUID = null;
         }
      } else {
         this.ownerUUID = null;
      }

      if (nbtTagCompound.hasKey("OwnerName")) {
         this.ownerName = nbtTagCompound.getString("OwnerName");
      } else {
         this.ownerName = "";
      }

      if (nbtTagCompound.hasKey("SummonSet")) {
         NBTTagCompound summonSetNBT = nbtTagCompound.getCompoundTag("SummonSet");
         SummonSet summonSet = new SummonSet((ExtendedPlayer)null);
         summonSet.readFromNBT(summonSetNBT);
         this.summonSet = summonSet;
      } else {
         this.summonSet = null;
      }

      if (nbtTagCompound.hasKey("MinionIDs")) {
         NBTTagList minionIDs = nbtTagCompound.getTagList("MinionIDs", 10);
         this.loadMinionIDs = new String[minionIDs.tagCount()];

         for(int i = 0; i < minionIDs.tagCount(); ++i) {
            NBTTagCompound minionID = minionIDs.getCompoundTagAt(i);
            if (minionID.hasKey("ID")) {
               this.loadMinionIDs[i] = minionID.getString("ID");
            }
         }
      }

      if (nbtTagCompound.hasKey("Fuel")) {
         this.summoningFuel = nbtTagCompound.getInteger("Fuel");
      }

      if (nbtTagCompound.hasKey("FuelMax")) {
         this.summoningFuelMax = nbtTagCompound.getInteger("FuelMax");
      }

      if (nbtTagCompound.hasKey("Items")) {
         NonNullList<ItemStack> itemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
         ItemStackHelper.loadAllItems(nbtTagCompound, itemStacks);

         for(int i = 0; i < itemStacks.size(); ++i) {
            if (i < this.getSizeInventory()) {
               ItemStack itemStack = (ItemStack)itemStacks.get(i);
               if (itemStack.isEmpty()) {
                  this.setInventorySlotContents(i, ItemStack.EMPTY);
               } else {
                  this.setInventorySlotContents(i, itemStack);
               }
            }
         }
      }

      super.readFromNBT(nbtTagCompound);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
      super.writeToNBT(nbtTagCompound);
      if (this.ownerUUID == null) {
         nbtTagCompound.setString("OwnerUUID", "");
      } else {
         nbtTagCompound.setString("OwnerUUID", this.ownerUUID.toString());
      }

      if (this.summonSet != null) {
         NBTTagCompound summonSetNBT = new NBTTagCompound();
         this.summonSet.writeToNBT(summonSetNBT);
         nbtTagCompound.setTag("SummonSet", summonSetNBT);
      }

      nbtTagCompound.setString("OwnerName", this.ownerName);
      if (this.minions.size() > 0) {
         NBTTagList minionIDs = new NBTTagList();

         for(EntityLivingBase minion : this.minions) {
            NBTTagCompound minionID = new NBTTagCompound();
            minionID.setString("ID", minion.getUniqueID().toString());
            minionIDs.appendTag(minionID);
         }

         nbtTagCompound.setTag("MinionIDs", minionIDs);
      }

      nbtTagCompound.setInteger("Fuel", this.summoningFuel);
      nbtTagCompound.setInteger("FuelMax", this.summoningFuelMax);
      ItemStackHelper.saveAllItems(nbtTagCompound, this.itemStacks);
      return nbtTagCompound;
   }

   public Object getGUI(EntityPlayer player) {
      return this.getWorld().isRemote ? new SummoningPedestalScreen(player, this) : new SummoningPedestalContainer(this, player.inventory);
   }
}
