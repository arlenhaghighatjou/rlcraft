package org.orecruncher.dsurround.client.gui;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.sound.SoundBuilder;
import org.orecruncher.dsurround.client.sound.SoundEngine;
import org.orecruncher.dsurround.client.sound.SoundInstance;
import org.orecruncher.lib.random.XorShiftRandom;
import org.orecruncher.lib.task.Scheduler;

@SideOnly(Side.CLIENT)
public final class HumDinger {
   private static boolean hasPlayed = false;

   public HumDinger() {
   }

   @SubscribeEvent
   public static void onGuiOpen(@Nonnull GuiOpenEvent event) {
      if (!hasPlayed && event.getGui() instanceof GuiMainMenu) {
         hasPlayed = true;
         List<String> possibles = (List)Arrays.stream(ModOptions.general.startupSoundList).map((s) -> {
            return StringUtils.trim(s);
         }).filter((s) -> {
            return s.length() > 0;
         }).collect(Collectors.toList());
         if (possibles.size() == 0) {
            return;
         }

         String res = (String)possibles.get(XorShiftRandom.current().nextInt(possibles.size()));
         if (!StringUtils.isEmpty(res)) {
            SoundEvent se = (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation(res));
            if (se != null) {
               Scheduler.scheduleDeferred(Side.CLIENT, () -> {
                  try {
                     SoundInstance snd = SoundBuilder.create(se, SoundCategory.MASTER);
                     SoundEngine.instance().playSound(snd);
                  } catch (Throwable var2) {
                     ModBase.log().error("Error executing ding", var2);
                  }

               });
            } else {
               ModBase.log().warn("Unable to locate startup sound [%s]", new Object[]{res});
            }
         } else {
            ModBase.log().warn("Improperly formatted startup sound list!", new Object[0]);
         }
      }

   }
}
