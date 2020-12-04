import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class grila {
    public static void main(String[] args) {
        List<Integer> myList = new ArrayList<Integer>(Arrays.asList(1,6,9,4,2,0,-12,24));
        List<String> mySecondList = new ArrayList<String>(Arrays.asList("some","problems","are","easier","than","they","seem","!"));
        System.out.println(Algorithm.myFunction(myList,0,8));
        System.out.println(Algorithm.myFunction(mySecondList,0,8));
    }

}
final class Algorithm {
    public static <T extends Comparable<? super T>> T myFunction(List<? extends T> list, int begin, int end) {
        T element = list.get(begin);
        for (++begin; begin < end; ++begin)
            if (element.compareTo(list.get(begin)) < 0)
                element = list.get(begin);
        return element;
    }
}
