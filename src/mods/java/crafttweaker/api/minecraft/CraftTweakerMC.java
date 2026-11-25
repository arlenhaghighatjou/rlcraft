package crafttweaker.api.minecraft;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.block.IMaterial;
import crafttweaker.api.command.ICommand;
import crafttweaker.api.container.IContainer;
import crafttweaker.api.creativetabs.ICreativeTab;
import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.data.IData;
import crafttweaker.api.dispenser.IBlockSource;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityAgeable;
import crafttweaker.api.entity.IEntityAnimal;
import crafttweaker.api.entity.IEntityArrow;
import crafttweaker.api.entity.IEntityArrowTipped;
import crafttweaker.api.entity.IEntityCreature;
import crafttweaker.api.entity.IEntityEquipmentSlot;
import crafttweaker.api.entity.IEntityFishHook;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.entity.IEntityLiving;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.entity.IEntityMob;
import crafttweaker.api.entity.IEntityThrowable;
import crafttweaker.api.entity.IEntityXp;
import crafttweaker.api.entity.attribute.IEntityAttribute;
import crafttweaker.api.entity.attribute.IEntityAttributeInstance;
import crafttweaker.api.entity.attribute.IEntityAttributeModifier;
import crafttweaker.api.game.ITeam;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemDefinition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IMutableItemStack;
import crafttweaker.api.item.IngredientOr;
import crafttweaker.api.item.IngredientUnknown;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.liquid.ILiquidDefinition;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.potions.IPotion;
import crafttweaker.api.potions.IPotionEffect;
import crafttweaker.api.potions.IPotionType;
import crafttweaker.api.server.IServer;
import crafttweaker.api.text.IStyle;
import crafttweaker.api.text.ITextComponent;
import crafttweaker.api.util.IAxisAlignedBB;
import crafttweaker.api.world.IBiome;
import crafttweaker.api.world.IBiomeType;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IExplosion;
import crafttweaker.api.world.IFacing;
import crafttweaker.api.world.IRayTraceResult;
import crafttweaker.api.world.IVector3d;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.block.MCBlockDefinition;
import crafttweaker.mc1120.block.MCBlockState;
import crafttweaker.mc1120.block.MCMaterial;
import crafttweaker.mc1120.block.MCRayTraceResult;
import crafttweaker.mc1120.block.MCSpecificBlock;
import crafttweaker.mc1120.block.MCWorldBlock;
import crafttweaker.mc1120.command.MCCommand;
import crafttweaker.mc1120.command.MCCommandSender;
import crafttweaker.mc1120.container.MCContainer;
import crafttweaker.mc1120.creativetabs.MCCreativeTab;
import crafttweaker.mc1120.damage.MCDamageSource;
import crafttweaker.mc1120.data.NBTConverter;
import crafttweaker.mc1120.dispenser.MCBlockSource;
import crafttweaker.mc1120.entity.MCEntity;
import crafttweaker.mc1120.entity.MCEntityAgeable;
import crafttweaker.mc1120.entity.MCEntityAnimal;
import crafttweaker.mc1120.entity.MCEntityArrow;
import crafttweaker.mc1120.entity.MCEntityArrowTipped;
import crafttweaker.mc1120.entity.MCEntityCreature;
import crafttweaker.mc1120.entity.MCEntityEquipmentSlot;
import crafttweaker.mc1120.entity.MCEntityFishHook;
import crafttweaker.mc1120.entity.MCEntityItem;
import crafttweaker.mc1120.entity.MCEntityLiving;
import crafttweaker.mc1120.entity.MCEntityLivingBase;
import crafttweaker.mc1120.entity.MCEntityMob;
import crafttweaker.mc1120.entity.MCEntityThrowable;
import crafttweaker.mc1120.entity.MCEntityXp;
import crafttweaker.mc1120.entity.attribute.MCEntityAttribute;
import crafttweaker.mc1120.entity.attribute.MCEntityAttributeInstance;
import crafttweaker.mc1120.entity.attribute.MCEntityAttributeModifier;
import crafttweaker.mc1120.entity.expand.ExpandEntityEquipmentSlot;
import crafttweaker.mc1120.game.MCTeam;
import crafttweaker.mc1120.item.MCItemStack;
import crafttweaker.mc1120.item.MCMutableItemStack;
import crafttweaker.mc1120.item.VanillaIngredient;
import crafttweaker.mc1120.liquid.MCLiquidDefinition;
import crafttweaker.mc1120.liquid.MCLiquidStack;
import crafttweaker.mc1120.oredict.MCOreDictEntry;
import crafttweaker.mc1120.player.MCPlayer;
import crafttweaker.mc1120.potions.MCPotion;
import crafttweaker.mc1120.potions.MCPotionEffect;
import crafttweaker.mc1120.potions.MCPotionType;
import crafttweaker.mc1120.text.MCStyle;
import crafttweaker.mc1120.text.MCTextComponent;
import crafttweaker.mc1120.util.MCAxisAlignedBB;
import crafttweaker.mc1120.world.MCBiome;
import crafttweaker.mc1120.world.MCBlockPos;
import crafttweaker.mc1120.world.MCExplosion;
import crafttweaker.mc1120.world.MCFacing;
import crafttweaker.mc1120.world.MCVector3d;
import crafttweaker.mc1120.world.MCWorld;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.IngredientNBT;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;

