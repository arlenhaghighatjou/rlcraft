package org.jwaresoftware.mcmods.lib.recipes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.IngredientNBT;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jwaresoftware.mcmods.lib.ModIntegration;
import org.jwaresoftware.mcmods.lib.SharedGlue;

public final class JR {
   public static final int C_TBD = 1;
   public static final int C_VFP = 2;
   public static final int C_HCB = 4;
   public static final int C_ORE = 8;
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   static final FastDateFormat STAMP_FORMAT = FastDateFormat.getInstance("yyyyMMdd'T'HHmmss.SSZ");
   private static final ThreadLocal<JR.ModDef> _current_modDef = new ThreadLocal();
   private static final File RECIPES_DIR = new File("C:/Temp/JR");
   private static final int _ANY_TYPE = 32767;

   public JR() {
   }

   public static final void setCurrentMod(@Nonnull String modid) {
      _current_modDef.set(new JR.ModDef(modid));
   }

   public static final void setCurrentMod(@Nonnull String modid, @Nonnull Map<String, String> rmap) {
      _current_modDef.set(new JR.ModDef(modid));
      ((JR.ModDef)_current_modDef.get()).rconstants = rmap;
   }

   public static final void disableLegacyMode() {
      ((JR.ModDef)_current_modDef.get()).legacymode = false;
   }

   public static final void clrCurrentMod() {
      _current_modDef.set((Object)null);
   }

   private static File getModDir(JR.ModDef mod) {
      File modDir = new File(new File(RECIPES_DIR, mod.modid), mod.stamp);
      if (!modDir.exists() && !modDir.mkdirs()) {
         throw new RuntimeException("Cannot mkdir " + modDir);
      } else {
         return modDir;
      }
   }

   public static final IRecipe newShapedOreRecipe(@Nullable IForgeRegistry<IRecipe> recipes, @Nonnull ResourceLocation rid, int flags, @Nonnull ItemStack result, Object... recipe) {
      Validate.notNull(rid, "Non-null recipe id required", new Object[0]);
      JR.ModDef mod = (JR.ModDef)_current_modDef.get();
      if (mod != null) {
         File modDir = getModDir(mod);
         Map<String, Object> json = new LinkedHashMap();
         conditions(mod, flags, json);
         json.put("result", serialize(mod, result, 1));
         json.put("type", "forge:ore_shaped");
         int keych0 = 0;
         ArrayList<String> pattern = new ArrayList();

         for(Object ro : recipe) {
            if (!(ro instanceof String)) {
               break;
            }

            pattern.add(ro.toString());
            ++keych0;
         }

         json.put("pattern", pattern);
         Map<String, Object> keymap = new LinkedHashMap();

         for(int i = keych0; i < recipe.length; i += 2) {
            keymap.put(recipe[i].toString(), serialize(mod, recipe[i + 1], 0));
         }

         json.put("key", keymap);
         File f = new File(modDir, rid.getResourcePath() + ".json");

         try {
            FileWriter w = new FileWriter(f);
            Throwable var31 = null;

            try {
               GSON.toJson(json, w);
            } catch (Throwable var23) {
               var31 = var23;
               throw var23;
            } finally {
               if (w != null) {
                  if (var31 != null) {
                     try {
                        w.close();
                     } catch (Throwable var22) {
                        var31.addSuppressed(var22);
                     }
                  } else {
                     w.close();
                  }
               }

            }
         } catch (IOException var25) {
            throw new RuntimeException(var25);
         }
      }

      ShapedOreRecipe r = new ShapedOreRecipe((ResourceLocation)null, result, recipe);
      r.setRegistryName(rid);
      if (recipes != null && (mod == null || mod.legacymode)) {
         recipes.register(r);
      }

      return r;
   }

   public static final IRecipe newShapedOreRecipe(@Nonnull ResourceLocation rid, int flags, @Nonnull ItemStack result, Object... recipe) {
      return newShapedOreRecipe((IForgeRegistry)null, rid, flags, result, recipe);
   }

   public static final IRecipe newShapedOreRecipe(@Nonnull ResourceLocation rid, @Nonnull ItemStack result, Object... recipe) {
      return newShapedOreRecipe((IForgeRegistry)null, rid, 0, result, recipe);
   }

