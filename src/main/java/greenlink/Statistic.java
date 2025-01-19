package greenlink;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Statistic {

    private HashMap<LineType, ArrayList<Object>> valuesMap;
    private BigDecimal countLines;
    private BigDecimal sumValues;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private BigDecimal averageValue;
    private BigDecimal minLength;
    private BigDecimal maxLength;
    private boolean needFull;
    private int errors;
    private boolean needTime;

    public Statistic(HashMap<LineType, ArrayList<Object>> valuesMap, boolean needFull, @Nullable Integer round, int errors, boolean needTime) {
        this.valuesMap = valuesMap;
        this.needFull = needFull;
        this.errors = errors;
        this.needTime = needTime;
        this.countLines = new BigDecimal(getNumbers().size() + getStrings().size());
        if (needFull) {
            minValue = getMinValue();
            maxValue = getMaxValue();
            sumValues = getSum();
            averageValue = getAverageValue();
            minLength = getMinLength();
            maxLength = getMaxLength();
            if (round != null) setRound(round);
        }
    }

    public void setRound(Integer round) {
        if (minValue != null) minValue = minValue.setScale(round, RoundingMode.HALF_EVEN);
        if (maxValue != null) maxValue = maxValue.setScale(round, RoundingMode.HALF_EVEN);
        if (sumValues != null) sumValues = sumValues.setScale(round, RoundingMode.HALF_EVEN);
        if (averageValue != null) averageValue = averageValue.setScale(round, RoundingMode.HALF_EVEN);
    }

    @Nullable
    public BigDecimal getMinValue() {
        return getNumbers().stream().min(BigDecimal::compareTo).orElse(null);
    }

    @Nullable
    public BigDecimal getMaxValue() {
        return getNumbers().stream().max(BigDecimal::compareTo).orElse(null);
    }

    @Nullable
    public BigDecimal getAverageValue() {
        BigDecimal sum = getSum();
        BigDecimal zero = new BigDecimal(0);
        return sum.equals(zero) ? zero : sum.divide(new BigDecimal(getNumbers().size()), RoundingMode.HALF_EVEN);
    }

    @Nullable
    public BigDecimal getMinLength() {
        return getStringsLen().stream().min(BigDecimal::compareTo).orElse(null);
    }

    @Nullable
    public BigDecimal getMaxLength() {
        return getStringsLen().stream().max(BigDecimal::compareTo).orElse(null);
    }

    public List<BigDecimal> getStringsLen() {
        return getStrings().stream().map(s -> new BigDecimal(s.length())).toList();
    }

    public BigDecimal getSum() {
        BigDecimal sum = new BigDecimal(0);
        for (Number number : getNumbers()) {
            sum = sum.add(new BigDecimal(number.toString()));
        }
        return sum;
    }

    public List<String> getStrings() {
        return valuesMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey() == LineType.STRING)
                .flatMap(entry -> entry.getValue().stream())
                .map(value -> (String) value)
                .toList();
    }

    public List<BigDecimal> getNumbers() {
        return valuesMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey() == LineType.FLOAT || entry.getKey() == LineType.INT)
                .flatMap(entry -> entry.getValue().stream())
                .map(value -> new BigDecimal(value.toString()))
                .toList();
    }

    public void printStatistic(long startTime) {
        String result;
        String errColor = errors > 0 ? "\033[91m" : "\033[92m";
        String time = "";
        if (needTime) {
            long elapsedTime = System.nanoTime() - startTime;
            double elapsedTimeInSecond = (double) elapsedTime / 1_000_000_000;
            time = "Время выполнения: \033[1;93m" + elapsedTimeInSecond + "\033[0m сек.\n";
        }
        if (needFull) {
            result = "------------------------------------------------------\n" +
                    "Количество элементов: \033[1;93m" + countLines + "\033[0m\n" +
                    "Минимальное значение: \033[1;93m" + minValue + "\033[0m\n" +
                    "Максимальное значение: \033[1;93m" + maxValue + "\033[0m\n" +
                    "Сумма: \033[1;93m" + sumValues + "\033[0m\n" +
                    "Среднее значение: \033[1;93m" + averageValue + "\033[0m\n" +
                    "Минимальная длина: \033[1;93m" + minLength + "\033[0m\n" +
                    "Максимальная длина: \033[1;93m" + maxLength + "\033[0m\n" +
                    "Ошибок: " + errColor + errors + "\033[0m\n" +
                    time +
                    "------------------------------------------------------";
        }
        else {
            result = "------------------------------------------------------\n " +
                    "Количество элементов: \033[1;93m" + countLines + "\033[0m\n" +
                    "Ошибок: " + errColor + errors + "\033[0m\n" +
                    time +
                    "------------------------------------------------------";
        }
        System.out.println(result);
    }
}