public class CraftTweakerMC {
   public static final IBiome[] biomes = new IBiome[Biome.REGISTRY.getKeys().size()];
   public static final Map<String, ICreativeTab> creativeTabs = new HashMap();
   private static final Map<Block, MCBlockDefinition> blockDefinitions = new HashMap();
   private static final HashMap<List, IOreDictEntry> oreDictArrays = new HashMap();

   private CraftTweakerMC() {
   }

   public static ItemStack getItemStack(IItemStack item) {
      if (item == null) {
         return ItemStack.EMPTY;
      } else {
         Object internal = item.getInternal();
         if (!(internal instanceof ItemStack)) {
            CraftTweakerAPI.logError("Not a valid item stack: " + item);
            throw new IllegalArgumentException("Not a valid item stack: " + item);
         } else {
            return ((ItemStack)internal).copy();
         }
      }
   }

   public static ItemStack getItemStack(IMutableItemStack item) {
      if (item == null) {
         return ItemStack.EMPTY;
      } else {
         Object internal = item.getInternal();
         if (!(internal instanceof ItemStack)) {
            CraftTweakerAPI.logError("Not a valid item stack: " + item);
            throw new IllegalArgumentException("Not a valid item stack: " + item);
         } else {
            return (ItemStack)internal;
         }
      }
   }

   public static ItemStack getItemStack(IIngredient ingredient) {
      if (ingredient == null) {
         return ItemStack.EMPTY;
      } else {
         List<IItemStack> items = ingredient.getItems();
         if (items.size() != 1) {
            CraftTweakerAPI.logError("Not an ingredient with a single item: " + ingredient);
         }

         return items.isEmpty() ? ItemStack.EMPTY : getItemStack((IItemStack)items.get(0));
      }
   }

   public static IItemStack getIItemStack(ItemStack item) {
      return item != null && !item.isEmpty() ? new MCItemStack(item) : null;
   }

   public static IItemStack getIItemStackWildcardSize(ItemStack item) {
      return item != null && !item.isEmpty() ? new MCItemStack(item, true) : null;
   }

   public static IItemStack getIItemStackMutable(ItemStack item) {
      return item != null && !item.isEmpty() ? new MCMutableItemStack(item) : null;
   }

   public static IItemStack getIItemStackMutableWildcardSize(ItemStack item) {
      return item != null && !item.isEmpty() ? new MCMutableItemStack(item, true) : null;
   }

   public static IItemStack getIItemStackForMatching(ItemStack item) {
      return getIItemStackForMatching(item, false);
   }

   public static IItemStack getIItemStackForMatching(ItemStack item, boolean wildcardSize) {
      return wildcardSize ? getIItemStackMutableWildcardSize(item) : getIItemStackMutable(item);
   }

   public static IItemStack getIItemStackWildcardSize(Item item, int meta) {
      return item == null ? null : new MCItemStack(new ItemStack(item, 1, meta), true);
   }

   public static IItemStack getIItemStackWildcard(Item item, int amount) {
      return item == null ? null : new MCItemStack(new ItemStack(item, 1, 32767));
   }

   public static ItemStack[] getExamples(IIngredient ingredient) {
      List<IItemStack> examples = ingredient.getItems();
      ItemStack[] result = new ItemStack[examples.size()];

      for(int i = 0; i < examples.size(); ++i) {
         result[i] = getItemStack((IItemStack)examples.get(i));
      }

      return result;
   }

   public static IItemStack getItemStack(Item item, int amount, int damage) {
      return item == null ? null : new MCItemStack(new ItemStack(item, amount, damage));
   }