   public static final IRecipe newShapelessOreRecipe(@Nullable IForgeRegistry<IRecipe> recipes, @Nonnull ResourceLocation rid, int flags, @Nonnull ItemStack result, Object... recipe) {
      Validate.notNull(rid, "Non-null recipe id required", new Object[0]);
      JR.ModDef mod = (JR.ModDef)_current_modDef.get();
      if (mod != null) {
         File modDir = getModDir(mod);
         Map<String, Object> json = new LinkedHashMap();
         conditions(mod, flags, json);
         json.put("result", serialize(mod, result, 1));
         json.put("type", "forge:ore_shapeless");
         ArrayList<Map<String, Object>> inglist = new ArrayList();

         for(Object ro : recipe) {
            inglist.add(serialize(mod, ro, 0));
         }

         json.put("ingredients", inglist);
         File f = new File(modDir, rid.getResourcePath() + ".json");

         try {
            FileWriter w = new FileWriter(f);
            Throwable var27 = null;

            try {
               GSON.toJson(json, w);
            } catch (Throwable var21) {
               var27 = var21;
               throw var21;
            } finally {
               if (w != null) {
                  if (var27 != null) {
                     try {
                        w.close();
                     } catch (Throwable var20) {
                        var27.addSuppressed(var20);
                     }
                  } else {
                     w.close();
                  }
               }

            }
         } catch (IOException var23) {
            throw new RuntimeException(var23);
         }
      }

      ShapelessOreRecipe r = new ShapelessOreRecipe((ResourceLocation)null, result, recipe);
      r.setRegistryName(rid);
      if (recipes != null && (mod == null || mod.legacymode)) {
         recipes.register(r);
      }

      return r;
   }

   public static final IRecipe newShapelessOreRecipe(@Nonnull ResourceLocation rid, int flags, @Nonnull ItemStack result, Object... recipe) {
      return newShapelessOreRecipe((IForgeRegistry)null, rid, flags, result, recipe);
   }

   public static final IRecipe newShapelessOreRecipe(@Nonnull ResourceLocation rid, @Nonnull ItemStack result, Object... recipe) {
      return newShapelessOreRecipe((IForgeRegistry)null, rid, 0, result, recipe);
   }

   private static Map<String, Object> serialize(JR.ModDef mod, Object o, int flags) {
      if (o instanceof Block) {
         return serialize(mod, new ItemStack((Block)o, 1, flags <= 0 ? 32767 : 0), flags);
      } else if (o instanceof Item) {
         return serialize(mod, new ItemStack((Item)o), flags);
      } else if (!(o instanceof ItemStack)) {
         if (o instanceof String) {
            Map<String, Object> ret = new LinkedHashMap();
            String oreid = o.toString();
            String mapid = mod.rconstant(oreid);
            if (mapid != null) {
               ret.put("item", "#" + mapid);
            } else {
               ret.put("type", "forge:ore_dict");
               ret.put("ore", oreid);
            }

            return ret;
         } else {
            throw new IllegalArgumentException("Object not a block, item, stack, or oredict name: " + o);
         }
      } else {
         ItemStack stack = (ItemStack)o;
         Map<String, Object> ret = new LinkedHashMap();
         ret.put("item", stack.getItem().getRegistryName().toString());
         if (stack.getItem().getHasSubtypes() || stack.getItemDamage() > 0) {
            ret.put("data", Integer.valueOf(stack.getItemDamage()));
         }

         if (flags > 0 && stack.getCount() > 1) {
            ret.put("count", Integer.valueOf(stack.getCount()));
         }

         if (flags > 0 && stack.hasTagCompound()) {
            ret.put("nbt", String.valueOf(stack.getTagCompound()));
         }

         return ret;
      }
   }

   private static void conditions(JR.ModDef mod, int flags, Map<String, Object> json) {
      if (flags > 0) {
         ArrayList<Map<String, Object>> conditions = new ArrayList();
         Map<String, Object> c = new LinkedHashMap();
         c.put("type", "forge:mod_loaded");
         if ((flags & 2) == 2) {
            c.put("modid", "vanillafoodpantry");
         } else if ((flags & 4) == 4) {
            c.put("modid", "superblocks");
         } else if ((flags & 8) == 8) {
            c.put("type", mod.modid + ":oreid_exists");
            c.put("ore", "*FIXME*");
         } else {
            c.put("modid", "*FIXME*");
         }

         conditions.add(c);
         json.put("conditions", conditions);
      }

   }

