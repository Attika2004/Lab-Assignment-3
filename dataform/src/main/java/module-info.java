module com.example.dataform {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires javafx.graphics;

    opens com.example.dataform to javafx.fxml;
    exports com.example.dataform;
}