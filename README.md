# Android Tinkoff Invest Bot

Android-приложение, позволяющее работать с брокером "Тинькофф Инвестиции" в режиме песочницы, используя Java SDK для Tinkoff Invest API ([invest-api-java-sdk](https://github.com/Tinkoff/invest-api-java-sdk)).

Проект был разработан для [конкурса разработчиков торговых роботов на API Тинькофф Инвестиций](https://github.com/Tinkoff/invest-robot-contest).

## Реализованные функции

- Получение списка счетов в песочнице;
- Открытие/закрытие счёта в песочнице;
- Пополнение баланса счёта в песочнице;
- Получение списка позиций (активов) в портфолио для счёта в песочнице;
- Получение данных о ценных бумагах с биржи (название инструмента, тикер, текущая цена, figi-идентификатор, отрасль экономики);
- Покупка/продажа акции;
- Получение заявок (ордеров) на покупку/продажу актива;
- Торговый робот (элементарная реализация алготрейдинга).

## Описание работы торгового робота

Робот в автоматическом режиме получает активы в портфеле, а также периодически следит (с интервалом в минуту) за текущей ценой этого актива на бирже. Продает бумаги робот только в том случае, если выполняется следующее условие: "выставлять заявку на продажу акции из портфеля текущего пользователя, если текущая цена по сравнению с ценой покупки поднялась на заранее заданное количество процентов".

Во время работы робот записывает каждое своё действие в наглядный лог, который доступен пользователю.

## Пререквизиты

- Android Studio IDE;
- Android SDK;
- Gradle JDK версии 11.0.12 (можно задать через Android Studio: `Settings` > `Build, Execution, Deployment` > `Build Tools` > `Gradle` > `Gradle JDK`, выбрав и скачав `11 version 11.0.12` или аналогичную 11 версию (или выше)). Это необходимо для того, чтобы Tinkoff Invest API для Java SDK работал под Android. Больше ничего не требуется, так как исправленные jar-файлы библиотеки, которые работают под Android уже подключены в проекте и находятся по пути: `android-tinkoff-invest-bot/app/libs/`.

## Сборка проекта

0. Получите токен для доступа к API в [личном кабинете](https://www.tinkoff.ru/invest/settings/) (вы должны быть клиентом Тинькофф Инвестиций);
1. Откройте проект в Android Studio;
2. Введите полученный токен в строковую переменную `tokenSandbox` в файле `android-tinkoff-invest-bot/app/src/main/java/com/kostylev/investbot/helpers/Token.java`;
3. Убедитесь, что Gradle успешно завершил работу по синхронизации библиотек;
4. Нажмите `Build` (для сборки apk-файла) или `Run` (для запуска на подключенном по USB-кабелю девайсе).
