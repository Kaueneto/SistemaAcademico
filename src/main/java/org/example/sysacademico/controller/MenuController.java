package org.example.sysacademico.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

public class MenuController {

    @FXML private Menu menuCadastros;
    @FXML private MenuBar menuAjuda;

    @FXML private MenuItem itemProfessor;
    @FXML private MenuItem itemCurso;
    @FXML private MenuItem itemDisciplinas;

    @FXML private MenuItem itemTurma;

    @FXML
    private void abrirProfessores() {
        abrirTela("/org/example/sysacademico/Professor.fxml",  "Professores");
    }

    @FXML
    private void abrirCursos()      {
        abrirTela("/org/example/sysacademico/Cursos.fxml",      "Cursos");
    }

    @FXML
    private void abrirDisciplinas() {
        abrirTela("/org/example/sysacademico/Disciplina.fxml", "Disciplinas");
    }

    @FXML
    private void abrirTurmas(){
        abrirTela("/org/example/sysacademico/turma.fxml",      "Turmas");
    }

    @FXML
    private void mostrarAbout() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Sobre");
        a.setHeaderText("Sistema de Gestão Acadêmica");
        a.setContentText("Versão 1.0\nDesenvolvido para a disciplina de POO.");
        a.showAndWait();
    }


    private void abrirTela(String caminhoFXML, String tituloJanela) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(caminhoFXML));
            Stage stage = new Stage();
            stage.setTitle(tituloJanela);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            Alert erro = new Alert(Alert.AlertType.ERROR,
                    "Não foi possível abrir a tela: " + e.getMessage());
            erro.showAndWait();
            e.printStackTrace();
        }
    }
}
