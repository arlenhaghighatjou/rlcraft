package org.orecruncher.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextComponent.Serializer;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.CheckResult;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class VersionChecker {
   private final String modId;
   private final String messageId;

   public VersionChecker(@Nonnull String id, @Nonnull String messageId) {
      this.modId = id;
      this.messageId = messageId;
   }

   private static boolean dontPrintMessage(@Nonnull CheckResult result) {
      return result == null || result.status == null || result.target == null || result.url == null || result.status == Status.UP_TO_DATE || result.status == Status.AHEAD || result.status == Status.PENDING || result.status == Status.FAILED;
   }

   @Nullable
   private String getUpdateMessage(@Nonnull String modId) {
      ModContainer mod = ForgeUtils.findModContainer(modId);
      if (mod == null) {
         return null;
      } else {
         CheckResult result = ForgeVersion.getResult(mod);
         if (dontPrintMessage(result)) {
            return null;
         } else {
            String t = result.target.toString();
            String u = result.url.toString();
            return Localization.format(this.messageId, mod.getName(), t, u);
         }
      }
   }

   public void playerLogin(@Nonnull PlayerLoggedInEvent event) {
      if (event.player instanceof EntityPlayer) {
         String updateMessage = this.getUpdateMessage(this.modId);
         if (updateMessage != null) {
            try {
               ITextComponent component = Serializer.jsonToComponent(updateMessage);
               event.player.sendMessage(component);
            } catch (Throwable var4) {
               var4.printStackTrace();
            }
         }
      }

   }

   public static void doCheck(@Nonnull PlayerLoggedInEvent event, @Nonnull String modId) {
      doCheck(event, modId, modId + ".msg.NewVersion");
   }

   public static void doCheck(@Nonnull PlayerLoggedInEvent event, @Nonnull String modId, @Nonnull String msgId) {
      if (event.player instanceof EntityPlayer) {
         (new VersionChecker(modId, msgId)).playerLogin(event);
      }

   }
}
