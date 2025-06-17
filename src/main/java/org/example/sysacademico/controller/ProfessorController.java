package org.example.sysacademico.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.sysacademico.dao.ProfessorDAO;
import org.example.sysacademico.model.Professor;

import java.util.regex.Pattern;

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
    @FXML private Button btnEditar;
    @FXML private Button btnAtualizar;
    @FXML private Button btnFechar;
    private final ObservableList<Professor> lista = FXCollections.observableArrayList();
    private final ProfessorDAO dao = new ProfessorDAO();
    private Professor selecionado;

    @FXML
    private void initialize() {
        colId      .setCellValueFactory(c -> c.getValue().idProperty().asObject());
        colNome    .setCellValueFactory(c -> c.getValue().nomeProperty());
        colEmail   .setCellValueFactory(c -> c.getValue().emailProperty());
        colFormacao.setCellValueFactory(c -> c.getValue().formacaoProperty());

        btnAtualizar.setDisable(true);

        tblProfessor.setItems(lista); // *** NOVO: bind uma vez só
        atualizarTabela(); // *** NOVO: popula logo após o bind

        tblProfessor.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> selecionado = newSel);
    }

    @FXML
    private void onSalvar(ActionEvent e) {
        if (!validar()) return;

        try {
            Professor p = new Professor();
            p.setNome(txtNome.getText());
            p.setEmail(txtEmail.getText());
            p.setFormacao(txtFormacao.getText());

            dao.create(p);
            info("Professor cadastrado!");
            atualizarTabela();
            limparForm();
        } catch (Exception ex) {
            erro(ex);
        }
    }

    @FXML
    private void onEditar(ActionEvent e) {
        if (selecionado == null) {
            warn("Selecione um professor para editar.");
            return;
        }
        preencherForm(selecionado);
        btnSalvar.setDisable(true);
        btnAtualizar.setDisable(false);
    }

    @FXML
    private void onAtualizar(ActionEvent e) {
        if (selecionado == null) {
            warn("Nenhum professor selecionado para atualizar.");
            return;
        }
        if (!validar()) return;

        try {
            selecionado.setNome(txtNome.getText());
            selecionado.setEmail(txtEmail.getText());
            selecionado.setFormacao(txtFormacao.getText());
            dao.update(selecionado);

            info("Professor atualizado!");
            atualizarTabela();
            limparForm();
        } catch (Exception ex) {
            erro(ex);
        }
    }

    @FXML
    private void onExcluir(ActionEvent e) {
        if (selecionado == null) {
            warn("Selecione um professor para excluir.");
            return;
        }

        if (!confirmar("Confirma a exclusão?")) return; // *** ALTERADO: confirma antes de excluir

        try {
            dao.delete(selecionado);
            atualizarTabela();
            limparForm();
            info("Excluído com sucesso.");
        } catch (Exception ex) {
            if (isForeignKeyViolation(ex)) {
                erro(new Exception("Não é possível excluir este registro, pois ele está associado a outros dados no sistema."));
            } else {
                erro(ex);
            }
        }
    }

    @FXML
    private void onFechar(ActionEvent e) {
        txtNome.getScene().getWindow().hide();
    }

    private void atualizarTabela() {
        var professores = dao.findAll();
        System.out.println("Professores retornados: " + professores); // Log para debug
        lista.setAll(professores);
        Platform.runLater(() -> {
            tblProfessor.refresh();
            System.out.println("Tabela atualizada com " + lista.size() + " registros.");
        });
    }

    private void preencherForm(Professor p) {
        txtNome.setText(p.getNome());
        txtEmail.setText(p.getEmail());
        txtFormacao.setText(p.getFormacao());
    }

    private void limparForm() {
        txtNome.clear();
        txtEmail.clear();
        txtFormacao.clear();
        tblProfessor.getSelectionModel().clearSelection();
        selecionado = null;

        btnSalvar.setDisable(false);
        btnAtualizar.setDisable(true);
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private boolean emailValido(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean validar() {
        String nome  = txtNome.getText();
        String email  = txtEmail.getText();
        String formacao = txtFormacao.getText();

        if (nome.isBlank() || email.isBlank() || formacao.isBlank()) {
            warn("é necessário preencher todos os campos.");
            return false;
        }
        if (!emailValido(email)) {
            warn("Informe um e-mail válido.");
            txtEmail.requestFocus();
            return false;
        }
        return true;
    }
    private void info(String msg) { alert(Alert.AlertType.INFORMATION, msg); }
    private void warn(String msg) { alert(Alert.AlertType.WARNING, msg); }
    private void erro(Exception ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : ex.toString();
        alert(Alert.AlertType.ERROR, msg);
    }

    private boolean isForeignKeyViolation(Exception ex) {
        Throwable t = ex;
        while (t != null) {
            String msg = t.getMessage();
            if (msg != null) {
                String lower = msg.toLowerCase();
                if (lower.contains("sqlstate 23503") ||
                        lower.contains("violates foreign key") ||
                        lower.contains("constraintviolationexception")) {
                    return true;
                }
            }
            t = t.getCause();
        }
        return false;
    }

    private boolean confirmar(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void alert(Alert.AlertType tipo, String msg) {
        Alert a = new Alert(tipo, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
