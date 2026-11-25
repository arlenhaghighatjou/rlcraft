package wiresegal.classyhats.item;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.core.client.ModelHandler;
import com.teamwizardry.librarianlib.core.common.LibCommonProxy;
import com.teamwizardry.librarianlib.features.base.IExtraVariantHolder;
import com.teamwizardry.librarianlib.features.base.item.IGlowingItem;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.base.item.IGlowingItem.DefaultImpls;
import com.teamwizardry.librarianlib.features.base.item.IGlowingItem.Helper;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.kotlin.CommonUtilMethods;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wiresegal.classyhats.ClassyHats;
import wiresegal.classyhats.ClassyHatsConfig;
import wiresegal.classyhats.capability.CapabilityHatContainer;
import wiresegal.classyhats.capability.data.IHatContainer;
import wiresegal.classyhats.client.keybind.KeyBindHandler;
import wiresegal.classyhats.util.HatData;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u009e\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003B\u0005¢\u0006\u0002\u0010\u0004J0\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u000f\u001a\u00020\f2\b\u0010\u0015\u001a\u0004\u0018\u00010\u00162\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00070\u00182\u0006\u0010\u0019\u001a\u00020\u001aH\u0017J\u0010\u0010\u001b\u001a\u00020\f2\u0006\u0010\u001c\u001a\u00020\fH\u0016J\u0010\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u000f\u001a\u00020\fH\u0016J\u000e\u0010\u001f\u001a\u00020 2\u0006\u0010\u000f\u001a\u00020\fJ\u0012\u0010!\u001a\u00020\"2\b\u0010\u000f\u001a\u0004\u0018\u00010\fH\u0016J\u0010\u0010#\u001a\u00020$2\u0006\u0010\u000f\u001a\u00020\fH\u0016J\u001e\u0010%\u001a\u00020\u00142\u0006\u0010&\u001a\u00020'2\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\f0)H\u0016J\u0010\u0010*\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\fH\u0016J\u0010\u0010+\u001a\u00020,2\u0006\u0010\u000f\u001a\u00020\fH\u0016J\u0018\u0010-\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u00072\b\b\u0002\u0010.\u001a\u00020\"J\u000e\u0010-\u001a\u00020\f2\u0006\u0010/\u001a\u00020 J&\u00100\u001a\b\u0012\u0004\u0012\u00020\f012\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u00102\u001a\u0002032\u0006\u00104\u001a\u000205H\u0016J\u0018\u00106\u001a\u00020,2\u0006\u0010\u001c\u001a\u00020\f2\u0006\u00107\u001a\u000208H\u0017J\u001a\u00109\u001a\u0004\u0018\u0001082\u0006\u0010\u001c\u001a\u00020\f2\u0006\u00107\u001a\u000208H\u0017R\u001c\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00070\u00068VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\b\u0010\tR1\u0010\n\u001a\u001f\u0012\u0013\u0012\u00110\f¢\u0006\f\b\r\u0012\b\b\u000e\u0012\u0004\b\b(\u000f\u0012\u0004\u0012\u00020\u0010\u0018\u00010\u000b8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012¨\u0006:"},
   d2 = {"Lwiresegal/classyhats/item/ItemHat;", "Lcom/teamwizardry/librarianlib/features/base/item/ItemMod;", "Lcom/teamwizardry/librarianlib/features/base/IExtraVariantHolder;", "Lcom/teamwizardry/librarianlib/features/base/item/IGlowingItem;", "()V", "extraVariants", "", "", "getExtraVariants", "()[Ljava/lang/String;", "meshDefinition", "Lkotlin/Function1;", "Lnet/minecraft/item/ItemStack;", "Lkotlin/ParameterName;", "name", "stack", "Lnet/minecraft/client/renderer/block/model/ModelResourceLocation;", "getMeshDefinition", "()Lkotlin/jvm/functions/Function1;", "addInformation", "", "worldIn", "Lnet/minecraft/world/World;", "tooltip", "", "flagIn", "Lnet/minecraft/client/util/ITooltipFlag;", "getContainerItem", "itemStack", "getEquipmentSlot", "Lnet/minecraft/inventory/EntityEquipmentSlot;", "getHat", "Lwiresegal/classyhats/util/HatData;", "getItemStackLimit", "", "getRarity", "Lnet/minecraft/item/EnumRarity;", "getSubItems", "tab", "Lnet/minecraft/creativetab/CreativeTabs;", "subItems", "Lnet/minecraft/util/NonNullList;", "getTranslationKey", "hasContainerItem", "", "ofHat", "amount", "hat", "onItemRightClick", "Lnet/minecraft/util/ActionResult;", "playerIn", "Lnet/minecraft/entity/player/EntityPlayer;", "handIn", "Lnet/minecraft/util/EnumHand;", "shouldDisableLightingForGlow", "model", "Lnet/minecraft/client/renderer/block/model/IBakedModel;", "transformToGlow", "classyhats"}
)
public final class ItemHat extends ItemMod implements IExtraVariantHolder, IGlowingItem {
   @NotNull
   public final HatData getHat(@NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      if (!stack.hasTagCompound()) {
         return ClassyHatsConfig.INSTANCE.getMissingno();
      } else {
         String var10000 = NBTHelper.getString(stack, "hat");
         if (var10000 != null) {
            String hatId = var10000;
            HatData var3 = (HatData)ClassyHatsConfig.INSTANCE.getHats().get(hatId);
            if (var3 == null) {
               var3 = ClassyHatsConfig.INSTANCE.getMissingno();
            }

            return var3;
         } else {
            return ClassyHatsConfig.INSTANCE.getMissingno();
         }
      }
   }