   public static ItemStack[] getItemStacks(IItemStack... items) {
      if (items == null) {
         return null;
      } else {
         ItemStack[] output = new ItemStack[items.length];

         for(int i = 0; i < items.length; ++i) {
            if (items[i] == null) {
               output[i] = ItemStack.EMPTY;
            } else {
               Object internal = items[i].getInternal();
               if (internal instanceof ItemStack && !((ItemStack)internal).isEmpty()) {
                  output[i] = (ItemStack)internal;
               }
            }
         }

         return output;
      }
   }

   public static ItemStack[] getItemStacks(List<IItemStack> items) {
      if (items == null) {
         return null;
      } else {
         ItemStack[] output = new ItemStack[items.size()];

         for(int i = 0; i < items.size(); ++i) {
            if (items.get(i) == null) {
               output[i] = ItemStack.EMPTY;
            } else {
               Object internal = ((IItemStack)items.get(i)).getInternal();
               if (internal instanceof ItemStack && !((ItemStack)internal).isEmpty()) {
                  output[i] = (ItemStack)internal;
               } else {
                  CraftTweakerAPI.logError("Invalid item stack: " + items.get(i));
               }
            }
         }

         return output;
      }
   }

   public static IItemStack[] getIItemStacks(ItemStack... items) {
      if (items == null) {
         return null;
      } else {
         IItemStack[] result = new IItemStack[items.length];

         for(int i = 0; i < items.length; ++i) {
            ItemStack itemStack = items[i];
            if (itemStack != null && !itemStack.isEmpty()) {
               result[i] = new MCItemStack(items[i]);
            }
         }

         return result;
      }
   }

   public static IItemStack[] getIItemStacks(List<ItemStack> items) {
      if (items == null) {
         return null;
      } else {
         IItemStack[] result = new IItemStack[items.size()];

         for(int i = 0; i < result.length; ++i) {
            ItemStack itemStack = (ItemStack)items.get(i);
            if (itemStack != null && !itemStack.isEmpty()) {
               result[i] = new MCItemStack(itemStack);
            }
         }

         return result;
      }
   }

   public static IOreDictEntry getOreDict(String name) {
      return new MCOreDictEntry(name);
   }

   public static IPlayer getIPlayer(EntityPlayer player) {
      return player == null ? null : new MCPlayer(player);
   }

   public static EntityPlayer getPlayer(IPlayer player) {
      if (player == null) {
         return null;
      } else if (!(player instanceof MCPlayer)) {
         CraftTweakerAPI.logError("Invalid player: " + player);
         return null;
      } else {
         return ((MCPlayer)player).getInternal();
      }
   }

   public static IData getIData(NBTBase nbt) {
      return nbt == null ? null : NBTConverter.from(nbt, true);
   }

   public static IData getIDataModifyable(NBTBase nbt) {
      return nbt == null ? null : NBTConverter.from(nbt, false);
   }

   public static NBTBase getNBT(IData data) {
      return data == null ? null : NBTConverter.from(data);
   }

   public static NBTTagCompound getNBTCompound(IData data) {
      return data == null ? null : (NBTTagCompound)NBTConverter.from(data);
   }

   public static IBlock getBlock(IBlockAccess blocks, int x, int y, int z) {
      return new MCWorldBlock(blocks, x, y, z);
   }

   public static IBlockDefinition getBlockDefinition(Block block) {
      if (!blockDefinitions.containsKey(block)) {
         blockDefinitions.put(block, new MCBlockDefinition(block));
      }

      return (IBlockDefinition)blockDefinitions.get(block);
   }

   public static IBlock getBlockAnyMeta(Block block) {
      return block == null ? null : new MCSpecificBlock(block, 32767);
   }

   public static IBlock getBlock(Block block, int meta) {
      return block == null ? null : new MCSpecificBlock(block, meta);
   }

   public static Block getBlock(IItemStack itemStack) {
      return itemStack == null ? null : ((MCBlockDefinition)itemStack.asBlock().getDefinition()).getInternalBlock();
   }

   public static Block getBlock(IBlock block) {
      return block == null ? null : ((MCBlockDefinition)block.getDefinition()).getInternalBlock();
   }

   public static Block getBlock(IBlockDefinition blockDefinition) {
      if (blockDefinition instanceof MCBlockDefinition) {
         return ((MCBlockDefinition)blockDefinition).getInternalBlock();
      } else {
         Object block = blockDefinition.getInternal();
         return block instanceof Block ? (Block)block : null;
      }
   }

