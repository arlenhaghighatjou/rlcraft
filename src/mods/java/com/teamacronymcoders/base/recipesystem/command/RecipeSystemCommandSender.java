package com.teamacronymcoders.base.recipesystem.command;

import com.teamacronymcoders.base.recipesystem.RecipeContainer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class RecipeSystemCommandSender implements ICommandSender {
   private final RecipeContainer recipeContainer;

   public RecipeSystemCommandSender(RecipeContainer recipeContainer) {
      this.recipeContainer = recipeContainer;
   }

   @Nonnull
   public String getName() {
      return "RecipeSystem";
   }

   public boolean canUseCommand(int permLevel, @Nonnull String commandName) {
      return true;
   }

   @Nonnull
   public World getEntityWorld() {
      return this.recipeContainer.getWorld();
   }

   @Nullable
   public MinecraftServer getServer() {
      return this.recipeContainer.getWorld().getMinecraftServer();
   }
}
