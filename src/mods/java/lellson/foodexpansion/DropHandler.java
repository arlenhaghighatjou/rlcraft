package lellson.foodexpansion;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DropHandler {
   public static final List<DropHandler.Drop> DROP_LIST = new ArrayList();

   public DropHandler() {
      this.addDrop(FoodExpansionConfig.disableSquidDrop, EntitySquid.class, FoodItems.itemSquid, FoodItems.itemCookedSquid, 2);
      this.addDrop(FoodExpansionConfig.disableHorseMeatDrop, AbstractHorse.class, FoodItems.itemHorseMeat, FoodItems.itemCookedHorseMeat, 3);
      this.addDrop(FoodExpansionConfig.disableBatWingDrop, EntityBat.class, FoodItems.itemBatWing, FoodItems.itemCookedBatWing, 1);
      this.addDrop(FoodExpansionConfig.disableWolfMeatDrop, EntityWolf.class, FoodItems.itemWolfMeat, FoodItems.itemCookedWolfMeat, 2);
      this.addDrop(FoodExpansionConfig.disableOcelotMeatDrop, EntityOcelot.class, FoodItems.itemOcelotMeat, FoodItems.itemCookedOcelotMeat, 1);
      this.addDrop(FoodExpansionConfig.disableParrotMeatDrop, EntityParrot.class, FoodItems.itemParrotMeat, FoodItems.itemCookedParrotMeat, 1);
      this.addDrop(FoodExpansionConfig.disableLlamaMeatDrop, EntityLlama.class, FoodItems.itemLlamaMeat, FoodItems.itemCookedLlamaMeat, 2);
      this.addDrop(FoodExpansionConfig.disablePolarBearMeatDrop, EntityPolarBear.class, FoodItems.itemPolarBearMeat, FoodItems.itemCookedPolarBearMeat, 3);
   }

   private void addDrop(boolean cfgDisable, Class entityClass, Item uncooked, Item cooked, int maxDropAmount) {
      DROP_LIST.add(new DropHandler.Drop(cfgDisable, entityClass, uncooked, cooked, maxDropAmount));
   }

   @SubscribeEvent
   public void onEntityDrop(LivingDropsEvent event) {
      for(DropHandler.Drop drop : DROP_LIST) {
         drop.tryDrop(event.getEntityLiving());
      }

   }

   public class Drop {
      public boolean cfgDisable;
      public Class entityClass;
      public Item uncooked;
      public Item cooked;
      public int maxDropAmount;

      public Drop(boolean cfgDisable, Class entityClass, Item uncooked, Item cooked, int maxDropAmount) {
         this.cfgDisable = cfgDisable;
         this.entityClass = entityClass;
         this.uncooked = uncooked;
         this.cooked = cooked;
         this.maxDropAmount = maxDropAmount;
      }

      public void tryDrop(EntityLivingBase entity) {
         if (!this.cfgDisable && this.entityClass.isInstance(entity) && !entity.isChild() && !(entity instanceof EntitySkeletonHorse) && !(entity instanceof EntityZombieHorse)) {
            entity.entityDropItem(new ItemStack(entity.isBurning() ? this.cooked : this.uncooked, entity.getEntityWorld().rand.nextInt(this.maxDropAmount) + 1), 0.5F);
         }

      }
   }
}
