package xzeroair.trinkets.traits;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.NonNullList;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.races.EntityRace;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.traits.abilities.interfaces.IContainerAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IEquippedAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IHeldAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableInventoryAbility;

public class AbilityHandler {
   protected Map<String, AbilityHandler.AbilityHolder> active = new TreeMap();
   protected boolean hasChanged = false;
   protected EntityLivingBase entity;

   public AbilityHandler(EntityLivingBase entity) {
      this.entity = entity;
   }

   public Map<String, AbilityHandler.AbilityHolder> getActiveAbilities() {
      return this.active;
   }

   public void registerAbilities(String source, List<? extends IAbilityInterface> abilities) {
      for(IAbilityInterface ability : abilities) {
         this.registerAbility(source, new TrinketHelper.SlotInformation(TrinketHelper.SlotInformation.ItemHandlerType.OTHER), ability);
      }

   }

   public void registerAbilities(String source, TrinketHelper.SlotInformation info, List<? extends IAbilityInterface> abilities) {
      for(IAbilityInterface ability : abilities) {
         this.registerAbility(source, info, ability);
      }

   }

   public void registerRaceAbility(String source, IAbilityInterface ability) {
      this.replaceAbility(source, new TrinketHelper.SlotInformation(TrinketHelper.SlotInformation.ItemHandlerType.RACE), ability);
   }

