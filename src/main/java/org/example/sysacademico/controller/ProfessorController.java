package org.example.sysacademico.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.sysacademico.dao.ProfessorDAO;
import org.example.sysacademico.model.Professor;

public class ProfessorController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private TextField txtFormacao;
    @FXML private TableView<Professor> tblProfessor;
    @FXML private TableColumn<Professor, Long>   colId;
    @FXML private TableColumn<Professor, String> colNome;
    @FXML private TableColumn<Professor, String> colEmail;
    @FXML private TableColumn<Professor, String> colFormacao;
    @FXML private Button btnSalvar;
    @FXML private Button btnExcluir;
    @FXML private Button btnAtualizar;
    @FXML private Button btnFechar;


    private final ObservableList<Professor> lista = FXCollections.observableArrayList();
    private final ProfessorDAO dao = new ProfessorDAO();
    private Professor selecionado;

    @FXML
    private void initialize() {
        colId       .setCellValueFactory(c -> c.getValue().idProperty().asObject());
        colNome     .setCellValueFactory(c -> c.getValue().nomeProperty());
        colEmail    .setCellValueFactory(c -> c.getValue().emailProperty());
        colFormacao .setCellValueFactory(c -> c.getValue().formacaoProperty());

        atualizarTabela();

        tblProfessor.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> preencherForm(newSel));
    }

    @FXML
    private void onSalvar(ActionEvent e) {
        if (!validar()) return;

        try {
            if (selecionado == null) {
                Professor p = new Professor();
                p.setNome(txtNome.getText());
                p.setEmail(txtEmail.getText());
                p.setFormacao(txtFormacao.getText());

                dao.create(p);
                info("Professor cadastrado!");
            } else {
                selecionado.setNome(txtNome.getText());
                selecionado.setEmail(txtEmail.getText());
                selecionado.setFormacao(txtFormacao.getText());
                dao.update(selecionado);
                info("Professor atualizado!");
            }
            atualizarTabela();
            limparForm();
        } catch (Exception ex) {
            erro(ex);
        }
    }

    @FXML
    private void onEditar(ActionEvent e) {
        if (selecionado == null)
            warn("Selecione um professor para editar.");
    }

    @FXML
    private void onExcluir(ActionEvent e) {
        if (selecionado == null) {
            warn("Selecione um professor para excluir.");
            return;
        }
        if (confirmar("Confirma a exclusão?")) {
            try {
                dao.delete(selecionado);
                atualizarTabela();
                limparForm();
                info("Excluído com sucesso.");
            } catch (Exception ex) {
                erro(ex);
            }
        }
    }

    @FXML
    private void onAtualizar(ActionEvent e) { atualizarTabela(); }

    @FXML
    private void onFechar(ActionEvent e) {
        txtNome.getScene().getWindow().hide();
    }

    private void atualizarTabela() {
        lista.setAll(dao.findAll());
        tblProfessor.setItems(lista);
    }

    private void preencherForm(Professor p) {
        selecionado = p;
        if (p != null) {
            txtNome     .setText(p.getNome());
            txtEmail    .setText(p.getEmail());
            txtFormacao .setText(p.getFormacao());
        } else {
            limparForm();
        }
    }

    private void limparForm() {
        txtNome.clear();
        txtEmail.clear();
        txtFormacao.clear();
        tblProfessor.getSelectionModel().clearSelection();
        selecionado = null;
    }

    private boolean validar() {
        if (txtNome.getText().isBlank() ||
                txtEmail.getText().isBlank() ||
                txtFormacao.getText().isBlank()) {
            warn("Preencha todos os campos.");
            return false;
        }
        return true;
    }


    private void info(String msg) { alert(Alert.AlertType.INFORMATION, msg); }
    private void warn(String msg) { alert(Alert.AlertType.WARNING,    msg); }
    private void erro(Exception ex) {
        alert(Alert.AlertType.ERROR, ex.getMessage() == null ?
                ex.toString() : ex.getMessage());
    }
    private boolean confirmar(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg,
                ButtonType.OK, ButtonType.CANCEL);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
    private void alert(Alert.AlertType tipo, String msg) {
        Alert a = new Alert(tipo, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
