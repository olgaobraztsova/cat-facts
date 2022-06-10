
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private final static String URL =
            "https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats";

    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {

        try (
                //создаем HTTP клиент
                CloseableHttpClient httpClient = HttpClientBuilder.create()
                        .setDefaultRequestConfig(RequestConfig.custom()
                                .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                                .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                                .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                                .build())
                        .build();
        ) {

            //создаем запрос с заголовками
            HttpGet request = new HttpGet(URL);
            request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

            // отправляем запрос
            CloseableHttpResponse response = httpClient.execute(request);

            //получаем заголовки и выводим на экран
            Arrays.stream(response.getAllHeaders()).forEach(System.out::println);
            System.out.println();

            //тело ответа кладем в список
            List<CatFacts> catFactsList =
                    mapper.readValue(response.getEntity().getContent(),
                            new TypeReference<List<CatFacts>>() {
                            });

            // фильтруем только факты с upvotes больше нуля и выводим на экран
            List<CatFacts> listWithNoUpvotes = new ArrayList<>();
            catFactsList.stream()
                    .filter(value -> value.getUpvotes() > 0)
                    .forEach(listWithNoUpvotes::add);

            for (CatFacts catFact : listWithNoUpvotes) {
                System.out.println(catFact);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
