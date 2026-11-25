package net.silentchaos512.lib.event;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.util.PlayerHelper;

@EventBusSubscriber(
   modid = "silentlib"
)
@ParametersAreNonnullByDefault
public final class InitialSpawnItems {
   private static final Map<ResourceLocation, Supplier<ItemStack>> SPAWN_ITEMS = new HashMap();
   private static final String NBT_KEY = "silentlib.SpawnItemsGiven";

   private InitialSpawnItems() {
   }

   public static void add(ResourceLocation key, Supplier<ItemStack> stack) {
      SPAWN_ITEMS.put(key, stack);
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      EntityPlayer player = event.player;
      NBTTagCompound givenItems = PlayerHelper.getPersistedDataSubcompound(player, "silentlib.SpawnItemsGiven");
      SPAWN_ITEMS.forEach((key, supplier) -> {
         String nbtKey = key.toString().replaceFirst(":", ".");
         if (!givenItems.getBoolean(nbtKey)) {
            ItemStack stack = (ItemStack)supplier.get();
            if (!stack.isEmpty()) {
               SilentLib.LOGGER.debug("Giving player {} spawn item \"{}\" = {}", player.getName(), nbtKey, stack);
               PlayerHelper.giveItem(player, stack);
               givenItems.setBoolean(nbtKey, true);
            }
         }

      });
   }
}
