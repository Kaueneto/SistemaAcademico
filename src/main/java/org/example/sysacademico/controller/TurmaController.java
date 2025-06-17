package org.example.sysacademico.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.sysacademico.model.Disciplina;
import org.example.sysacademico.model.Professor;
import org.example.sysacademico.model.Turma;
import org.example.sysacademico.dao.DisciplinaDAO;
import org.example.sysacademico.dao.ProfessorDAO;
import org.example.sysacademico.dao.TurmaDAO;

public class TurmaController {

    @FXML private TextField txtSemestre;
    @FXML private TextField txtHorario;
    @FXML private ComboBox<Professor> cboxProf;
    @FXML private ComboBox<Disciplina> cboxDisciplina;

    @FXML private TableView<Turma> tblTurma;
    @FXML private TableColumn<Turma, Long> colId;
    @FXML private TableColumn<Turma, String> colSemestre;
    @FXML private TableColumn<Turma, String> colHorario;
    @FXML private TableColumn<Turma, String> colDisciplina;
    @FXML private TableColumn<Turma, String> colProf;

    @FXML private Button btnSalvar;
    @FXML private Button btnFechar;
    @FXML private Button btnatualizar;
    @FXML private Button btnexcluir;
    @FXML private Button btneditar;

    private final TurmaDAO dao = new TurmaDAO();
    private final DisciplinaDAO disciplinaDAO = new DisciplinaDAO();
    private final ProfessorDAO professorDAO = new ProfessorDAO();

    private final ObservableList<Turma> lista = FXCollections.observableArrayList();
    private final ObservableList<Disciplina> disciplinas = FXCollections.observableArrayList();
    private final ObservableList<Professor> professores = FXCollections.observableArrayList();

    private Turma selecionada;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> c.getValue().idProperty().asObject());
        colSemestre.setCellValueFactory(c -> c.getValue().semestreProperty());
        colHorario.setCellValueFactory(c -> c.getValue().horarioProperty());
        colDisciplina.setCellValueFactory(c -> {
            Disciplina d = c.getValue().getDisciplina();
            return new SimpleStringProperty(d != null ? d.getNome() : "");
        });
        colProf.setCellValueFactory(c -> {
            Professor p = c.getValue().getProfessor();
            return new SimpleStringProperty(p != null ? p.getNome() : "");
        });

        disciplinas.setAll(disciplinaDAO.findAll());
        professores.setAll(professorDAO.findAll());
        System.out.println("Professores carregados no ComboBox: " + professores.size());
        professores.forEach(p -> System.out.println("Professor ID: " + p.getId() + ", Nome: " + p.getNome()));
        cboxDisciplina.setItems(disciplinas);
        cboxProf.setItems(professores);

        tblTurma.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        tblTurma.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selecionada = newVal;
        });

        carregarTabela();
        btnatualizar.setDisable(true);

    }

    @FXML
    private void onSalvar() {
        if (!validarEntrada()) return;

        String semestre = txtSemestre.getText();
        String horario = txtHorario.getText();
        Disciplina disciplina = cboxDisciplina.getValue();
        Professor professor = cboxProf.getValue();

        dao.create(new Turma(null, semestre, horario, disciplina, professor));

        carregarTabela();
        limparForm();
        alert("Turma criada com sucesso.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void onEditar() {
        if (selecionada == null) {
            alert("Selecione uma turma na tabela.", Alert.AlertType.WARNING);
            return;
        }

        preencherForm(selecionada);
        liberarEdicao();

    }

    @FXML
    private void onAtualizar() {
        if (selecionada == null) {
            alert("Selecione uma turma para atualizar.", Alert.AlertType.WARNING);
            return;
        }

        if (!validarEntrada()) return;

        selecionada.setSemestre(txtSemestre.getText());
        selecionada.setHorario(txtHorario.getText());
        selecionada.setDisciplina(cboxDisciplina.getValue());
        selecionada.setProfessor(cboxProf.getValue());

        try {
            dao.update(selecionada);
            carregarTabela();
            limparForm();
            bloquearEdicao();
            alert("Turma atualizada com sucesso.", Alert.AlertType.INFORMATION);
        } catch (RuntimeException e) {
            alert("Erro ao atualizar turma: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void onExcluir() {
        if (selecionada == null) {
            alert("Selecione uma turma para excluir.", Alert.AlertType.WARNING);
            return;
        }

        try {
            dao.delete(selecionada);
            carregarTabela();
            limparForm();
            alert("Turma excluída com sucesso.", Alert.AlertType.INFORMATION);
        } catch (RuntimeException e) {
            alert("Erro ao excluir turma: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void onFechar() {
        txtSemestre.getScene().getWindow().hide();
    }

    private void carregarTabela() {
        System.out.println("Carregando tabela de turmas...");
        lista.clear();
        lista.setAll(dao.findAll());
        tblTurma.setItems(lista);
        tblTurma.getSelectionModel().clearSelection();
        tblTurma.refresh();
        System.out.println("Itens na tabela: " + lista.size());
    }

    private void preencherForm(Turma t) {
        if (t != null) {
            txtSemestre.setText(t.getSemestre());
            txtHorario.setText(t.getHorario());
            cboxDisciplina.setValue(t.getDisciplina());


            Professor professor = t.getProfessor();
            System.out.println("Tentando preencher professor: ID=" + (professor != null ? professor.getId() : "null") + ", Nome=" + (professor != null ? professor.getNome() : "null"));
            if (professor != null) {
                Professor professorSelecionado = professores.stream()
                        .filter(p -> p.getId().equals(professor.getId()))
                        .findFirst()
                        .orElse(null);
                if (professorSelecionado != null) {
                    cboxProf.setValue(professorSelecionado);
                    System.out.println("Professor selecionado no ComboBox: ID=" + professorSelecionado.getId() + ", Nome=" + professorSelecionado.getNome());
                } else {
                    System.out.println("Professor não encontrado na lista de professores.");
                    cboxProf.getSelectionModel().clearSelection();
                }
            } else {
                cboxProf.getSelectionModel().clearSelection();
            }
        } else {
            limparForm();
        }
    }

    private void limparForm() {
        txtSemestre.clear();
        txtHorario.clear();
        cboxDisciplina.getSelectionModel().clearSelection();
        cboxProf.getSelectionModel().clearSelection();
        selecionada = null;
    }

    private boolean validarEntrada() {
        String semestre = txtSemestre.getText();
        String horario = txtHorario.getText();
        Disciplina disciplina = cboxDisciplina.getValue();
        Professor professor = cboxProf.getValue();

        if (semestre.isBlank() || horario.isBlank() || disciplina == null || professor == null) {
            alert("Preencha todos os campos.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void bloquearEdicao() {
        btnSalvar.setDisable(false);
        btnatualizar.setDisable(true);
        txtSemestre.setDisable(true);
        txtHorario.setDisable(true);
        cboxDisciplina.setDisable(true);
        cboxProf.setDisable(true);
        tblTurma.setDisable(false);
    }

    private void liberarEdicao() {
        btnSalvar.setDisable(true);
        btnatualizar.setDisable(false);
        txtSemestre.setDisable(false);
        txtHorario.setDisable(false);
        cboxDisciplina.setDisable(false);
        cboxProf.setDisable(false);
        tblTurma.setDisable(true);
    }

    private void alert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }
    @FXML
    private void onFechar(ActionEvent e) {
        btnFechar.getScene().getWindow().hide();
    }
}