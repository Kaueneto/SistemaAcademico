package org.example.sysacademico.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.sysacademico.dao.CursoDAO;
import org.example.sysacademico.model.Curso;

import java.awt.event.ActionEvent;

public class CursoController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCargaHoraria;

    @FXML private TableView<Curso> tblCurso;
    @FXML private TableColumn<Curso,Long>    colId;
    @FXML private TableColumn<Curso,String>  colNome;
    @FXML private TableColumn<Curso,Integer> colCarga;

    @FXML private Button btnSalvar;
    @FXML private Button btnEditar;
    @FXML private Button btnAtualizar;
    @FXML private Button btnExcluir;
    @FXML private Button BTNfechar;

    private final CursoDAO dao = new CursoDAO();
    private final ObservableList<Curso> lista = FXCollections.observableArrayList();
    private Curso selecionado;


    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> c.getValue().idProperty().asObject());
        colNome.setCellValueFactory(c -> c.getValue().nomeProperty());
        colCarga.setCellValueFactory(c -> c.getValue().cargaHorariaProperty().asObject());

        tblCurso.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        carregarTabela();
        bloquearEdicao();
    }


    @FXML
    private void onSalvar() {
        if (!validarEntrada()) return;

        int carga = Integer.parseInt(txtCargaHoraria.getText());
        dao.create(new Curso(null, txtNome.getText(), carga, null));

        carregarTabela();
        limparForm();
    }

    @FXML
    private void onEditar() {
        selecionado = tblCurso.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            alert("Selecione um curso na tabela.");
            return;
        }
        System.out.println("Editando curso: " + selecionado);
        preencherForm(selecionado);
        liberarEdicao();
    }

    @FXML
    private void onAtualizar() {
        if (selecionado == null) {
            alert("Nenhum curso selecionado para atualizar.");
            return;
        }

        if (!validarEntrada()) return;

        selecionado.setNome(txtNome.getText());
        selecionado.setCargaHoraria(Integer.parseInt(txtCargaHoraria.getText()));
        System.out.println("Atualizando curso com ID: " + selecionado.getId());
        dao.update(selecionado);

        carregarTabela();
        limparForm();
        bloquearEdicao();
    }

// ===== CursoController.java =====

// ...

    @FXML
    private void onExcluir() {
        Curso alvo = tblCurso.getSelectionModel().getSelectedItem();
        if (alvo == null) {
            alert("Selecione um curso pra exclusao.");
            return;
        }

        try {
            dao.delete(alvo);
            carregarTabela();
            limparForm();
            bloquearEdicao();
            alert("Curso excluido com sucesso.");
        } catch (Exception ex) {
            if (isForeignKeyViolation(ex)) {
                alert("Não é possível excluir este curso, pois ele está associado a outras tabelas.");
            } else {
                alert("Erro ao excluir curso: " + ex.getMessage());
            }
            ex.printStackTrace();
        }
    }

    /** Detecta violação de chave estrangeira (PostgreSQL SQLState 23503 ou texto-chave) */
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

// ...


    @FXML
    private void onFechar() {
        txtNome.getScene().getWindow().hide();
    }

    private void carregarTabela() {
        System.out.println("Carregando tabela...");
        lista.setAll(dao.findAll());
        tblCurso.setItems(lista);
        System.out.println("Itens na tabela: " + lista.size());
    }

    private void preencherForm(Curso c) {
        txtNome.setText(c.getNome());
        txtCargaHoraria.setText(String.valueOf(c.getCargaHoraria()));
    }

    private void limparForm() {
        txtNome.clear();
        txtCargaHoraria.clear();
        selecionado = null;
    }

    private boolean validarEntrada() {
        String nome = txtNome.getText();
        String cargaStr = txtCargaHoraria.getText();

        if (nome.isBlank() || cargaStr.isBlank()) {
            alert("Preencha todos os campos.");
            return false;
        }
        try {
            Integer.parseInt(cargaStr);
        } catch (NumberFormatException e) {
            alert("Carga horária inválida.");
            return false;
        }
        return true;
    }

    private void bloquearEdicao() {
        btnSalvar.setDisable(false);
        btnAtualizar.setDisable(true);
        txtNome.setDisable(false);
        txtCargaHoraria.setDisable(false);
    }

    private void liberarEdicao() {
        btnSalvar.setDisable(true);
        btnAtualizar.setDisable(false);
        txtNome.setDisable(false);
        txtCargaHoraria.setDisable(false);
    }
    @FXML
    private void onFechar(ActionEvent e) {
        BTNfechar.getScene().getWindow().hide();
    }


    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}