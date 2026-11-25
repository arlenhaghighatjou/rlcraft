package com.oblivioussp.spartanweaponry.event;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.IWeaponPropertyContainer;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.client.KeyBinds;
import com.oblivioussp.spartanweaponry.client.gui.HudLoadState;
import com.oblivioussp.spartanweaponry.client.gui.HudQuiverAmmo;
import com.oblivioussp.spartanweaponry.init.EnchantmentRegistrySW;
import com.oblivioussp.spartanweaponry.item.IHudLoadState;
import com.oblivioussp.spartanweaponry.item.ItemCrossbow;
import com.oblivioussp.spartanweaponry.item.ItemDagger;
import com.oblivioussp.spartanweaponry.item.ItemLongbow;
import com.oblivioussp.spartanweaponry.item.ItemThrowingWeapon;
import com.oblivioussp.spartanweaponry.network.NetworkHandler;
import com.oblivioussp.spartanweaponry.network.PacketKeyHandle;
import com.oblivioussp.spartanweaponry.network.PacketLongReachAttack;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import com.oblivioussp.spartanweaponry.util.Log;
import com.oblivioussp.spartanweaponry.util.NBTHelper;
import com.oblivioussp.spartanweaponry.util.QuiverHelper;
import com.oblivioussp.spartanweaponry.util.StringHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber({Side.CLIENT})
public class EventHandlerClient {
   private static ItemStack activeQuiverStack = ItemStack.EMPTY;
   protected static final ResourceLocation CROSSHAIR_TEXTURES = new ResourceLocation("spartanweaponry", "textures/gui/crosshairs.png");

   public EventHandlerClient() {
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void onKeyInputEvent(KeyInputEvent ev) {
      Minecraft mc = Minecraft.getMinecraft();
      EntityPlayer player = mc.player;
      if (player != null && !mc.isGamePaused() && mc.inGameHasFocus && KeyBinds.KEY_ACCESS_QUIVER.isPressed()) {
         NetworkHandler.sendPacketToServer(new PacketKeyHandle());
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public static void onMouseEvent(MouseEvent ev) {
      if (!ModSpartanWeaponry.isRLCombatLoaded) {
         KeyBinding keyAttack = Minecraft.getMinecraft().gameSettings.keyBindAttack;
         if (keyAttack.getKeyCode() < 0 && ev.getButton() == keyAttack.getKeyCode() + 100 && ev.isButtonstate()) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.player;
            if (player == null || player.isActiveItemStackBlocking()) {
               return;
            }

            ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
            WeaponProperty reachProperty = null;
            if (!stack.isEmpty()) {
               if (stack.getItem() instanceof IWeaponPropertyContainer) {
                  IWeaponPropertyContainer container = (IWeaponPropertyContainer)stack.getItem();
                  reachProperty = container.getFirstWeaponPropertyWithType("reach");
               }

               if (reachProperty != null) {
                  double initialReach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
                  float reach = reachProperty.getMagnitude() - 5.0F + (float)initialReach;
                  Log.debug("Initial reach value: " + initialReach + " - Extended reach value: " + reach);
                  if (mc.pointedEntity != null) {
                     return;
                  }

                  RayTraceResult result = getMouseOverExtended(reach);
                  if (result != null && result.entityHit != null && result.entityHit.hurtResistantTime == 0 && result.entityHit != player && result.entityHit != player.getRidingEntity()) {
                     float velocity = 0.0F;
                     if (player.isRiding()) {
                        Entity riding = player.getRidingEntity();
                        Vec3d vec = new Vec3d(riding.motionX, riding.motionY, riding.motionZ);
                        velocity = (float)vec.lengthVector();
                     }

                     NetworkHandler.sendPacketToServer(new PacketLongReachAttack(result.entityHit.getEntityId(), velocity));
                  }
               }
            }
         }

      }
   }

   protected static RayTraceResult getMouseOverExtended(float distance) {
      RayTraceResult result = null;
      Minecraft mc = Minecraft.getMinecraft();
      Entity renderViewEntity = mc.getRenderViewEntity();
      if (renderViewEntity != null && mc.world != null) {
         double d0 = (double)distance;
         result = renderViewEntity.rayTrace(d0, 0.0F);
         Vec3d eyePos = renderViewEntity.getPositionEyes(0.0F);
         boolean flag = false;
         double d1 = d0;
         if (mc.playerController.extendedReach() && d0 < 6.0D) {
            d1 = 6.0D;
            d0 = d1;
         } else if (d0 > (double)distance) {
            flag = true;
         }

         if (result != null) {
            d1 = result.hitVec.distanceTo(eyePos);
         }

         Vec3d lookVec = renderViewEntity.getLook(0.0F);
         Vec3d vec3d2 = eyePos.addVector(lookVec.x * d0, lookVec.y * d0, lookVec.z * d0);
         Entity pointedEntity = null;
         Vec3d vec3d3 = null;
         List<Entity> list = mc.world.getEntitiesInAABBexcluding(renderViewEntity, renderViewEntity.getEntityBoundingBox().expand(lookVec.x * d0, lookVec.y * d0, lookVec.z * d0).expand(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
            public boolean apply(@Nullable Entity p_apply_1_) {
               return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
            }
         }));
         double d2 = d1;

         for(int j = 0; j < list.size(); ++j) {
            Entity entity1 = (Entity)list.get(j);
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double)entity1.getCollisionBorderSize());
            RayTraceResult result0 = axisalignedbb.calculateIntercept(eyePos, vec3d2);
            if (axisalignedbb.contains(eyePos)) {
               if (d2 >= 0.0D) {
                  pointedEntity = entity1;
                  vec3d3 = result0 == null ? eyePos : result0.hitVec;
                  d2 = 0.0D;
               }
            } else if (result0 != null) {
               double d3 = eyePos.distanceTo(result0.hitVec);
               if (d3 < d2 || d2 == 0.0D) {
                  if (entity1.getLowestRidingEntity() == renderViewEntity.getLowestRidingEntity() && !renderViewEntity.canRiderInteract()) {
                     if (d2 == 0.0D) {
                        pointedEntity = entity1;
                        vec3d3 = result0.hitVec;
                     }
                  } else {
                     pointedEntity = entity1;
                     vec3d3 = result0.hitVec;
                     d2 = d3;
                  }
               }
            }
         }

         if (pointedEntity != null && flag && eyePos.distanceTo(vec3d3) > (double)distance) {
            pointedEntity = null;
            result = new RayTraceResult(Type.MISS, vec3d3, (EnumFacing)null, new BlockPos(vec3d3));
            Log.debug("Ray Trace for extended reach has missed!");
         }

         if (pointedEntity != null && (d2 < d1 || result == null)) {
            result = new RayTraceResult(pointedEntity, vec3d3);
            Log.debug("Ray Trace for extended reach has hit a " + pointedEntity.toString() + " d1 = " + d1 + " - d2 = " + d2);
            if (!(pointedEntity instanceof EntityLivingBase) && pointedEntity instanceof EntityItemFrame) {
               ;
            }
         }
      }

