module com.example.currencyconverter {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires com.google.gson;

    opens com.example.currencyconverter to javafx.fxml;
    exports com.example.currencyconverter;
}