package com.teamacronymcoders.base.recipesystem.output;

import com.teamacronymcoders.base.recipesystem.RecipeContainer;
import com.teamacronymcoders.base.recipesystem.command.RecipeSystemCommandSender;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.server.MinecraftServer;

public class CommandOutput implements IOutput {
   private final String command;

   public CommandOutput(String command) {
      this.command = command;
   }

   public boolean canOutput(RecipeContainer recipeContainer) {
      return true;
   }

   public void output(RecipeContainer recipeContainer) {
      Optional.ofNullable(recipeContainer.getWorld().getMinecraftServer()).map(MinecraftServer::getCommandManager).ifPresent((iCommandManager) -> {
         iCommandManager.executeCommand(new RecipeSystemCommandSender(recipeContainer), this.command);
      });
   }
}