   public static FluidStack getLiquidStack(ILiquidStack stack) {
      return stack == null ? null : (FluidStack)stack.getInternal();
   }

   public static FluidStack[] getLiquidStacks(ILiquidStack[] stacks) {
      if (stacks == null) {
         return null;
      } else {
         FluidStack[] res = new FluidStack[stacks.length];

         for(int i = 0; i < stacks.length; ++i) {
            ILiquidStack liquidStack = stacks[i];
            res[i] = getLiquidStack(liquidStack);
         }

         return res;
      }
   }

   public static ILiquidStack getILiquidStack(FluidStack stack) {
      return stack == null ? null : new MCLiquidStack(stack);
   }

   public static IOreDictEntry getOreDictEntryFromArray(List array) {
      if (!oreDictArrays.containsKey(array)) {
         for(String ore : OreDictionary.getOreNames()) {
            if (OreDictionary.getOres(ore) == array) {
               oreDictArrays.put(array, CraftTweakerAPI.oreDict.get(ore));
            }
         }
      }

      return (IOreDictEntry)oreDictArrays.get(array);
   }

   public static IIngredient getIIngredient(Object ingredient) {
      if (ingredient == null) {
         return null;
      } else if (ingredient instanceof String) {
         return CraftTweakerAPI.oreDict.get((String)ingredient);
      } else if (ingredient instanceof Item) {
         return getIItemStack(new ItemStack((Item)ingredient, 1, 0));
      } else if (ingredient instanceof ItemStack) {
         return getIItemStack((ItemStack)ingredient);
      } else if (ingredient instanceof List) {
         IOreDictEntry entry = getOreDictEntryFromArray((List)ingredient);
         return (IIngredient)(entry == null ? IngredientUnknown.INSTANCE : entry);
      } else if (ingredient instanceof FluidStack) {
         return new MCLiquidStack((FluidStack)ingredient);
      } else if (ingredient instanceof Ingredient) {
         if (ingredient instanceof OreIngredient) {
            return getOreDict((OreIngredient)ingredient);
         } else if (ingredient instanceof CompoundIngredient) {
            return mergeIngredients((IIngredient[])((CompoundIngredient)ingredient).getChildren().stream().map(CraftTweakerMC::getIIngredient).toArray((x$0) -> {
               return new IIngredient[x$0];
            }));
         } else {
            ItemStack[] matchingStacks = ((Ingredient)ingredient).matchingStacks;
            return ingredient != Ingredient.EMPTY && matchingStacks.length > 0 && !((Ingredient)ingredient).apply(ItemStack.EMPTY) ? mergeIngredients(getIItemStacks(matchingStacks)) : null;
         }
      } else {
         throw new IllegalArgumentException("Not a valid ingredient: " + ingredient);
      }
   }

   public static IIngredient mergeIngredients(IIngredient... ingredients) {
      if (ingredients != null && ingredients.length != 0) {
         IIngredient[] objects = (IIngredient[])Arrays.stream(ingredients).filter((i) -> {
            return i != IngredientUnknown.INSTANCE;
         }).toArray((x$0) -> {
            return new IIngredient[x$0];
         });
         if (objects.length == 0) {
            return null;
         } else {
            return (IIngredient)(objects.length == 1 ? objects[0] : new IngredientOr(objects));
         }
      } else {
         return null;
      }
   }

   public static Ingredient getIngredient(IIngredient ingredient) {
      if (ingredient == null) {
         return Ingredient.EMPTY;
      } else if (ingredient instanceof IOreDictEntry) {
         return new OreIngredient(((IOreDictEntry)ingredient).getName());
      } else if (ingredient instanceof IItemStack) {
         return (Ingredient)(((IItemStack)ingredient).hasTag() && ((IItemStack)ingredient).getMatchTagExact() ? new IngredientNBT(getItemStack(ingredient)) {
         } : Ingredient.fromStacks(new ItemStack[]{getItemStack((IItemStack)ingredient)}));
      } else {
         return new VanillaIngredient(ingredient);
      }
   }

   private static IOreDictEntry getOreDict(OreIngredient oreIngredient) {
      return MCOreDictEntry.getFromIngredient(oreIngredient);
   }

   public static IWorld getWorldByID(int id) {
      return new MCWorld(DimensionManager.getWorld(id));
   }

   public static IWorld getIWorld(World world) {
      return world == null ? null : new MCWorld(world);
   }

   public static World getWorld(IWorld world) {
      return world == null ? null : (World)world.getInternal();
   }

