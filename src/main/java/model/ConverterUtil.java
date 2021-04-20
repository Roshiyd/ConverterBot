package model;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class ConverterUtil {
    public static List<Currency> currencyList;

    public static List<Currency> getCurrencyList() throws IOException {
        HttpGet httpGet = new HttpGet("http://cbu.uz/ru/arkhiv-kursov-valyut/json/"); //ulanish
        HttpClient httpClient = HttpClients.createDefault(); //tunel
        HttpResponse httpResponse = httpClient.execute(httpGet);


        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
        Gson gson = new Gson();

        Currency[] currencies = gson.fromJson(bufferedReader, Currency[].class);
        currencyList = Arrays.asList(currencies);

        System.out.println(currencyList);

        return currencyList;
    }

    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

}
