package ichttt.mods.firstaid.client;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractPartHealer;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.client.gui.FirstaidIngameGui;
import ichttt.mods.firstaid.client.gui.GuiHealthScreen;
import ichttt.mods.firstaid.client.tutorial.GuiTutorial;
import ichttt.mods.firstaid.client.util.EventCalendar;
import ichttt.mods.firstaid.client.util.PlayerModelRenderer;
import ichttt.mods.firstaid.common.AABBAlignedBoundingBox;
import ichttt.mods.firstaid.common.CapProvider;
import ichttt.mods.firstaid.common.EventHandler;
import ichttt.mods.firstaid.common.apiimpl.FirstAidRegistryImpl;
import ichttt.mods.firstaid.common.apiimpl.RegistryManager;
import ichttt.mods.firstaid.common.config.ConfigEntry;
import ichttt.mods.firstaid.common.config.ExtraConfig;
import ichttt.mods.firstaid.common.items.FirstAidItems;
import ichttt.mods.firstaid.common.util.ArmorUtils;
import ichttt.mods.firstaid.common.util.CommonUtils;
import ichttt.mods.firstaid.common.util.PlayerSizeHelper;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandler {
   private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
   private static int id;

   public ClientEventHandler() {
   }

   @SubscribeEvent
   public static void registerModels(ModelRegistryEvent event) {
      ModelLoader.setCustomModelResourceLocation(FirstAidItems.BANDAGE, 0, new ModelResourceLocation("firstaid:bandage"));
      ModelLoader.setCustomModelResourceLocation(FirstAidItems.PLASTER, 0, new ModelResourceLocation("firstaid:plaster"));
      ModelLoader.setCustomModelResourceLocation(FirstAidItems.MORPHINE, 0, new ModelResourceLocation("firstaid:morphine"));
   }

   @SubscribeEvent
   public static void clientTick(ClientTickEvent event) {
      if (event.phase != Phase.END) {
         Minecraft mc = Minecraft.getMinecraft();
         if (mc.world != null && mc.player != null && mc.player.connection != null && !mc.isGamePaused()) {
            if (EventCalendar.isGuiFun()) {
               GuiHealthScreen.BED_ITEMSTACK.setItemDamage(id);
               if (mc.world != null && mc.world.getWorldTime() % 3L == 0L) {
                  ++id;
               }

               if (id > 15) {
                  id = 0;
               }

               PlayerModelRenderer.tickFun();
            }

            if (!RegistryManager.debuffConfigErrors.isEmpty() && mc.world.isRemote) {
               mc.player.sendStatusMessage(new TextComponentString("[FirstAid] FirstAid has detected invalid debuff config entries."), false);

               for(String s : RegistryManager.debuffConfigErrors) {
                  mc.player.sendStatusMessage(new TextComponentString("[FirstAid] " + s), false);
               }

               RegistryManager.debuffConfigErrors.clear();
            }

            if (HUDHandler.INSTANCE.ticker >= 0) {
               --HUDHandler.INSTANCE.ticker;
            }

         }
      }
   }

   @SubscribeEvent
   public static void onKeyPress(KeyInputEvent event) {
      if (ClientProxy.showWounds.isPressed()) {
         Minecraft mc = Minecraft.getMinecraft();
         AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)Objects.requireNonNull(mc.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null));
         if (!damageModel.hasTutorial) {
            damageModel.hasTutorial = true;
            CapProvider.tutorialDone.add(mc.player.getName());
            Minecraft.getMinecraft().displayGuiScreen(new GuiTutorial());
         } else {
            mc.displayGuiScreen(new GuiHealthScreen(damageModel));
         }
      }

   }

   @SubscribeEvent
   public static void preRender(Pre event) {
      ElementType type = event.getType();
      if (type == ElementType.HEALTH) {
         FirstAidConfig.Overlay.VanillaHealthbarMode vanillaHealthBarMode = FirstAidConfig.overlay.vanillaHealthBarMode;
         if (vanillaHealthBarMode != FirstAidConfig.Overlay.VanillaHealthbarMode.NORMAL) {
            event.setCanceled(true);
            if (vanillaHealthBarMode == FirstAidConfig.Overlay.VanillaHealthbarMode.HIGHLIGHT_CRITICAL_PATH && FirstAidConfig.vanillaHealthCalculation == FirstAidConfig.VanillaHealthCalculationMode.AVERAGE_ALL) {
               FirstaidIngameGui.renderHealth(Minecraft.getMinecraft().ingameGUI, event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight());
            }
         }
      }

   }

   @SubscribeEvent
   public static void renderOverlay(net.minecraftforge.client.event.RenderGameOverlayEvent.Post event) {
      ElementType type = event.getType();
      if (type == ElementType.ALL || type == ElementType.TEXT && FirstAidConfig.overlay.overlayMode != FirstAidConfig.Overlay.OverlayMode.OFF && FirstAidConfig.overlay.pos == FirstAidConfig.Overlay.Position.BOTTOM_LEFT) {
         Minecraft mc = Minecraft.getMinecraft();
         if (!mc.player.isEntityAlive()) {
            return;
         }

         mc.mcProfiler.startSection("FirstAidOverlay");
         HUDHandler.INSTANCE.renderOverlay(event.getResolution(), event.getPartialTicks());
         mc.mcProfiler.endSection();
         mc.mcProfiler.endSection();
      }

   }

   @SubscribeEvent
   public static void onLivingRender(Post<EntityPlayer> event) {
      Entity entity = event.getEntity();
      if (entity instanceof EntityPlayer) {
         RenderManager renderDispatcher = Minecraft.getMinecraft().getRenderManager();
         if (renderDispatcher.isDebugBoundingBox()) {
            GlStateManager.pushMatrix();
            if (entity.isSneaking()) {
               GlStateManager.translate(0.0D, 0.125D, 0.0D);
            }

            AxisAlignedBB aabb = entity.getEntityBoundingBox();
            Collection<AABBAlignedBoundingBox> allBoxes = PlayerSizeHelper.getBoxes(entity).values();
            float r = 0.25F;
            float g = 1.0F;
            float b = 1.0F;

            for(AABBAlignedBoundingBox box : allBoxes) {
               AxisAlignedBB bbox = box.createAABB(aabb);
               RenderGlobal.drawSelectionBoundingBox(bbox.grow(0.02D).offset(-entity.posX, -entity.posY, -entity.posZ), r, g, b, 1.0F);
               r = r + 0.25F;
               g = g + 0.5F;
               b = b + 0.1F;
               r = r % 1.0F;
               g = g % 1.0F;
               b = b % 1.0F;
            }

            GlStateManager.popMatrix();
         }
      }

   }

   private static String makeArmorMsg(double value) {
      return value >= 0.0D ? TextFormatting.BLUE + I18n.format("firstaid.specificarmor", new Object[]{FORMAT.format(value)}) + TextFormatting.RESET : TextFormatting.RED + I18n.format("firstaid.specificarmor.take", new Object[]{FORMAT.format(-value)}) + TextFormatting.RESET;
   }

   private static String makeToughnessMsg(double value) {
      return value >= 0.0D ? TextFormatting.BLUE + I18n.format("firstaid.specifictoughness", new Object[]{FORMAT.format(value)}) + TextFormatting.RESET : TextFormatting.RED + I18n.format("firstaid.specifictoughness.take", new Object[]{FORMAT.format(-value)}) + TextFormatting.RESET;
   }

   private static <T> void replaceOrAppend(List<T> list, T search, T replace) {
      int index = list.indexOf(search);
      if (FirstAidConfig.overlay.armorTooltipMode == FirstAidConfig.Overlay.TooltipMode.REPLACE && index >= 0) {
         list.set(index, replace);
      } else {
         list.add(replace);
      }

   }

   private static String buildOriginalText(double val, String attribute) {
      String plusOrTake = "plus";
      TextFormatting startFormatting = TextFormatting.BLUE;
      if (val < 0.0D) {
         val = -val;
         plusOrTake = "take";
         startFormatting = TextFormatting.RED;
      }

      return startFormatting + " " + net.minecraft.util.text.translation.I18n.translateToLocalFormatted("attribute.modifier." + plusOrTake + ".0", new Object[]{FORMAT.format(val), net.minecraft.util.text.translation.I18n.translateToLocal("attribute.name.generic." + attribute)});
   }

   @SubscribeEvent
   public static void tooltipItems(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      Item item = stack.getItem();
      if (item == FirstAidItems.MORPHINE) {
         event.getToolTip().add(I18n.format("firstaid.tooltip.morphine", new Object[]{"3:30-4:30"}));
      } else {
         if (FirstAidConfig.overlay.armorTooltipMode != FirstAidConfig.Overlay.TooltipMode.NONE) {
            boolean set = false;
            if (item instanceof ISpecialArmor) {
               ISpecialArmor armor = (ISpecialArmor)item;
               EntityPlayer player = event.getEntityPlayer();
               if (player != null) {
                  int slot = player.inventory.armorInventory.indexOf(stack);
                  if (slot == -1 && item instanceof ItemArmor) {
                     slot = ((ItemArmor)item).armorType.getIndex();
                  }

                  if (slot >= 0 && slot <= 3) {
                     int displayArmor = armor.getArmorDisplay(event.getEntityPlayer(), stack, slot);
                     if (displayArmor != 0) {
                        set = true;
                        double totalArmor = ArmorUtils.applyArmorModifier(CommonUtils.ARMOR_SLOTS[slot], (double)displayArmor);
                        String original = buildOriginalText((double)displayArmor, "armor");
                        replaceOrAppend(event.getToolTip(), original, makeArmorMsg(totalArmor));
                     }
                  }
               }
            }

            if (item instanceof ItemArmor && !set) {
               ItemArmor armor = (ItemArmor)item;
               List<String> tooltip = event.getToolTip();
               double normalArmor = ArmorUtils.getArmor(stack, armor.getEquipmentSlot(), false);
               double totalArmor = ArmorUtils.applyArmorModifier(armor.armorType, normalArmor);
               if (totalArmor > 0.0D) {
                  String original = buildOriginalText(normalArmor, "armor");
                  replaceOrAppend(tooltip, original, makeArmorMsg(totalArmor));
               }

               double normalToughness = ArmorUtils.getArmorToughness(stack, armor.getEquipmentSlot(), false);
               double totalToughness = ArmorUtils.applyToughnessModifier(armor.armorType, normalToughness);
               if (totalToughness > 0.0D) {
                  String original = buildOriginalText(normalToughness, "armorToughness");
                  replaceOrAppend(tooltip, original, makeToughnessMsg(totalToughness));
               }

               if (ArmorUtils.QUALITY_TOOLS_PRESENT) {
                  double qualityToolsNormalArmor = ArmorUtils.getValueFromQualityTools(SharedMonsterAttributes.ARMOR, stack);
                  double qualityToolsTotalArmor = qualityToolsNormalArmor * ArmorUtils.getArmorMultiplier(armor.armorType);
                  if (qualityToolsTotalArmor != 0.0D) {
                     String original = buildOriginalText(qualityToolsNormalArmor, "armor");
                     replaceOrAppend(tooltip, original, makeArmorMsg(qualityToolsTotalArmor));
                  }

                  double qualityToolsNormalToughness = ArmorUtils.getValueFromQualityTools(SharedMonsterAttributes.ARMOR_TOUGHNESS, stack);
                  double qualityToolsTotalToughness = qualityToolsNormalToughness * ArmorUtils.getToughnessMultiplier(armor.armorType);
                  if (qualityToolsTotalToughness != 0.0D) {
                     String original = buildOriginalText(qualityToolsNormalToughness, "armorToughness");
                     replaceOrAppend(tooltip, original, makeToughnessMsg(qualityToolsTotalToughness));
                  }
               }
            }
         }

         if (item instanceof ItemPotion) {
            List<PotionEffect> list = PotionUtils.getEffectsFromStack(stack);
            if (!list.isEmpty()) {
               for(PotionEffect potionEffect : list) {
                  if (EventHandler.RESISTANCE != null && potionEffect.getPotion() == EventHandler.RESISTANCE) {
                     Potion potion = potionEffect.getPotion();
                     Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();
                     if (!map.isEmpty()) {
                        for(Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
                           AttributeModifier falseModifier = (AttributeModifier)entry.getValue();
                           AttributeModifier realModifier = new AttributeModifier(falseModifier.getName(), potion.getAttributeModifierAmount(potionEffect.getAmplifier(), falseModifier), falseModifier.getOperation());
                           double d1;
                           if (realModifier.getOperation() != 1 && realModifier.getOperation() != 2) {
                              d1 = realModifier.getAmount();
                           } else {
                              d1 = realModifier.getAmount() * 100.0D;
                           }

                           String raw = TextFormatting.BLUE + net.minecraft.util.text.translation.I18n.translateToLocalFormatted("attribute.modifier.plus." + realModifier.getOperation(), new Object[]{ItemStack.DECIMALFORMAT.format(d1), net.minecraft.util.text.translation.I18n.translateToLocal("attribute.name." + ((IAttribute)entry.getKey()).getName())});
                           String replacement = TextFormatting.BLUE + net.minecraft.util.text.translation.I18n.translateToLocalFormatted("attribute.modifier.plus." + realModifier.getOperation(), new Object[]{ItemStack.DECIMALFORMAT.format(d1 * (double)((float)FirstAidConfig.externalHealing.resistanceReductionPercentPerLevel / 20.0F)), net.minecraft.util.text.translation.I18n.translateToLocal("attribute.name." + ((IAttribute)entry.getKey()).getName())});
                           List<String> toolTip = event.getToolTip();
                           int index = toolTip.indexOf(raw);
                           if (index != -1) {
                              toolTip.set(index, replacement);
                           }
                        }
                     }
                  }
               }
            }
         }

         AbstractPartHealer healer = FirstAidRegistryImpl.INSTANCE.getPartHealer(stack);
         if (healer != null) {
            event.getToolTip().add(I18n.format("firstaid.tooltip.healer", new Object[]{healer.maxHeal / 2, StringUtils.ticksToElapsedTime(healer.ticksPerHeal)}));
         }

      }
   }

   @SubscribeEvent
   public static void onDisconnect(ClientDisconnectionFromServerEvent event) {
      FirstAid.isSynced = false;

      for(ConfigEntry<ExtraConfig.Sync> option : FirstAid.syncedConfigOptions) {
         if (option.hasRemoteData()) {
            option.revert();
         }
      }

      HUDHandler.INSTANCE.ticker = -1;
   }
}