   public static boolean matches(IItemStack iitem, ItemStack stack, boolean wildcardsize) {
      ItemStack internal = ItemStack.EMPTY;
      if (iitem != null) {
         internal = (ItemStack)iitem.getInternal();
         if (stack.hasTagCompound()) {
            return matchesExact(iitem, stack);
         }
      }

      return !internal.isEmpty() && !stack.isEmpty() && internal.getItem() == stack.getItem() && (wildcardsize || internal.getCount() >= stack.getCount()) && (stack.getItemDamage() == 32767 || stack.getItemDamage() == internal.getItemDamage() || !stack.getHasSubtypes() && !stack.getItem().isDamageable());
   }

   public static boolean matches(IItemStack iitem, ItemStack stack) {
      return matches(iitem, stack, true);
   }

   public static boolean matchesExact(IItemStack item, ItemStack stack) {
      ItemStack internal = getItemStack(item);
      if (internal.getTagCompound() != null && stack.getTagCompound() == null) {
         return false;
      } else if (internal.getTagCompound() == null && stack.getTagCompound() != null) {
         return false;
      } else if (internal.getTagCompound() == null) {
         return stack.getItem() == internal.getItem() && (internal.getMetadata() == 32767 || stack.getMetadata() == internal.getMetadata());
      } else {
         if (internal.getTagCompound().getKeySet().equals(stack.getTagCompound().getKeySet())) {
            for(String s : internal.getTagCompound().getKeySet()) {
               if (!internal.getTagCompound().getTag(s).equals(stack.getTagCompound().getTag(s))) {
                  return false;
               }
            }
         }

         return stack.getItem() == internal.getItem() && (internal.getMetadata() == 32767 || stack.getMetadata() == internal.getMetadata());
      }
   }

   public static IBlockState getBlockState(net.minecraft.block.state.IBlockState block) {
      return block == null ? null : new MCBlockState(block);
   }

   public static net.minecraft.block.state.IBlockState getBlockState(IBlockState block) {
      return block == null ? null : (net.minecraft.block.state.IBlockState)block.getInternal();
   }

   public static IEntity getIEntity(Entity entity) {
      if (entity == null) {
         return null;
      } else if (entity instanceof EntityLivingBase) {
         return getIEntityLivingBase((EntityLivingBase)entity);
      } else if (entity instanceof EntityItem) {
         return new MCEntityItem((EntityItem)entity);
      } else if (entity instanceof EntityXPOrb) {
         return new MCEntityXp((EntityXPOrb)entity);
      } else if (entity instanceof EntityFishHook) {
         return new MCEntityFishHook((EntityFishHook)entity);
      } else if (entity instanceof EntityArrow) {
         return getIEntityArrow((EntityArrow)entity);
      } else {
         return (IEntity)(entity instanceof EntityThrowable ? new MCEntityThrowable((EntityThrowable)entity) : new MCEntity(entity));
      }
   }

   public static IEntityXp getIEntityXp(EntityXPOrb entityXPOrb) {
      return entityXPOrb == null ? null : new MCEntityXp(entityXPOrb);
   }

   public static IEntityItem getIEntityItem(EntityItem entityItem) {
      return entityItem == null ? null : new MCEntityItem(entityItem);
   }

   public static Entity getEntity(IEntity entity) {
      return entity == null ? null : (Entity)entity.getInternal();
   }

   public static IEntityLivingBase getIEntityLivingBase(EntityLivingBase entityLivingBase) {
      if (entityLivingBase == null) {
         return null;
      } else if (entityLivingBase instanceof EntityPlayer) {
         return getIPlayer((EntityPlayer)entityLivingBase);
      } else {
         return (IEntityLivingBase)(entityLivingBase instanceof EntityLiving ? getIEntityLiving((EntityLiving)entityLivingBase) : new MCEntityLivingBase(entityLivingBase));
      }
   }

   public static IEntityLiving getIEntityLiving(EntityLiving entityLiving) {
      if (entityLiving == null) {
         return null;
      } else {
         return (IEntityLiving)(entityLiving instanceof EntityCreature ? getIEntityCreature((EntityCreature)entityLiving) : new MCEntityLiving(entityLiving));
      }
   }

   public static IEntityCreature getIEntityCreature(EntityCreature entityCreature) {
      if (entityCreature == null) {
         return null;
      } else if (entityCreature instanceof EntityAgeable) {
         return getIEntityAgeable((EntityAgeable)entityCreature);
      } else {
         return (IEntityCreature)(entityCreature instanceof EntityMob ? getIEntityMob((EntityMob)entityCreature) : new MCEntityCreature(entityCreature));
      }
   }