      return result;
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void onFOVUpdate(FOVUpdateEvent ev) {
      if (!ConfigHandler.disableFOVZoomIn) {
         EntityPlayer player = ev.getEntity();
         ItemStack activeStack = player.getActiveItemStack();
         if (!activeStack.isEmpty()) {
            float currentFov = ev.getFov();
            float fovZoomIn = 0.05F;
            int ticksToZoomIn = 0;
            int currentTicks = activeStack.getMaxItemUseDuration() - player.getItemInUseCount();
            if (activeStack.getItem() instanceof ItemLongbow) {
               ticksToZoomIn = ((ItemLongbow)activeStack.getItem()).getDrawTicks();
            } else if (activeStack.getItem() instanceof ItemCrossbow && NBTHelper.getBoolean(activeStack, "isLoaded")) {
               ticksToZoomIn = ((ItemCrossbow)activeStack.getItem()).getAimTicks(activeStack);
            } else if (activeStack.getItem() instanceof ItemDagger || activeStack.getItem() instanceof ItemThrowingWeapon) {
               ticksToZoomIn = 5;
               fovZoomIn = 0.1F;
            }

            if (ticksToZoomIn > 0) {
               if (currentTicks >= ticksToZoomIn) {
                  currentTicks = ticksToZoomIn;
               }

               float newFov = currentFov - fovZoomIn * ((float)currentTicks / (float)ticksToZoomIn);
               ev.setNewfov(newFov);
            }

         }
      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void onTooltip(ItemTooltipEvent ev) {
      ItemStack stack = ev.getItemStack();
      if (stack.getItem() instanceof ItemThrowingWeapon) {
         ItemThrowingWeapon weapon = (ItemThrowingWeapon)stack.getItem();
         int j = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_DAMAGE, stack);
         float throwDamage = (weapon.getDirectAttackDamage() + 1.0F) * weapon.getThrownDamageMultiplier() + (float)j * 0.5F;
         ev.getToolTip().add("");
         ev.getToolTip().add(StringHelper.translateString("modifiers.thrown", "tooltip"));
         ev.getToolTip().add(" " + StringHelper.translateFormattedString("attribute.damage", "tooltip", "spartanweaponry", ItemStack.DECIMALFORMAT.format((double)throwDamage)));
      }

      if (ModSpartanWeaponry.debugMode && stack.hasTagCompound() && ev.getFlags().isAdvanced()) {
         ev.getToolTip().add(TextFormatting.DARK_PURPLE + "NBT: " + TextFormatting.DARK_GRAY + stack.getTagCompound().toString());
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void onSpecificHandRender(RenderSpecificHandEvent ev) {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (player.getActiveHand() != ev.getHand()) {
         ItemStack activeStack = player.getActiveItemStack();
         if (activeStack.getItem() instanceof ItemLongbow) {
            ev.setCanceled(true);
         } else if (activeStack.getItem() instanceof ItemCrossbow) {
            ev.setCanceled(true);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onRenderGameOverlayPre(Pre ev) {
      if (ev.getType() == ElementType.CROSSHAIRS) {
         Minecraft mc = Minecraft.getMinecraft();
         EntityPlayer player = Minecraft.getMinecraft().player;
         int x = ev.getResolution().getScaledWidth() / 2;
         int y = ev.getResolution().getScaledHeight() / 2;
         if (player.isSpectator()) {
            return;
         }

         ItemStack equipStack = ItemStack.EMPTY;
         ItemStack mainStack = player.getHeldItemMainhand().isEmpty() ? player.getHeldItemOffhand() : player.getHeldItemMainhand();
         ItemStack activeStack = player.getActiveItemStack();
         equipStack = player.isHandActive() && activeStack.getItem() instanceof ItemCrossbow ? activeStack : ItemStack.EMPTY;
         if (!equipStack.isEmpty()) {
            renderCrossbowCrosshair(ev, mc, player, equipStack, x, y);
            return;
         }

         equipStack = player.isHandActive() && activeStack.getItem() instanceof ItemThrowingWeapon ? activeStack : (mainStack.getItem() instanceof ItemThrowingWeapon ? mainStack : ItemStack.EMPTY);
         if (!equipStack.isEmpty()) {
            renderThrowingWeaponCrosshair(ev, mc, player, equipStack, x, y);
            return;
         }

         equipStack = mainStack.getItem() instanceof ItemCrossbow ? mainStack : ItemStack.EMPTY;
         if (!equipStack.isEmpty()) {
            renderCrossbowCrosshair(ev, mc, player, equipStack, x, y);
            return;
         }

         equipStack = mainStack.getItem() instanceof ItemThrowingWeapon ? mainStack : ItemStack.EMPTY;
         if (!equipStack.isEmpty()) {
            renderThrowingWeaponCrosshair(ev, mc, player, equipStack, x, y);
            return;
         }
      }

   }

   private static void renderCrossbowCrosshair(Pre ev, Minecraft mc, EntityPlayer player, ItemStack equipStack, int x, int y) {
      GameSettings settings = mc.gameSettings;
      if ((!ConfigHandler.disableNewCrosshairCrossbow || ConfigHandler.forceCompatibilityCrosshairs) && settings.thirdPersonView == 0 && !equipStack.isEmpty()) {
         ItemStack crossbow = player.getActiveItemStack();
         int offset = 80 / ev.getResolution().getScaleFactor();
         if (player.isHandActive() && NBTHelper.getBoolean(equipStack, "isLoaded")) {
            int currentTicks = equipStack.getMaxItemUseDuration() - player.getItemInUseCount();
            if (!(crossbow.getItem() instanceof ItemCrossbow)) {
               return;
            }

            ItemCrossbow crossbowItem = (ItemCrossbow)crossbow.getItem();
            int aimTicks = crossbowItem.getAimTicks(crossbow);
            float percentage = aimTicks == 0 ? 1.0F : MathHelper.clamp(((float)currentTicks + ev.getPartialTicks()) / (float)aimTicks, 0.0F, 1.0F);
            offset = (int)((float)offset * (1.0F - percentage));
         }

         if (settings.showDebugInfo && !settings.hideGUI && !mc.player.hasReducedDebug() && !settings.reducedDebugInfo) {
            return;
         }

         GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.disableBlend();
         GlStateManager.tryBlendFuncSeparate(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.enableBlend();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         mc.getTextureManager().bindTexture(CROSSHAIR_TEXTURES);
         if (!ModSpartanWeaponry.isRLCombatLoaded && !ConfigHandler.forceCompatibilityCrosshairs) {
            mc.ingameGUI.drawTexturedModalRect(x - 1, y - 1, 4, 4, 3, 3);
            mc.ingameGUI.drawTexturedModalRect(x, y - 5 - offset, 5, 0, 1, 4);
            mc.ingameGUI.drawTexturedModalRect(x, y + 2 + offset, 5, 7, 1, 4);
            mc.ingameGUI.drawTexturedModalRect(x - 5 - offset, y, 0, 5, 4, 1);
            mc.ingameGUI.drawTexturedModalRect(x + 2 + offset, y, 7, 5, 4, 1);
            ev.setCanceled(true);
         } else {
            offset = (int)Math.sqrt((double)(offset * offset) / 2.0D);
            mc.ingameGUI.drawTexturedModalRect(x - 5 - offset, y - 5 - offset, 11, 12, 4, 4);
            mc.ingameGUI.drawTexturedModalRect(x + 2 + offset, y - 5 - offset, 18, 12, 4, 4);
            mc.ingameGUI.drawTexturedModalRect(x - 5 - offset, y + 2 + offset, 11, 19, 4, 4);
            mc.ingameGUI.drawTexturedModalRect(x + 2 + offset, y + 2 + offset, 18, 19, 4, 4);
         }

         GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.disableBlend();
      }

   }

   private static void renderThrowingWeaponCrosshair(Pre ev, Minecraft mc, EntityPlayer player, ItemStack equipStack, int x, int y) {
      GameSettings settings = mc.gameSettings;
      if ((!ConfigHandler.disableNewCrosshairThrowingWeapon || ConfigHandler.forceCompatibilityCrosshairs) && settings.thirdPersonView == 0 && !equipStack.isEmpty()) {
         ItemThrowingWeapon throwingWeapon = (ItemThrowingWeapon)equipStack.getItem();
         int offset = ModSpartanWeaponry.isRLCombatLoaded ? 24 : 10;
         if (player.isHandActive()) {
            int currentTicks = equipStack.getMaxItemUseDuration() - player.getItemInUseCount();
            float percentage = MathHelper.clamp(((float)currentTicks + ev.getPartialTicks()) / (float)throwingWeapon.getMaxChargeTicks(equipStack), 0.0F, 1.0F);
            offset = (int)((float)offset * (1.0F - percentage));
         }

         if (settings.showDebugInfo && !settings.hideGUI && !mc.player.hasReducedDebug() && !settings.reducedDebugInfo) {
            return;
         }

         GlStateManager.tryBlendFuncSeparate(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.enableBlend();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         mc.getTextureManager().bindTexture(CROSSHAIR_TEXTURES);
         if (!ModSpartanWeaponry.isRLCombatLoaded && !ConfigHandler.forceCompatibilityCrosshairs) {
            mc.ingameGUI.drawTexturedModalRect(x - 4, y - 4, 12, 1, 9, 5);
            mc.ingameGUI.drawTexturedModalRect(x - 4, y - 7 - offset, 12, 1, 9, 5);
            ev.setCanceled(true);
         } else {
            offset = (int)Math.sqrt((double)(offset * offset) / 2.0D);
            mc.ingameGUI.drawTexturedModalRect(x - 5 - offset, y - 5 - offset, 11, 12, 4, 4);
            mc.ingameGUI.drawTexturedModalRect(x + 2 + offset, y - 5 - offset, 18, 12, 4, 4);
            mc.ingameGUI.drawTexturedModalRect(x - 5 - offset, y + 2 + offset, 11, 19, 4, 4);
            mc.ingameGUI.drawTexturedModalRect(x + 2 + offset, y + 2 + offset, 18, 19, 4, 4);
         }

         if (mc.gameSettings.attackIndicator == 1 && !ModSpartanWeaponry.isRLCombatLoaded && !ConfigHandler.forceCompatibilityCrosshairs) {
            TextureManager var10000 = mc.getTextureManager();
            GuiIngame var10001 = mc.ingameGUI;
            var10000.bindTexture(GuiIngame.ICONS);
            GlStateManager.enableAlpha();
            float f = player.getCooledAttackStrength(0.0F);
            boolean flag = false;
            if (mc.pointedEntity != null && mc.pointedEntity instanceof EntityLivingBase && f >= 1.0F) {
               flag = mc.player.getCooldownPeriod() > 5.0F;
               flag = flag & ((EntityLivingBase)mc.pointedEntity).isEntityAlive();
            }

            int i = y - 7 + 16;
            int j = x - 8;
            if (flag) {
               mc.ingameGUI.drawTexturedModalRect(j, i, 68, 94, 16, 16);
            } else if (f < 1.0F) {
               int k = (int)(f * 17.0F);
               mc.ingameGUI.drawTexturedModalRect(j, i, 36, 94, 16, 4);
               mc.ingameGUI.drawTexturedModalRect(j, i, 52, 94, k, 4);
            }

            GlStateManager.disableAlpha();
         }

         GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.disableBlend();
         if (equipStack.hasTagCompound() && equipStack.getTagCompound().hasKey("AmmoUsed")) {
            NBTTagCompound tag = equipStack.getTagCompound();
            int maxAmmo = throwingWeapon.getMaxAmmo(equipStack);
            int ammo = maxAmmo - tag.getInteger("AmmoUsed");
            String text = String.format("%d/%d", ammo, maxAmmo);
            mc.fontRenderer.drawStringWithShadow(text, (float)(x - mc.fontRenderer.getStringWidth(text) / 2), (float)(y + 20), -1);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void onRenderGameOverlay(Post ev) {
      if (ev.getType() == ElementType.HOTBAR) {
         EntityPlayer player = Minecraft.getMinecraft().player;
         int loadSlot = -1;
         ItemStack loadStack = ItemStack.EMPTY;
         IHudLoadState loadItem = null;
         boolean isOffhand = false;
         if (player.getHeldItemMainhand().getItem() instanceof IHudLoadState) {
            loadStack = player.getHeldItemMainhand();
            loadItem = (IHudLoadState)loadStack.getItem();
         } else if (player.getHeldItemOffhand().getItem() instanceof IHudLoadState) {
            loadStack = player.getHeldItemOffhand();
            loadItem = (IHudLoadState)loadStack.getItem();
            isOffhand = true;
         }

         if (loadStack != null && loadItem != null && (!loadStack.isEmpty() && player.getActiveItemStack().isItemEqual(loadStack) || loadItem.isLoaded(loadStack))) {
            if (HudLoadState.hudActive == null) {
               HudLoadState.hudActive = new HudLoadState();
            }

            HudLoadState.hudActive.setLoaded(loadItem.isLoaded(loadStack));
            HudLoadState.hudActive.setLoadProgress(loadItem.getLoadProgress(loadStack, player));
            HudLoadState.hudActive.setOffhand(isOffhand);
            if (!isOffhand) {
               loadSlot = player.inventory.currentItem;
               HudLoadState.hudActive.setHighlightedSlot(loadSlot);
            }

            if (HudLoadState.hudActive != null) {
               HudLoadState.hudActive.render();
            }
         } else if (HudLoadState.hudActive != null) {
            HudLoadState.hudActive = null;
         }

         ItemStack quiverStack = ItemStack.EMPTY;

         for(QuiverHelper.IQuiverInfo info : QuiverHelper.info) {
            if (info.isWeapon(player.getHeldItemMainhand())) {
               quiverStack = QuiverHelper.findFirstOfType(player, info);
               break;
            }
         }

         if (quiverStack.isEmpty()) {
            quiverStack = QuiverHelper.findFirst(player);
         }

         if (quiverStack.isEmpty()) {
            HudQuiverAmmo.hudActive = null;
            activeQuiverStack = ItemStack.EMPTY;
         } else {
            activeQuiverStack = quiverStack;
            if (!ModSpartanWeaponry.isSpartanHudBaublesLoaded || !QuiverHelper.isInBaublesSlot(player, activeQuiverStack)) {
               if (HudQuiverAmmo.hudActive == null) {
                  HudQuiverAmmo.hudActive = new HudQuiverAmmo(22, 22, activeQuiverStack);
               } else {
                  HudQuiverAmmo.hudActive.setQuiver(activeQuiverStack);
               }

               HudQuiverAmmo.hudActive.render();
            }
         }
      }

   }

   public static ItemStack getActiveQuiverStack() {
      return activeQuiverStack;
   }
}
