package cursedflames.bountifulbaubles.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import baubles.api.render.IRenderBauble.RenderType;
import com.google.common.collect.Multimap;
import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.baubleeffect.BaubleAttributeModifierHandler;
import cursedflames.bountifulbaubles.client.layer.IRenderObject;
import cursedflames.bountifulbaubles.item.base.IItemAttributeModifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.item.IPhantomInkable;

public class ItemShieldCobalt extends ItemShield implements IBauble, IRenderBauble, IItemAttributeModifier, IRenderObject {
   public static final EaglercraftUUID KNOCKBACK_RESISTANCE_UUID = EaglercraftUUID.fromString("418ed1da-15ae-4c7b-ac5e-4807ca52ffe3");
   public static final EaglercraftUUID KNOCKBACK_RESISTANCE_BAUBLE_UUID = EaglercraftUUID.fromString("9016ba1d-70dd-46c4-b0b4-fc4ea39886c1");
   private static final Map<IAttribute, AttributeModifier> modMap = new HashMap();

   public ItemShieldCobalt(String name) {
      this.setRegistryName(name);
      this.setUnlocalizedName("bountifulbaubles." + name);
      this.setCreativeTab(BountifulBaubles.TAB);
      this.setMaxDamage(1008);
      BountifulBaubles.registryHelper.addItemModel(this);
   }

   public static boolean isUsable(ItemStack stack) {
      return stack.getItemDamage() < stack.getMaxDamage();
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (isUsable(itemstack)) {
         playerIn.setActiveHand(handIn);
         return new ActionResult(EnumActionResult.SUCCESS, itemstack);
      } else {
         return new ActionResult(EnumActionResult.FAIL, itemstack);
      }
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return repair.getItem() == Items.IRON_INGOT;
   }

   @SubscribeEvent
   public static void onLivingAttack(LivingAttackEvent event) {
      if (event.getEntityLiving() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntityLiving();
         if (player.getActiveItemStack() != null) {
            ItemStack stack = player.getActiveItemStack();
            float damage = event.getAmount();
            if (damage > 5.0F && stack != null && stack.getItem() instanceof ItemShieldCobalt) {
               int i = Math.min(1 + (int)damage, stack.getMaxDamage() - stack.getItemDamage());
               stack.damageItem(i, player);
               if (stack.isEmpty() || stack.getItemDamage() >= stack.getMaxDamage()) {
                  if (stack.isEmpty()) {
                     EnumHand enumhand = player.getActiveHand();
                     ForgeEventFactory.onPlayerDestroyItem(player, stack, enumhand);
                     if (enumhand == EnumHand.MAIN_HAND) {
                        player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, (ItemStack)null);
                     } else {
                        player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, (ItemStack)null);
                     }

                     stack = null;
                  }

                  player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_SHIELD_BREAK, SoundCategory.PLAYERS, 0.9F, 0.8F + player.world.rand.nextFloat() * 0.4F);
               }
            }

         }
      }
   }

   @SubscribeEvent
   public static void onAnvilUpdate(AnvilUpdateEvent event) {
      resetRepairValue(event.getLeft());
      resetRepairValue(event.getRight());
   }

   @SubscribeEvent
   public static void onAnvilUpdate(AnvilRepairEvent event) {
      resetRepairValue(event.getItemResult());
   }

   private static void resetRepairValue(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() instanceof ItemShieldCobalt && stack.hasTagCompound()) {
         stack.getTagCompound().removeTag("RepairCost");
      }

   }

   public String getItemStackDisplayName(ItemStack stack) {
      return BountifulBaubles.proxy.translate(this.getUnlocalizedName() + ".name");
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      if (!stack.getTagCompound().hasKey("HideFlags")) {
         stack.getTagCompound().setInteger("HideFlags", 2);
      }

      if (stack.getItemDamage() >= stack.getMaxDamage()) {
         tooltip.add(I18n.translateToLocal("bountifulbaubles.broken"));
      }

      tooltip.add(I18n.translateToLocal(this.getUnlocalizedName() + ".tooltip.0"));
      if (GuiScreen.isShiftKeyDown()) {
         tooltip.add(I18n.translateToLocal(this.getUnlocalizedName() + ".tooltip.1"));
         tooltip.add(I18n.translateToLocal(this.getUnlocalizedName() + ".tooltip.2"));
         if (stack.getItem() instanceof IPhantomInkable && ((IPhantomInkable)stack.getItem()).hasPhantomInk(stack)) {
            tooltip.add(BountifulBaubles.proxy.translate("bountifulbaubles.misc.hasPhantomInk"));
         }
      } else {
         tooltip.add(I18n.translateToLocal("bountifulbaubles.moreinfo"));
      }

   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
      Multimap<String, AttributeModifier> mods = super.getAttributeModifiers(slot, stack);
      if (slot == EntityEquipmentSlot.MAINHAND || slot == EntityEquipmentSlot.OFFHAND) {
         String knockback = SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName();
         mods.put(knockback, new AttributeModifier(KNOCKBACK_RESISTANCE_UUID, "Cobalt Shield knockback resistance", 10.0D, 0));
      }

      return mods;
   }

   public BaubleType getBaubleType(ItemStack arg0) {
      return BaubleType.TRINKET;
   }

   public void onEquipped(ItemStack stack, EntityLivingBase player) {
      BaubleAttributeModifierHandler.baubleModified(stack, player, true);
   }

   public void onUnequipped(ItemStack stack, EntityLivingBase player) {
      BaubleAttributeModifierHandler.baubleModified(stack, player, false);
   }

   public Map<IAttribute, AttributeModifier> getModifiers(ItemStack stack, EntityPlayer player) {
      return modMap;
   }

   public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float partialTicks) {
   }

   @SideOnly(Side.CLIENT)
   public void onRenderObject(ItemStack stack, EntityPlayer player, boolean isSlim, float partialTicks, float scale) {
      boolean armor = !player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty();
      GlStateManager.scale(0.6D, 0.6D, 0.6D);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(0.5D, -0.25D, armor ? 0.75D : 0.7D);
      Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.NONE);
   }

   public RenderType getRenderType() {
      return RenderType.BODY;
   }

   static {
      modMap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, new AttributeModifier(KNOCKBACK_RESISTANCE_BAUBLE_UUID, "Cobalt Shield (bauble slot) knockback resistance", 10.0D, 0));
   }
}