   public static IEntityAgeable getIEntityAgeable(EntityAgeable entityAgeable) {
      if (entityAgeable == null) {
         return null;
      } else {
         return (IEntityAgeable)(entityAgeable instanceof EntityAnimal ? getIEntityAnimal((EntityAnimal)entityAgeable) : new MCEntityAgeable(entityAgeable));
      }
   }

   public static IEntityAnimal getIEntityAnimal(EntityAnimal entityAnimal) {
      return entityAnimal == null ? null : new MCEntityAnimal(entityAnimal);
   }

   public static IEntityMob getIEntityMob(EntityMob entityMob) {
      return entityMob == null ? null : new MCEntityMob(entityMob);
   }

   public static IBlockPos getIBlockPos(BlockPos pos) {
      return pos == null ? null : new MCBlockPos(pos);
   }

   public static BlockPos getBlockPos(IBlockPos pos) {
      return pos == null ? null : (BlockPos)pos.getInternal();
   }

   public static ITeam getITeam(Team team) {
      return team == null ? null : new MCTeam(team);
   }

   public static Team getTeam(ITeam team) {
      return team == null ? null : (Team)team.getInternal();
   }

   public static IDamageSource getIDamageSource(DamageSource source) {
      return source == null ? null : new MCDamageSource(source);
   }

   public static DamageSource getDamageSource(IDamageSource source) {
      return source == null ? null : (DamageSource)source.getInternal();
   }

   public static IMaterial getIMaterial(Material material) {
      return material == null ? null : new MCMaterial(material);
   }

   public static Material getMaterial(IMaterial material) {
      return material == null ? null : (Material)material.getInternal();
   }

   public static EntityAnimal getEntityAnimal(IEntityAnimal entityAnimal) {
      return entityAnimal == null ? null : (EntityAnimal)entityAnimal.getInternal();
   }

   public static IEntityEquipmentSlot getIEntityEquipmentSlot(EntityEquipmentSlot slot) {
      return slot == null ? null : new MCEntityEquipmentSlot(slot);
   }

   public static EntityEquipmentSlot getEntityEquipmentSlot(IEntityEquipmentSlot slot) {
      return slot == null ? null : (EntityEquipmentSlot)slot.getInternal();
   }

   public static EntityLivingBase getEntityLivingBase(IEntityLivingBase entityLivingBase) {
      return entityLivingBase == null ? null : (EntityLivingBase)entityLivingBase.getInternal();
   }

   public static IPotion getIPotion(Potion potion) {
      return potion == null ? null : new MCPotion(potion);
   }

   public static Potion getPotion(IPotion potion) {
      return potion == null ? null : (Potion)potion.getInternal();
   }

   public static IPotionEffect getIPotionEffect(PotionEffect potionEffect) {
      return potionEffect == null ? null : new MCPotionEffect(potionEffect);
   }

   public static PotionEffect getPotionEffect(IPotionEffect potionEffect) {
      return potionEffect == null ? null : (PotionEffect)potionEffect.getInternal();
   }

   public static IIngredient[] getIIngredients(List<Ingredient> ingredientList) {
      if (ingredientList == null) {
         return new IIngredient[0];
      } else {
         IIngredient[] out = new IIngredient[ingredientList.size()];

         for(int index = 0; index < out.length; ++index) {
            out[index] = getIIngredient(ingredientList.get(index));
         }

         return out;
      }
   }

   public static EntityItem getEntityItem(IEntityItem entityItem) {
      return entityItem == null ? null : (EntityItem)entityItem.getInternal();
   }

   public static IRayTraceResult getIRayTraceResult(RayTraceResult rayTraceResult) {
      return rayTraceResult == null ? null : new MCRayTraceResult(rayTraceResult);
   }

   public static RayTraceResult getRayTraceResult(IRayTraceResult rayTraceResult) {
      return rayTraceResult == null ? null : (RayTraceResult)rayTraceResult.getInternal();
   }

   public static IContainer getIContainer(Container container) {
      return container == null ? null : new MCContainer(container);
   }

   public static Container getContainer(IContainer container) {
      return container == null ? null : (Container)container.getInternal();
   }

   public static IFacing getIFacing(EnumFacing sideHit) {
      return sideHit == null ? null : new MCFacing(sideHit);
   }

   public static CreativeTabs getCreativeTabs(ICreativeTab tab) {
      return tab == null ? null : (CreativeTabs)tab.getInternal();
   }

