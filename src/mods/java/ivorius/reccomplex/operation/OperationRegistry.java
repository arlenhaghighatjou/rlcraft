package ivorius.reccomplex.operation;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.lax1dude.eaglercraft.v1_8.netty.Unpooled;
import ivorius.ivtoolkit.lang.IvClasses;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.capability.RCEntityInfo;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.utils.RCPacketBuffer;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class OperationRegistry {
   private static BiMap<String, Class<? extends Operation>> operations = HashBiMap.create();

   public OperationRegistry() {
   }

   public static void register(String id, Class<? extends Operation> operation) {
      operations.put(id, operation);
   }

   @Nullable
   public static Operation readOperation(@Nonnull NBTTagCompound compound) {
      String opID = compound.getString("opID");
      Class<? extends Operation> clazz = (Class)operations.get(opID);
      if (clazz == null) {
         RecurrentComplex.logger.error(String.format("Unrecognized Operation ID '%s'", opID));
         return null;
      } else {
         return (Operation)NBTCompoundObjects.read(compound, () -> {
            return (Operation)IvClasses.instantiate(clazz);
         });
      }
   }

   public static NBTTagCompound writeOperation(@Nonnull Operation operation) {
      NBTTagCompound compound = NBTCompoundObjects.write(operation);
      compound.setString("opID", (String)operations.inverse().get(operation.getClass()));
      return compound;
   }

   public static boolean queueOperation(Operation operation, ICommandSender commandSender) throws CommandException {
      if (operation.checkDead(commandSender)) {
         return false;
      } else {
         boolean instant = true;
         if (commandSender instanceof EntityPlayer) {
            EntityPlayer player = CommandBase.getCommandSenderAsPlayer(commandSender);
            RCEntityInfo info = RCEntityInfo.get(player, (EnumFacing)null);
            if (info != null && info.getPreviewType() != Operation.PreviewType.NONE) {
               info.queueOperation(operation, player);
               instant = false;
               ITextComponent confirmComponent = new TextComponentString("/" + RCCommands.confirm.getName());
               confirmComponent.getStyle().setColor(TextFormatting.GREEN);
               confirmComponent.getStyle().setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/" + RCCommands.confirm.getName()));
               confirmComponent.getStyle().setHoverEvent(new HoverEvent(net.minecraft.util.text.event.HoverEvent.Action.SHOW_TEXT, RecurrentComplex.translations.get("commands.rcconfirm.run")));
               ITextComponent cancelComponent = new TextComponentString("/" + RCCommands.cancel.getName());
               cancelComponent.getStyle().setColor(TextFormatting.RED);
               cancelComponent.getStyle().setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/" + RCCommands.cancel.getName()));
               cancelComponent.getStyle().setHoverEvent(new HoverEvent(net.minecraft.util.text.event.HoverEvent.Action.SHOW_TEXT, RecurrentComplex.translations.get("commands.rccancel.run")));
               commandSender.sendMessage(RecurrentComplex.translations.format("commands.rc.queuedOp", confirmComponent, cancelComponent));
            }
         }

         if (instant) {
            operation.perform((WorldServer)commandSender.getEntityWorld());
         }

         return true;
      }
   }

   @Nullable
   public static NBTTagCompound dummyOperation(@Nonnull Operation operation, int level) {
      if (level == 0) {
         return writeOperation(operation);
      } else {
         if (operation instanceof OperationGenerateStructure) {
            OperationGenerateStructure genStructure = (OperationGenerateStructure)operation;
            if (level == 1 && genStructure.structure != null) {
               IvWorldData dummyWorldData = genStructure.structure.constructWorldData();
               dummyWorldData.entities.clear();
               dummyWorldData.tileEntities.clear();

               for(BlockPos pos : dummyWorldData.blockCollection.area()) {
                  dummyWorldData.blockCollection.setBlockState(pos, (dummyWorldData.blockCollection.getBlockState(pos).isNormalCube() ? Blocks.STONE : Blocks.AIR).getDefaultState());
               }

               GenericStructure dummyStructure = new GenericStructure();
               dummyStructure.worldDataCompound = dummyWorldData.createTagCompound();
               return writeOperation(genStructure.dummy(dummyStructure));
            }

            if (level == 2) {
               return writeOperation(new OperationClearArea(genStructure.generationArea()));
            }
         }

         return null;
      }
   }

   public static void writeBestPreview(ByteBuf buffer, Operation operation) {
      if (operation == null) {
         ByteBufUtils.writeTag(buffer, (NBTTagCompound)null);
      } else {
         int level = 0;

         NBTTagCompound nbt;
         while((nbt = dummyOperation(operation, level++)) != null) {
            if (canSend(nbt)) {
               (new RCPacketBuffer(buffer)).writeCompoundTag(nbt);
               return;
            }
         }

         (new RCPacketBuffer(buffer)).writeCompoundTag((NBTTagCompound)null);
      }
   }

   protected static boolean canSend(NBTTagCompound danglingNBT) {
      ByteBuf temp = Unpooled.buffer();
      (new RCPacketBuffer(temp)).writeCompoundTag(danglingNBT);
      return canSend(temp);
   }

   protected static boolean canSend(ByteBuf temp) {
      return temp.writerIndex() <= 838860;
   }
}
