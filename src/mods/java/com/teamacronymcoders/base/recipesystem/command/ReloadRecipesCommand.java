package com.teamacronymcoders.base.recipesystem.command;

import com.teamacronymcoders.base.recipesystem.RecipeSystem;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class ReloadRecipesCommand extends CommandBase {
   public ReloadRecipesCommand() {
   }

   @Nonnull
   public String getName() {
      return "reload";
   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public String getUsage(ICommandSender sender) {
      return "Reloads the Recipe System";
   }

   @ParametersAreNonnullByDefault
   public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
      RecipeSystem.reloadRecipe();
   }
}
