Пример запуска - ```java -jar util.jar -o /output/path -p result_ -a -f -r 2 -t input1.txt input2.txt input3.txt```\
• Дополнительная реализация:\
    • -t - выводит в статистике время затраченное на выполнение программы\
    • -r - округляет числа в статистике до указанного количества знаков после запятой\
    • Добавлен вывод  количества ошибок во время выполнения\
• Версия Java - 17\
• Система сборки - Gradle (Version - 8.10)\
• Сторонние библиотеки - JetBrains Java Annotations
-
Блок зависимостей:\
dependencies {

    implementation("org.jetbrains:annotations:24.0.0")

}
