package xzeroair.trinkets.client.events;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.race.EntityProperties;
import xzeroair.trinkets.capabilities.race.KeybindHandler;
import xzeroair.trinkets.client.keybinds.KeyHandler;
import xzeroair.trinkets.client.keybinds.ModKeyBindings;
import xzeroair.trinkets.entity.AlphaWolf;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.init.ModPotionTypes;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.keybinds.KeybindPacket;
import xzeroair.trinkets.network.keybinds.MovementKeyPacket;
import xzeroair.trinkets.network.trinketcontainer.OpenTrinketGui;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.traits.abilities.interfaces.IKeyBindInterface;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ConfigHelper;
import xzeroair.trinkets.util.helpers.TranslationHelper;

@SideOnly(Side.CLIENT)
public class EventHandlerClient {
   private final String[] keys = new String[]{"Left", "Right", "Forward", "Backward", "Jump", "Sneak"};

   public EventHandlerClient() {
   }

   @SubscribeEvent
   public void clientTickEvent(ClientTickEvent event) {
      if (event.side == Side.CLIENT && event.phase == Phase.START) {
         EntityPlayerSP player = Minecraft.getMinecraft().player;
         if (player != null && TrinketsConfig.SERVER.GUI.guiEnabled && ModKeyBindings.TRINKET_GUI.isPressed()) {
            NetworkHandler.sendToServer(new OpenTrinketGui(0));
         }
      }

      if (event.phase == Phase.END) {
         if (!FMLClientHandler.instance().getClient().inGameHasFocus) {
            return;
         }

         EntityPlayerSP player = Minecraft.getMinecraft().player;
         if (player != null && player.world != null) {
            try {
               if (player.isRiding()) {
                  Entity mount = player.getRidingEntity();
                  if (mount instanceof AlphaWolf) {
                     AlphaWolf wolf = (AlphaWolf)mount;
                     if (player.movementInput.jump) {
                        wolf.setJumping(true);
                     }
                  }
               }
            } catch (Exception var5) {
               var5.printStackTrace();
            }

            EntityProperties var7 = Capabilities.getEntityProperties(player, (prop) -> {
               Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

               for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
                  String key = (String)entry.getKey();
                  AbilityHandler.AbilityHolder holder = (AbilityHandler.AbilityHolder)entry.getValue();

                  try {
                     IAbilityInterface ability = holder.getAbility();
                     if (ability instanceof IKeyBindInterface) {
                        IKeyBindInterface keybind = (IKeyBindInterface)ability;
                        String keybind1 = keybind.getKey().replace(" ", "");
                        String keybind2 = keybind.getAuxKey().replace(" ", "");
                        boolean keyDown = !keybind1.isEmpty() ? ModKeyBindings.isKeyDownFromName(keybind1) : false;
                        boolean auxDown = !keybind2.isEmpty() ? ModKeyBindings.isKeyDownFromName(keybind2) : false;
                        KeyHandler keyHand = prop.getKeybindHandler().getKeyHandler(key + "." + keybind1);
                        if (keyHand != null) {
                           keyHand.handler(keyDown, (press) -> {
                              if (keybind.onKeyPress(player, auxDown)) {
                                 NetworkHandler.sendToServer(new KeybindPacket(player, key, keyDown, auxDown, 0));
                                 return true;
                              } else {
                                 return false;
                              }
                           }, (down) -> {
                              if (keybind.onKeyDown(player, auxDown)) {
                                 NetworkHandler.sendToServer(new KeybindPacket(player, key, keyDown, auxDown, 1));
                                 return true;
                              } else {
                                 return false;
                              }
                           }, (release) -> {
                              if (keybind.onKeyRelease(player, auxDown)) {
                                 NetworkHandler.sendToServer(new KeybindPacket(player, key, false, auxDown, 2));
                                 return true;
                              } else {
                                 return false;
                              }
                           });
                        }
                     }
                  } catch (Exception var14) {
                     Trinkets.log.error("Trinkets had an Error with Ability:" + key);
                     var14.printStackTrace();
                  }
               }

               GameSettings s = Minecraft.getMinecraft().gameSettings;
               KeybindHandler.keyEnum[] defaultKeys = KeybindHandler.keyEnum.values();

               for(KeybindHandler.keyEnum key : defaultKeys) {
                  KeyBinding kb = null;
                  switch(key) {
                  case LEFT:
                     kb = s.keyBindLeft;
                     break;
                  case RIGHT:
                     kb = s.keyBindRight;
                     break;
                  case FORWARD:
                     kb = s.keyBindForward;
                     break;
                  case BACK:
                     kb = s.keyBindBack;
                     break;
                  case JUMP:
                     kb = s.keyBindJump;
                     break;
                  case SNEAK:
                     kb = s.keyBindSneak;
                  }

                  if (kb != null) {
                     KeyHandler handler = prop.getKeybindHandler().getKeyHandler(key.getName());
                     if (handler != null) {
                        handler.handler(kb.isKeyDown(), (press) -> {
                           if (prop.getKeybindHandler().pressKey(player, key.getName(), 0)) {
                              MovementKeyPacket message = new MovementKeyPacket(player, key.getName(), 0);
                              NetworkHandler.sendToServer(message);
                              return true;
                           } else {
                              return false;
                           }
                        }, (down) -> {
                           if (prop.getKeybindHandler().pressKey(player, key.getName(), 1)) {
                              MovementKeyPacket message = new MovementKeyPacket(player, key.getName(), 1);
                              NetworkHandler.sendToServer(message);
                              return true;
                           } else {
                              return false;
                           }
                        }, (release) -> {
                           if (prop.getKeybindHandler().pressKey(player, key.getName(), 2)) {
                              MovementKeyPacket message = new MovementKeyPacket(player, key.getName(), 2);
                              NetworkHandler.sendToServer(message);
                              return true;
                           } else {
                              return false;
                           }
                        });
                     }
                  }
               }

               if (TrinketsConfig.SERVER.misc.movement && prop.getRaceHandler().isTransforming()) {
                  if (player.posX != player.prevPosX || player.posZ != player.prevPosZ) {
                     player.setPositionAndUpdate(player.prevPosX, player.posY, player.prevPosZ);
                  }

                  if (player == Minecraft.getMinecraft().player) {
                     KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode(), false);
                     KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode(), false);
                     KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode(), false);
                     KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode(), false);
                     KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode(), false);
                     KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode(), false);
                  }
               }

            });
         }
      }

   }

   @SubscribeEvent
   public void ItemToolTipEvent(ItemTooltipEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      if (event.getEntityPlayer() != null) {
         if (player.getEntityWorld() != null) {
            ItemStack stack = event.getItemStack();
            Item item = stack.getItem();
            String regName = item.getRegistryName().toString();
            String ModID = item.getRegistryName().getResourceDomain();
            String ItemID = item.getRegistryName().getResourcePath();
            if (TrinketsConfig.CLIENT.debug.showOreDictEntries) {
               for(int or : OreDictionary.getOreIDs(stack)) {
                  event.getToolTip().add(OreDictionary.getOreName(or));
               }
            }

            if (stack.getItem() instanceof ItemArmor) {
               ItemArmor armor = (ItemArmor)stack.getItem();
               if (TrinketsConfig.CLIENT.debug.debugArmorMaterials) {
                  event.getToolTip().add(armor.getArmorMaterial().toString());
               }

               if (TrinketsConfig.SERVER.races.faelis.penalties) {
                  boolean isFaelis = ((Boolean)Capabilities.getEntityProperties(player, Boolean.valueOf(false), (prop, rtn) -> {
                     return prop.getCurrentRace().equals(EntityRaces.faelis);
                  })).booleanValue();
                  if (isFaelis) {
                     TreeMap<String, ConfigHelper.ArmorEntry> ArmorWeightValues = ConfigHelper.TrinketConfigStorage.ArmorWeightValues;

                     for(ConfigHelper.ArmorEntry entry : ArmorWeightValues.values()) {
                        if (entry.doesItemMatchEntry(stack) && entry.getWeight() != 0.0D) {
                           String color = entry.getWeight() > 0.0D ? "" + TextFormatting.RED : "" + TextFormatting.BLUE;
                           event.getToolTip().add("Weight: " + color + entry.getWeight());
                           break;
                        }
                     }
                  }
               }
            }

            if (TrinketsConfig.SERVER.mana.mana_enabled) {
               try {
                  TreeMap<String, ConfigHelper.MPRecoveryItem> MagicRecoveryItems = ConfigHelper.TrinketConfigStorage.MagicRecoveryItems;
                  float amount = 0.0F;
                  boolean multiplied = false;

                  for(ConfigHelper.MPRecoveryItem entry : MagicRecoveryItems.values()) {
                     if (entry.doesItemMatchEntry(stack)) {
                        amount = entry.getAmount();
                        multiplied = entry.isMultiplied();
                        break;
                     }
                  }

                  if (amount != 0.0F) {
                     String additive = amount > 0.0F ? TextFormatting.DARK_AQUA + "+" : TextFormatting.DARK_RED + "-";
                     if (multiplied) {
                        event.getToolTip().add(additive + amount + "% MP");
                     } else {
                        event.getToolTip().add(additive + amount + " MP");
                     }
                  }
               } catch (Exception var21) {
                  var21.printStackTrace();
               }
            }

            if (stack.getItem().equals(Items.POTIONITEM)) {
               PotionType pot = PotionUtils.getPotionFromItem(stack);
               if (ModPotionTypes.TrinketPotionTypes.containsValue(pot)) {
                  TranslationHelper helper = TranslationHelper.INSTANCE;
                  TranslationHelper.KeyEntry human = new TranslationHelper.OptionEntry("humanticks", (float)MathHelper.clamp(TrinketsConfig.SERVER.Potion.human.Duration, 0, Integer.MAX_VALUE) / 20.0F);
                  TranslationHelper.KeyEntry fairy = new TranslationHelper.OptionEntry("fairyticks", (float)MathHelper.clamp(TrinketsConfig.SERVER.Potion.fairy.Duration, 0, Integer.MAX_VALUE) / 20.0F);
                  TranslationHelper.KeyEntry dwarf = new TranslationHelper.OptionEntry("dwarfticks", (float)MathHelper.clamp(TrinketsConfig.SERVER.Potion.dwarf.Duration, 0, Integer.MAX_VALUE) / 20.0F);
                  TranslationHelper.KeyEntry titan = new TranslationHelper.OptionEntry("titanticks", (float)MathHelper.clamp(TrinketsConfig.SERVER.Potion.titan.Duration, 0, Integer.MAX_VALUE) / 20.0F);
                  TranslationHelper.KeyEntry goblin = new TranslationHelper.OptionEntry("goblinticks", (float)MathHelper.clamp(TrinketsConfig.SERVER.Potion.goblin.Duration, 0, Integer.MAX_VALUE) / 20.0F);
                  TranslationHelper.KeyEntry elf = new TranslationHelper.OptionEntry("elfticks", (float)MathHelper.clamp(TrinketsConfig.SERVER.Potion.elf.Duration, 0, Integer.MAX_VALUE) / 20.0F);
                  TranslationHelper.KeyEntry faelis = new TranslationHelper.OptionEntry("faelisticks", (float)MathHelper.clamp(TrinketsConfig.SERVER.Potion.faelis.Duration, 0, Integer.MAX_VALUE) / 20.0F);
                  TranslationHelper.KeyEntry dragon = new TranslationHelper.OptionEntry("dragonticks", (float)MathHelper.clamp(TrinketsConfig.SERVER.Potion.dragon.Duration, 0, Integer.MAX_VALUE) / 20.0F);

                  for(int i = 1; i < 10; ++i) {
                     String key = "xat." + stack.getUnlocalizedName() + "." + pot.getRegistryName().getResourcePath() + ".tooltip" + i;
                     String string = helper.getLangTranslation(key, (lang) -> {
                        return helper.formatAddVariables(lang, human, fairy, dwarf, titan, goblin, elf, faelis, dragon);
                     });
                     if (!helper.isStringEmpty(string)) {
                        event.getToolTip().add(string);
                     }
                  }
               }
            }

         }
      }
   }
}
