package com.teamacronymcoders.contenttweaker.modules.vanilla.items.food;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.client.models.IHasModel;
import com.teamacronymcoders.base.client.models.generator.IHasGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.items.IHasItemColor;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import com.teamacronymcoders.contenttweaker.api.MissingFieldsException;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.MCBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.MCBlockState;
import com.teamacronymcoders.contenttweaker.api.ctobjects.entity.EntityHelper;
import com.teamacronymcoders.contenttweaker.api.ctobjects.entity.player.CTPlayer;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Facing;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Hand;
import com.teamacronymcoders.contenttweaker.api.ctobjects.mutableitemstack.MCMutableItemStack;
import com.teamacronymcoders.contenttweaker.api.ctobjects.resourcelocation.CTResourceLocation;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.MCWorld;
import com.teamacronymcoders.contenttweaker.api.utils.CTUtils;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IItemFoodEaten;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.util.Position3f;
import crafttweaker.mc1120.item.MCItemStack;
import crafttweaker.mc1120.util.MCPosition3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemContentFood extends ItemFood implements IHasModel, IHasGeneratedModel, IHasItemColor {
   private final ItemFoodRepresentation itemRepresentation;
   private CreativeTabs creativeTab;
   private IBaseMod mod;
   private EnumAction itemUseAction;
   private EnumRarity rarity;

   public ItemContentFood(ItemFoodRepresentation itemRepresentation) {
      super(itemRepresentation.getHealAmount(), itemRepresentation.getSaturation(), itemRepresentation.isWolfFood());
      this.itemRepresentation = itemRepresentation;
      this.checkFields();
      this.setFields();
   }

   public IBaseMod getMod() {
      return this.mod;
   }

   public void setMod(IBaseMod mod) {
      this.mod = mod;
   }

   public void checkFields() {
      List<String> missingFields = new ArrayList();
      if (this.itemRepresentation.getUnlocalizedName() == null) {
         missingFields.add("unlocalizedName");
      }

      if (!missingFields.isEmpty()) {
         throw new MissingFieldsException("ItemFoodRepresentation", missingFields);
      }
   }

   public void setFields() {
      this.setUnlocalizedName(this.itemRepresentation.getUnlocalizedName());
      if (this.itemRepresentation.getCreativeTab() != null) {
         Object creativeTab = this.itemRepresentation.getCreativeTab().getInternal();
         if (creativeTab instanceof CreativeTabs) {
            this.setCreativeTab((CreativeTabs)this.itemRepresentation.getCreativeTab().getInternal());
         }
      }

      this.setMaxStackSize(this.itemRepresentation.getMaxStackSize());
      this.setHarvestLevel(this.itemRepresentation.getToolClass(), this.itemRepresentation.getToolLevel());
      this.itemUseAction = (EnumAction)CTUtils.getEnum(this.itemRepresentation.getItemUseAction(), EnumAction.class);
      this.rarity = (EnumRarity)CTUtils.getEnum(this.itemRepresentation.getRarity(), EnumRarity.class);
      if (this.itemRepresentation.getMaxDamage() > 0) {
         this.setMaxDamage(this.itemRepresentation.getMaxDamage());
      }

      if (this.itemRepresentation.alwaysEdible) {
         this.setAlwaysEdible();
      }

   }

   @Nonnull
   public EnumRarity getRarity(@Nonnull ItemStack itemStack) {
      return this.rarity;
   }

   public float getSmeltingExperience(@Nonnull ItemStack itemStack) {
      return this.itemRepresentation.getSmeltingExperience();
   }

   public boolean isBeaconPayment(@Nonnull ItemStack itemStack) {
      return this.itemRepresentation.isBeaconPayment();
   }

   @SideOnly(Side.CLIENT)
   public CreativeTabs getCreativeTab() {
      return this.creativeTab;
   }

   @Nonnull
   public Item setCreativeTab(@Nonnull CreativeTabs creativeTab) {
      this.creativeTab = creativeTab;
      return this;
   }

   @SideOnly(Side.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return this.itemRepresentation.isGlowing() || super.hasEffect(stack);
   }

   @Nonnull
   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      EnumActionResult actionResult = EnumActionResult.PASS;
      if (Objects.nonNull(this.itemRepresentation.getOnItemUse())) {
         Position3f blockTouch = new MCPosition3f(hitX, hitY, hitZ);
         actionResult = this.itemRepresentation.getOnItemUse().useItem(new CTPlayer(player), new MCWorld(world), new MCBlockPos(pos), Hand.of(hand), Facing.of(facing), blockTouch).getInternal();
      }

      return actionResult;
   }

   public float getDestroySpeed(ItemStack stack, IBlockState state) {
      return ((Float)Optional.ofNullable(this.itemRepresentation.getItemDestroySpeed()).map((value) -> {
         return value.getDestroySpeed(new MCMutableItemStack(stack), new MCBlockState(state));
      }).orElseGet(() -> {
         return super.getDestroySpeed(stack, state);
      })).floatValue();
   }

   public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
      return ((Boolean)Optional.ofNullable(this.itemRepresentation.getItemDestroyedBlock()).map((value) -> {
         return value.onBlockDestroyed(new MCMutableItemStack(stack), new MCWorld(world), new MCBlockState(state), new MCBlockPos(pos), EntityHelper.getIEntityLivingBase(entityLiving));
      }).orElseGet(() -> {
         return super.onBlockDestroyed(stack, world, state, pos, entityLiving);
      })).booleanValue();
   }

   @Nonnull
   public EnumAction getItemUseAction(@Nonnull ItemStack stack) {
      return this.itemUseAction;
   }

   public List<String> getModelNames(List<String> modelNames) {
      modelNames.add(this.getUnlocalizedName().substring(5));
      return modelNames;
   }

   public List<ItemStack> getAllSubItems(List<ItemStack> itemStacks) {
      itemStacks.add(new ItemStack(this));
      return itemStacks;
   }

   public Item getItem() {
      return this;
   }

   public List<IGeneratedModel> getGeneratedModels() {
      List<IGeneratedModel> models = Lists.newArrayList();
      TemplateFile templateFile = TemplateManager.getTemplateFile("item_model");
      Map<String, String> replacements = Maps.newHashMap();
      replacements.put("texture", Optional.ofNullable(this.itemRepresentation.getTextureLocation()).map(CTResourceLocation::getInternal).map(ResourceLocation::toString).orElseGet(() -> {
         return "contenttweaker:items/" + this.itemRepresentation.getUnlocalizedName();
      }));
      templateFile.replaceContents(replacements);
      models.add(new GeneratedModel(this.itemRepresentation.getUnlocalizedName(), ModelType.ITEM_MODEL, templateFile.getFileContents()));
      return models;
   }

   public boolean hasContainerItem(ItemStack itemStack) {
      return this.itemRepresentation.getItemGetContainerItem() != null || super.hasContainerItem(itemStack);
   }

   @Nonnull
   public ItemStack getContainerItem(@Nonnull ItemStack itemStack) {
      return (ItemStack)Optional.ofNullable(this.itemRepresentation.getItemGetContainerItem()).map((getContainerItem) -> {
         return getContainerItem.getContainerItem(new MCItemStack(itemStack));
      }).map((result) -> {
         return (ItemStack)result.getInternal();
      }).orElseGet(() -> {
         return super.getContainerItem(itemStack);
      });
   }

   public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
      return this.itemRepresentation.getItemColorSupplier().getColor(new MCItemStack(stack), tintIndex).getIntColor();
   }

   @Nonnull
   public String getItemStackDisplayName(@Nonnull ItemStack stack) {
      return (String)Optional.ofNullable(this.itemRepresentation.getLocalizedNameSupplier()).map((supplier) -> {
         return supplier.getLocalizedName(new MCItemStack(stack));
      }).orElseGet(() -> {
         return super.getItemStackDisplayName(stack);
      });
   }

   protected void onFoodEaten(ItemStack stack, World worldIn, @Nonnull EntityPlayer player) {
      IItemFoodEaten onItemFoodEaten = this.itemRepresentation.getOnItemFoodEaten();
      if (onItemFoodEaten != null) {
         onItemFoodEaten.handle(new MCMutableItemStack(stack), new MCWorld(worldIn), new CTPlayer(player));
      } else {
         super.onFoodEaten(stack, worldIn, player);
      }

   }

   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
      return (ItemStack)Optional.ofNullable(this.itemRepresentation.getOnItemUseFinish()).map((iItemUseFinish) -> {
         return iItemUseFinish.getResult(new MCMutableItemStack(stack), new MCWorld(worldIn), EntityHelper.getIEntityLivingBase(entityLiving));
      }).map(CraftTweakerMC::getItemStack).orElseGet(() -> {
         return super.onItemUseFinish(stack, worldIn, entityLiving);
      });
   }
}