   @NotNull
   public final ItemStack ofHat(@NotNull HatData hat) {
      Intrinsics.checkParameterIsNotNull(hat, "hat");
      return ofHat$default(this, hat.getName(), 0, 2, (Object)null);
   }

   @NotNull
   public final ItemStack ofHat(@NotNull String name, int amount) {
      Intrinsics.checkParameterIsNotNull(name, "name");
      ItemStack stack = new ItemStack((Item)this, amount);
      NBTHelper.setString(stack, "hat", name);
      return stack;
   }

   public int getItemStackLimit(@Nullable ItemStack stack) {
      return 1;
   }

   @NotNull
   public EntityEquipmentSlot getEquipmentSlot(@NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      return EntityEquipmentSlot.HEAD;
   }

   @NotNull
   public ActionResult<ItemStack> onItemRightClick(@NotNull World worldIn, @NotNull EntityPlayer playerIn, @NotNull EnumHand handIn) {
      Intrinsics.checkParameterIsNotNull(worldIn, "worldIn");
      Intrinsics.checkParameterIsNotNull(playerIn, "playerIn");
      Intrinsics.checkParameterIsNotNull(handIn, "handIn");
      ItemStack stack = playerIn.getHeldItem(handIn);
      if (playerIn.isSneaking()) {
         playerIn.openGui(ClassyHats.Companion.getINSTANCE(), 0, worldIn, 0, 0, 0);
         ActionResult var13 = ActionResult.newResult(EnumActionResult.SUCCESS, stack);
         Intrinsics.checkExpressionValueIsNotNull(var13, "ActionResult.newResult(E…ionResult.SUCCESS, stack)");
         return var13;
      } else {
         IHatContainer hatInv = CapabilityHatContainer.Companion.getCapability(playerIn);
         ItemStack stackInSlot = hatInv.getEquipped();
         ItemStack var7 = stack.copy();
         var7.setCount(1);
         Intrinsics.checkExpressionValueIsNotNull(var7, "stack.copy().apply { count = 1 }");
         hatInv.setEquipped(var7);
         playerIn.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
         playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, ItemStack.EMPTY);
         if (CommonUtilMethods.isNotEmpty(stackInSlot)) {
            ActionResult var12 = ActionResult.newResult(EnumActionResult.SUCCESS, stackInSlot.copy());
            Intrinsics.checkExpressionValueIsNotNull(var12, "ActionResult.newResult(E…CESS, stackInSlot.copy())");
            return var12;
         } else {
            ActionResult var10000 = super.onItemRightClick(worldIn, playerIn, handIn);
            Intrinsics.checkExpressionValueIsNotNull(var10000, "super.onItemRightClick(worldIn, playerIn, handIn)");
            return var10000;
         }
      }
   }

   public boolean hasContainerItem(@NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      return true;
   }

   @NotNull
   public ItemStack getContainerItem(@NotNull ItemStack itemStack) {
      Intrinsics.checkParameterIsNotNull(itemStack, "itemStack");
      ItemStack var2 = itemStack.copy();
      var2.setCount(1);
      Intrinsics.checkExpressionValueIsNotNull(var2, "itemStack.copy().apply { count = 1 }");
      return var2;
   }

   @NotNull
   public String[] getExtraVariants() {
      Iterable $receiver$iv = (Iterable)ClassyHatsConfig.INSTANCE.getHats().values();
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));

      for(Object item$iv$iv : $receiver$iv) {
         HatData it = (HatData)item$iv$iv;
         String var11 = it.getName();
         destination$iv$iv.add(var11);
      }

      Collection var12 = (Collection)((List)destination$iv$iv);
      Object[] var10000 = var12.toArray(new String[0]);
      if (var10000 == null) {
         throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
      } else {
         return (String[])var10000;
      }
   }

   @Nullable
   public Function1<ItemStack, ModelResourceLocation> getMeshDefinition() {
      return (Function1)(new Function1<ItemStack, ModelResourceLocation>() {
         @NotNull
         public final ModelResourceLocation invoke(@NotNull ItemStack it) {
            Intrinsics.checkParameterIsNotNull(it, "it");
            ModelResourceLocation var10000 = ModelHandler.INSTANCE.getResource("classyhats", ItemHat.this.getHat(it).getName());
            if (var10000 == null) {
               Intrinsics.throwNpe();
            }

            return var10000;
         }
      });
   }

   public void getSubItems(@NotNull CreativeTabs tab, @NotNull NonNullList<ItemStack> subItems) {
      Intrinsics.checkParameterIsNotNull(tab, "tab");
      Intrinsics.checkParameterIsNotNull(subItems, "subItems");
      if (this.isInCreativeTab(tab)) {
         Iterable $receiver$iv = (Iterable)ClassyHatsConfig.INSTANCE.getHats().values();
         Collection destination$iv$iv = (Collection)(new ArrayList());

         for(Object element$iv$iv : $receiver$iv) {
            HatData it = (HatData)element$iv$iv;
            if (Intrinsics.areEqual(it, ClassyHatsConfig.INSTANCE.getMissingno()) ^ true && !it.getElusive()) {
               destination$iv$iv.add(element$iv$iv);
            }
         }

         for(destination$iv$iv : (Iterable)((List)destination$iv$iv)) {
            Collection var10000 = (Collection)subItems;
            HatData var21 = (HatData)destination$iv$iv;
            Collection var12 = var10000;
            ItemStack var13 = this.ofHat(var21);
            var12.add(var13);
         }

         Collection var28 = (Collection)subItems;
         $receiver$iv = (Iterable)ClassyHatsConfig.INSTANCE.getHats().values();
         destination$iv$iv = (Collection)(new ArrayList());

         for(Object element$iv$iv : $receiver$iv) {
            HatData it = (HatData)element$iv$iv;
            if (Intrinsics.areEqual(it, ClassyHatsConfig.INSTANCE.getMissingno()) ^ true && it.getElusive()) {
               destination$iv$iv.add(element$iv$iv);
            }
         }

         for(destination$iv$iv : (Iterable)((List)destination$iv$iv)) {
            var28 = (Collection)subItems;
            HatData var23 = (HatData)destination$iv$iv;
            Collection var26 = var28;
            ItemStack var27 = this.ofHat(var23);
            var26.add(var27);
         }

         var28 = (Collection)subItems;
      }

   }

   @NotNull
   public String getUnlocalizedName(@NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      return "hat.classyhats." + this.getHat(stack).getName();
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull final List<String> tooltip, @NotNull ITooltipFlag flagIn) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      Intrinsics.checkParameterIsNotNull(tooltip, "tooltip");
      Intrinsics.checkParameterIsNotNull(flagIn, "flagIn");
      String desc = stack.getUnlocalizedName() + ".desc";
      String used = LibrarianLib.INSTANCE.getPROXY().canTranslate(desc) ? desc : desc + '0';
      if (LibrarianLib.INSTANCE.getPROXY().canTranslate(used)) {
         TooltipHelper.addToTooltip(tooltip, used, new Object[0]);
         int i = 0;

         while(true) {
            LibCommonProxy var10000 = LibrarianLib.INSTANCE.getPROXY();
            StringBuilder var10001 = (new StringBuilder()).append(desc);
            ++i;
            if (!var10000.canTranslate(var10001.append(i).toString())) {
               break;
            }

            TooltipHelper.addToTooltip(tooltip, desc + i, new Object[0]);
         }
      }

      tooltip.add("");
      TooltipHelper.INSTANCE.tooltipIfShift(tooltip, new Function1<List<String>, Unit>() {
         public final void invoke(@NotNull List<String> it) {
            Intrinsics.checkParameterIsNotNull(it, "it");
            TooltipHelper.addToTooltip(tooltip, "tooltip.classyhats.hat_inventory", new Object[]{KeyBindHandler.INSTANCE.getKEY().getDisplayName()});
            TooltipHelper.addToTooltip(tooltip, "tooltip.classyhats.hat_inventory_sneak", new Object[0]);
         }
      });
   }

   @NotNull
   public EnumRarity getRarity(@NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      return Intrinsics.areEqual(this.getHat(stack), ClassyHatsConfig.INSTANCE.getMissingno()) ? EnumRarity.EPIC : (this.getHat(stack).getElusive() ? EnumRarity.UNCOMMON : EnumRarity.COMMON);
   }

   @SideOnly(Side.CLIENT)
   @Nullable
   public IBakedModel transformToGlow(@NotNull ItemStack itemStack, @NotNull IBakedModel model) {
      Intrinsics.checkParameterIsNotNull(itemStack, "itemStack");
      Intrinsics.checkParameterIsNotNull(model, "model");
      return Helper.wrapperBake(model, false, new int[]{99});
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldDisableLightingForGlow(@NotNull ItemStack itemStack, @NotNull IBakedModel model) {
      Intrinsics.checkParameterIsNotNull(itemStack, "itemStack");
      Intrinsics.checkParameterIsNotNull(model, "model");
      return true;
   }

   public ItemHat() {
      super("hat", new String[0]);
   }

   @SideOnly(Side.CLIENT)
   public int packedGlowCoords(@NotNull ItemStack itemStack, @NotNull IBakedModel model) {
      Intrinsics.checkParameterIsNotNull(itemStack, "itemStack");
      Intrinsics.checkParameterIsNotNull(model, "model");
      return DefaultImpls.packedGlowCoords(this, itemStack, model);
   }
}
