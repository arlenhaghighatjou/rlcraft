package com.dhanantry.scapeandrunparasites.network;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class SRPCommand implements ICommand {
   private final List aliases = new ArrayList();
   protected String fullEntityName;
   protected Entity conjuredEntity;

   public SRPCommand() {
      this.aliases.add("conjure");
      this.aliases.add("conj");
   }

   public int compareTo(ICommand arg0) {
      return 0;
   }

   public String getName() {
      return "conjure";
   }

   public String getUsage(ICommandSender sender) {
      return "conjure <text>";
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] argString) throws CommandException {
      World world = sender.getEntityWorld();
      if (!world.isRemote) {
         if (argString.length == 0) {
            sender.sendMessage(new TextComponentString("Invalid argument"));
            return;
         }

         sender.sendMessage(new TextComponentString("Conjuring: [" + argString[0] + "]"));
         this.fullEntityName = "srparasites:" + argString[0];
         ResourceLocation ccc = new ResourceLocation(this.fullEntityName);
         if (EntityList.isRegistered(ccc)) {
            this.conjuredEntity = EntityList.createEntityByIDFromName(ccc, world);
            this.conjuredEntity.copyLocationAndAnglesFrom(sender.getCommandSenderEntity());
            world.spawnEntity(this.conjuredEntity);
         } else {
            sender.sendMessage(new TextComponentString("Entity not found"));
         }
      }

   }

   public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      return sender.canUseCommand(2, this.getName());
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
      List<String> atm = new ArrayList();
      atm.add("sentry");
      atm.add("carrier_heavy");
      return atm;
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return false;
   }
}