   public static final ShapedPrimer primerFrom(@Nonnull ShapedOreRecipe template) {
      ShapedPrimer p = new ShapedPrimer();
      p.height = template.getRecipeHeight();
      p.width = template.getRecipeWidth();
      p.input = template.getIngredients();
      return p;
   }

   public static final NonNullList<Ingredient> parseShapelessIngredients(JsonContext context, JsonObject json, String templateString, String emptyErrString) {
      NonNullList<Ingredient> ingredients = NonNullList.create();

      for(JsonElement ingredient : JsonUtils.getJsonArray(json, templateString)) {
         ingredients.add(CraftingHelper.getIngredient(ingredient, context));
      }

      if (ingredients.isEmpty()) {
         throw new JsonParseException(emptyErrString);
      } else {
         return ingredients;
      }
   }

   public static final NonNullList<Ingredient> parseShapelessIngredients(JsonContext context, JsonObject json, String emptyErrString) {
      return parseShapelessIngredients(context, json, "template", emptyErrString);
   }

   public static final class BucketFluidIngredient extends IngredientNBT {
      private final FluidStack _match;

      public BucketFluidIngredient(ItemStack bucket_sample) {
         super(bucket_sample);
         Validate.isTrue(!bucket_sample.isEmpty());
         FluidStack f = FluidUtil.getFluidContained(bucket_sample);
         if (f == null) {
            throw new IllegalArgumentException("Unable to find fluid from input stack: " + bucket_sample);
         } else {
            this._match = f;
         }
      }

      public BucketFluidIngredient(ItemStack mod_bucket, @Nonnull FluidStack f) {
         super(mod_bucket);
         Validate.isTrue(!mod_bucket.isEmpty());
         Validate.notNull(f, "A non-null fluid stack is required", new Object[0]);
         this._match = f;
      }

      public boolean apply(@Nullable ItemStack input) {
         return ModIntegration.isBucketOf(input, this._match) ? true : super.apply(input);
      }

      public boolean isSimple() {
         return false;
      }
   }

   public static final class CompoundIngredientNBTSensitive extends CompoundIngredient {
      public CompoundIngredientNBTSensitive(Collection<Ingredient> children) {
         super(children);
      }
   }

   public abstract static class FilledUniversalBucketIngredientFactory implements IIngredientFactory {
      static final Gson GSON = (new GsonBuilder()).disableHtmlEscaping().create();

      public FilledUniversalBucketIngredientFactory() {
      }

      public static final Ingredient parse(ModIntegration modid, JsonContext context, JsonObject json) {
         Ingredient fallback_bucket = null;
         if (json.has("mod_bucket")) {
            JsonElement e = json.get("mod_bucket");
            if (e.isJsonObject()) {
               ItemStack owned_bucket = CraftingHelper.getItemStack(e.getAsJsonObject(), context);
               if (!owned_bucket.isEmpty()) {
                  fallback_bucket = new JR.IngredientNBTSensitive(owned_bucket);
               }
            } else {
               String fallback_def = e.getAsString();
               if (fallback_def.charAt(0) == '#') {
                  fallback_bucket = new OreIngredient(fallback_def.substring(1));
               } else {
                  ItemStack owned_bucket = modid.itemstack(fallback_def);
                  if (!owned_bucket.isEmpty()) {
                     fallback_bucket = new JR.IngredientNBTSensitive(owned_bucket);
                  }
               }
            }
         }

         String fluidName = JsonUtils.getString(json, "fluid");
         Fluid fluid = FluidRegistry.getFluid(fluidName);
         if (fluid == null && fallback_bucket != null) {
            return fallback_bucket;
         } else if (fluid == null) {
            throw new JsonSyntaxException("Unregistered fluid '" + fluidName + "'");
         } else {
            FluidStack contents = new FluidStack(fluid, 1000);
            if (json.has("nbt")) {
               try {
                  JsonElement e = json.get("nbt");
                  NBTTagCompound nbt;
                  if (e.isJsonObject()) {
                     nbt = JsonToNBT.getTagFromJson(GSON.toJson(e));
                  } else {
                     nbt = JsonToNBT.getTagFromJson(e.getAsString());
                  }

                  contents = new FluidStack(fluid, 1000, nbt);
               } catch (Throwable var10) {
                  var10.printStackTrace();
                  throw new JsonSyntaxException("Unable to read fluid NBT because '" + var10.getMessage() + "'");
               }
            }

            ItemStack filled_bucket = FluidUtil.getFilledBucket(contents);
            if (filled_bucket.isEmpty()) {
               if (fallback_bucket != null) {
                  return fallback_bucket;
               } else {
                  throw new JsonSyntaxException("No bucket found for fluid '" + fluidName + "'");
               }
            } else {
               Ingredient out = null;
               if (fallback_bucket != null) {
                  NonNullList<Ingredient> both = NonNullList.create();
                  both.add(fallback_bucket);
                  both.add(new JR.BucketFluidIngredient(filled_bucket, contents));
                  out = new JR.CompoundIngredientNBTSensitive(both);
               } else {
                  out = new JR.BucketFluidIngredient(filled_bucket, contents);
               }

               return out;
            }
         }
      }
   }

