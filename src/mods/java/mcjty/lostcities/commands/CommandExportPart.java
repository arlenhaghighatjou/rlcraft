package mcjty.lostcities.commands;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.nio.charset.StandardCharsets;
import java.io.OutputStreamWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import java.io.PrintWriter;
import java.util.ArrayList;
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

public class CommandExportPart implements ICommand {
   public CommandExportPart() {
   }

   public String getName() {
      return "lc_exportpart";
   }

   public String getUsage(ICommandSender sender) {
      return this.getName() + " <file> <slices>";
   }

   public List<String> getAliases() {
      return Collections.emptyList();
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      try {
         int cntSlices = Integer.parseInt(args[1]);
         JsonArray array = new JsonArray();
         EntityPlayer player = (EntityPlayer)sender;
         BlockPos start = player.getPosition().down();
         String palettechars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+-=[]{}\\|`~:;',./<>?";
         int idx = 0;
         Map<IBlockState, Character> mapping = new HashMap();
         Palette palette = new Palette("old");
         Palette paletteNew = new Palette("new");
         LostCityChunkGenerator provider = WorldTypeTools.getChunkGenerator(sender.getEntityWorld().provider.getDimension());
         BuildingInfo info = BuildingInfo.getBuildingInfo(start.getX() >> 4, start.getZ() >> 4, provider);

         for(Character character : info.getCompiledPalette().getCharacters()) {
            IBlockState state = info.getCompiledPalette().getStraight(character.charValue());
            if (state != null) {
               palette.addMapping(character.charValue(), state);
               mapping.put(state, character);
            }
         }

         List<CommandExportPart.Slice> slices = new ArrayList();

         for(int f = 0; f < cntSlices; ++f) {
            CommandExportPart.Slice slice = new CommandExportPart.Slice();
            slices.add(slice);
            int cx = (start.getX() >> 4) * 16;
            int cy = start.getY() + f;
            int cz = (start.getZ() >> 4) * 16;
            MutableBlockPos pos = new MutableBlockPos(cx, cy, cz);

            for(int x = 0; x < 16; ++x) {
               for(int z = 0; z < 16; ++z) {
                  pos.setPos(cx + x, cy, cz + z);
                  IBlockState state = server.getEntityWorld().getBlockState(pos);
                  Character character = (Character)mapping.get(state);
                  if (character == null) {
                     while(true) {
                        character = state.getBlock() == Blocks.AIR ? ' ' : palettechars.charAt(idx);
                        ++idx;
                        if (!palette.getPalette().containsKey(character) && !paletteNew.getPalette().containsKey(character)) {
                           break;
                        }
                     }

                     paletteNew.addMapping(character.charValue(), state);
                     mapping.put(state, character);
                  }

                  slice.sequence[z * 16 + x] = "" + character;
               }
            }
         }

         String[] sl = new String[cntSlices];

         for(int i = 0; i < cntSlices; ++i) {
            sl[i] = StringUtils.join(((CommandExportPart.Slice)slices.get(i)).sequence);
         }

         BuildingPart part = new BuildingPart("part", 16, 16, sl);
         array.add(part.writeToJSon());
         array.add(palette.writeToJSon());
         array.add(paletteNew.writeToJSon());
         Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
         PrintWriter writer = new PrintWriter(new OutputStreamWriter((new VFile2(args[0])).getOutputStream(), StandardCharsets.UTF_8));
         Throwable var47 = null;

         try {
            writer.print(gson.toJson(array));
            writer.flush();
         } catch (Throwable var35) {
            var47 = var35;
            throw var35;
         } finally {
            if (writer != null) {
               if (var47 != null) {
                  try {
                     writer.close();
                  } catch (Throwable var34) {
                     var47.addSuppressed(var34);
                  }
               } else {
                  writer.close();
               }
            }

         }
      } catch (Exception var38) {
         var38.printStackTrace();
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
