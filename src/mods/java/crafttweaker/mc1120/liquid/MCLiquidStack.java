package crafttweaker.mc1120.liquid;

import crafttweaker.api.data.IData;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemCondition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IItemTransformer;
import crafttweaker.api.item.IItemTransformerNew;
import crafttweaker.api.item.IngredientOr;
import crafttweaker.api.liquid.ILiquidDefinition;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.mc1120.data.NBTConverter;
import crafttweaker.mc1120.item.MCItemStack;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class MCLiquidStack implements ILiquidStack {
   private final FluidStack stack;
   private final IItemTransformerNew transformerNew;
   private IData tag;

   public MCLiquidStack(FluidStack stack) {
      this(stack, (IData)null, (IItemTransformerNew)null);
   }

   private MCLiquidStack(FluidStack stack, IData tag, IItemTransformerNew transformerNew) {
      this.stack = stack;
      this.tag = tag;
      this.transformerNew = transformerNew;
   }

   public ILiquidDefinition getDefinition() {
      return new MCLiquidDefinition(this.stack.getFluid());
   }

   public String getName() {
      return this.stack.getFluid().getName();
   }

   public String getDisplayName() {
      return this.stack.getFluid().getLocalizedName(this.stack);
   }

   public int getAmount() {
      return this.stack.amount;
   }

   public IData getTag() {
      if (this.tag == null) {
         if (this.stack.tag == null) {
            return null;
         }

         this.tag = NBTConverter.from(this.stack.tag, true);
      }

      return this.tag;
   }

   public ILiquidStack withTag(IData data) {
      FluidStack result = new FluidStack(this.stack.getFluid(), this.stack.amount);
      result.tag = (NBTTagCompound)NBTConverter.from(data);
      return new MCLiquidStack(result, data.immutable(), this.transformerNew);
   }

   public ILiquidStack withAmount(int amount) {
      FluidStack result = new FluidStack(this.stack.getFluid(), amount);
      result.tag = this.stack.tag;
      return new MCLiquidStack(result, this.tag, this.transformerNew);
   }

   public int getLuminosity() {
      return this.stack.getFluid().getLuminosity(this.stack);
   }

   public int getDensity() {
      return this.stack.getFluid().getDensity(this.stack);
   }

   public int getTemperature() {
      return this.stack.getFluid().getTemperature(this.stack);
   }

   public int getViscosity() {
      return this.stack.getFluid().getViscosity(this.stack);
   }

   public boolean isGaseous() {
      return this.stack.getFluid().isGaseous(this.stack);
   }

   public Object getInternal() {
      return this.stack;
   }

   public String getMark() {
      return null;
   }

   public List<IItemStack> getItems() {
      IItemStack stack = CraftTweakerMC.getIItemStack(FluidUtil.getFilledBucket(this.stack.copy()));
      if (stack != null) {
         String name = LanguageMap.instance.translateKeyFormat("crafttweaker.any_liquid_container", new Object[]{this.stack.getLocalizedName(), this.getAmount()});
         return Collections.singletonList(stack.withDisplayName(name));
      } else {
         return Collections.emptyList();
      }
   }

   public IItemStack[] getItemArray() {
      return (IItemStack[])this.getItems().toArray(new IItemStack[0]);
   }

   public List<ILiquidStack> getLiquids() {
      return Collections.singletonList(this);
   }

   public IIngredient amount(int amount) {
      return this.withAmount(amount);
   }

   public IIngredient or(IIngredient ingredient) {
      return new IngredientOr(this, ingredient);
   }

   public IIngredient transformNew(IItemTransformerNew transformer) {
      return new MCLiquidStack(this.stack, this.tag, transformer);
   }

   public IIngredient only(IItemCondition condition) {
      throw new UnsupportedOperationException("Liquid stack can't have conditions");
   }

   public IIngredient marked(String mark) {
      throw new UnsupportedOperationException("Liquid stack can't be marked");
   }

   public boolean matches(IItemStack item) {
      FluidStack fluidStack = FluidUtil.getFluidContained(CraftTweakerMC.getItemStack(item));
      return fluidStack != null && this.matches(CraftTweakerMC.getILiquidStack(fluidStack));
   }

   public boolean matchesExact(IItemStack item) {
      return this.matches(item);
   }

   public boolean matches(ILiquidStack liquid) {
      return liquid != null && this.getDefinition().equals(liquid.getDefinition()) && this.getAmount() <= liquid.getAmount();
   }

   public boolean contains(IIngredient ingredient) {
      if (ingredient == null) {
         return false;
      } else {
         for(ILiquidStack liquid : ingredient.getLiquids()) {
            if (!this.matches(liquid)) {
               return false;
            }
         }

         return !ingredient.getLiquids().isEmpty();
      }
   }

   public IItemStack applyTransform(IItemStack item, IPlayer byPlayer) {
      return item;
   }

   public IItemStack applyNewTransform(IItemStack item) {
      if (this.transformerNew != null) {
         return this.transformerNew.transform(item);
      } else {
         ItemStack itemStack = CraftTweakerMC.getItemStack(item);
         Item item1 = itemStack.getItem();
         if (!(item1 instanceof ItemBucket) && !(item1 instanceof UniversalBucket) && item1 != Items.MILK_BUCKET && item1 != Items.WATER_BUCKET && item1 != Items.LAVA_BUCKET && item1 != ForgeModContainer.getInstance().universalBucket) {
            IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(itemStack);
            if (fluidHandler == null) {
               return CraftTweakerMC.getIItemStack(ForgeHooks.getContainerItem(itemStack));
            } else {
               fluidHandler.drain(this.stack.copy(), true);
               return MCItemStack.createNonCopy(itemStack);
            }
         } else {
            return new MCItemStack(new ItemStack(Items.BUCKET, 1));
         }
      }
   }

   public boolean hasNewTransformers() {
      return true;
   }

   public boolean hasTransformers() {
      return false;
   }

   public IIngredient transform(IItemTransformer transformer) {
      throw new UnsupportedOperationException("Liquid stack can't have transformers");
   }

   public String toString() {
      return "<liquid:" + this.getName() + ">";
   }

   public String toCommandString() {
      return this.toString();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCLiquidStack that = (MCLiquidStack)o;
         return Objects.equals(this.stack, that.stack) && Objects.equals(this.tag, that.tag);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.stack, this.tag});
   }
}
