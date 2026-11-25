package vazkii.quark.tweaks.feature;

import java.util.regex.Pattern;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class StackableItems extends Feature {
   public static String[] items;

   public StackableItems() {
   }

   public void setupConfig() {
      int minecarts = 16;
      int soups = 64;
      int saddle = 8;
      if (this.hasConfigKey("Minecarts")) {
         minecarts = Math.min(64, this.loadPropInt("Minecarts", "", 16));
         this.removeLegacyKey("Minecarts");
      }

      if (this.hasConfigKey("Soups")) {
         soups = Math.min(64, this.loadPropInt("Soups", "", 64));
         this.removeLegacyKey("Soups");
      }

      if (this.hasConfigKey("Saddle")) {
         saddle = Math.min(64, this.loadPropInt("Saddle", "", 8));
         this.removeLegacyKey("Saddle");
      }

      items = this.loadPropStringList("Stackable Items", "The format for setting an item's max size is item name|stacksize. i.e. `minecraft:saddle|8`", new String[]{Items.MINECART.getRegistryName() + "|" + minecarts, Items.CHEST_MINECART.getRegistryName() + "|" + minecarts, Items.COMMAND_BLOCK_MINECART.getRegistryName() + "|" + minecarts, Items.FURNACE_MINECART.getRegistryName() + "|" + minecarts, Items.HOPPER_MINECART.getRegistryName() + "|" + minecarts, Items.TNT_MINECART.getRegistryName() + "|" + minecarts, Items.MUSHROOM_STEW.getRegistryName() + "|" + soups, Items.RABBIT_STEW.getRegistryName() + "|" + soups, Items.BEETROOT_SOUP.getRegistryName() + "|" + soups, Items.SADDLE.getRegistryName() + "|" + saddle});
   }

   public void init() {
      for(String key : items) {
         String[] split = key.split("\\|");
         if (split.length == 2 && Pattern.matches("\\d+", split[1])) {
            ResourceLocation loc = new ResourceLocation(split[0]);
            int size = Math.min(64, Math.max(0, Integer.parseInt(split[1])));

            for(Item item : Item.REGISTRY) {
               if (loc.equals(item.getRegistryName())) {
                  item.setMaxStackSize(size);
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void finishEvent(Finish event) {
      if (event.getEntity() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntity();
         ItemStack original = event.getItem();
         ItemStack result = event.getResultStack();
         if (original.getCount() > 1 && (result.getItem() == Items.BOWL || result.getItem() == Items.BUCKET)) {
            ItemStack newResult = original.copy();
            newResult.setCount(original.getCount() - 1);
            event.setResultStack(newResult);
            player.addItemStackToInventory(result);
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
