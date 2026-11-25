package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.info.AltarInfo;
import com.lycanitesmobs.core.item.ItemBase;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemSoulkey extends ItemBase {
   public int variant = -1;

   public ItemSoulkey(String itemName, int variant) {
      this.itemName = itemName;
      this.variant = variant;
      this.setup();
   }

   public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
      super.onUpdate(itemStack, world, entity, par4, par5);
   }

   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack itemStack = player.getHeldItem(hand);
      if (!AltarInfo.checkAltarsEnabled() && !player.getEntityWorld().isRemote) {
         String message = LanguageManager.translate("message.soulkey.disabled");
         player.sendMessage(new TextComponentString(message));
         return EnumActionResult.FAIL;
      } else {
         List<AltarInfo> possibleAltars = new ArrayList();
         if (AltarInfo.altars.isEmpty()) {
            LycanitesMobs.logWarning("", "No altars have been registered, Soulkeys will not work at all.");
         }

         for(AltarInfo altarInfo : AltarInfo.altars.values()) {
            if (altarInfo.checkBlockEvent(player, world, pos) && altarInfo.quickCheck(player, world, pos)) {
               possibleAltars.add(altarInfo);
            }
         }

         if (possibleAltars.isEmpty()) {
            String message = LanguageManager.translate("message.soulkey.none");
            player.sendMessage(new TextComponentString(message));
            return EnumActionResult.FAIL;
         } else {
            for(AltarInfo altarInfo : possibleAltars) {
               if (altarInfo.fullCheck(player, world, pos)) {
                  if (!player.getEntityWorld().isRemote) {
                     if (!altarInfo.activate(player, world, pos, this.variant)) {
                        String message = LanguageManager.translate("message.soulkey.badlocation");
                        player.sendMessage(new TextComponentString(message));
                        return EnumActionResult.FAIL;
                     }

                     if (!player.capabilities.isCreativeMode) {
                        itemStack.shrink(1);
                     }

                     String message = LanguageManager.translate("message.soulkey.active");
                     player.sendMessage(new TextComponentString(message));
                  }

                  return EnumActionResult.SUCCESS;
               }
            }

            if (!player.capabilities.isCreativeMode) {
               String message = LanguageManager.translate("message.soulkey.invalid");
               player.sendMessage(new TextComponentString(message));
            }

            return EnumActionResult.FAIL;
         }
      }
   }
}
