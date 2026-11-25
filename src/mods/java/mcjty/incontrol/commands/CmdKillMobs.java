package mcjty.incontrol.commands;

import com.google.common.base.Predicate;
import java.util.List;
import mcjty.incontrol.InControl;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

public class CmdKillMobs extends CommandBase {
   public CmdKillMobs() {
   }

   public static String findEntityIdByClass(Class<? extends Entity> clazz) {
      ResourceLocation key = EntityList.getKey(clazz);
      return key == null ? null : key.toString();
   }

   public String getName() {
      return "ctrlkill";
   }

   public String getUsage(ICommandSender sender) {
      return "ctrlkill";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length <= 0) {
         sender.sendMessage(new TextComponentString(TextFormatting.RED + "Use 'all', 'passive', 'hostile' or name of the mob followed by optional dimension id"));
         InControl.setup.getLogger().error("Use 'all', 'passive', 'hostile', 'entity' or name of the mob followed by optional dimension id");
      } else {
         int dimension = sender instanceof EntityPlayer ? sender.getEntityWorld().provider.getDimension() : 0;
         if (args.length > 1) {
            dimension = Integer.parseInt(args[1]);
         }

         String arg0 = args[0].toLowerCase();
         boolean all = "all".equals(arg0);
         boolean passive = "passive".equals(arg0);
         boolean hostile = "hostile".equals(arg0);
         boolean entity = "entity".equals(arg0);
         WorldServer worldServer = server.getWorld(dimension);
         List<Entity> entities = worldServer.getEntities(Entity.class, (input) -> {
            if (all) {
               return !(input instanceof EntityPlayer);
            } else if (passive) {
               return input instanceof IAnimals && !(input instanceof IMob);
            } else if (hostile) {
               return input instanceof IMob;
            } else if (!entity) {
               String id = findEntityIdByClass(input.getClass());
               return arg0.equals(id);
            } else {
               return !(input instanceof IAnimals) && !(input instanceof EntityPlayer);
            }
         });

         for(Entity e : entities) {
            worldServer.removeEntity(e);
         }

         sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Removed " + entities.size() + " entities!"));
      }
   }
}
