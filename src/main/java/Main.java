import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        double[] b = new double[]{2, 1, 4, 2, 3};
        task(b);
    }

    /**
     * @param a – массив чисел, длина массива может быть больше 10млн.
     * @return массив чисел, в которых нeту дубликатов.
     * Порядок чисел в оригинальном массиве должен быть сохранён.
     * Из дубликатов нужно оставлять последний элемент, например, для {2,1,4,2,3} правильное решение - {1,4,2,3}, а не {2,1,4,3}

    В случае, если во входном массиве есть элемент меньше 0, то нужно выдавать ошибку.
    Например, для {2,3,-1,5} обработка должна закончиться ошибкой.
     */
    public static double[] task(double[] a) throws Exception {
        Set<Double> targetSet = new LinkedHashSet<>();
        for (int i = a.length-1; i > 0; i--)
        {
            if (a[i] < 0){
                throw new Exception("Найден элемент меньше нуля");
            }
            else {
                targetSet.add(a[i]);
            }
        }
        //targetSet.stream().mapToDouble(num -> num).toArray(); - можно свапнуть
        Object[] tmp = targetSet.toArray();
        double[] b = new double[tmp.length];
        for (int i = tmp.length; i > 0; i--){
            b[tmp.length-i] = (double) tmp[i-1];
        }
//
//        Set<Double> targetSet2 = new HashSet<>();
//        for (int i = a.length-1; i > 0; i--){
//            if (a[i] < 0){
//                throw new Exception("Найден элемент меньше нуля");
//            }
//            if (targetSet2.contains((Double) a[i]) ){
//                a[i] = -1;
//            }else{
//                targetSet2.add((Double) a[i]) ;
//            }
//        }
        //Arrays.stream(a).filter(a -> a > 0).toArray();

        return b;
    }



}






