import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Exercise3 {

  public static void main(String[] args) {
    System.out.println(stockPairs(List.of(1, 3, 46, 1, 3, 9), 47));   // = 1
    System.out.println(stockPairs(List.of(6, 6, 3, 9, 3, 5, 1), 12)); // = 2
  }

  public static int stockPairs(List<Integer> stocksProfit, long target)  {
    int distinctPairs = 0;
    Set<Integer> uniqueValues = new HashSet<>(stocksProfit);

    for (Integer stock : uniqueValues) {
      if (uniqueValues.contains((int) target - stock) && target != 2L * stock) {
        distinctPairs += 1;
      }
    }

    if (target % 2 == 0 && Collections.frequency(stocksProfit, (int) target / 2) > 1) {
      distinctPairs += 2;
    }

    return distinctPairs / 2;
  }
}
