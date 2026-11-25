package invtweaks;

import java.awt.Point;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InvTweaksConfigSortingRule implements Comparable<InvTweaksConfigSortingRule> {
   private static final Pattern constraintVertical = Pattern.compile("v", 82);
   private static final Pattern constraintReverse = Pattern.compile("r", 82);
   private String constraint;
   @Nullable
   private int[] preferredPositions;
   private String keyword;
   private InvTweaksConfigSortingRuleType type;
   private int priority;
   private int containerSize;
   private int containerRowSize;

   public InvTweaksConfigSortingRule(@NotNull InvTweaksItemTree tree, String constraint_, String keyword_, int containerSize_, int containerRowSize_) {
      this.keyword = keyword_;
      this.constraint = constraint_;
      this.containerSize = containerSize_;
      this.containerRowSize = containerRowSize_;
      this.type = getRuleType(this.constraint, this.containerRowSize);
      this.preferredPositions = this.getRulePreferredPositions(this.constraint);
      this.priority = this.type.getLowestPriority() + 100000 + tree.getKeywordDepth(this.keyword) * 1000 - tree.getKeywordOrder(this.keyword);
   }

   @Nullable
   public static int[] getRulePreferredPositions(@NotNull String constraint, int containerSize, int containerRowSize) {
      int[] result = null;
      int containerColumnSize = containerSize / containerRowSize;
      if (constraint.length() >= 5) {
         boolean vertical = false;
         Matcher verticalMatcher = constraintVertical.matcher(constraint);
         if (verticalMatcher.find()) {
            vertical = true;
            constraint = verticalMatcher.reset().replaceAll("");
         }

         String[] elements = constraint.split("-");
         if (elements.length == 2) {
            int[] slots1 = getRulePreferredPositions(elements[0], containerSize, containerRowSize);
            int[] slots2 = getRulePreferredPositions(elements[1], containerSize, containerRowSize);
            if (slots1.length == 1 && slots2.length == 1) {
               int slot1 = slots1[0];
               int slot2 = slots2[0];
               Point point1 = new Point(slot1 % containerRowSize, slot1 / containerRowSize);
               Point point2 = new Point(slot2 % containerRowSize, slot2 / containerRowSize);
               result = new int[(Math.abs(point2.y - point1.y) + 1) * (Math.abs(point2.x - point1.x) + 1)];
               int resultIndex = 0;
               if (vertical) {
                  for(Point p : new Point[]{point1, point2}) {
                     int buffer = p.x;
                     p.x = p.y;
                     p.y = buffer;
                  }
               }

               int y = point1.y;

               while(true) {
                  if (point1.y < point2.y) {
                     if (y > point2.y) {
                        break;
                     }
                  } else if (y < point2.y) {
                     break;
                  }

                  int x = point1.x;

                  while(true) {
                     if (point1.x < point2.x) {
                        if (x > point2.x) {
                           break;
                        }
                     } else if (x < point2.x) {
                        break;
                     }

                     result[resultIndex++] = vertical ? index(containerRowSize, x, y) : index(containerRowSize, y, x);
                     x += point1.x < point2.x ? 1 : -1;
                  }

                  y += point1.y < point2.y ? 1 : -1;
               }

               if (constraintReverse.matcher(constraint).find()) {
                  reverseArray(result);
               }
            }
         }
      } else {
         int column = -1;
         int row = -1;
         boolean reverse = false;

         for(int i = 0; i < constraint.length(); ++i) {
            char c = constraint.charAt(i);
            int digitValue = Character.digit(c, 36);
            if (digitValue >= 1 && digitValue <= containerRowSize && digitValue < 10) {
               column = digitValue - 1;
            } else if (digitValue >= 10 && digitValue - 10 <= containerColumnSize) {
               row = digitValue - 10;
            } else if (charEqualsIgnoreCase(c, 'r')) {
               reverse = true;
            }
         }

         if (column != -1 && row != -1) {
            result = new int[]{index(containerRowSize, row, column)};
         } else if (row != -1) {
            result = new int[containerRowSize];

            for(int i = 0; i < containerRowSize; ++i) {
               result[i] = index(containerRowSize, row, reverse ? containerRowSize - 1 - i : i);
            }
         } else {
            result = new int[containerColumnSize];

            for(int i = 0; i < containerColumnSize; ++i) {
               result[i] = index(containerRowSize, reverse ? i : containerColumnSize - 1 - i, column);
            }
         }
      }

      if (result == null) {
         InvTweaks.logInGameStatic("InvTweaks Config: Rule Constraint \"" + constraint + "\" was unable to be correctly determined.");
      }

      return result;
   }

   @NotNull
   public static InvTweaksConfigSortingRuleType getRuleType(@NotNull String constraint, int rowSize) {
      InvTweaksConfigSortingRuleType result = InvTweaksConfigSortingRuleType.SLOT;
      if (constraint.length() == 1 || constraint.length() == 2 && constraintReverse.matcher(constraint).find()) {
         constraint = constraintReverse.matcher(constraint).replaceAll("");
         int digitValue = Character.digit(constraint.charAt(0), 10);
         if (digitValue >= 1 && digitValue <= rowSize) {
            result = InvTweaksConfigSortingRuleType.COLUMN;
         } else {
            result = InvTweaksConfigSortingRuleType.ROW;
         }
      } else if (constraint.length() > 4) {
         if (charEqualsIgnoreCase(constraint.charAt(1), constraint.charAt(4))) {
            result = InvTweaksConfigSortingRuleType.COLUMN;
         } else if (charEqualsIgnoreCase(constraint.charAt(0), constraint.charAt(3))) {
            result = InvTweaksConfigSortingRuleType.ROW;
         } else {
            result = InvTweaksConfigSortingRuleType.RECTANGLE;
         }
      }

      return result;
   }

   private static boolean charEqualsIgnoreCase(char a, char b) {
      char aU = Character.toUpperCase(a);
      char bU = Character.toUpperCase(b);
      return aU == bU || Character.toLowerCase(aU) == Character.toLowerCase(bU);
   }

   private static int index(int rowSize, int row, int column) {
      return row * rowSize + column;
   }

   private static void reverseArray(@NotNull int[] data) {
      int left = 0;

      for(int right = data.length - 1; left < right; --right) {
         int temp = data[left];
         data[left] = data[right];
         data[right] = temp;
         ++left;
      }

   }

   public InvTweaksConfigSortingRuleType getType() {
      return this.type;
   }

   @Nullable
   public int[] getPreferredSlots() {
      return this.preferredPositions;
   }

   public String getKeyword() {
      return this.keyword;
   }

   public int getPriority() {
      return this.priority;
   }

   public int getContainerSize() {
      return this.containerSize;
   }

   public int compareTo(@NotNull InvTweaksConfigSortingRule o) {
      return this.priority - o.priority;
   }

   @Nullable
   public int[] getRulePreferredPositions(@NotNull String constraint) {
      return getRulePreferredPositions(constraint, this.containerSize, this.containerRowSize);
   }

   @NotNull
   public String toString() {
      return this.constraint + " " + this.keyword;
   }
}
