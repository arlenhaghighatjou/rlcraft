package svenhjol.charm.tweaks.feature;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import svenhjol.meson.Feature;
import svenhjol.meson.Meson;
import svenhjol.meson.helper.ObfuscationHelper;

public class RemoveNitwits extends Feature {
   public RemoveNitwits() {
   }

   public String getDescription() {
      return "Removes nitwits as spawnable villagers.";
   }

   @SubscribeEvent
   public void onVillagerSpawn(EntityJoinWorldEvent event) {
      if (!event.isCanceled() && !event.getWorld().isRemote && event.getEntity() instanceof EntityVillager) {
         int i = 0;
         EntityVillager villager = (EntityVillager)event.getEntity();
         ResourceLocation res = villager.getProfessionForge().getRegistryName();
         DifficultyInstance difficulty = event.getWorld().getDifficultyForLocation(new BlockPos(villager));

         while(res != null && res.toString().equals("minecraft:nitwit")) {
            VillagerRegistry.setRandomProfession(villager, event.getWorld().rand);
            res = villager.getProfessionForge().getRegistryName();
            ++i;
            if (i > 20) {
               break;
            }
         }

         if (i > 0) {
            ReflectionHelper.setPrivateValue(EntityVillager.class, villager, Integer.valueOf(0), ObfuscationHelper.Fields.CAREER_LEVEL[2], ObfuscationHelper.Fields.CAREER_LEVEL[1]);
            villager.finalizeMobSpawn(difficulty, (IEntityLivingData)null, false);
            Meson.debug("Changed nitwit to " + res);
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
