package net.silentchaos512.lib.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.advancements.LibTriggers;
import net.silentchaos512.lib.advancements.UseItemTrigger;
import net.silentchaos512.lib.item.ItemGuideBookSL;
import net.silentchaos512.lib.util.PlayerHelper;

public final class SilentLibCommonEvents {
   private static final String NBT_ROOT_GUIDE_BOOKS = "silentlib_guide_books";

   public SilentLibCommonEvents() {
   }

   public void register(EventBus bus) {
      bus.register(PlayerLoggedInEvent.class, this::onPlayerLoggedIn);
      bus.register(RightClickBlock.class, this::onPlayerRightClickBlock);
      bus.register(EntityInteract.class, this::onPlayerRightClickEntity);
      bus.register(RightClickItem.class, this::onPlayerRightClickItem);
   }

   public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      EntityPlayer player = event.player;
      NBTTagCompound guideData = PlayerHelper.getPersistedDataSubcompound(player, "silentlib_guide_books");
      int id = 0;

      for(ItemGuideBookSL item = ItemGuideBookSL.getBookById(id); item != null && item.giveBookOnFirstLogin; item = ItemGuideBookSL.getBookById(id)) {
         ResourceLocation name = (ResourceLocation)Objects.requireNonNull(item.getRegistryName());
         if (!guideData.getBoolean(name.toString())) {
            guideData.setBoolean(name.toString(), true);
            PlayerHelper.giveItem(player, new ItemStack(item));
            SilentLib.LOGGER.info("Player has been given guide book {}", name);
         }

         ++id;
      }

   }

   public void onPlayerRightClickBlock(RightClickBlock event) {
      if (event.getEntityPlayer() instanceof EntityPlayerMP) {
         LibTriggers.USE_ITEM.trigger((EntityPlayerMP)event.getEntityPlayer(), event.getItemStack(), UseItemTrigger.Target.BLOCK);
      }

   }

   public void onPlayerRightClickEntity(EntityInteract event) {
      if (event.getEntityPlayer() instanceof EntityPlayerMP) {
         LibTriggers.USE_ITEM.trigger((EntityPlayerMP)event.getEntityPlayer(), event.getItemStack(), UseItemTrigger.Target.ENTITY);
      }

   }

   public void onPlayerRightClickItem(RightClickItem event) {
      if (event.getEntityPlayer() instanceof EntityPlayerMP) {
         LibTriggers.USE_ITEM.trigger((EntityPlayerMP)event.getEntityPlayer(), event.getItemStack(), UseItemTrigger.Target.ITEM);
      }

   }
}
