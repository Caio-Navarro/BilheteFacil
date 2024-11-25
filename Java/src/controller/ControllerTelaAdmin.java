/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import conexao.Conexao;
import static controller.ControllerTelaPrincipal.idEvento;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerTelaAdmin implements Initializable {

    //----------------------------------------- cadastro de eventos
    @FXML
    private TextField txtNomeEvento;

    @FXML
    private TextField txtLocal;

    @FXML
    private TextField txtDescricao;

    @FXML
    private TextField txtValorIngresso;

    @FXML
    private TextField txtQuantidadeIngressos;

    @FXML
    private DatePicker txtDataEvento;

    @FXML
    private Button btnCadastrarEvento;

    //---------------------------------------- editar evento
    @FXML
    private TextField txtIdEvento;

    @FXML
    private TextField txtNomeEvento2;

    @FXML
    private DatePicker dpDataEvento;

    @FXML
    private TextField txtLocalEvento;

    @FXML
    private TextField txtPrecoEvento;

    @FXML
    private TextField txtQuantidadeIngressos2;

    @FXML
    private TextField txtDescricaoEvento;

    @FXML
    private Button btnConfirmarAlteracoes;

    @FXML
    private ComboBox<String> cbStatusEvento;

    public void trocarTela(Stage stage, String caminhoFXML, String tituloCena) {
        try {
            Parent novaCena = FXMLLoader.load(getClass().getResource(caminhoFXML));
            Scene scene = new Scene(novaCena);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void telaLogin(ActionEvent event) throws Exception {
        Stage telaLogin = (Stage) ((Node) event.getSource()).getScene().getWindow();
        trocarTela(telaLogin, "/telas/TelaLogin.fxml", "BilheteFácil");
    }

    @FXML
    public void telaIngressos(ActionEvent event) throws Exception {
        Stage telaIngressos = (Stage) ((Node) event.getSource()).getScene().getWindow();
        trocarTela(telaIngressos, "/telas/TelaPrincipalIngressos.fxml", "BilheteFácil");
    }

    @FXML
    public void telaCadastrarEvento(ActionEvent event) throws Exception {
        Stage telaCadastrarEvento = (Stage) ((Node) event.getSource()).getScene().getWindow();
        trocarTela(telaCadastrarEvento, "/telas/TelaPrincipalCadastrarEvento.fxml", "BilheteFácil");
    }

    @FXML
    public void telaAdmin(ActionEvent event) throws Exception {
        Stage telaAdmin = (Stage) ((Node) event.getSource()).getScene().getWindow();
        trocarTela(telaAdmin, "/telas/TelaAdmin.fxml", "BilheteFácil");
    }

    public void cadastrarEvento(Event event) {
        String sqlEvento = "INSERT INTO eventos(nome, dt_evento, local_evento, descricao) VALUES (?, ?, ?, ?)";
        String sqlIngressos = "INSERT INTO ingressos(id_evento, valor, quantidade_disponivel) VALUES (?, ?, ?)";
        PreparedStatement psEvento = null;
        PreparedStatement psIngressos = null;

        String nomeEvento = txtNomeEvento.getText();
        LocalDate data = txtDataEvento.getValue();

        java.sql.Date dataEvento = java.sql.Date.valueOf(data);
        String localEvento = txtLocal.getText();
        String descricaoEvento = txtDescricao.getText();
        float valorIngresso = Float.parseFloat(txtValorIngresso.getText());
        int quantidadeIngressos = Integer.parseInt(txtQuantidadeIngressos.getText());

        try {
            Conexao.getConexao().setAutoCommit(false);

            psEvento = Conexao.getConexao().prepareStatement(sqlEvento, PreparedStatement.RETURN_GENERATED_KEYS);
            psEvento.setString(1, nomeEvento);
            psEvento.setDate(2, dataEvento);
            psEvento.setString(3, localEvento);
            psEvento.setString(4, descricaoEvento);
            psEvento.executeUpdate();

            idEvento = -1;
            try (ResultSet rs = psEvento.getGeneratedKeys()) {
                if (rs.next()) {
                    idEvento = rs.getInt(1);
                }
            }

            if (idEvento == -1) {
                throw new SQLException("Falha ao obter o ID do evento.");
            }

            psIngressos = Conexao.getConexao().prepareStatement(sqlIngressos);
            psIngressos.setInt(1, idEvento);
            psIngressos.setFloat(2, valorIngresso);
            psIngressos.setInt(3, quantidadeIngressos);
            psIngressos.executeUpdate();

            Conexao.getConexao().commit();

            Alert confirmacao = new Alert(Alert.AlertType.INFORMATION);
            confirmacao.setTitle("Sucesso");
            confirmacao.setHeaderText("Evento cadastrado com sucesso!");
            confirmacao.showAndWait();

            Stage telaCadastrarEvento = (Stage) ((Node) event.getSource()).getScene().getWindow();
            trocarTela(telaCadastrarEvento, "/telas/TelaPrincipalCadastrarEvento.fxml", "BilheteFácil");

        } catch (SQLException e) {
            try {
                Conexao.getConexao().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (psEvento != null) {
                    psEvento.close();
                }
                if (psIngressos != null) {
                    psIngressos.close();
                }
                Conexao.getConexao().setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void editarEvento(Event event) {
        String idEvento = txtIdEvento.getText();
        String nomeEvento = txtNomeEvento2.getText();
        LocalDate dataEvento = dpDataEvento.getValue();
        String localEvento = txtLocalEvento.getText();
        String precoEvento = txtPrecoEvento.getText();
        String quantidadeIngressos = txtQuantidadeIngressos2.getText();
        String descricaoEvento = txtDescricaoEvento.getText();
        String statusEvento = cbStatusEvento.getValue();

        if (statusEvento.equals("Ativo")) {
            statusEvento = "A";
        } else if (statusEvento.equals("Inativo")) {
            statusEvento = ("I");
        } else {
            statusEvento = "";
        }

        if (idEvento.isEmpty() || nomeEvento.isEmpty() || dataEvento == null || localEvento.isEmpty() || precoEvento.isEmpty() || quantidadeIngressos.isEmpty() || descricaoEvento.isEmpty() || cbStatusEvento.getValue() == null) {
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro");
            erro.setHeaderText("Por favor, preencha todos os campos!");
            erro.showAndWait();
            return;
        }

        int id = Integer.parseInt(idEvento);
        int quantidadeDisponivel = Integer.parseInt(quantidadeIngressos);
        float valorEvento = Float.parseFloat(precoEvento);

        String sqlEvento = "UPDATE eventos SET nome = ?, dt_evento = ?, local_evento = ?, descricao = ?, sts = ? WHERE id_evento = ?";
        String sqlIngresso = "UPDATE ingressos SET valor = ?, quantidade_disponivel = ? WHERE id_evento = ?";

        try {
            PreparedStatement psEvento = Conexao.getConexao().prepareStatement(sqlEvento);
            PreparedStatement psIngresso = Conexao.getConexao().prepareStatement(sqlIngresso);

            psEvento.setString(1, nomeEvento);
            psEvento.setDate(2, java.sql.Date.valueOf(dataEvento));
            psEvento.setString(3, localEvento);
            psEvento.setString(4, descricaoEvento);
            psEvento.setString(5, statusEvento);
            psEvento.setInt(6, id);

            psEvento.executeUpdate();

            psIngresso.setFloat(1, valorEvento);
            psIngresso.setInt(2, quantidadeDisponivel);
            psIngresso.setInt(3, id);
            psIngresso.executeUpdate();

            Alert confirmacao = new Alert(Alert.AlertType.INFORMATION);
            confirmacao.setTitle("Sucesso");
            confirmacao.setHeaderText("Evento editado com sucesso!");
            confirmacao.showAndWait();

            Stage telaAdmin = (Stage) ((Node) event.getSource()).getScene().getWindow();
            trocarTela(telaAdmin, "/telas/TelaAdmin.fxml", "BilheteFácil");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cbStatusEvento != null) {
            cbStatusEvento.setItems(FXCollections.observableArrayList("Ativo", "Inativo"));
        } else {
            System.out.println("cbStatusEvento está nulo!");
        }
    }

}
