package org.example.sysacademico;

import jakarta.persistence.EntityManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.sysacademico.utils.JPAUtil;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // teste de conexao
        try (EntityManager em = JPAUtil.getEntityManager()) {
            System.out.println("Conectado ao banco !");
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Falha ao conectar:\n" + ex.getMessage()).showAndWait();
            Platform.exit();
            return;
        }


        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/sysacademico/Menu.fxml"));
        Scene scene = new Scene(loader.load(), 569, 363);

        stage.setTitle("SysAcadêmico");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
