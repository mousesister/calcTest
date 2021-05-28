package newCalc;


import java.util.*;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;

interface Expr {
}

class Number implements Expr {
    int val;
    String type;

    Number() {
    }

    public int get() {
        return val;
    }

    protected Number(int v, String t) {
        val = v;
        type = t;
    }
}

class Decimal extends Number {
    public Decimal(String src) throws Exception {

        int arabic = Integer.parseInt(src);
        if (arabic < 1 || arabic > 10) {
            throw new Exception("Out of range");
        }
        this.val = arabic;
        this.type = "arabic";
    }
}

class Roman extends Number {

    static int parse(String src) throws Exception {
        String upperCased = src.toUpperCase();
        String[] vals = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        for (int i = 0; i < vals.length; ++i)
            if (vals[i].equals(upperCased))
                return i + 1;


        throw new Exception();
    }

    public Roman(String src) throws Exception {
        super(parse(src), "roman");
    }
}


enum RomanNumeral {
    I(1), IV(4), V(5), IX(9), X(10),
    XL(40), L(50), XC(90), C(100),
    CD(400), D(500), CM(900), M(1000);

    private final int value;

    RomanNumeral(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static List<RomanNumeral> getReverseSortedValues() {
        return Arrays.stream(values())
                .sorted(Comparator.comparing((RomanNumeral e) -> e.value).reversed())
                .collect(Collectors.toList());
    }
}

class Op implements Expr {
    Number lhs;
    Number rhs;
    IntBinaryOperator op;
    String type;

    public int get() {
        return op.applyAsInt(lhs.get(), rhs.get());
    }

    public Op(Number l, Number r, IntBinaryOperator o, String t) {
        lhs = l;
        rhs = r;
        op = o;
        type = t;
    }
}

class Calc {

    static Number parseNumber(String src) throws Exception {
        try {                              // получение чисел, если входные данные некорректы, исключения выбрасывает соотв. класс
            return new Roman(src);
        } catch (Exception e) {
            return new Decimal(src);
        }
    }

    static Op parseOp(String src) throws Exception {
        String[] srcs = src.split(" ");  //разделим входящую строку на массив по пробелу
        if (srcs.length != 3) throw new Exception(); // если длина массива не 3, то исключение


        Number lhs = parseNumber(srcs[0]); //   первый операнд
        Number rhs = parseNumber(srcs[2]); //   второй операнд
        if (!Objects.equals(lhs.type, rhs.type)) {
            throw new Exception("Unmatched types");   //если на вход и римские, и арабские, то исключение
        }

        String type = lhs.type;

        switch (srcs[1]) {                    //вычисление результата в зависимости от оператора
            case "+":
                return new Op(lhs, rhs, Integer::sum, type);
            case "-":
                return new Op(lhs, rhs, (a, b) -> a - b, type);
            case "*":
                return new Op(lhs, rhs, (a, b) -> a * b, type);
            case "/":
                return new Op(lhs, rhs, (a, b) -> a / b, type);

        }
        throw new Exception();     // если вместо оператора некорректное значение, то исключение
    }

    //конвертация результата в римские цифры, если они были на вход
    public static String arabicToRoman(int number) {
        if ((number < 1) || (number > 100)) {
            throw new IllegalArgumentException(number + " is not in range (1,100)");
        }

        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;
        StringBuilder sb = new StringBuilder();

        while ((number > 0) && (i < romanNumerals.size())) {
            RomanNumeral currentSymbol = romanNumerals.get(i);
            if (currentSymbol.getValue() <= number) {
                sb.append(currentSymbol.name());
                number -= currentSymbol.getValue();
            } else {
                i++;
            }
        }

        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("> ");
            String line = scanner.nextLine();  // считывание из консоли
            if (line.isEmpty())
                break;                         // если на входе пустая строка, то закрыть программу
            try {
                Op op = parseOp(line);
                int result = op.get();
                String type = op.type;

                if (type.equals("roman")) {
                    System.out.println(Calc.arabicToRoman(result)); //если были римские, то результат конвертир. в римские и выводится
                } else {
                    System.out.println(result); //если нет, то результат выводится как есть
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw e;      // если на вход поступили некорректные значения, выбросить исключение
            }
        }
    }
}



