package reborncore.common.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import reborncore.common.util.ItemUtils;

public class CommandListRecipes extends CommandBase {
   public CommandListRecipes() {
   }

   public String getName() {
      return "rc_listRecipes";
   }

   public String getUsage(ICommandSender sender) {
      return "rc_listRecipes";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (!(sender instanceof EntityPlayer)) {
         sender.sendMessage(new TextComponentString("Command must be ran as a player!"));
      } else {
         EntityPlayer player = (EntityPlayer)sender;
         if (player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
            sender.sendMessage(new TextComponentString("You must be holding an item in your main hand"));
         } else {
            List<IRecipe> recipeList = new ArrayList();
            CraftingManager.REGISTRY.forEach((value) -> {
               if (value instanceof IRecipe && ItemUtils.isItemEqual(value.getRecipeOutput(), player.getHeldItem(EnumHand.MAIN_HAND), true, true, true)) {
                  recipeList.add(value);
               }

            });
            if (recipeList.isEmpty()) {
               sender.sendMessage(new TextComponentString("No recipes found. Does this item have a recipe? Is this item using the vanilla crafting manager?"));
            } else {
               sender.sendMessage(new TextComponentString("Found " + recipeList.size() + " recipes"));
               recipeList.forEach((recipe) -> {
                  sender.sendMessage(new TextComponentString(recipe.getRegistryName().toString()));
               });
            }
         }
      }
   }
}
