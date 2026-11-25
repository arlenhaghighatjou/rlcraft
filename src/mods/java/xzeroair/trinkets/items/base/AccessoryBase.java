package xzeroair.trinkets.items.base;

import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.attributes.UpdatingAttribute;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.Trinket.TrinketProperties;
import xzeroair.trinkets.init.Elements;
import xzeroair.trinkets.traits.abilities.base.ItemAbilityProvider;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.traits.elements.Element;
import xzeroair.trinkets.traits.elements.IElementProvider;
import xzeroair.trinkets.util.Reference;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ConfigHelper;
import xzeroair.trinkets.util.helpers.AttributeHelper;
import xzeroair.trinkets.util.helpers.TranslationHelper;
import xzeroair.trinkets.util.interfaces.IAccessoryInterface;
import xzeroair.trinkets.util.interfaces.IsModelLoaded;

public abstract class AccessoryBase extends Item implements IsModelLoaded, IAccessoryInterface, ItemAbilityProvider, IElementProvider {
   protected EaglercraftUUID uuid;

   public AccessoryBase(String name) {
      this.setUnlocalizedName(name);
      this.setRegistryName(name);
      this.setMaxStackSize(1);
      this.setCreativeTab(Trinkets.trinketstab);
   }

   /** @deprecated */
   @Deprecated
   public AccessoryBase setAttributeConfig(String[] attributeConfig) {
      return this;
   }

   public String[] getAttributeConfig() {
      return new String[0];
   }

   public void initAbilities(ItemStack stack, EntityLivingBase entity, List<IAbilityInterface> abilities) {
   }

