package mcjty.fxcontrol.commands;

import java.util.Iterator;
import mcjty.tools.varia.LookAtTools;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

public class CmdDumpBlockNBT extends CommandBase {
   public CmdDumpBlockNBT() {
   }

   public String getName() {
      return "fctrldumpblock";
   }

   public String getUsage(ICommandSender sender) {
      return "fctrldumpblock";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (sender instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)sender;
         RayTraceResult result = LookAtTools.getMovingObjectPositionFromPlayer(player.getEntityWorld(), player, false);
         if (result != null && result.typeOfHit == Type.BLOCK) {
            IBlockState state = player.getEntityWorld().getBlockState(result.getBlockPos());
            sender.sendMessage(new TextComponentString(TextFormatting.GOLD + state.getBlock().getRegistryName().toString()));

            for(IProperty<?> key : state.getPropertyKeys()) {
               String value = state.getValue(key).toString();
               sender.sendMessage(new TextComponentString("    " + key.getName() + " = " + value));
            }
         }
      } else {
         sender.sendMessage(new TextComponentString(TextFormatting.RED + "This can only be done for a player!"));
      }

   }

   private static void dumpNBT(ICommandSender sender, int indent, NBTTagCompound nbt) {
      label37:
      for(String key : nbt.getKeySet()) {
         NBTBase base = nbt.getTag(key);
         byte id = base.getId();
         switch(id) {
         case 1:
            sender.sendMessage(new TextComponentString(StringUtils.repeat(' ', indent) + "(Byte) " + key + " = " + nbt.getByte(key)));
            continue;
         case 2:
            sender.sendMessage(new TextComponentString(StringUtils.repeat(' ', indent) + "(Short) " + key + " = " + nbt.getShort(key)));
            continue;
         case 3:
            sender.sendMessage(new TextComponentString(StringUtils.repeat(' ', indent) + "(Int) " + key + " = " + nbt.getInteger(key)));
            continue;
         case 4:
            sender.sendMessage(new TextComponentString(StringUtils.repeat(' ', indent) + "(Long) " + key + " = " + nbt.getLong(key)));
            continue;
         case 5:
            sender.sendMessage(new TextComponentString(StringUtils.repeat(' ', indent) + "(Float) " + key + " = " + nbt.getFloat(key)));
            continue;
         case 6:
            sender.sendMessage(new TextComponentString(StringUtils.repeat(' ', indent) + "(Double) " + key + " = " + nbt.getDouble(key)));
            continue;
         case 7:
         default:
            sender.sendMessage(new TextComponentString(StringUtils.repeat(' ', indent) + "(?) " + key));
            continue;
         case 8:
            sender.sendMessage(new TextComponentString(StringUtils.repeat(' ', indent) + "(String) " + key + " = " + nbt.getString(key)));
            continue;
         case 9:
            sender.sendMessage(new TextComponentString(StringUtils.repeat(' ', indent) + "(List) " + key));
            NBTBase b = nbt.getTag(key);
            if (((NBTTagList)b).getTagType() != 10) {
               continue;
            }

            NBTTagList list = nbt.getTagList(key, 10);
            int idx = 0;
            Iterator var10 = list.iterator();

            while(true) {
               if (!var10.hasNext()) {
                  continue label37;
               }

               NBTBase bs = (NBTBase)var10.next();
               sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + StringUtils.repeat(' ', indent + 2) + "Index " + idx));
               ++idx;
               dumpNBT(sender, indent + 4, (NBTTagCompound)bs);
            }
         case 10:
         }

         sender.sendMessage(new TextComponentString(StringUtils.repeat(' ', indent) + "(NBT) " + key));
         dumpNBT(sender, indent + 2, nbt.getCompoundTag(key));
      }

   }
}
