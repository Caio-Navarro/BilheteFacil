/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import conexao.Conexao;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerTelaCompra implements Initializable {

    @FXML
    private Label txtNomeUsuario;

    //-----------------------------------tela compra ingressos
    @FXML
    private Label lblNomeEventoCompra;

    @FXML
    private Label lblDataEventoCompra;

    @FXML
    private Label lblLocalEventoCompra;

    @FXML
    private Label lblDescricaoEventoCompra;

    @FXML
    private Label lblQuantidadeIngressosCompra;

    @FXML
    private Label lblPrecoEventoCompra;

    @FXML
    private TextField txtQuantidadeCompra;

    @FXML
    private Button btnConfirmarCompra;

    String nome = "";
    String localEvento = "";
    String descricao = "";
    String dataString = "";
    String precoString = "";
    int quantidadeIngressos = 0;
    String quantidadeString = "";

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
    public void telaMeusIngressos(ActionEvent event) throws Exception {
        Stage telaLogin = (Stage) ((Node) event.getSource()).getScene().getWindow();
        trocarTela(telaLogin, "/telas/TelaMeusIngressos.fxml", "BilheteFácil");
    }

    @FXML
    public void telaIngressos(ActionEvent event) throws Exception {
        Stage telaIngressos = (Stage) ((Node) event.getSource()).getScene().getWindow();
        trocarTela(telaIngressos, "/telas/TelaPrincipalIngressos.fxml", "BilheteFácil");
    }

    @FXML
    public void telaMinhaConta(ActionEvent event) throws Exception {
        Stage telaConta = (Stage) ((Node) event.getSource()).getScene().getWindow();
        trocarTela(telaConta, "/telas/TelaPrincipalConta.fxml", "BilheteFácil");
    }

    @FXML
    public void telaCadastrarEvento(ActionEvent event) throws Exception {
        Stage telaCadastrarEvento = (Stage) ((Node) event.getSource()).getScene().getWindow();
        trocarTela(telaCadastrarEvento, "/telas/TelaPrincipalCadastrarEvento.fxml", "BilheteFácil");
    }

    @FXML
    public void telaCompra(ActionEvent event) throws Exception {
        Stage telaCompra = (Stage) ((Node) event.getSource()).getScene().getWindow();
        trocarTela(telaCompra, "/telas/TelaCompra.fxml", "BilheteFácil");
    }

    public void confirmarCompra(Event event) {
        try {
            // Limpar o texto do preço e converter para float
            String precoText = lblPrecoEventoCompra.getText().replaceAll("[^0-9.,]", "").replace(",", ".");
            if (precoText.isEmpty()) {
                System.out.println("O campo de preço está vazio.");
                return;
            }

            System.out.println("Preço (Texto Limpo): " + precoText);

            float valorUnitario = Float.parseFloat(precoText);
            int quantidade = Integer.parseInt(txtQuantidadeCompra.getText().trim());
            float valorTotal = quantidade * valorUnitario;

            // Inserir a compra na tabela 'compras'
            String sqlCompra = "INSERT INTO compras (id_usuario, id_evento, id_ingresso, quantidade, valor_total) "
                    + "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement psCompra = Conexao.getConexao().prepareStatement(sqlCompra)) {
                psCompra.setInt(1, ControllerLogin.idUsuario);
                psCompra.setInt(2, ControllerTelaPrincipal.idEvento);
                psCompra.setInt(3, idIngresso);
                psCompra.setInt(4, quantidade);
                psCompra.setFloat(5, valorTotal);

                int rowsInserted = psCompra.executeUpdate();
                if (rowsInserted > 0) {
                    // Atualizar a quantidade de ingressos disponíveis após a compra
                    String sqlUpdateIngressos = "UPDATE ingressos SET quantidade_disponivel = quantidade_disponivel - ? WHERE id_ingresso = ?";
                    try (PreparedStatement psUpdate = Conexao.getConexao().prepareStatement(sqlUpdateIngressos)) {
                        psUpdate.setInt(1, quantidade);
                        psUpdate.setInt(2, idIngresso);
                        psUpdate.executeUpdate();
                    }

                    // Exibir alerta de sucesso
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Compra Confirmada");
                    alert.setHeaderText("Compra realizada com sucesso!");
                    alert.setContentText("Ingressos comprados: " + quantidade);
                    alert.showAndWait();

                    Stage telaIngressos = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    trocarTela(telaIngressos, "/telas/TelaPrincipalIngressos.fxml", "BilheteFácil");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Erro ao converter quantidade ou preço: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void carregarDetalhesEvento(int idEvento) {
        ControllerTelaPrincipal.idEvento = idEvento;

        String sql = "SELECT eventos.nome, eventos.dt_evento, eventos.local_evento, eventos.descricao, "
                + "ingressos.id_ingresso, ingressos.valor, ingressos.quantidade_disponivel "
                + "FROM eventos "
                + "JOIN ingressos ON eventos.id_evento = ingressos.id_evento "
                + "WHERE eventos.id_evento = ?";

        try (PreparedStatement ps = Conexao.getConexao().prepareStatement(sql)) {
            ps.setInt(1, idEvento);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                this.idIngresso = rs.getInt("id_ingresso");

                nome = rs.getString("nome");
                localEvento = rs.getString("local_evento");
                descricao = rs.getString("descricao");
                dataString = rs.getDate("dt_evento") != null
                        ? new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("dt_evento"))
                        : "Data Indisponível";
                precoString = "R$: " + rs.getFloat("valor");
                quantidadeIngressos = rs.getInt("quantidade_disponivel");
                quantidadeString = String.valueOf(quantidadeIngressos);

                lblNomeEventoCompra.setText("Nome: " + nome);
                lblDataEventoCompra.setText("Data: " + dataString);
                lblLocalEventoCompra.setText("Local: " + localEvento);
                lblDescricaoEventoCompra.setText("Descrição: " + descricao);
                lblPrecoEventoCompra.setText("Valor: " + precoString);
                lblQuantidadeIngressosCompra.setText("Ingressos disponíveis: " + quantidadeString);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int idIngresso;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtNomeUsuario.setText(ControllerLogin.nomeUser);
    }
}
