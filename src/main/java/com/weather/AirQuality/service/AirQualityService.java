package com.weather.AirQuality.service;

import com.weather.AirQuality.http.AirQualityInterface;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class AirQualityService {

    @Autowired
    private AirQualityInterface airQualityInterface;
    @Autowired
    private ApplicationContext applicationContext;

    public void sendAirQualityNow(long chatId) {
        try {
            ResponseEntity<String> response = airQualityInterface.getAirQualityData();
            String responseBody = response.getBody();

            if (responseBody != null) {
                JSONObject jsonResponse = new JSONObject(responseBody);
                log.info("Received air quality data: " + jsonResponse.toString());
                sendAirQualityUpdate(jsonResponse, chatId);
            }
        } catch (JSONException e) {
            log.error("Error parsing JSON response", e);
        }
    }

    private void sendAirQualityUpdate(JSONObject jsonResponse, long chatId) {
        try {
            // Извлекаем основные данные
            double pm25Value = extractPm25Value(jsonResponse);
            String cityName = extractCityName(jsonResponse);
            double temperature = extractTemperature(jsonResponse);
            double humidity = extractHumidity(jsonResponse);

            // Получаем рекомендации на основе PM2.5
            String pm25Recommendation = getPm25Recommendation(pm25Value);

            // Формируем сообщение для Telegram
            String message = String.format(
                    "🌍 *Качество воздуха*\n" +
                            "Город: *%s*\n" +
                            "PM2.5: *%.1f µg/m³*\n" +
                            "Температура: *%.1f°C*\n" +
                            "Влажность: *%.1f%%*\n" +
                            "Рекомандации: *%s*\n",
                    cityName, pm25Value, temperature, humidity, pm25Recommendation
            );

            // Логируем сообщение
            log.info("Sending air quality update to Telegram: " + message);

            // Отправляем сообщение в Telegram (здесь необходимо использовать метод отправки сообщения)
            sendMessageToTelegram(chatId, message);

        } catch (Exception e) {
            log.error("Error parsing JSON response", e);
        }
    }
    private String getPm25Recommendation(double pm25Value) {
        if (pm25Value <= 15.0) {
            return "Качество воздуха отличное. Можно выходить на улицу и заниматься активностями на свежем воздухе!";
        } else if (pm25Value <= 40.0) {
            return "Качество воздуха умеренное. Можно выходить на улицу, но следите за самочувствием, если у вас есть респираторные проблемы.";
        } else if (pm25Value <= 65.0) {
            return "Качество воздуха нездоровое для чувствительных групп. Людям с астмой и другими заболеваниями дыхательных путей рекомендуется избегать активностей на улице.";
        } else if (pm25Value <= 150.0) {
            return "Качество воздуха нездоровое. Рекомендуется ограничить активные действия на улице, особенно людям с хроническими заболеваниями.";
        } else if (pm25Value <= 250.0) {
            return "Очень плохое качество воздуха. По возможности оставайтесь в помещении. Рекомендуется надевать маску, если нужно выйти на улицу.";
        } else if (pm25Value <= 350.0) {
            return "Опасное качество воздуха. Не выходите на улицу без крайней необходимости. Обязательно используйте средства защиты.";
        } else {
            return "Чрезвычайно опасное качество воздуха! Оставайтесь дома и избегайте пребывания на улице.";
        }
    }
    private void sendMessageToTelegram(long chatId, String message) {
        TelegramBot telegramBot = applicationContext.getBean(TelegramBot.class);
        SendMessage telegramMessage = new SendMessage();
        telegramMessage.setChatId(String.valueOf(chatId));
        telegramMessage.setText(message);
        telegramMessage.enableMarkdown(true); // Если используешь форматирование Markdown
        try {
            telegramBot.execute(telegramMessage); // Отправка сообщения
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private double extractPm25Value(JSONObject jsonResponse) throws JSONException {
        JSONObject data = jsonResponse.getJSONObject("data");
        JSONObject iaqi = data.getJSONObject("iaqi");
        JSONObject pm25 = iaqi.getJSONObject("pm25");

        Object value = pm25.get("v");
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            return Double.parseDouble((String) value);
        } else {
            throw new JSONException("Unexpected value type for pm25: " + value.getClass().getName());
        }
    }
    private String extractCityName(JSONObject jsonResponse) throws JSONException {
        return jsonResponse.getJSONObject("data").getJSONObject("city").getString("name");
    }

    private int extractAqiValue(JSONObject jsonResponse) throws JSONException {
        return jsonResponse.getJSONObject("data").getInt("aqi");
    }

    private double extractTemperature(JSONObject jsonResponse) throws JSONException {
        JSONObject iaqi = jsonResponse.getJSONObject("data").getJSONObject("iaqi");
        return iaqi.getJSONObject("t").getDouble("v");
    }

    private double extractHumidity(JSONObject jsonResponse) throws JSONException {
        JSONObject iaqi = jsonResponse.getJSONObject("data").getJSONObject("iaqi");
        return iaqi.getJSONObject("h").getDouble("v");
    }
}