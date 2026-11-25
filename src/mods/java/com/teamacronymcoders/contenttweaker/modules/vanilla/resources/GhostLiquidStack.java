package com.teamacronymcoders.contenttweaker.modules.vanilla.resources;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemCondition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IItemTransformer;
import crafttweaker.api.item.IItemTransformerNew;
import crafttweaker.api.liquid.ILiquidDefinition;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.player.IPlayer;
import crafttweaker.mc1120.brackets.BracketHandlerLiquid;
import crafttweaker.mc1120.liquid.MCLiquidStack;
import java.util.List;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class GhostLiquidStack implements ILiquidStack {
   public static final FluidStack FAKE_STACK = new FluidStack(FluidRegistry.WATER, 1);
   public static final ILiquidStack FAKE_LSTACK = new MCLiquidStack(FAKE_STACK);
   private static boolean warned = false;
   private final String name;
   private ILiquidStack liquid;

   public GhostLiquidStack(String name) {
      this.liquid = FAKE_LSTACK;
      this.name = name;
      if (!warned) {
         CraftTweakerAPI.logInfo("Liquid <liquid:" + name + "> has not been found, trying to use a ghost representative. This message will only be printed once, all subsequent missing fluids will be handled the same way.");
         warned = true;
      }

      this.update();
   }

   public void update() {
      if (FAKE_LSTACK.matches(this.liquid)) {
         ILiquidStack stack = BracketHandlerLiquid.getLiquid(this.name);
         if (stack != null) {
            stack = stack.withAmount(this.liquid.getAmount());
            if (this.liquid.getTag() != null) {
               stack = stack.withTag(this.liquid.getTag());
            }

            this.liquid = stack;
         }
      }
   }

   public ILiquidDefinition getDefinition() {
      this.update();
      return this.liquid.getDefinition();
   }

   public String getName() {
      this.update();
      return this.liquid.getName();
   }

   public String getDisplayName() {
      this.update();
      return this.liquid.getDisplayName();
   }

   public String getMark() {
      this.update();
      return this.liquid.getMark();
   }

   public int getAmount() {
      this.update();
      return this.liquid.getAmount();
   }

   public List<IItemStack> getItems() {
      this.update();
      return this.liquid.getItems();
   }

   public IItemStack[] getItemArray() {
      this.update();
      return this.liquid.getItemArray();
   }

   public List<ILiquidStack> getLiquids() {
      this.update();
      return this.liquid.getLiquids();
   }

   public IIngredient amount(int amount) {
      this.update();
      return this.liquid.amount(amount);
   }

   public IIngredient or(IIngredient ingredient) {
      this.update();
      return this.liquid.or(ingredient);
   }

   public IIngredient transformNew(IItemTransformerNew transformer) {
      this.update();
      return this.liquid.transformNew(transformer);
   }

   public IIngredient only(IItemCondition condition) {
      this.update();
      return this.liquid.only(condition);
   }

   public IIngredient marked(String mark) {
      this.update();
      return this.liquid.marked(mark);
   }

   public boolean matches(IItemStack item) {
      this.update();
      return this.liquid.matches(item);
   }

   public boolean matchesExact(IItemStack item) {
      this.update();
      return this.liquid.matchesExact(item);
   }

   public boolean matches(ILiquidStack liquid) {
      this.update();
      return liquid.matches(liquid);
   }

   public boolean contains(IIngredient ingredient) {
      this.update();
      return this.liquid.contains(ingredient);
   }

   public IItemStack applyTransform(IItemStack item, IPlayer byPlayer) {
      this.update();
      return this.liquid.applyTransform(item, byPlayer);
   }

   public IItemStack applyNewTransform(IItemStack item) {
      this.update();
      return this.liquid.applyNewTransform(item);
   }

   public boolean hasNewTransformers() {
      this.update();
      return this.liquid.hasNewTransformers();
   }

   public boolean hasTransformers() {
      this.update();
      return this.liquid.hasTransformers();
   }

   public IIngredient transform(IItemTransformer transformer) {
      this.update();
      return this.liquid.transform(transformer);
   }

   public int getLuminosity() {
      this.update();
      return this.liquid.getLuminosity();
   }

   public int getDensity() {
      this.update();
      return this.liquid.getDensity();
   }

   public int getTemperature() {
      this.update();
      return this.liquid.getTemperature();
   }

   public int getViscosity() {
      this.update();
      return this.liquid.getViscosity();
   }

   public boolean isGaseous() {
      this.update();
      return this.liquid.isGaseous();
   }

   public IData getTag() {
      this.update();
      return this.liquid.getTag();
   }

   public ILiquidStack withTag(IData data) {
      this.update();
      this.liquid = this.liquid.withTag(data);
      return this;
   }

   public ILiquidStack withAmount(int amount) {
      this.update();
      this.liquid = this.liquid.withAmount(amount);
      return this;
   }

   public Object getInternal() {
      this.update();
      if (this.liquid.getDefinition().getInternal().equals(FAKE_STACK.getFluid())) {
         CraftTweakerAPI.logError("Trying to access Ghost liquid before its ready: <liquid:" + this.name + ">");
      }

      return this.liquid.getInternal();
   }

   public String toCommandString() {
      this.update();
      return this.liquid.toCommandString();
   }
}