   public static ICommandSender getICommandSender(crafttweaker.api.command.ICommandSender sender) {
      return sender == null ? null : (ICommandSender)sender.getInternal();
   }

   public static crafttweaker.api.command.ICommandSender getICommandSender(ICommandSender sender) {
      if (sender instanceof Entity) {
         return getIEntity((Entity)sender);
      } else {
         return sender == null ? null : new MCCommandSender(sender);
      }
   }

   public static ICommand getICommand(net.minecraft.command.ICommand iCommand) {
      return iCommand == null ? null : new MCCommand(iCommand);
   }

   public static net.minecraft.command.ICommand getICommand(ICommand command) {
      return command == null ? null : (net.minecraft.command.ICommand)command.getInternal();
   }

   public static MinecraftServer getMCServer(IServer server) {
      return server == null ? null : (MinecraftServer)server.getInternal();
   }

   public static Vec3d getVec3d(IVector3d vector3d) {
      return vector3d == null ? null : (Vec3d)vector3d.getInternal();
   }

   public static IVector3d getIVector3d(Vec3d vec3d) {
      return vec3d == null ? null : new MCVector3d(vec3d);
   }

   public static List<IItemStack> getIItemStackList(List<ItemStack> items) {
      List<IItemStack> out = new ArrayList();
      if (items != null && !items.isEmpty()) {
         for(ItemStack itemStack : items) {
            IItemStack stack = getIItemStack(itemStack);
            if (stack != null) {
               out.add(stack);
            }
         }

         return out;
      } else {
         return out;
      }
   }

   public static List<IItemStack> getIItemStacks(Collection<ItemStack> items) {
      List<IItemStack> convertedOutputs = new ArrayList();
      if (items != null && !items.isEmpty()) {
         for(ItemStack itemStack : items) {
            IItemStack stack = getIItemStack(itemStack);
            if (stack != null && !stack.isEmpty()) {
               convertedOutputs.add(stack);
            }
         }
      }

      return convertedOutputs;
   }

   public static List<WeightedItemStack> getWeightedItemStackList(List<ItemStack> items) {
      List<WeightedItemStack> out = new ArrayList();
      if (items != null && !items.isEmpty()) {
         for(ItemStack itemStack : items) {
            IItemStack stack = getIItemStack(itemStack);
            if (stack != null) {
               out.add(new WeightedItemStack(stack, 100.0F));
            }
         }

         return out;
      } else {
         return out;
      }
   }

   public static Fluid getFluid(ILiquidDefinition liquidDefinition) {
      return liquidDefinition == null ? null : (Fluid)liquidDefinition.getInternal();
   }

   public static ILiquidDefinition getILiquidDefinition(Fluid fluid) {
      return fluid == null ? null : new MCLiquidDefinition(fluid);
   }

   public static Biome getBiome(IBiome biome) {
      return biome == null ? null : (biome instanceof MCBiome ? ((MCBiome)biome).getInternal() : (Biome)Biome.REGISTRY.getObject(new ResourceLocation(biome.getId())));
   }

   public static IExplosion getIExplosion(Explosion explosion) {
      return explosion == null ? null : new MCExplosion(explosion);
   }

   public static Explosion getExplosion(IExplosion explosion) {
      return explosion instanceof MCExplosion ? (Explosion)explosion.getInternal() : null;
   }

   public static IEntityFishHook getIEntityFishHook(EntityFishHook entity) {
      return entity == null ? null : new MCEntityFishHook(entity);
   }

   public static IEntityAttributeInstance getAttributeInstance(IAttributeInstance instance) {
      return instance == null ? null : new MCEntityAttributeInstance(instance);
   }

   public static IAttributeInstance getAttributeInstance(IEntityAttributeInstance instance) {
      return instance == null ? null : (IAttributeInstance)instance.getInternal();
   }

   public static AttributeModifier getAttributeModifier(IEntityAttributeModifier modifier) {
      return modifier == null ? null : (AttributeModifier)modifier.getInternal();
   }

   public static IEntityAttributeModifier getIEntityAttributeModifier(AttributeModifier modifier) {
      return modifier == null ? null : new MCEntityAttributeModifier(modifier);
   }

   public static IEntityArrow getIEntityArrow(EntityArrow entity) {
      if (entity == null) {
         return null;
      } else {
         return (IEntityArrow)(entity instanceof EntityTippedArrow ? new MCEntityArrowTipped((EntityTippedArrow)entity) : new MCEntityArrow(entity));
      }
   }

