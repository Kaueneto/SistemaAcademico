package org.example.sysacademico.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.sysacademico.dao.CursoDAO;
import org.example.sysacademico.dao.DisciplinaDAO;
import org.example.sysacademico.model.Curso;
import org.example.sysacademico.model.Disciplina;

public class DisciplinaController {

    @FXML private TextField txtNome;
    @FXML private TextField txtDescricao;
    @FXML private ComboBox<Curso> cboxCurso;
    @FXML private TableView<Disciplina> tblDisciplina;
    @FXML private TableColumn<Disciplina, Long> colId;
    @FXML private TableColumn<Disciplina, String> colNome;
    @FXML private TableColumn<Disciplina, String> colDescricao;
    @FXML private TableColumn<Disciplina, String> colCurso;

    @FXML private Button btnSalvar;
    @FXML private Button btnEditar;
    @FXML private Button btnAtualizar;
    @FXML private Button btnExcluir;
    @FXML private Button btnFechar;


    private final DisciplinaDAO dao = new DisciplinaDAO();
    private final CursoDAO cursoDAO = new CursoDAO();
    private final ObservableList<Disciplina> lista = FXCollections.observableArrayList();
    private final ObservableList<Curso> cursos = FXCollections.observableArrayList();
    private Disciplina selecionada;


    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> c.getValue().idProperty().asObject());
        colNome.setCellValueFactory(c -> c.getValue().nomeProperty());
        colDescricao.setCellValueFactory(c -> c.getValue().descricaoProperty());
        colCurso.setCellValueFactory(c -> c.getValue().cursoProperty());

        tblDisciplina.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);



        cursos.setAll(cursoDAO.findAll());
        cboxCurso.setItems(cursos);

        carregarTabela();
        bloquearEdicao();
    }


    @FXML
    private void onSalvar() {
        if (!validarEntrada()) return;

        String nome = txtNome.getText();
        String descricao = txtDescricao.getText();
        Curso curso = cboxCurso.getValue();

        dao.create(new Disciplina(null, nome, descricao, curso, null));

        carregarTabela();
        limparForm();
    }

    @FXML
    private void onEditar() {
        selecionada = tblDisciplina.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            alert("Selecione uma disciplina na tabela.");
            return;
        }
        System.out.println("Editando disciplina: " + selecionada);
        preencherForm(selecionada);
        liberarEdicao();
    }

    @FXML
    private void onAtualizar() {
        if (selecionada == null) {
            alert("Nenhuma disciplina selecionada para atualizar.");
            return;
        }

        if (!validarEntrada()) return;

        selecionada.setNome(txtNome.getText());
        selecionada.setDescricao(txtDescricao.getText());
        selecionada.setCurso(cboxCurso.getValue());
        System.out.println("Atualizando disciplina com ID: " + selecionada.getId());
        dao.update(selecionada);

        carregarTabela();
        limparForm();
        bloquearEdicao();
    }
    @FXML
    private void onExcluir() {
        selecionada = tblDisciplina.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            alert("Selecione uma disciplina para excluir.");
            return;
        }

        System.out.println("Excluindo disciplina com ID: " + selecionada.getId() + ", Nome: " + selecionada.getNome());
        try {
            dao.delete(selecionada);
            carregarTabela();
            System.out.println("Exclusão concluída com sucesso para ID: " + selecionada.getId());
            alert("Disciplina excluída com sucesso.");
            limparForm();
            bloquearEdicao();
        } catch (RuntimeException e) {
            System.out.println("Erro ao excluir disciplina: " + e.getMessage());
            e.printStackTrace();
            alert("Erro ao excluir disciplina pois ela esta associada a outros dados na tabela " );
        }
    }

    @FXML
    private void onFechar() {
        txtNome.getScene().getWindow().hide();
    }

    private void carregarTabela() {
        System.out.println("Carregando tabela de disciplinas...");
        lista.clear(); // Limpa a lista para evitar duplicatas
        lista.setAll(dao.findAll());
        tblDisciplina.setItems(lista);
        tblDisciplina.getSelectionModel().clearSelection();
        tblDisciplina.refresh(); // Força atualização visual
        System.out.println("Itens na tabela: " + lista.size());
    }

    private void preencherForm(Disciplina d) {
        if (d != null) {
            txtNome.setText(d.getNome());
            txtDescricao.setText(d.getDescricao());
            cboxCurso.setValue(d.getCurso());
        } else {
            limparForm();
        }
    }

    private void limparForm() {
        txtNome.clear();
        txtDescricao.clear();
        cboxCurso.getSelectionModel().clearSelection();
        selecionada = null;
    }

    private boolean validarEntrada() {
        String nome = txtNome.getText();
        String descricao = txtDescricao.getText();
        Curso curso = cboxCurso.getValue();

        if (nome.isBlank() || descricao.isBlank() || curso == null) {
            alert("Preencha todos os campos.");
            return false;
        }
        return true;
    }

    private void bloquearEdicao() {
        btnSalvar.setDisable(false);
        btnAtualizar.setDisable(true);
        txtNome.setDisable(false);
        txtDescricao.setDisable(false);
        cboxCurso.setDisable(false);
    }

    private void liberarEdicao() {
        btnSalvar.setDisable(true);
        btnAtualizar.setDisable(false);
        txtNome.setDisable(false);
        txtDescricao.setDisable(false);
        cboxCurso.setDisable(false);
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}