package vazkii.quark.misc.feature;

import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.ai.EntityAIHorseFollow;
import vazkii.quark.misc.item.ItemHorseWhistle;

public class HorseWhistle extends Feature {
   public static boolean horsesAreMagical;
   public static double horseSummonRange;
   public static ItemHorseWhistle horse_whistle;

   public HorseWhistle() {
   }

   public void setupConfig() {
      horsesAreMagical = this.loadPropBool("Horses Teleport When Whistled", "Should horses teleport when you call for them if they can't find a way to get to you?", false);
      horseSummonRange = this.loadPropDouble("Horse Summoning Range", "How far away should horses be able to hear the whistle?", 64.0D);
   }

   public void preInit(FMLPreInitializationEvent event) {
      horse_whistle = new ItemHorseWhistle("horse_whistle");
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(horse_whistle), new Object[]{"PPP", "NSP", 'P', "plankWood", 'N', "nuggetIron", 'S', "stickWood"});
   }

   @SubscribeEvent
   public void onEntityTick(LivingUpdateEvent event) {
      if (event.getEntity() instanceof AbstractHorse) {
         AbstractHorse horse = (AbstractHorse)event.getEntity();

         for(EntityAITaskEntry task : horse.tasks.taskEntries) {
            if (task.action instanceof EntityAIHorseFollow) {
               return;
            }
         }

         horse.tasks.addTask(1, new EntityAIHorseFollow(horse, 2.0D));
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
