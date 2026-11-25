package org.orecruncher.dsurround.capabilities.speech;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public class SpeechData implements ISpeechData {
   private final List<SpeechBubbleData> data = new ArrayList();
   private final ObjectArray<String> preppedList = new ObjectArray();
   private RenderContext ctx;

   public SpeechData() {
   }

   public void addMessage(@Nonnull String string, int ticksTTL) {
      SpeechBubbleData data = new SpeechBubbleData(string, EnvironStateHandler.EnvironState.getTickCounter() + ticksTTL);
      this.data.add(data);
      this.generateTextForRender();
   }

   public void onUpdate(int currentTick) {
      int oldSize = this.data.size();
      if (oldSize > 0) {
         this.data.removeIf((d) -> {
            return d.isExpired(currentTick);
         });
         if (oldSize != this.data.size()) {
            this.generateTextForRender();
         }
      }

   }

   public ObjectArray<String> getText() {
      return this.preppedList;
   }

   @Nullable
   public RenderContext getRenderContext() {
      return this.ctx;
   }

   protected void generateTextForRender() {
      this.preppedList.clear();

      for(SpeechBubbleData entry : this.data) {
         this.preppedList.addAll(entry.getText());
      }

      this.ctx = new RenderContext(this.preppedList);
   }
}
