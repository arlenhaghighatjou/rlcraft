package mcjty.lostcities.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import mcjty.lostcities.dimensions.world.LostCityChunkGenerator;
import mcjty.lostcities.dimensions.world.WorldTypeTools;
import mcjty.lostcities.dimensions.world.lost.BuildingInfo;
import mcjty.lostcities.dimensions.world.lost.cityassets.BuildingPart;
import mcjty.lostcities.dimensions.world.lost.cityassets.Palette;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.StringUtils;

public class CommandExportBuilding implements ICommand {
   public CommandExportBuilding() {
   }

   public String getName() {
      return "lc_exportbuilding";
   }

   public String getUsage(ICommandSender sender) {
      return this.getName() + " <file> <floors>";
   }

   public List<String> getAliases() {
      return Collections.emptyList();
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      try {
         int floors = Integer.parseInt(args[1]);
         JsonArray array = new JsonArray();
         EntityPlayer player = (EntityPlayer)sender;
         BlockPos start = player.getPosition().down();
         String palettechars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
         int idx = 0;
         Map<IBlockState, Character> mapping = new HashMap();
         Palette palette = new Palette("give_name");
         LostCityChunkGenerator provider = WorldTypeTools.getChunkGenerator(sender.getEntityWorld().provider.getDimension());
         BuildingInfo info = BuildingInfo.getBuildingInfo(start.getX() >> 4, start.getZ() >> 4, provider);

         for(Character character : info.getCompiledPalette().getCharacters()) {
            IBlockState state = info.getCompiledPalette().getStraight(character.charValue());
            if (state != null) {
               palette.addMapping(character.charValue(), state);
               mapping.put(state, character);
            }
         }

         for(int f = 0; f < floors; ++f) {
            CommandExportBuilding.Slice[] slices = new CommandExportBuilding.Slice[6];

            for(int y = 0; y < 6; ++y) {
               slices[y] = new CommandExportBuilding.Slice();
            }

            int cx = (start.getX() >> 4) * 16;
            int cy = start.getY() + f * 6;
            int cz = (start.getZ() >> 4) * 16;
            MutableBlockPos pos = new MutableBlockPos(cx, cy, cz);

            for(int x = 0; x < 16; ++x) {
               for(int z = 0; z < 16; ++z) {
                  for(int y = 0; y < 6; ++y) {
                     pos.setPos(cx + x, cy + y, cz + z);
                     IBlockState state = server.getEntityWorld().getBlockState(pos);
                     Character character = (Character)mapping.get(state);
                     if (character == null) {
                        while(true) {
                           character = state.getBlock() == Blocks.AIR ? ' ' : palettechars.charAt(idx);
                           ++idx;
                           if (!palette.getPalette().containsKey(character)) {
                              break;
                           }
                        }

                        palette.addMapping(character.charValue(), state);
                        mapping.put(state, character);
                     }

                     slices[y].sequence[z * 16 + x] = "" + character;
                  }
               }
            }

            String[] sl = new String[6];

            for(int i = 0; i < 6; ++i) {
               sl[i] = StringUtils.join(slices[i].sequence);
            }

            BuildingPart part = new BuildingPart("p" + f, 16, 16, sl);
            array.add(part.writeToJSon());
         }

         array.add(palette.writeToJSon());
         Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
         PrintWriter writer = new PrintWriter(new File(args[0]));
         Throwable var44 = null;

         try {
            writer.print(gson.toJson(array));
            writer.flush();
         } catch (Throwable var34) {
            var44 = var34;
            throw var34;
         } finally {
            if (writer != null) {
               if (var44 != null) {
                  try {
                     writer.close();
                  } catch (Throwable var33) {
                     var44.addSuppressed(var33);
                  }
               } else {
                  writer.close();
               }
            }

         }
      } catch (FileNotFoundException var36) {
         sender.sendMessage(new TextComponentString("Error writing to file '" + args[0] + "'!"));
      } catch (Exception var37) {
         var37.printStackTrace();
      }

   }

   public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      return true;
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      return Collections.emptyList();
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return false;
   }

   public int compareTo(ICommand o) {
      return this.getName().compareTo(o.getName());
   }

   public static class Slice {
      String[] sequence = new String[256];

      public Slice() {
      }
   }
}