   public static final class IngredientNBTSensitive extends IngredientNBT {
      public IngredientNBTSensitive(ItemStack stack) {
         super(stack);
      }
   }

   public static class LegacyShapelessOreRecipe extends ShapelessOreRecipe {
      protected LegacyShapelessOreRecipe(@Nullable ResourceLocation group, @Nonnull ItemStack result, Object... recipe) {
         super(group, result, recipe);
      }

      public LegacyShapelessOreRecipe(@Nullable ResourceLocation group, @Nonnull ShapelessOreRecipe template) {
         super(group, template.getIngredients(), template.getRecipeOutput());
      }

      protected LegacyShapelessOreRecipe(@Nullable ResourceLocation group, NonNullList<Ingredient> input, @Nonnull ItemStack result) {
         super(group, input, result);
      }

      public boolean matches(InventoryCrafting grid, World world) {
         boolean m = super.matches(grid, world);
         if (!m) {
            m = this.old_matches_only_if_you_have_to(grid, world);
         }

         return m;
      }

      protected final boolean old_matches_only_if_you_have_to(InventoryCrafting grid, World world) {
         NonNullList<Ingredient> required = NonNullList.create();
         required.addAll(this.input);

         for(int i = 0; i < grid.getSizeInventory(); ++i) {
            ItemStack slot = grid.getStackInSlot(i);
            if (!slot.isEmpty()) {
               boolean inRecipe = false;
               Iterator<Ingredient> req = required.iterator();

               while(req.hasNext()) {
                  if (((Ingredient)req.next()).apply(slot)) {
                     inRecipe = true;
                     req.remove();
                     break;
                  }
               }

               if (!inRecipe) {
                  return false;
               }
            }
         }

         return required.isEmpty();
      }
   }

   public static final class ModDef {
      public final String modid;
      public final String stamp;
      public boolean legacymode = true;
      public Map<String, String> rconstants = Collections.emptyMap();

      ModDef(String id) {
         this.modid = id;
         this.stamp = JR.STAMP_FORMAT.format(System.currentTimeMillis());
      }

      @Nullable
      String rconstant(String oreid) {
         return (String)this.rconstants.get(oreid);
      }
   }

   public abstract static class NoLeftoversShapelessOreRecipe extends JR.LegacyShapelessOreRecipe {
      public NoLeftoversShapelessOreRecipe(@Nullable ResourceLocation group, @Nonnull ItemStack result, Object... recipe) {
         super(group, result, recipe);
      }

      public NoLeftoversShapelessOreRecipe(@Nullable ResourceLocation group, @Nonnull ShapelessOreRecipe template) {
         super(group, template.getIngredients(), template.getRecipeOutput());
      }

      public NoLeftoversShapelessOreRecipe(@Nullable ResourceLocation group, NonNullList<Ingredient> input, @Nonnull ItemStack result) {
         super(group, input, result);
      }

      public NonNullList<ItemStack> getRemainingItems(InventoryCrafting grid) {
         return SharedGlue.emptyNonNullItemStackList(grid.getSizeInventory());
      }
   }
}
