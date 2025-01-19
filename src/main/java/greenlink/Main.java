package greenlink;

import java.io.*;
import java.util.*;

public class Main {

    public static int errors = 0;

    public static void main(String[] args) {

        long startTime = System.nanoTime();

        boolean needAppend = false;
        String prefix = "";
        String path = "";
        boolean needFullStat = false;
        Integer round = null;
        boolean needTime = false;

        HashMap<Argument, String> argsMap = getArgsMap(args);

        for (Map.Entry<Argument, String> entry : argsMap.entrySet()) {
            switch (entry.getKey()) {
                case ADD -> needAppend = true;
                case PATH -> path = entry.getValue();
                case PREFIX -> prefix = entry.getValue();
                case SHORT_STAT -> needFullStat = false;
                case FULL_STAT -> needFullStat = true;
                case ROUND -> round = Integer.parseInt(entry.getValue());
                case TIME -> needTime = true;
            }
        }

        HashMap<LineType, ArrayList<Object>> valuesMap = new HashMap<>();
        Arrays.stream(LineType.values()).forEach(type -> valuesMap.put(type, new ArrayList<>()));

        processValues(args, valuesMap);

        HashMap<LineType, ArrayList<Object>> writtenData = writeData(valuesMap, needAppend, path, prefix);

        Statistic statistic = new Statistic(writtenData, needFullStat, round, errors, needTime);
        statistic.printStatistic(startTime);
    }

    public static HashMap<LineType, ArrayList<Object>> writeData(HashMap<LineType, ArrayList<Object>> valuesMap, boolean needAppend, String path, String prefix) {
        HashMap<LineType, ArrayList<Object>> writtenData = new HashMap<>();
        for (Map.Entry<LineType, ArrayList<Object>> entry : valuesMap.entrySet()) {
            LineType key = entry.getKey();
            writtenData.putIfAbsent(key, new ArrayList<>());
            ArrayList<Object> values = entry.getValue();
            File file = new File(path + prefix + key.getFileName());

            if (values.isEmpty()) continue;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, needAppend))) {
                for (Object value : values) {
                    writer.write(value.toString());
                    writer.newLine();
                    writtenData.get(key).add(value);
                }
            } catch (IOException e) {
                System.err.println("Ошибка записи в файл " + file + " (проверьте путь)");
                errors++;
            }
        }
        return writtenData;
    }

    public static void processValues(String[] args, HashMap<LineType, ArrayList<Object>> valuesMap) {
        for (File argFile : getInputFiles(args)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(argFile))) {
                reader.lines().forEach(line -> {
                    LineType lineType = LineType.parseLineValue(line.trim());
                    valuesMap.get(lineType).add(lineType.parse(line));
                });
            } catch (IOException e) {
                System.err.println("Файл " + argFile + " не найден");
                errors++;
            }
        }
    }

    public static HashMap<Argument, String> getArgsMap(String[] args) {
        HashMap<Argument, String> argsMap = new HashMap<>();
        for (int i = 0; i < args.length; i++) {

            String arg = args[i];
            String possibleValue = (args.length > i + 1) ? args[i + 1] : null;

            Argument findedArgument = Argument.findArgument(arg);
            if (findedArgument == null) continue;

            handleArgument(findedArgument, possibleValue, argsMap);
        }
        return argsMap;
    }

    public static void handleArgument(Argument argument, String possibleValue, HashMap<Argument, String> argsMap) {
        if (argument.isHaveValue()) {
            if (possibleValue == null) {
                System.err.println("Нет значения для аргумента [" + argument.getArg() +  "] (будет пропущен)");
                errors++;
                return;
            }

            if (argument == Argument.PATH) {
                possibleValue = possibleValue.replace("\\", "/");
                if (!possibleValue.endsWith("/")) possibleValue += "/";
                if (!isValidPath(possibleValue)) {
                    System.err.println("Неверно указан путь для аргумента [" + argument.getArg() +  "] (будет пропущен)");
                    errors++;
                    return;
                }
            }
            if (argument == Argument.ROUND) {
                try {
                    int i = Integer.parseInt(possibleValue);
                    if (i < 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    System.err.println("Неверно указано значение для аргумента [" + argument.getArg() +  "] (будет пропущен)");
                    errors++;
                    return;
                }
            }

            argsMap.put(argument, possibleValue);
        }
        else argsMap.put(argument, null);
    }

    public static ArrayList<File> getInputFiles(String[] args) {
        args = Arrays.stream(args).filter(arg -> arg.contains(".")).toList().toArray(String[]::new);
        ArrayList<File> files = new ArrayList<>();
        for (String arg : args) {
            File file = new File(arg);

            if (file.exists()) files.add(file);
            else {
                System.err.println("Файл " + arg + " не найден");
                errors++;
            }
        }
        return files;
    }

    public static boolean isValidPath(String path) {
        File file = new File(path);
        try {
            file.getCanonicalPath();
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }
}