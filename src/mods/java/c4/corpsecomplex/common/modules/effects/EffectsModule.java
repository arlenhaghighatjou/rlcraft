package c4.corpsecomplex.common.modules.effects;

import c4.corpsecomplex.CorpseComplex;
import c4.corpsecomplex.common.Module;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import org.apache.logging.log4j.Level;

public class EffectsModule extends Module {
   private static ArrayList<String[]> effects;
   private static ArrayList<String[]> customCureEffects;
   private static String[] cfgCureList;
   private static boolean cfgEnabled;

   @SubscribeEvent
   public void onItemUseFinish(Finish evt) {
      EntityLivingBase entityLivingBase = evt.getEntityLiving();
      if (!entityLivingBase.world.isRemote && cfgCureList.length > 0) {
         entityLivingBase.curePotionEffects(evt.getItem());
      }

   }

   @SubscribeEvent
   public void onPlayerRespawnFinish(PlayerRespawnEvent e) {
      if (!e.player.world.isRemote && !e.isEndConquered()) {
         EntityPlayer player = e.player;
         addPotionEffects(player, effects, false);
         addPotionEffects(player, customCureEffects, true);
      }

   }

   public EffectsModule() {
      super("Effects", "Add potions effects that will be applied to players on respawn");
      this.submoduleClasses = new ArrayList();
      this.addSubmodule(MoriModule.class);
   }

   public void loadModuleConfig() {
      this.setCategoryComment();
      cfgEnabled = this.getBool("Enable Effects Module", false, "Set to true to enable effects module", true);
      cfgCureList = this.getStringList("Curative Items", new String[]{"minecraft:milk_bucket"}, "List of items that will be used by 'Curable Respawn Effects'", false);
      String[] cfgEffects = this.getStringList("Uncurable Respawn Effects", new String[]{"minecraft:mining_fatigue 30 4"}, "List of effects to apply to player on respawn\nFormat: [effect] [duration(secs)] [power]", false);
      String[] cfgCustomCureEffects = this.getStringList("Curable Respawn Effects", new String[0], "List of effects to apply to players on respawn that can be cured by the curing items list\nFormat: [effect] [duration(secs)] [power]", false);
      effects = new ArrayList(initEffectsList(cfgEffects));
      customCureEffects = new ArrayList(initEffectsList(cfgCustomCureEffects));
   }

   public void initPropOrder() {
      this.propOrder = new ArrayList(Collections.singletonList("Enable Effects Module"));
   }

   public void setEnabled() {
      this.enabled = cfgEnabled;
   }

   private static void addPotionEffects(EntityPlayer player, ArrayList<String[]> effectsToApply, boolean useCureList) {
      if (!effectsToApply.isEmpty()) {
         for(String[] s : effectsToApply) {
            Potion potion = Potion.getPotionFromResourceLocation(s[0]);
            if (potion != null) {
               int duration = Integer.parseInt(s[1]) * 20;
               int amp = Integer.parseInt(s[2]) - 1;
               PotionEffect potionEffect = new PotionEffect(potion, duration, amp);
               if (!useCureList) {
                  potionEffect.setCurativeItems(new ArrayList(0));
               } else {
                  ArrayList<ItemStack> cureList = new ArrayList();

                  for(String s1 : cfgCureList) {
                     Item item = Item.getByNameOrId(s1);
                     if (item != null) {
                        cureList.add(new ItemStack(item));
                     }
                  }

                  potionEffect.setCurativeItems(cureList);
               }

               player.addPotionEffect(potionEffect);
            }
         }

      }
   }

   private static ArrayList<String[]> initEffectsList(String[] effectsList) {
      ArrayList<String[]> effectsToApply = new ArrayList();

      for(String s : effectsList) {
         String[] elements = s.split("\\s+");
         String[] effectAttributes = new String[3];
         int i = 0;
         int j = 0;

         try {
            i = Integer.parseInt(elements[1]);
            j = Integer.parseInt(elements[2]);
         } catch (Exception var14) {
            CorpseComplex.logger.log(Level.ERROR, "Problem parsing respawn effects list!", var14);
         } finally {
            elements[1] = Integer.toString(Math.max(1, Math.min(i, 1600)));
            elements[2] = Integer.toString(Math.max(1, Math.min(j, 10)));
            effectAttributes[0] = elements[0];
            effectAttributes[1] = elements[1];
            effectAttributes[2] = elements[2];
         }

         effectsToApply.add(effectAttributes);
      }

      return effectsToApply;
   }
}
