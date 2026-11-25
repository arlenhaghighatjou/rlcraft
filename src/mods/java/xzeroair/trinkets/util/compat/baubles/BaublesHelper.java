package xzeroair.trinkets.util.compat.baubles;

import baubles.api.BaubleType;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.entity.EntityLivingBase;
import xzeroair.trinkets.capabilities.Capabilities;

public class BaublesHelper {
   public BaublesHelper() {
   }

   public static IBaublesItemHandler getBaublesHandler(EntityLivingBase entity) {
      return (IBaublesItemHandler)Capabilities.getCapabilityWithConsumer(entity, BaublesCapabilities.CAPABILITY_BAUBLES, (handler) -> {
         handler.setPlayer(entity);
      });
   }

   public static IBaublesItemHandler getBaublesHandler(EntityLivingBase entity, Consumer<IBaublesItemHandler> consumer) {
      return (IBaublesItemHandler)Capabilities.getCapabilityWithConsumer(entity, BaublesCapabilities.CAPABILITY_BAUBLES, (handler) -> {
         handler.setPlayer(entity);
         consumer.accept(handler);
      });
   }

   public static <R> R getBaublesHandler(EntityLivingBase entity, R ret, BiFunction<IBaublesItemHandler, R, R> func) {
      return (R)Capabilities.getCapabilityWithReturn(entity, BaublesCapabilities.CAPABILITY_BAUBLES, ret, (handler, rtn) -> {
         handler.setPlayer(entity);
         return (R)func.apply(handler, rtn);
      });
   }

   public static BaubleType getBaubleType(String string) {
      string = string.toLowerCase();
      if (!string.contentEquals("amulet") && !string.contentEquals("necklace") && !string.contentEquals("pendant")) {
         if (!string.contentEquals("ring") && !string.contentEquals("rings")) {
            if (string.contentEquals("belt")) {
               return BaubleType.BELT;
            } else if (!string.contentEquals("head") && !string.contentEquals("hat")) {
               if (!string.contentEquals("body") && !string.contentEquals("chest")) {
                  return string.contentEquals("charm") ? BaubleType.CHARM : BaubleType.TRINKET;
               } else {
                  return BaubleType.BODY;
               }
            } else {
               return BaubleType.HEAD;
            }
         } else {
            return BaubleType.RING;
         }
      } else {
         return BaubleType.AMULET;
      }
   }
}
