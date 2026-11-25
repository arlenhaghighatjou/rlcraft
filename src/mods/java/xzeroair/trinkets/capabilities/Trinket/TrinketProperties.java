package xzeroair.trinkets.capabilities.Trinket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.CapabilityBase;
import xzeroair.trinkets.capabilities.race.ElementalAttributes;
import xzeroair.trinkets.items.trinkets.TrinketTeddyBear;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.SyncItemDataPacket;
import xzeroair.trinkets.traits.abilities.base.ItemAbilityProvider;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.traits.abilities.interfaces.IHeldAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableInventoryAbility;
import xzeroair.trinkets.util.handlers.Counter;

public class TrinketProperties extends CapabilityBase<TrinketProperties, ItemStack> {
   protected int variant = 0;
   protected int exp = 0;
   protected float mana = 0.0F;
   protected boolean mainAbility = false;
   protected boolean altAbility = false;
   protected TrinketHelper.SlotInformation slotInfo;
   protected boolean sync;
   protected String crafter = "";
   protected String crafterUUID = "";
   protected ElementalAttributes elements = new ElementalAttributes();

   public TrinketProperties(ItemStack stack) {
      super(stack);
      this.slotInfo = new TrinketHelper.SlotInformation(stack, TrinketHelper.SlotInformation.ItemHandlerType.NONE.getName(), -1);
   }

   public ElementalAttributes getElementAttributes() {
      return this.elements;
   }

   public NBTTagCompound getTag() {
      if (((ItemStack)this.object).getTagCompound() == null) {
         NBTTagCompound itemTag = new NBTTagCompound();
         this.saveToNBT(itemTag);
         ((ItemStack)this.object).setTagCompound(itemTag);
      }

      return ((ItemStack)this.object).getTagCompound();
   }

   public TrinketHelper.SlotInformation getSlotInfo() {
      return this.slotInfo;
   }

   public void itemRightClicked() {
   }

   public void itemLeftClicked() {
   }

   public void itemUsed() {
   }

   public void onCrafted(ItemStack stack, World world, EntityPlayer player) {
      if (player != null) {
         try {
            if (player.getUniqueID() != null) {
               this.setCrafterUUID(player.getUniqueID().toString());
            }

            this.setCrafter(player.getDisplayNameString());
         } catch (Exception var5) {
            var5.printStackTrace();
         }

      }
   }

   public String getCrafter() {
      return this.crafter;
   }

   public String getCrafterUUID() {
      return this.crafterUUID;
   }

   public void setCrafter(String crafter) {
      if (!crafter.isEmpty() && !this.crafter.contentEquals(crafter)) {
         this.crafter = crafter;
         this.saveToNBT(this.getTag());
      }

   }

