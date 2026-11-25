package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.event.INoteBlockEvent")
@ZenRegister
public interface INoteBlockEvent extends IBlockEvent {
   @ZenGetter("note")
   @ZenMethod
   String getNote();

   @ZenSetter("note")
   @ZenMethod
   void setNote(String var1);

   @ZenGetter("octave")
   @ZenMethod
   String getOctave();

   @ZenSetter("octave")
   @ZenMethod
   void setOctave(String var1);

   @ZenMethod
   void setBoth(String var1, String var2);

   @ZenGetter("noteId")
   @ZenMethod
   int getNoteId();
}
