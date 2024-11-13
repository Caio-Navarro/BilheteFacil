/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import conexao.Conexao;
import static controller.ControllerTelaPrincipal.idEvento;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerTelaAdmin {
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
    
    public void cadastrarEvento() {
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
    
}
