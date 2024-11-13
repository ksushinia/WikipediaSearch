// Подключаем необходимые библиотеки
import com.google.gson.*; // Используется для работы с JSON (парсинг и обработка данных).
import java.io.*; // Позволяет выполнять операции ввода-вывода, например, чтение ответа от сервера.
import java.net.*; // Обеспечивает сетевое взаимодействие и кодирование URL.
import java.util.Scanner; // Позволяет считывать ввод от пользователя через консоль.
import java.awt.Desktop; // Предоставляет возможность открыть браузер для перехода по ссылке.
import java.net.URI; // Используется для работы с URI (идентификаторы ресурсов).

public class WikipediaSearch { // Определяем класс для поиска по Википедии
    public static void main(String[] args) { // Точка входа в программу
        Scanner scanner = new Scanner(System.in); // Инициализируем объект Scanner для ввода с консоли
        try {
            System.out.print("Введите поисковый запрос: "); // Запрашиваем у пользователя ввод
            String query = scanner.nextLine(); // Считываем строку, введённую пользователем

            // Кодируем запрос для корректного использования в URL (заменяем пробелы на %20)
            String encodedQuery = URLEncoder.encode(query, "UTF-8").replace("+", "%20");
            // Формируем URL для обращения к API Википедии с указанным запросом
            String apiUrl = "https://ru.wikipedia.org/w/api.php?action=query&list=search&utf8=&format=json&srsearch=" + encodedQuery;

            // Создаём объект URL для доступа к API Википедии
            URL url = new URL(apiUrl);
            // Открываем соединение и приводим его к HttpURLConnection для выполнения GET-запроса
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // Устанавливаем метод запроса (GET)

            // Инициализируем поток для чтения данных из ответа сервера
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine; // Переменная для хранения строки из потока
            StringBuilder content = new StringBuilder(); // StringBuilder для построения полного ответа

            // Читаем построчно ответ от сервера и добавляем его к content
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine); // Добавляем каждую строку к содержимому
            }
            in.close(); // Закрываем поток после чтения

            // Парсим JSON-ответ от API в объект JsonObject
            JsonObject jsonObject = JsonParser.parseString(content.toString()).getAsJsonObject();
            // Извлекаем массив результатов поиска
            JsonArray searchResults = jsonObject.getAsJsonObject("query").getAsJsonArray("search");

            // Выводим результаты поиска в консоль
            System.out.println("Результаты поиска:");
            for (int i = 0; i < searchResults.size(); i++) { // Проходим по всем элементам массива
                JsonObject result = searchResults.get(i).getAsJsonObject(); // Получаем один результат
                // Выводим название и описание статьи
                System.out.println((i + 1) + ". " + result.get("title").getAsString());
                System.out.println("Описание: " + result.get("snippet").getAsString());
            }

            // Запрашиваем у пользователя номер статьи для открытия
            System.out.print("Введите номер статьи для открытия: ");
            int choice = scanner.nextInt(); // Читаем введённое число
            scanner.nextLine(); // Очищаем буфер после ввода числа

            // Проверяем, что выбранный номер находится в допустимом диапазоне
            if (choice > 0 && choice <= searchResults.size()) {
                // Получаем заголовок выбранной статьи
                String pageTitle = searchResults.get(choice - 1).getAsJsonObject().get("title").getAsString();
                // Формируем URL для открытия статьи в браузере (заменяем пробелы на %20)
                String pageUrl = "https://ru.wikipedia.org/wiki/" + pageTitle.replace(" ", "%20");

                // Проверяем, поддерживается ли открытие браузера
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI(pageUrl)); // Открываем статью в браузере
                } else {
                    System.out.println("Открытие браузера не поддерживается."); // Сообщаем о невозможности открыть браузер
                }
            } else {
                System.out.println("Неверный выбор."); // Выводим сообщение при неверном выборе
            }
        } catch (IOException e) { // Обрабатываем исключения, связанные с вводом-выводом
            System.out.println("Ошибка при работе с сетью: " + e.getMessage());
        } catch (URISyntaxException e) { // Обрабатываем ошибки синтаксиса URI
            System.out.println("Ошибка в URL: " + e.getMessage());
        } catch (Exception e) { // Обрабатываем любые другие исключения
            System.out.println("Произошла ошибка: " + e.getMessage());
        } finally {
            scanner.close(); // Закрываем Scanner для освобождения ресурсов
        }
    }
}