   public void setCrafterUUID(String crafterUUID) {
      if (!crafterUUID.isEmpty() && !this.crafterUUID.contentEquals(crafterUUID)) {
         this.crafterUUID = crafterUUID;
         this.saveToNBT(this.getTag());
      }

   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      if (stack.getItem() instanceof TrinketTeddyBear) {
         ;
      }

      if (entity instanceof EntityLivingBase) {
         boolean onHotBar = !isSelected && entity instanceof EntityPlayer && InventoryPlayer.isHotbar(itemSlot);
         boolean moved = this.slotInfo.getSlot() != itemSlot;
         boolean notSelected = this.slotInfo.getHandlerType().equals(TrinketHelper.SlotInformation.ItemHandlerType.MAINHAND) && !isSelected;
         boolean selected = !this.slotInfo.getHandlerType().equals(TrinketHelper.SlotInformation.ItemHandlerType.MAINHAND) && isSelected;
         if (moved || notSelected || selected) {
            TrinketHelper.SlotInformation.ItemHandlerType type = TrinketHelper.SlotInformation.ItemHandlerType.INVENTORY;
            if (onHotBar && !isSelected) {
               type = TrinketHelper.SlotInformation.ItemHandlerType.HOTBAR;
            }

            if (itemSlot <= 3) {
               TrinketHelper.SlotInformation hands = TrinketHelper.getSlotInfoForItemFromHeldEquipment((EntityLivingBase)entity, (s) -> {
                  return this.compareStacks(s, stack, false);
               });
               if (hands != null) {
                  type = hands.getHandlerType();
               } else {
                  TrinketHelper.SlotInformation armor = TrinketHelper.getSlotInfoForItemFromEquipment((EntityLivingBase)entity, (s) -> {
                     return this.compareStacks(s, stack, false);
                  });
                  if (armor != null) {
                     type = armor.getHandlerType();
                  }
               }
            }

            this.slotInfo.setHandler(type);
            this.slotInfo.setSlot(itemSlot);
         }

         if (stack.getItem() instanceof ItemAbilityProvider) {
            TrinketHelper.SlotInformation.ItemHandlerType cacheType = this.slotInfo.getHandlerType();
            List<IAbilityInterface> abilities = new ArrayList();
            ItemAbilityProvider provider = (ItemAbilityProvider)stack.getItem();
            provider.initAbilities(stack, (EntityLivingBase)entity, abilities);
            if (!abilities.isEmpty()) {
               List<IAbilityInterface> abilitiesToAdd = new ArrayList();

               for(IAbilityInterface attachedAbility : abilities) {
                  if ((cacheType == TrinketHelper.SlotInformation.ItemHandlerType.INVENTORY || cacheType == TrinketHelper.SlotInformation.ItemHandlerType.HOTBAR) && attachedAbility instanceof ITickableInventoryAbility) {
                     abilitiesToAdd.add(attachedAbility);
                  }

                  if ((cacheType == TrinketHelper.SlotInformation.ItemHandlerType.MAINHAND || cacheType == TrinketHelper.SlotInformation.ItemHandlerType.OFFHAND) && attachedAbility instanceof IHeldAbility) {
                     abilitiesToAdd.add(attachedAbility);
                  }
               }

               if (!abilitiesToAdd.isEmpty()) {
                  Capabilities.getEntityProperties(entity, (prop) -> {
                     prop.getAbilityHandler().registerAbilities(stack.getItem().getRegistryName().toString(), new TrinketHelper.SlotInformation(stack, cacheType, itemSlot), abilitiesToAdd);
                  });
               }
            }
         }

         if (!this.tickHandler.getCounters().isEmpty()) {
            for(Entry<String, Counter> counter : this.tickHandler.getCounters().entrySet()) {
               ((Counter)counter.getValue()).Tick();
            }
         }
      }

   }

