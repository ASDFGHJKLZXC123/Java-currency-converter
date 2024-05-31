package com.example.currencyconverter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ComboBox;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HelloController {
    // currencyOne and currencyTwo holds the currency tickers (from and to)
    // apiKey - holds the api key to use the api
    private String currencyOne, currencyTwo, apiKey;

    private ArrayList<String> CurrencyList;

    @FXML
    private ImageView logo;
    @FXML
    private TextField enterAmountField;
    @FXML
    private ComboBox<String> currencyOneBox, currencyTwoBox;
    @FXML
    private Label resultLabel;

    public void initialize(){
        getApikey();

        loadLogo();

        ArrayList<String> currencyList = loadCurrencyList();

        ObservableList<String> options = FXCollections.observableArrayList(currencyList);
        currencyOneBox.setItems(options);
        currencyTwoBox.setItems(options);


    }

    private void getApikey(){
        BufferedReader reader = null;
        try{
            String filePath = getClass().getResource("/apikey.txt").getPath();
            reader = new BufferedReader(new FileReader(filePath));

            // get the key
            apiKey = reader.readLine();



        }catch(IOException e){
            System.out.println("Error: " + e);
        }finally{
            try{
                if(reader != null) reader.close();
            }catch(IOException e){
                System.out.print("Error" + e);
            }
        }
    }

    private void loadLogo(){
        String logoPath = getClass().getResource("/logo.png").getPath().replaceAll("%20", " ");
        logo.setImage(new Image(new File(logoPath).getAbsolutePath()));
    }


    public void convertCurrency(ActionEvent actionEvent) {
        if(enterAmountField.getText().equals("") || enterAmountField.getText() == null) return;
        if(currencyOne == null || currencyTwo == null) return;

        float conversionRate = getConversionRate();

        float conversionResult = Float.parseFloat(enterAmountField.getText()) * conversionRate;

        resultLabel.setText(conversionResult + " " + currencyTwo);

    }

    public ArrayList<String> loadCurrencyList(){
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        Request request = new Request.Builder()
                .url("https://api.apilayer.com/currency_data/list")
                .addHeader("apiKey", apiKey)
                .method("GET", null)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Gson gson = new Gson();
        JsonElement jsonElement = gson.fromJson(response.body().charStream(), JsonElement.class);
        JsonObject jsonobject = jsonElement.getAsJsonObject();

        ArrayList<String> currencyList = new ArrayList<>();
        for(String currency: jsonobject.getAsJsonObject("currencies").keySet()){
            currencyList.add(currency);
        }
        return currencyList;


    }

    @FXML
    private void setCurrencyOne(){
        currencyOne = currencyOneBox.getValue();
    }

    public void setCurrencyTwo() {
        currencyTwo = currencyTwoBox.getValue();
    }


    private float getConversionRate() {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        Request request = new Request.Builder()
                .url("https://api.apilayer.com/currency_data/live?source=" + currencyOne + "&currencies=" + currencyTwo)
                .addHeader("apiKey", apiKey)
                .method("GET", null)
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Gson gson = new Gson();
        JsonElement jsonElement = gson.fromJson(response.body().charStream(), JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String key = currencyOne + currencyTwo;
        return Float.parseFloat(jsonObject.getAsJsonObject("quotes").get(key).getAsString());

    }
}