package com;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;

public class CurrencyConverter {

    private static final OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Type currency to convert from: ");
            String convertFrom = scanner.nextLine().trim().toUpperCase();

            System.out.print("Type currency to convert to: ");
            String convertTo = scanner.nextLine().trim().toUpperCase();

            System.out.print("Type quantity to convert: ");
            if (!scanner.hasNextBigDecimal()) {
                System.out.println("Invalid amount entered.");
                return;
            }
            BigDecimal quantity = scanner.nextBigDecimal();

            BigDecimal result = convertCurrency(convertFrom, convertTo, quantity);
            if (result != null) {
                System.out.printf("%s %s is equivalent to %.2f %s%n", quantity, convertFrom, result, convertTo);
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static BigDecimal convertCurrency(String convertFrom, String convertTo, BigDecimal quantity) {
        String urlString = "https://www.frankfurter.app/latest?from=" + convertFrom;

        try {
            Request request = new Request.Builder().url(urlString).get().build();
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                System.out.println("Failed to fetch exchange rates. HTTP Code: " + response.code());
                return null;
            }

            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONObject ratesObject = jsonObject.getJSONObject("rates");

            if (!ratesObject.has(convertTo)) {
                System.out.println("Invalid currency code: " + convertTo);
                return null;
            }

            BigDecimal rate = ratesObject.getBigDecimal(convertTo);
            return rate.multiply(quantity);

        } catch (IOException e) {
            System.out.println("Network error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }

        return null;
    }
}
