package vazkii.quark.tweaks.feature;

import com.google.common.collect.Sets;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.entity.EntityArchaeologist;

public class VillagerPursueEmeralds extends Feature {
   public VillagerPursueEmeralds() {
   }

   @SubscribeEvent
   public void onEntityTick(LivingUpdateEvent event) {
      if (event.getEntity() instanceof EntityVillager) {
         EntityVillager villager = (EntityVillager)event.getEntity();

         for(EntityAITaskEntry task : villager.tasks.taskEntries) {
            if (task.action instanceof EntityAITempt) {
               return;
            }
         }

         villager.tasks.addTask(2, new EntityAITempt(villager, 0.6D, Item.getItemFromBlock(Blocks.EMERALD_BLOCK), false));
      }

      if (event.getEntity() instanceof EntityArchaeologist) {
         EntityArchaeologist villager = (EntityArchaeologist)event.getEntity();

         for(EntityAITaskEntry task : villager.tasks.taskEntries) {
            if (task.action instanceof EntityAITempt) {
               return;
            }
         }

         villager.tasks.addTask(2, new EntityAITempt(villager, 0.6D, false, Sets.newHashSet(new Item[]{Item.getItemFromBlock(Blocks.EMERALD_BLOCK), Item.getItemFromBlock(Blocks.BONE_BLOCK)})));
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public String getFeatureIngameConfigName() {
      return "Villagers Pursue Emeralds";
   }
}