   protected void initAttributes(String[] attributeConfig, EntityLivingBase entity) {
      if (attributeConfig != null && attributeConfig.length > 0) {
         for(String entry : attributeConfig) {
            ConfigHelper.AttributeEntry attributeShell = ConfigHelper.getAttributeEntry(entry);
            if (attributeShell != null && this.getUUID() != null) {
               String name = attributeShell.getAttribute();
               double amount = attributeShell.getAmount();
               int operation = attributeShell.getOperation();
               boolean isSaved = attributeShell.isSaved();
               if (!(this instanceof TrinketRaceBase)) {
                  UpdatingAttribute attribute = (new UpdatingAttribute(this.getUnlocalizedName() + "." + name, this.uuid, name)).setAmount(amount).setOperation(operation).setSavedInNBT(isSaved);
                  if (name.equalsIgnoreCase("forge.swimSpeed")) {
                     boolean skip = false;
                     if (!TrinketsConfig.SERVER.misc.depthStacks && EnchantmentHelper.getDepthStriderModifier(entity) > 0) {
                        skip = true;
                     }

                     try {
                        if (Trinkets.SoManyEnchantments && !TrinketsConfig.SERVER.misc.underwaterStriderStacks) {
                           Enchantment e = Enchantment.getEnchantmentByLocation("somanyenchantments:underwaterstrider");
                           boolean hasUnderwaterStrider = e != null && EnchantmentHelper.getMaxEnchantmentLevel(e, entity) > 0;
                           if (hasUnderwaterStrider) {
                              skip = true;
                           }
                        }
                     } catch (Exception var17) {
                        ;
                     }

                     if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isFlying) {
                        skip = true;
                     }

                     if (skip) {
                        attribute.removeModifier(entity);
                        continue;
                     }
                  }

                  attribute.addModifier(entity, amount, operation);
               }
            }
         }
      }

   }

   public boolean hasCustomEntity(ItemStack stack) {
      return super.hasCustomEntity(stack);
   }

   public Entity createEntity(World world, Entity location, ItemStack itemstack) {
      return super.createEntity(world, location, itemstack);
   }

   public boolean isShield(ItemStack stack, EntityLivingBase entity) {
      return super.isShield(stack, entity);
   }

   public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
      return super.isValidArmor(stack, armorType, entity);
   }

   public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
      return super.onDroppedByPlayer(item, player);
   }

   public boolean onEntityItemUpdate(EntityItem entityItem) {
      return super.onEntityItemUpdate(entityItem);
   }

   public String getItemStackDisplayName(ItemStack stack) {
      return super.getItemStackDisplayName(stack);
   }

   public boolean isDamageable() {
      return false;
   }

   public int getSlot(ItemStack stack) {
      return ((Integer)Capabilities.getTrinketProperties(stack, Integer.valueOf(-1), (prop, slot) -> {
         return prop.getSlot();
      })).intValue();
   }

   public String getItemHandler(ItemStack stack) {
      return (String)Capabilities.getTrinketProperties(stack, TrinketHelper.SlotInformation.ItemHandlerType.NONE.getName(), (prop, slot) -> {
         return prop.getSlotInfo().getHandler();
      });
   }

   public String getAccessoryType() {
      return "trinket";
   }

   public EaglercraftUUID getUUID() {
      return this.uuid;
   }

   protected void setUUID(String uuid) {
      this.uuid = EaglercraftUUID.fromString(uuid);
   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      super.onUpdate(stack, world, entity, itemSlot, isSelected);
      Capabilities.getTrinketProperties(stack, (cap) -> {
         cap.onUpdate(stack, world, entity, itemSlot, isSelected);
      });
   }

   public void onCreated(ItemStack stack, World world, EntityPlayer player) {
      super.onCreated(stack, world, player);
      Capabilities.getTrinketProperties(stack, (cap) -> {
         cap.onCrafted(stack, world, player);
      });
   }

   public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
      this.onEntityArmorTick(world, player, itemStack);
   }

   public void onEntityArmorTick(World world, EntityLivingBase entity, ItemStack stack) {
   }

   public void onAccessoryEquipped(ItemStack stack, EntityLivingBase entity) {
      boolean isClient = entity.world.isRemote;
      Capabilities.getTrinketProperties(stack, (cap) -> {
         if (!isClient) {
            cap.itemEquipped(stack, entity);
         } else {
            entity.playSound(SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 0.75F, 1.9F);
         }

      });
   }

   public void eventLivingUpdateTick(ItemStack stack, EntityLivingBase entity) {
      Capabilities.getTrinketProperties(stack, (cap) -> {
         cap.onEntityTick(stack, entity);
      });
   }

   public void eventPlayerTick(ItemStack stack, EntityPlayer player) {
      this.initAttributes(this.getAttributeConfig(), player);
      Capabilities.getTrinketProperties(stack, (cap) -> {
         cap.onEntityTick(stack, player);
      });
   }

   public void onAccessoryUnequipped(ItemStack stack, EntityLivingBase entity) {
      boolean isClient = entity.world.isRemote;
      Capabilities.getTrinketProperties(stack, (cap) -> {
         if (!isClient) {
            cap.itemUnequipped(stack, entity);
         } else {
            entity.playSound(SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 0.75F, 2.0F);
         }

      });
      AttributeHelper.removeAttributesByUUID(entity, this.getUUID());
   }

   public void eventPlayerLogout(ItemStack stack, EntityPlayer player) {
      AttributeHelper.removeAttributesByUUID(player, this.getUUID());
   }

   public boolean canUnequipAccessory(ItemStack stack, EntityLivingBase player) {
      if (player instanceof EntityPlayer && ((EntityPlayer)player).capabilities.isCreativeMode) {
         return true;
      } else {
         return !EnchantmentHelper.hasBindingCurse(stack) || player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Item.getItemById(399);
      }
   }

   public NBTTagCompound getNBTShareTag(ItemStack stack) {
      return (NBTTagCompound)Capabilities.getTrinketProperties(stack, super.getNBTShareTag(stack), (prop, tag) -> {
         if (tag == null) {
            tag = new NBTTagCompound();
         }

         return prop.saveToNBT(tag);
      });
   }

   public void readNBTShareTag(ItemStack stack, NBTTagCompound nbt) {
      super.readNBTShareTag(stack, nbt);
      Capabilities.getTrinketProperties(stack, (prop) -> {
         prop.loadFromNBT(nbt);
      });
   }

   public NBTTagCompound getTagCompoundSafe(ItemStack stack) {
      if (stack.getTagCompound() == null) {
         stack.setTagCompound(new NBTTagCompound());
      }

      return stack.getTagCompound();
   }

   public boolean canEquipAccessory(ItemStack stack, EntityLivingBase player) {
      return !TrinketHelper.AccessoryCheck(player, this);
   }

   @SideOnly(Side.CLIENT)
   protected String customItemInformation(ItemStack stack, World world, ITooltipFlag flagIn, int index, String translation) {
      TranslationHelper helper = TranslationHelper.INSTANCE;
      return helper.formatAddVariables(translation);
   }

   public Element getPrimaryElement(ItemStack stack) {
      return (Element)Capabilities.getTrinketProperties(stack, this.getPrimaryElement(), (prop, element) -> {
         Element e = Element.getByNameOrId(prop.getVariant() + "");
         if (prop.getVariant() == 1) {
            prop.getElementAttributes().setPrimaryElement(Elements.ICE);
         }

         Element primary = prop.getElementAttributes().getPrimaryElement();
         return primary != Elements.NEUTRAL ? primary : element;
      });
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, world, tooltip, flagIn);
      if (world != null) {
         EntityPlayer player = null;

         try {
            player = Minecraft.getMinecraft().player;
         } catch (Exception var32) {
            ;
         }

         if (player != null) {
            if (TrinketsConfig.CLIENT.debug.showID) {
               TrinketProperties prop = Capabilities.getTrinketProperties(stack);
               if (prop != null) {
                  TrinketHelper.SlotInformation info = prop.getSlotInfo();
                  tooltip.add("Â§6" + info.getHandler() + "Â§r:Â§f" + info.getSlot() + "Â§r");
               }
            }

            TranslationHelper helper = TranslationHelper.INSTANCE;

            for(int i = 1; i < 10; ++i) {
               String string = helper.getLangTranslation(stack.getUnlocalizedName() + ".tooltip" + i, (lang) -> {
                  return (void)this.customItemInformation(stack, world, flagIn, index, lang);
               });
               if (!helper.isStringEmpty(string)) {
                  tooltip.add(string);
               }
            }

            TextComponentTranslation ctrl = new TextComponentTranslation("xat.holdctrl", new Object[0]);
            boolean tanEnabled = Trinkets.ToughAsNails && TrinketsConfig.compat.toughasnails;
            boolean sdEnabled = Trinkets.SimpleDifficulty && TrinketsConfig.compat.simpledifficulty;
            boolean faEnabled = Trinkets.FirstAid;
            boolean evEnabled = Trinkets.EnhancedVisuals && TrinketsConfig.compat.enhancedvisuals;
            String TAN = !tanEnabled && !sdEnabled ? "" : helper.getLangTranslation(stack.getUnlocalizedName() + ".compat.tan", (lang) -> {
               return (void)this.customItemInformation(stack, world, flagIn, 11, lang);
            });
            String FA = !faEnabled ? "" : helper.getLangTranslation(stack.getUnlocalizedName() + ".compat.firstaid", (lang) -> {
               return (void)this.customItemInformation(stack, world, flagIn, 12, lang);
            });
            String EV = !evEnabled ? "" : helper.getLangTranslation(stack.getUnlocalizedName() + ".compat.enhancedvisuals", (lang) -> {
               return (void)this.customItemInformation(stack, world, flagIn, 13, lang);
            });
            if (GuiScreen.isCtrlKeyDown()) {
               if (!helper.isStringEmpty(TAN)) {
                  String modifier = sdEnabled ? " (Simple Difficulty)" : (tanEnabled ? " (Tough as Nails)" : "");
                  tooltip.add(TAN + helper.gold + modifier);
               }

               if (!helper.isStringEmpty(FA)) {
                  tooltip.add(FA + helper.gold + " (First Aid)");
               }

               if (!helper.isStringEmpty(EV)) {
                  tooltip.add(EV + helper.gold + " (Enhanced Visuals)");
               }
            } else if (!helper.isStringEmpty(TAN) || !helper.isStringEmpty(EV) || !helper.isStringEmpty(FA)) {
               tooltip.add(helper.reset + "" + helper.dGray + ctrl.getFormattedText());
            }

            try {
               TextComponentTranslation shift = new TextComponentTranslation("xat.holdshift", new Object[0]);
               String[] attributeConfig = this.getAttributeConfig();
               if (attributeConfig != null && attributeConfig.length > 0) {
                  List<ConfigHelper.AttributeEntry> attributes = new ArrayList();

                  for(String entry : attributeConfig) {
                     ConfigHelper.AttributeEntry attributeShell = ConfigHelper.getAttributeEntry(entry);
                     if (attributeShell != null) {
                        double amount = attributeShell.getAmount();
                        if (amount != 0.0D) {
                           attributes.add(attributeShell);
                        }
                     }
                  }

                  if (!attributes.isEmpty()) {
                     tooltip.add(helper.reset + "" + helper.dGray + shift.getFormattedText());
                     if (GuiScreen.isShiftKeyDown()) {
                        for(ConfigHelper.AttributeEntry entry : attributes) {
                           String name = entry.getAttribute();
                           double amount = entry.getAmount();
                           int operation = entry.getOperation();
                           if (player.getAttributeMap().getAttributeInstanceByName(name) != null) {
                              boolean flag = false;
                              double d1;
                              if (operation != 1 && operation != 2) {
                                 d1 = amount;
                              } else {
                                 d1 = amount * 100.0D;
                              }

                              TextComponentTranslation AttrName = new TextComponentTranslation("attribute.name." + name, new Object[0]);
                              if (flag) {
                                 TextComponentTranslation never = new TextComponentTranslation("attribute.modifier.equals." + operation, new Object[]{Reference.DECIMALFORMAT.format(d1), AttrName.getFormattedText()});
                                 tooltip.add(" " + never.getFormattedText());
                              } else if (amount > 0.0D) {
                                 TextComponentTranslation addition = new TextComponentTranslation("attribute.modifier.plus." + operation, new Object[]{Reference.DECIMALFORMAT.format(d1), AttrName.getFormattedText()});
                                 addition.getStyle().setColor(TextFormatting.BLUE);
                                 String s = addition.getFormattedText();
                                 tooltip.add(" " + s);
                              } else if (amount < 0.0D) {
                                 d1 = d1 * -1.0D;
                                 TextComponentTranslation subtraction = new TextComponentTranslation("attribute.modifier.take." + operation, new Object[]{Reference.DECIMALFORMAT.format(d1), AttrName.getFormattedText()});
                                 subtraction.getStyle().setColor(TextFormatting.RED);
                                 String s = subtraction.getFormattedText();
                                 tooltip.add(" " + s);
                              }
                           }
                        }
                     }
                  }
               }
            } catch (Exception var33) {
               ;
            }

         }
      }
   }

   public String getUnlocalizedName(ItemStack stack) {
      return super.getUnlocalizedName() + "." + stack.getItemDamage();
   }

   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.RARE;
   }
}
