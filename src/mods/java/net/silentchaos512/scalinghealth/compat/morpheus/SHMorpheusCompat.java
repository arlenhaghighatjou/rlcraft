package net.silentchaos512.scalinghealth.compat.morpheus;

import com.google.common.base.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.quetzi.morpheus.Morpheus;
import net.quetzi.morpheus.MorpheusRegistry;
import net.quetzi.morpheus.api.INewDayHandler;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;
import net.silentchaos512.scalinghealth.world.ScalingHealthSavedData;

public class SHMorpheusCompat {
   public SHMorpheusCompat() {
   }

   public static void init() {
      INewDayHandler parent = null;
      if (Morpheus.register.isDimRegistered(0)) {
         parent = (INewDayHandler)MorpheusRegistry.registry.get(Integer.valueOf(0));
      }

      INewDayHandler newHandler = new SHMorpheusCompat.NewDayHandler(parent);
      ScalingHealth.logHelper.info("Replacing Morpheus new day handler for dimension {}!", new Object[]{Integer.valueOf(0)});
      ScalingHealth.logHelper.info("Parent handler: {}", new Object[]{parent});
      Morpheus.register.registerHandler(newHandler, 0);
   }

   public static class NewDayHandler implements INewDayHandler {
      INewDayHandler parent;

      public NewDayHandler(INewDayHandler parent) {
         this.parent = parent;
      }

      public void startNewDay() {
         if (this.parent != null) {
            this.parent.startNewDay();
         }

         MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
         if (server != null) {
            World world = server.worlds[0];
            if (world != null) {
               for(EntityPlayer player : world.getPlayers(EntityPlayer.class, (e) -> {
                  return true;
               })) {
                  SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
                  if (data != null) {
                     data.incrementDifficulty((double)Config.Difficulty.forSleeping, false);
                  }
               }

               ScalingHealthSavedData data = ScalingHealthSavedData.get(world);
               if (data != null) {
                  data.difficulty += (double)Config.Difficulty.forSleeping;
               }

            }
         }
      }
   }
}