   public void replaceAbility(String source, TrinketHelper.SlotInformation info, IAbilityInterface ability) {
      String key = ability.getRegistryName().toString();
      if (info == null) {
         info = new TrinketHelper.SlotInformation(TrinketHelper.SlotInformation.ItemHandlerType.OTHER);
      }

      AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)this.active.get(key);
      if (value == null) {
         ability.onAbilityAdded(this.entity);
         AbilityHandler.AbilityHolder holder = new AbilityHandler.AbilityHolder(source, info, ability);
         this.active.put(key, holder);
         this.hasChanged = true;
      } else {
         boolean sameSource = value.getSourceID().contentEquals(source);
         boolean sameHandler = value.getInfo().getHandlerType() == info.getHandlerType();
         boolean sameSlot = value.getInfo().getSlot() == info.getSlot();
         if (!sameSource || !sameHandler || !sameSlot) {
            value.getAbility().onAbilityRemoved(this.entity);
            ability.onAbilityAdded(this.entity);
            AbilityHandler.AbilityHolder holder = new AbilityHandler.AbilityHolder(source, info, ability);
            this.active.put(key, holder);
            this.hasChanged = true;
         }
      }

   }

   @Nullable
   public IAbilityInterface registerAbility(String source, IAbilityInterface ability) {
      return this.registerAbility(source, new TrinketHelper.SlotInformation(TrinketHelper.SlotInformation.ItemHandlerType.OTHER), ability);
   }

   @Nullable
   public IAbilityInterface registerAbility(String source, TrinketHelper.SlotInformation info, IAbilityInterface ability) {
      String key = ability.getRegistryName().toString();
      if (info == null) {
         info = new TrinketHelper.SlotInformation(TrinketHelper.SlotInformation.ItemHandlerType.OTHER);
      }

      if (!this.active.containsKey(key)) {
         ability.onAbilityAdded(this.entity);
         AbilityHandler.AbilityHolder holder = new AbilityHandler.AbilityHolder(source, info, ability);
         this.active.put(key, holder);
         this.hasChanged = true;
         return null;
      } else {
         return ability;
      }
   }

   @Nullable
   public IAbilityInterface removeAbility(String ability) {
      if (this.active.containsKey(ability)) {
         AbilityHandler.AbilityHolder oldHolder = (AbilityHandler.AbilityHolder)this.active.remove(ability);
         IAbilityInterface oldAbility = oldHolder.getAbility();
         oldAbility.onAbilityRemoved(this.entity);
         this.hasChanged = true;
         return oldAbility;
      } else {
         return null;
      }
   }

   @Nullable
   public AbilityHandler.AbilityHolder getAbilityHolder(String ability) {
      return this.active.containsKey(ability) ? (AbilityHandler.AbilityHolder)this.active.get(ability) : null;
   }

   @Nullable
   public IAbilityInterface getAbility(String ability) {
      AbilityHandler.AbilityHolder holder = this.getAbilityHolder(ability);
      return holder != null ? holder.getAbility() : null;
   }

   public void updateAbilityHandler() {
      this.active.values().removeIf((cachex) -> {
         return cache.getAbility().shouldRemove();
      });

      for(Entry<String, AbilityHandler.AbilityHolder> entry : this.active.entrySet()) {
         String key = (String)entry.getKey();
         AbilityHandler.AbilityHolder cache = (AbilityHandler.AbilityHolder)entry.getValue();
         String source = cache.getSourceID();
         TrinketHelper.SlotInformation sourceInfo = cache.getInfo();
         IAbilityInterface ability = cache.getAbility();
         boolean remove = this.shouldRemove(sourceInfo, source, this.entity);
         if (this.hasChanged) {
            NBTTagCompound data = (NBTTagCompound)Capabilities.getEntityProperties(this.entity, new NBTTagCompound(), (prop, rtn) -> {
               return prop.getTag();
            });
            if (data.hasKey("Abilities")) {
               NBTTagCompound tag = data.getCompoundTag("Abilities");
               this.loadAbilityFromNBT(ability, tag);
            }
         }

         this.processAbility(ability, this.entity);
         if (remove || ability.shouldRemove()) {
            ability.scheduleRemoval();
            ability.onAbilityRemoved(this.entity);
            NBTTagCompound entityTag = (NBTTagCompound)Capabilities.getEntityProperties(this.entity, new NBTTagCompound(), (prop, rtn) -> {
               return prop.getTag();
            });
            if (!entityTag.hasKey("Abilities")) {
               entityTag.setTag("Abilities", new NBTTagCompound());
            }

            NBTTagCompound abilitiesTag = entityTag.getCompoundTag("Abilities");
            this.saveAbilityToNBT(ability, abilitiesTag);
         }
      }

      if (this.hasChanged) {
         this.hasChanged = false;
      }

   }

   private void processAbility(IAbilityInterface ability, EntityLivingBase entity) {
      try {
         AbilityHandler.AbilityHolder holder = this.getAbilityHolder(ability.getRegistryName().toString());
         TrinketHelper.SlotInformation info = holder != null ? holder.getInfo() : null;
         if (ability instanceof ITickableAbility) {
            ((ITickableAbility)ability).tickAbility(entity);
         }

         if (ability instanceof IEquippedAbility) {
            IEquippedAbility a = (IEquippedAbility)ability;
            a.head(entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD), entity);
            a.chest(entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST), entity);
            a.legs(entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS), entity);
            a.feet(entity.getItemStackFromSlot(EntityEquipmentSlot.FEET), entity);
         }

         if (ability instanceof IHeldAbility) {
            IHeldAbility a = (IHeldAbility)ability;
            a.heldMainHand(entity.getHeldItemMainhand(), entity);
            a.heldOffhand(entity.getHeldItemOffhand(), entity);
         }

         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (ability instanceof IContainerAbility) {
               IContainerAbility a = (IContainerAbility)ability;
               a.inventoryContainer(player.inventoryContainer);
               a.openContainer(player.openContainer);
               a.playerInventory(player.inventory);
            }

            if (ability instanceof ITickableInventoryAbility) {
               ITickableInventoryAbility a = (ITickableInventoryAbility)ability;
               int inHand = player.inventory.currentItem;
               if (info != null) {
                  ItemStack stack = info.getStackFromHandler(player);
                  if (!stack.isEmpty()) {
                     int slot = info.getSlot();
                     boolean selected = info.getHandlerType().equals(TrinketHelper.SlotInformation.ItemHandlerType.MAINHAND) && inHand == slot;
                     a.onUpdate(stack, player.world, player, slot, selected);
                  } else {
                     NonNullList<ItemStack> inventory = player.inventory.mainInventory;

                     for(int index = 0; index < inventory.size(); ++index) {
                        ItemStack IStack = (ItemStack)inventory.get(index);
                        if (!IStack.isEmpty()) {
                           a.onUpdate(IStack, player.world, player, index, inHand == index);
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception var12) {
         Trinkets.log.error("Error with ability:" + ability.getRegistryName().toString());
         var12.printStackTrace();
      }

   }

   private boolean shouldRemove(TrinketHelper.SlotInformation info, String source, EntityLivingBase entity) {
      switch(info.getHandlerType()) {
      case NONE:
         return true;
      case OTHER:
         return false;
      case RACE:
         EntityRace race = (EntityRace)Capabilities.getEntityProperties(entity, EntityRaces.none, (prop, rtn) -> {
            return prop.getCurrentRace();
         });
         if (!race.isNone() && race.getRegistryName().toString().contentEquals(source)) {
            return false;
         }

         return true;
      case POTION:
         Potion potion = Potion.getPotionFromResourceLocation(source);
         return potion == null ? true : !entity.isPotionActive(potion);
      default:
         ItemStack s = info.getStackFromHandler(entity);
         boolean remove = s.isEmpty() || !s.getItem().getRegistryName().toString().contentEquals(source);
         return remove;
      }
   }

   public void copyFrom(AbilityHandler source, boolean wasDeath, boolean keepInv) {
      if (!wasDeath) {
         this.active = source.active;
      }

   }

   public NBTTagCompound saveAbilityToNBT(IAbilityInterface ability, NBTTagCompound compound) {
      String key = ability.getRegistryName().toString();
      if (!compound.hasKey(key)) {
         compound.setTag(key, new NBTTagCompound());
      }

      ability.saveStorage(compound.getCompoundTag(key));
      return compound;
   }

   public void loadAbilityFromNBT(IAbilityInterface ability, NBTTagCompound compound) {
      String key = ability.getRegistryName().toString();
      if (compound.hasKey(key)) {
         ability.loadStorage(compound.getCompoundTag(key));
      }

   }

   public NBTTagCompound saveAbilitiesToNBT(NBTTagCompound compound) {
      if (!compound.hasKey("Abilities")) {
         compound.setTag("Abilities", new NBTTagCompound());
      }

      NBTTagCompound abilityNBT = compound.getCompoundTag("Abilities");

      for(Entry<String, AbilityHandler.AbilityHolder> entry : this.active.entrySet()) {
         String key = (String)entry.getKey();
         AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

         try {
            this.saveAbilityToNBT(value.getAbility(), abilityNBT);
         } catch (Exception var8) {
            Trinkets.log.error("Error when saving ability:" + key);
            var8.printStackTrace();
         }
      }

      return compound;
   }

   public void loadAbilitiesFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("Abilities")) {
         NBTTagCompound abilityNBT = compound.getCompoundTag("Abilities");

         for(Entry<String, AbilityHandler.AbilityHolder> entry : this.active.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               this.loadAbilityFromNBT(value.getAbility(), abilityNBT);
            } catch (Exception var8) {
               Trinkets.log.error("Error when saving ability:" + key);
               var8.printStackTrace();
            }
         }
      }

   }

   public class AbilityHolder {
      protected String source;
      protected TrinketHelper.SlotInformation info;
      protected IAbilityInterface ability;

      public AbilityHolder(String source, TrinketHelper.SlotInformation info, IAbilityInterface ability) {
         this.source = source;
         this.info = info;
         this.ability = ability.cacheAbilityHolder(this);
      }

      public final String getSourceID() {
         return this.source;
      }

      public final TrinketHelper.SlotInformation getInfo() {
         return this.info;
      }

      public final IAbilityInterface getAbility() {
         return this.ability;
      }
   }
}
