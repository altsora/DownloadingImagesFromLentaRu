import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger rootLogger = LogManager.getRootLogger();
    private static final Logger errorLogger = LogManager.getLogger("errorLogger");
    private static final String PATH = "images/";
    private static final String ROOT_URL = "https://lenta.ru/";

    public static void main(String[] args) {
        rootLogger.info("Старт.");
        List<String> images = new ArrayList<>();
        try {
            Document document = Jsoup.connect(ROOT_URL).maxBodySize(0).get();
            Elements media = document.select("img[src~=(?i)\\.(png|jpg|jpeg?g)]");
            media.stream()
                    .map(element -> element.attr("abs:src"))
                    .filter(img -> img.startsWith("https"))
                    .forEach(images::add);
            downloadImages(images);
        } catch (IOException ex) {
            errorLogger.error(ex.getMessage(), ex);
            ex.printStackTrace();
        }
        rootLogger.info("Конец.");
    }

    private static void downloadImages(List<String> images) throws IOException {
        rootLogger.info("Начинается загрузка изображений...");
        for (String path : images) {
            try (InputStream in = new URL(path).openStream()) {
                Files.copy(in, Paths.get(PATH + new File(path).getName()), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        rootLogger.info("Загрузка успешно завершена. Загружено изображений: {}", images.size());
    }
}
