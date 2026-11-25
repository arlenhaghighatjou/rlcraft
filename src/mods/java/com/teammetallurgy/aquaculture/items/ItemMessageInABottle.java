package com.teammetallurgy.aquaculture.items;

import com.teammetallurgy.aquaculture.items.meta.MetaItem;
import com.teammetallurgy.aquaculture.items.meta.SubItem;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ItemMessageInABottle extends SubItem {
   private Random rand = new Random();

   public ItemMessageInABottle(MetaItem metaItem) {
      super(metaItem);
   }

   @Nonnull
   public ItemStack onItemRightClick(@Nonnull ItemStack stack, World world, EntityPlayer player) {
      if (world.isRemote) {
         return stack;
      } else {
         world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 0.5F, 0.4F / (this.rand.nextFloat() * 0.4F + 0.8F));
         int messageRoll = this.rand.nextInt(29) + 1;
         String message = "";
         if (messageRoll == 0) {
            message = "ERROR! Fish Escaping!";
         } else {
            message = "aquaculture.message" + messageRoll;
         }

         if (player instanceof EntityPlayerMP) {
            TextComponentTranslation chatMessage = new TextComponentTranslation(message, new Object[0]);
            player.sendMessage(chatMessage);
         }

         stack.shrink(1);
         return stack;
      }
   }
}