   public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
   }

   public void onEntityTick(ItemStack stack, EntityLivingBase entity) {
      ItemStack logicCheck = this.slotInfo.getStackFromHandler(entity);
      if (!this.compareStacks(stack, logicCheck, true)) {
         TrinketHelper.SlotInformation info = TrinketHelper.getSlotInfoForItemFromAccessory(entity, (s) -> {
            return this.compareStacks(s, stack, false);
         });
         if (info == null) {
            info = TrinketHelper.getSlotInfoForItemFromEquipment(entity, (s) -> {
               return this.compareStacks(s, stack, false);
            });
            if (info == null) {
               info = TrinketHelper.getSlotInfoForItemFromHeldEquipment(entity, (s) -> {
                  return this.compareStacks(s, stack, false);
               });
            }
         }

         if (info != null) {
            TrinketHelper.SlotInformation.ItemHandlerType handler = info.getHandlerType();
            int slot = info.getSlot();
            this.slotInfo.setHandler(handler);
            this.slotInfo.setSlot(slot);
         }
      }

      if (stack.getItem() instanceof ItemAbilityProvider) {
         List<IAbilityInterface> abilities = new ArrayList();
         ItemAbilityProvider provider = (ItemAbilityProvider)stack.getItem();
         provider.initAbilities(stack, entity, abilities);
         if (!abilities.isEmpty()) {
            Capabilities.getEntityProperties(entity, (prop) -> {
               prop.getAbilityHandler().registerAbilities(stack.getItem().getRegistryName().toString(), new TrinketHelper.SlotInformation(stack, this.slotInfo.getHandlerType(), this.slotInfo.getSlot()), abilities);
            });
            abilities.clear();
         }
      }

      if (this.sync) {
         this.sendInformationToTracking(entity);
         this.sync = false;
      }

   }

   private boolean compareStacks(ItemStack first, ItemStack second, boolean ignoreDurability) {
      if (first != null && second != null) {
         if ((!first.isEmpty() || second.isEmpty()) && (first.isEmpty() || !second.isEmpty())) {
            Item firstItem = first.getItem();
            Item secondItem = second.getItem();
            String firstItemID = firstItem.getRegistryName().toString();
            String secondItemID = secondItem.getRegistryName().toString();
            if (!firstItemID.contentEquals(secondItemID)) {
               return false;
            } else if (first.isItemEqual(second)) {
               return true;
            } else {
               return first.isItemEqualIgnoreDurability(second) && ignoreDurability;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void itemEquipped(ItemStack stack, EntityLivingBase entity) {
      boolean isClient = entity.world.isRemote;
      if (!isClient) {
         TrinketHelper.SlotInformation info = TrinketHelper.getSlotInfoForItemFromAccessory(entity, (s) -> {
            return s.isItemEqual(stack);
         });
         if (info != null) {
            int slot = info.getSlot();
            int handler = info.getHandlerType().getId();
            this.slotInfo.setHandler(info.getHandlerType());
            this.slotInfo.setSlot(slot);
            NBTTagCompound tag = new NBTTagCompound();
            this.saveToNBT(tag);
            if (entity instanceof EntityPlayer) {
               NetworkHandler.sendTo(new SyncItemDataPacket(entity, (ItemStack)this.object, tag, slot, handler, handler == TrinketHelper.SlotInformation.ItemHandlerType.BAUBLES.getId(), false), (EntityPlayerMP)entity);
            }
         }
      }

   }

   public void itemUnequipped(ItemStack stack, EntityLivingBase entity) {
      boolean isClient = entity.world.isRemote;
      if (!isClient) {
         int slot = this.slotInfo.getSlot();
         int handler = this.slotInfo.getHandlerType().getId();
         this.slotInfo.setHandler(TrinketHelper.SlotInformation.ItemHandlerType.NONE);
         this.slotInfo.setSlot(-1);
         this.turnOff();
         this.tickHandler.clearCounters();
         NBTTagCompound tag = new NBTTagCompound();
         this.saveToNBT(tag);
         if (entity instanceof EntityPlayer) {
            NetworkHandler.sendTo(new SyncItemDataPacket(entity, (ItemStack)this.object, tag, slot, handler, handler == TrinketHelper.SlotInformation.ItemHandlerType.BAUBLES.getId(), false), (EntityPlayerMP)entity);
         }
      }

   }

   public void turnOff() {
      this.toggleMainAbility(false);
      this.toggleAltAbility(false);
   }

   public boolean isEquipped() {
      return (this.slotInfo.getHandlerType().equals(TrinketHelper.SlotInformation.ItemHandlerType.TRINKETS) || this.slotInfo.getHandlerType().equals(TrinketHelper.SlotInformation.ItemHandlerType.BAUBLES)) && this.slotInfo.getSlot() > -1;
   }

   public void sendInformationToPlayer(EntityLivingBase e, EntityLivingBase receiver) {
      if (!e.getEntityWorld().isRemote && receiver instanceof EntityPlayer) {
         NBTTagCompound tag = new NBTTagCompound();
         this.saveToNBT(tag);
         NetworkHandler.sendTo(new SyncItemDataPacket(e, (ItemStack)this.object, tag, this.slotInfo.getSlot(), this.slotInfo.getHandlerType().getId()), (EntityPlayerMP)receiver);
      }

   }

   public void sendInformationToServer(EntityLivingBase e) {
      if (e.getEntityWorld().isRemote) {
         NBTTagCompound tag = new NBTTagCompound();
         this.saveToNBT(tag);
         NetworkHandler.sendToServer(new SyncItemDataPacket(e, (ItemStack)this.object, tag, this.slotInfo.getSlot(), this.slotInfo.getHandlerType().getId()));
      }

   }

   public void sendInformationToTracking(EntityLivingBase e) {
      World world = e.getEntityWorld();
      if (!world.isRemote && world instanceof WorldServer) {
         WorldServer w = (WorldServer)world;
         NBTTagCompound tag = new NBTTagCompound();
         this.saveToNBT(tag);
         int slot = this.slotInfo.getSlot();
         int handler = this.slotInfo.getHandlerType().getId();
         SyncItemDataPacket packet = new SyncItemDataPacket(e, (ItemStack)this.object, tag, slot, handler, handler == TrinketHelper.SlotInformation.ItemHandlerType.BAUBLES.getId(), this.isEquipped());
         NetworkHandler.sendToTracking(packet, e);
      }

   }

   public void scheduleResync() {
      this.sync = true;
   }

   public boolean mainAbility() {
      return this.mainAbility;
   }

   public void toggleMainAbility(boolean bool) {
      if (this.mainAbility != bool) {
         this.mainAbility = bool;
         this.saveToNBT(this.getTag());
      }

   }

   public boolean altAbility() {
      return this.altAbility;
   }

   public void toggleAltAbility(boolean bool) {
      if (this.altAbility != bool) {
         this.altAbility = bool;
         this.saveToNBT(this.getTag());
      }

   }

   public int StoredExp() {
      return this.exp;
   }

   public void setStoredExp(int integer) {
      if (this.exp != integer) {
         this.exp = integer;
         this.saveToNBT(this.getTag());
      }

   }

   public int getSlot() {
      return this.slotInfo.getSlot();
   }

   public float StoredMana() {
      return this.mana;
   }

   public void setStoredMana(float mana) {
      if (this.mana != mana) {
         this.mana = mana;
         this.saveToNBT(this.getTag());
      }

   }

   public final int getVariant() {
      return this.variant;
   }

   public final void setVariant(int variant) {
      if (this.variant != variant) {
         this.variant = variant;
         this.saveToNBT(this.getTag());
      }

   }

   public NBTTagCompound saveToNBT(NBTTagCompound compound) {
      String capTag = "xat:Trinket";
      NBTTagCompound tag = new NBTTagCompound();
      tag.setInteger("variant", this.variant);
      tag.setInteger("slot", this.slotInfo.getSlot());
      tag.setString("handler", this.slotInfo.getHandler());
      tag.setInteger("exp", this.exp);
      tag.setFloat("mana", this.mana);
      tag.setBoolean("main.ability", this.mainAbility);
      tag.setBoolean("alt.ability", this.altAbility);
      tag.setString("crafter.name", this.crafter);
      tag.setString("crafter.uuid", this.crafterUUID);

      try {
         this.tickHandler.saveCountersToNBT(tag);
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      try {
         this.elements.saveToNBT(tag);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      compound.setTag("xat:Trinket", tag);
      return compound;
   }

   public void loadFromNBT(NBTTagCompound compound) {
      String capTag = "xat:Trinket";
      if (compound.hasKey("xat:Trinket")) {
         NBTTagCompound tag = compound.getCompoundTag("xat:Trinket");
         if (tag.hasKey("variant")) {
            this.variant = tag.getInteger("variant");
         }

         if (tag.hasKey("slot")) {
            this.slotInfo.setSlot(tag.getInteger("slot"));
         }

         if (tag.hasKey("handler")) {
            this.slotInfo.setHandler(tag.getString("handler"));
         }

         if (tag.hasKey("exp")) {
            this.exp = tag.getInteger("exp");
         }

         if (tag.hasKey("mana")) {
            this.mana = tag.getFloat("mana");
         }

         if (tag.hasKey("main.ability")) {
            this.mainAbility = tag.getBoolean("main.ability");
         }

         if (tag.hasKey("alt.ability")) {
            this.altAbility = tag.getBoolean("alt.ability");
         }

         if (tag.hasKey("crafter.name")) {
            this.crafter = tag.getString("crafter.name");
         }

         if (tag.hasKey("crafter.uuid")) {
            this.crafterUUID = tag.getString("crafter.uuid");
         }

         try {
            this.tickHandler.loadCountersFromNBT(tag);
         } catch (Exception var6) {
            var6.printStackTrace();
         }

         try {
            this.elements.loadFromNBT(tag);
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }

   }
}
