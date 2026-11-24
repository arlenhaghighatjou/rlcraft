package net.minecraft.item.crafting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.Reader;
import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.World;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class CraftingManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static int nextAvailableId;
    public static final RegistryNamespaced<ResourceLocation, IRecipe> REGISTRY = net.minecraftforge.registries.GameData.getWrapper(IRecipe.class);

    public static boolean init()
    {
        try
        {
            register("armordye", new RecipesArmorDyes());
            register("bookcloning", new RecipeBookCloning());
            register("mapcloning", new RecipesMapCloning());
            register("mapextending", new RecipesMapExtending());
            register("fireworks", new RecipeFireworks());
            register("repairitem", new RecipeRepairItem());
            register("tippedarrow", new RecipeTippedArrow());
            register("bannerduplicate", new RecipesBanners.RecipeDuplicatePattern());
            register("banneraddpattern", new RecipesBanners.RecipeAddPattern());
            register("shielddecoration", new ShieldRecipes.Decoration());
            register("shulkerboxcoloring", new ShulkerBoxRecipes.ShulkerBoxColoring());
            return parseJsonRecipes();
        }
        catch (Throwable var1)
        {
            return false;
        }
    }

	private static boolean parseJsonRecipes() {
		String directory = "/assets/minecraft/recipes/";
		String index = net.lax1dude.eaglercraft.v1_8.EagRuntime.getResourceString(directory + "_files.txt");
		if (index == null) {
			LOGGER.error("Could not find the Minecraft recipe index");
			return false;
		}
		Gson gson = new GsonBuilder().create();
		for (String file : index.split("\n")) {
			file = file.trim();
			if (file.isEmpty()) {
				continue;
			}
			try (Reader reader = new java.io.InputStreamReader(
					net.lax1dude.eaglercraft.v1_8.EagRuntime.getRequiredResourceStream(directory + file),
					java.nio.charset.StandardCharsets.UTF_8)) {
				register(file.substring(0, file.length() - ".json".length()),
					parseRecipeJson(JsonUtils.fromJson(gson, reader, JsonObject.class)));
			} catch (JsonParseException | IOException ex) {
				LOGGER.error("Could not load Minecraft recipe " + file, ex);
				return false;
			}
		}
		return true;
	}

    private static IRecipe parseRecipeJson(JsonObject p_193376_0_)
    {
        String s = JsonUtils.getString(p_193376_0_, "type");

        if ("crafting_shaped".equals(s))
        {
            return ShapedRecipes.deserialize(p_193376_0_);
        }
        else if ("crafting_shapeless".equals(s))
        {
            return ShapelessRecipes.deserialize(p_193376_0_);
        }
        else
        {
            throw new JsonSyntaxException("Invalid or unsupported recipe type '" + s + "'");
        }
    }

    //Forge: Made private use GameData/Registry events!
    private static void register(String name, IRecipe recipe)
    {
        register(new ResourceLocation(name), recipe);
    }

    //Forge: Made private use GameData/Registry events!
    private static void register(ResourceLocation name, IRecipe recipe)
    {
        if (REGISTRY.containsKey(name))
        {
            throw new IllegalStateException("Duplicate recipe ignored with ID " + name);
        }
        else
        {
            REGISTRY.register(nextAvailableId++, name, recipe);
        }
    }

    public static ItemStack findMatchingResult(InventoryCrafting craftMatrix, World worldIn)
    {
        for (IRecipe irecipe : REGISTRY)
        {
            if (irecipe.matches(craftMatrix, worldIn))
            {
                return irecipe.getCraftingResult(craftMatrix);
            }
        }

        return ItemStack.EMPTY;
    }

    @Nullable
    public static IRecipe findMatchingRecipe(InventoryCrafting craftMatrix, World worldIn)
    {
        for (IRecipe irecipe : REGISTRY)
        {
            if (irecipe.matches(craftMatrix, worldIn))
            {
                return irecipe;
            }
        }

        return null;
    }

    public static NonNullList<ItemStack> getRemainingItems(InventoryCrafting craftMatrix, World worldIn)
    {
        for (IRecipe irecipe : REGISTRY)
        {
            if (irecipe.matches(craftMatrix, worldIn))
            {
                return irecipe.getRemainingItems(craftMatrix);
            }
        }

        NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(craftMatrix.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i)
        {
            nonnulllist.set(i, craftMatrix.getStackInSlot(i));
        }

        return nonnulllist;
    }

    @Nullable
    public static IRecipe getRecipe(ResourceLocation name)
    {
        return REGISTRY.getObject(name);
    }

    @Deprecated //DO NOT USE THIS
    public static int getIDForRecipe(IRecipe recipe)
    {
        return REGISTRY.getIDForObject(recipe);
    }

    @Deprecated //DO NOT USE THIS
    @Nullable
    public static IRecipe getRecipeById(int id)
    {
        return REGISTRY.getObjectById(id);
    }
}
