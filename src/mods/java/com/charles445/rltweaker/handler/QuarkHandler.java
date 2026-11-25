package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.QuarkReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class QuarkHandler {
   QuarkReflect reflector;

   public QuarkHandler() {
      try {
         this.reflector = new QuarkReflect();
         CompatUtil.wrapSpecificHandler("QKAncientTomeAnvilUpdate", (x$0) -> {
            new QuarkHandler.QKAncientTomeAnvilUpdate(x$0);
         }, "vazkii.quark.misc.feature.AncientTomes", "onAnvilUpdate");
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup PotionCoreHandlerClient!", var2);
         ErrorUtil.logSilent("PotionCore Client Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   public class QKAncientTomeAnvilUpdate {
      private IEventListener handler;

      public QKAncientTomeAnvilUpdate(IEventListener handler) {
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent
      public void onUseItem(RightClickItem event) {
         if (event.getHand() == EnumHand.MAIN_HAND && ModConfig.server.quark.ancientTomesAlternateBehavior) {
            EntityPlayer player = event.getEntityPlayer();
            if (player != null) {
               ItemStack offhandStack = player.getHeldItemOffhand();
               if (!offhandStack.isEmpty()) {
                  ItemStack mainhandStack = player.getHeldItemMainhand();
                  if (!mainhandStack.isEmpty() && mainhandStack.getItem() != Items.ENCHANTED_BOOK) {
                     try {
                        Item ancient_tome = QuarkHandler.this.reflector.getAncientTomeItem();
                        if (ancient_tome == null) {
                           ErrorUtil.logSilent("Quark AncientTomes Item Null");
                        } else if (offhandStack.getItem() == ancient_tome) {
                           if (mainhandStack.getItem() != ancient_tome) {
                              Map<Enchantment, Integer> tomeEnchants = EnchantmentHelper.getEnchantments(offhandStack);
                              Map<Enchantment, Integer> itemEnchants = EnchantmentHelper.getEnchantments(mainhandStack);
                              boolean matched = false;

                              for(Entry<Enchantment, Integer> entry : tomeEnchants.entrySet()) {
                                 Enchantment enchant = (Enchantment)entry.getKey();
                                 if (enchant != null) {
                                    Integer val = (Integer)itemEnchants.get(enchant);
                                    if (val != null && val == entry.getValue()) {
                                       itemEnchants.put(enchant, Integer.valueOf(val.intValue() + 1));
                                       matched = true;
                                    }
                                 }
                              }

                              if (matched) {
                                 ItemStack returnStack = mainhandStack.copy();
                                 EnchantmentHelper.setEnchantments(itemEnchants, returnStack);
                                 player.setHeldItem(EnumHand.MAIN_HAND, returnStack);
                                 player.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
                                 if (player.world != null && !player.world.isRemote) {
                                    player.world.playSound((EntityPlayer)null, player.getPosition(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.7F, player.world.rand.nextFloat() * 0.1F + 0.9F);
                                 }
                              }

                           }
                        }
                     } catch (IllegalAccessException | IllegalArgumentException var13) {
                        ErrorUtil.logSilent("Quark AncientTomes Invocation");
                     }
                  }
               }
            }
         }
      }

      @SubscribeEvent
      public void anvilUpdate(AnvilUpdateEvent event) {
         if (!ModConfig.server.quark.ancientTomesAlternateBehavior) {
            this.handler.invoke(event);
         }
      }
   }
}
