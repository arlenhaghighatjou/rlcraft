package vazkii.quark.base.network.message;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraftforge.common.ForgeHooks;
import vazkii.arl.network.TileEntityMessage;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.misc.feature.NoteBlockInterface;

public class MessageTuneNoteBlock extends TileEntityMessage<TileEntityNote> {
   public boolean next;
   public byte target;

   public MessageTuneNoteBlock() {
   }

   public MessageTuneNoteBlock(TileEntityNote note, boolean next, byte target) {
      super(note.getPos());
      this.next = next;
      this.target = target;
   }

   public Runnable getAction() {
      return () -> {
         if (ModuleLoader.isFeatureEnabled(NoteBlockInterface.class)) {
            EntityPlayer player = this.context.getServerHandler().player;
            byte old = ((TileEntityNote)this.tile).note;
            boolean tuned = false;
            if (this.next) {
               ((TileEntityNote)this.tile).changePitch();
            } else {
               ((TileEntityNote)this.tile).note = this.target;
               if (ForgeHooks.onNoteChange((TileEntityNote)this.tile, old)) {
                  ((TileEntityNote)this.tile).markDirty();
                  player.addStat(StatList.NOTEBLOCK_TUNED);
                  tuned = true;
               }
            }

            if (!tuned) {
               player.addStat(StatList.NOTEBLOCK_PLAYED);
            }

            ((TileEntityNote)this.tile).triggerNote(((TileEntityNote)this.tile).getWorld(), this.pos);
         }
      };
   }
}
