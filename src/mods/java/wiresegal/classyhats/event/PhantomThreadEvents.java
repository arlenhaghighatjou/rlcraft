package wiresegal.classyhats.event;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.kotlin.CommonUtilMethods;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(
   modid = "classyhats"
)
@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0010\u001a\u00020\tH\u0002J\u0016\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014H\u0007J\u0016\u0010\u0016\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0017H\u0007J\u0010\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0019H\u0007J\u0018\u0010\u001a\u001a\u00020\t2\u0006\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u001c\u001a\u00020\u001dH\u0002J \u0010\u001e\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u0010\u001a\u00020\tH\u0002J\u0010\u0010\u001f\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020 H\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000b\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\f\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\r\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006!"},
   d2 = {"Lwiresegal/classyhats/event/PhantomThreadEvents;", "", "()V", "PHANTOM_ITEM_TAG", "", "PHANTOM_TAG", "captureSounds", "", "lastChestStack", "Lnet/minecraft/item/ItemStack;", "kotlin.jvm.PlatformType", "lastFeetStack", "lastHeadStack", "lastLegsStack", "getPhantomTag", "Lnet/minecraft/nbt/NBTTagCompound;", "stack", "onRenderLivingPost", "", "event", "Lnet/minecraftforge/client/event/RenderLivingEvent$Post;", "Lnet/minecraft/entity/EntityLivingBase;", "onRenderLivingPre", "Lnet/minecraftforge/client/event/RenderLivingEvent$Pre;", "onRenderTooltip", "Lnet/minecraftforge/event/entity/player/ItemTooltipEvent;", "setPhantomStack", "entity", "slot", "Lnet/minecraft/inventory/EntityEquipmentSlot;", "setSlotFor", "sound", "Lnet/minecraftforge/event/entity/PlaySoundAtEntityEvent;", "classyhats"}
)
public final class PhantomThreadEvents {
   @NotNull
   public static final String PHANTOM_TAG = "classy_hat_invisible";
   @NotNull
   public static final String PHANTOM_ITEM_TAG = "classy_hat_disguise";
   private static ItemStack lastHeadStack = ItemStack.EMPTY;
   private static ItemStack lastChestStack = ItemStack.EMPTY;
   private static ItemStack lastLegsStack = ItemStack.EMPTY;
   private static ItemStack lastFeetStack = ItemStack.EMPTY;
   private static boolean captureSounds;
   public static final PhantomThreadEvents INSTANCE;

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   @JvmStatic
   public static final void onRenderTooltip(@NotNull ItemTooltipEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      if (event.getItemStack().hasTagCompound()) {
         ItemStack var10000 = event.getItemStack();
         Intrinsics.checkExpressionValueIsNotNull(var10000, "event.itemStack");
         if (NBTHelper.getBoolean(var10000, "classy_hat_invisible", false)) {
            String locale = "tooltip.classyhats.phantom";
            var10000 = event.getItemStack();
            Intrinsics.checkExpressionValueIsNotNull(var10000, "event.itemStack");
            NBTTagCompound var5 = NBTHelper.getCompound(var10000, "classy_hat_disguise");
            if (var5 == null) {
               var5 = new NBTTagCompound();
            }

            NBTTagCompound nbt = var5;
            ItemStack container = new ItemStack(nbt);
            if (!container.isEmpty()) {
               event.getToolTip().add(I18n.format(locale + ".camo", new Object[0]) + " " + container.getRarity().rarityColor + (container.hasDisplayName() ? TextFormatting.ITALIC : "" + container.getDisplayName()));
            } else {
               List var6 = event.getToolTip();
               Intrinsics.checkExpressionValueIsNotNull(var6, "event.toolTip");
               TooltipHelper.addToTooltip(var6, locale + ".desc", new Object[0]);
            }

            List var7 = event.getToolTip();
            Intrinsics.checkExpressionValueIsNotNull(var7, "event.toolTip");
            TooltipHelper.addToTooltip(var7, locale, new Object[0]);
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   @JvmStatic
   public static final void onRenderLivingPre(@NotNull Pre<EntityLivingBase> event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      captureSounds = true;
      PhantomThreadEvents var10000 = INSTANCE;
      EntityLivingBase var10001 = event.getEntity();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "event.entity");
      lastHeadStack = var10000.setPhantomStack(var10001, EntityEquipmentSlot.HEAD);
      var10000 = INSTANCE;
      var10001 = event.getEntity();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "event.entity");
      lastChestStack = var10000.setPhantomStack(var10001, EntityEquipmentSlot.CHEST);
      var10000 = INSTANCE;
      var10001 = event.getEntity();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "event.entity");
      lastLegsStack = var10000.setPhantomStack(var10001, EntityEquipmentSlot.LEGS);
      var10000 = INSTANCE;
      var10001 = event.getEntity();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "event.entity");
      lastFeetStack = var10000.setPhantomStack(var10001, EntityEquipmentSlot.FEET);
      captureSounds = false;
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   @JvmStatic
   public static final void onRenderLivingPost(@NotNull Post<EntityLivingBase> event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      captureSounds = true;
      PhantomThreadEvents var10000 = INSTANCE;
      EntityLivingBase var10001 = event.getEntity();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "event.entity");
      EntityEquipmentSlot var10002 = EntityEquipmentSlot.HEAD;
      ItemStack var10003 = lastHeadStack;
      Intrinsics.checkExpressionValueIsNotNull(lastHeadStack, "lastHeadStack");
      var10000.setSlotFor(var10001, var10002, var10003);
      var10000 = INSTANCE;
      var10001 = event.getEntity();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "event.entity");
      var10002 = EntityEquipmentSlot.CHEST;
      var10003 = lastChestStack;
      Intrinsics.checkExpressionValueIsNotNull(lastChestStack, "lastChestStack");
      var10000.setSlotFor(var10001, var10002, var10003);
      var10000 = INSTANCE;
      var10001 = event.getEntity();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "event.entity");
      var10002 = EntityEquipmentSlot.LEGS;
      var10003 = lastLegsStack;
      Intrinsics.checkExpressionValueIsNotNull(lastLegsStack, "lastLegsStack");
      var10000.setSlotFor(var10001, var10002, var10003);
      var10000 = INSTANCE;
      var10001 = event.getEntity();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "event.entity");
      var10002 = EntityEquipmentSlot.FEET;
      var10003 = lastFeetStack;
      Intrinsics.checkExpressionValueIsNotNull(lastFeetStack, "lastFeetStack");
      var10000.setSlotFor(var10001, var10002, var10003);
      captureSounds = false;
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   @JvmStatic
   public static final void sound(@NotNull PlaySoundAtEntityEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      if (captureSounds) {
         event.setCanceled(true);
      }

   }

   private final void setSlotFor(EntityLivingBase entity, EntityEquipmentSlot slot, ItemStack stack) {
      if (CommonUtilMethods.isNotEmpty(stack)) {
         entity.setItemStackToSlot(slot, stack);
      }

   }

   private final ItemStack setPhantomStack(EntityLivingBase entity, EntityEquipmentSlot slot) {
      ItemStack stack = entity.getItemStackFromSlot(slot);
      ItemStack var10000;
      if (stack.hasTagCompound()) {
         Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
         if (NBTHelper.getBoolean(stack, "classy_hat_invisible", false)) {
            NBTTagCompound var5 = this.getPhantomTag(stack);
            if (var5 == null) {
               var5 = new NBTTagCompound();
            }

            NBTTagCompound comp = var5;
            entity.setItemStackToSlot(slot, new ItemStack(comp));
            var10000 = stack;
            return var10000;
         }
      }

      var10000 = ItemStack.EMPTY;
      Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
      return var10000;
   }

   private final NBTTagCompound getPhantomTag(ItemStack stack) {
      NBTTagCompound var2;
      if (NBTHelper.hasNBTEntry(stack, "classy_hat_disguise")) {
         var2 = stack.getTagCompound();
         var2 = var2 != null ? var2.getCompoundTag("classy_hat_disguise") : null;
      } else {
         var2 = null;
      }

      return var2;
   }

   private PhantomThreadEvents() {
   }

   static {
      PhantomThreadEvents var0 = new PhantomThreadEvents();
      INSTANCE = var0;
   }
}
