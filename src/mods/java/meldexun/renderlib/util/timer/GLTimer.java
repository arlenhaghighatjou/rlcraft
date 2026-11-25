package meldexun.renderlib.util.timer;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import meldexun.renderlib.util.GLQueryResult;
import meldexun.renderlib.util.Pair;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;

public class GLTimer extends Timer {
   private final GLTimer.Frame[] results;
   private GLQueryResult start;

   public GLTimer(String name, int maxResultCount) {
      super(name, maxResultCount);
      this.results = (GLTimer.Frame[])IntStream.range(0, maxResultCount).mapToObj((i) -> {
         return new GLTimer.Frame();
      }).toArray((x$0) -> {
         return new GLTimer.Frame[x$0];
      });
   }

   protected void updateInternal() {
      this.results[this.frame].reset();
   }

   protected void startInternal() {
      this.start = timestamp();
   }

   protected void stopInternal() {
      this.results[this.frame].addPending(this.start, timestamp());
   }

   public LongStream results() {
      return IntStream.range(0, this.results.length).filter((i) -> {
         return i != this.frame;
      }).mapToObj((i) -> {
         return this.results[i];
      }).filter(GLTimer.Frame::ready).mapToLong(GLTimer.Frame::result);
   }

   private static GLQueryResult timestamp() {
      int q = GL15.glGenQueries();
      GL33.glQueryCounter(q, 36392);
      return new GLQueryResult(q);
   }

   private static class Frame {
      private final List<Pair<GLQueryResult, GLQueryResult>> pending;
      private long time;

      private Frame() {
         this.pending = new LinkedList();
      }

      public void addPending(GLQueryResult start, GLQueryResult end) {
         this.pending.add(new Pair(start, end));
      }

      public void reset() {
         this.pending.forEach((pair) -> {
            ((GLQueryResult)pair.getLeft()).dispose();
            ((GLQueryResult)pair.getRight()).dispose();
         });
         this.pending.clear();
         this.time = 0L;
      }

      public void checkPending() {
         this.pending.removeIf((pair) -> {
            if (((GLQueryResult)pair.getLeft()).ready() && ((GLQueryResult)pair.getRight()).ready()) {
               this.time += ((GLQueryResult)pair.getRight()).time() - ((GLQueryResult)pair.getLeft()).time();
               ((GLQueryResult)pair.getLeft()).dispose();
               ((GLQueryResult)pair.getRight()).dispose();
               return true;
            } else {
               return false;
            }
         });
      }

      public boolean ready() {
         this.checkPending();
         return this.pending.isEmpty();
      }

      public long result() {
         return this.time;
      }
   }
}