   public static IEntityArrowTipped getIEntityArrowTipped(EntityTippedArrow entity) {
      return entity == null ? null : new MCEntityArrowTipped(entity);
   }

   public static IEntityThrowable getIEntityThrowable(EntityThrowable entity) {
      return entity == null ? null : new MCEntityThrowable(entity);
   }

   public static IPotionEffect[] getIPotionEffects(List<PotionEffect> potionEffects) {
      if (potionEffects == null) {
         return null;
      } else {
         IPotionEffect[] result = new IPotionEffect[potionEffects.size()];

         for(int i = 0; i < result.length; ++i) {
            PotionEffect potionEffect = (PotionEffect)potionEffects.get(i);
            if (potionEffect != null) {
               result[i] = new MCPotionEffect(potionEffect);
            }
         }

         return result;
      }
   }

   public static PotionType getPotionType(IPotionType potionType) {
      return potionType == null ? null : (PotionType)potionType.getInternal();
   }

   public static IPotionType getIPotionType(PotionType potionType) {
      return potionType == null ? null : new MCPotionType(potionType);
   }

   public static AxisAlignedBB getAxisAlignedBB(IAxisAlignedBB aabb) {
      return aabb == null ? null : (AxisAlignedBB)aabb.getInternal();
   }

   public static IAxisAlignedBB getIAxisAlignedBB(AxisAlignedBB aabb) {
      return aabb == null ? null : new MCAxisAlignedBB(aabb);
   }

   public static IEntityAttribute getIEntityAttribute(IAttribute attribute) {
      return attribute == null ? null : new MCEntityAttribute(attribute);
   }

   public static IEntityAttributeInstance getIEntityAttributeInstance(IAttributeInstance attribute) {
      return attribute == null ? null : new MCEntityAttributeInstance(attribute);
   }

   public static EnumHand getHand(IEntityEquipmentSlot hand) {
      if (hand == null) {
         return null;
      } else {
         switch(getEntityEquipmentSlot(hand)) {
         case MAINHAND:
            return EnumHand.MAIN_HAND;
         case OFFHAND:
            return EnumHand.OFF_HAND;
         default:
            return null;
         }
      }
   }

   public static IEntityEquipmentSlot getIEntityEquipmentSlot(EnumHand hand) {
      if (hand == null) {
         return null;
      } else {
         switch(hand) {
         case MAIN_HAND:
            return ExpandEntityEquipmentSlot.mainHand();
         case OFF_HAND:
            return ExpandEntityEquipmentSlot.offhand();
         default:
            return null;
         }
      }
   }

   public static EnumFacing getFacing(IFacing facing) {
      return facing == null ? null : (EnumFacing)facing.getInternal();
   }

   public static Item getItem(IItemDefinition itemDefinition) {
      return itemDefinition == null ? null : (Item)itemDefinition.getInternal();
   }

   public static Type getBiomeType(IBiomeType biomeType) {
      return biomeType == null ? null : (Type)biomeType.getInternal();
   }

   public static IStyle getIStyle(Style style) {
      return style == null ? null : new MCStyle(style);
   }

   public static Style getStyle(IStyle style) {
      return style == null ? null : (Style)style.getInternal();
   }

   public static ITextComponent getITextComponent(net.minecraft.util.text.ITextComponent textComponent) {
      return textComponent == null ? null : new MCTextComponent(textComponent);
   }

   public static net.minecraft.util.text.ITextComponent getITextComponent(ITextComponent textComponent) {
      return textComponent == null ? null : (net.minecraft.util.text.ITextComponent)textComponent.getInternal();
   }

   public static IBlockSource getBlockSource(net.minecraft.dispenser.IBlockSource blockSource) {
      return blockSource == null ? null : new MCBlockSource(blockSource);
   }

   public static net.minecraft.dispenser.IBlockSource getBlockSource(IBlockSource blockSource) {
      return blockSource == null ? null : (net.minecraft.dispenser.IBlockSource)blockSource.getInternal();
   }

   static {
      for(int i = 0; i < Biome.REGISTRY.getKeys().size(); ++i) {
         if (Biome.REGISTRY.getObjectById(i) != null) {
            biomes[i] = new MCBiome((Biome)Biome.REGISTRY.getObjectById(i));
         }
      }

      for(CreativeTabs tab : CreativeTabs.CREATIVE_TAB_ARRAY) {
         if (tab != null) {
            String label = tab.tabLabel;
            creativeTabs.put(label, new MCCreativeTab(tab, label));
         }
      }

   }
}
