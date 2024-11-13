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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ControllerMeusIngressos implements Initializable{
    @FXML
    private Label txtNomeUsuario;
    
    //-----------------------------------Tela meus ingressos
    @FXML
    private Label lblNomeEventoIng;

    @FXML
    private Label lblDataEventoIng;

    @FXML
    private Label lblLocalEventoIng;

    @FXML
    private Label lblDescricaoEventoIng;

    @FXML
    private Label lblValorTotalIng;

    @FXML
    private Label lblQuantidadeCompradaIng;
    
    @FXML
    private Button btnCarregarIngressos;
    
    int idCompra;
    String nomeEvento;
    int quantidade;
    float valorTotal;
    String descricao;
    String localEvento;
    String dataStringIng;
    String valorTotalIng;
    String quantidadeIng;
    
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
    
    public void carregarMeusIngressos() {
        String sql = "SELECT c.id_compra, e.nome, c.quantidade, c.valor_total, e.descricao, e.local_evento, e.dt_evento "
                + "FROM compras c "
                + "JOIN eventos e ON c.id_evento = e.id_evento "
                + "JOIN ingressos i ON c.id_ingresso = i.id_ingresso "
                + "WHERE c.id_usuario = ?";

        try (PreparedStatement ps = Conexao.getConexao().prepareStatement(sql)) {
            ps.setInt(1, ControllerLogin.idUsuario);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                idCompra = rs.getInt("id_compra");
                nomeEvento = rs.getString("nome");
                quantidade = rs.getInt("quantidade");
                valorTotal = rs.getFloat("valor_total");
                descricao = rs.getString("descricao");
                localEvento = rs.getString("local_evento");
                Date dataEvento = rs.getDate("dt_evento");

                dataStringIng = new SimpleDateFormat("dd/MM/yyyy").format(dataEvento);

                valorTotalIng = String.valueOf(valorTotal);
                quantidadeIng = String.valueOf(quantidade);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtNomeUsuario.setText(ControllerLogin.nomeUser);
        
        carregarMeusIngressos();
        lblNomeEventoIng.setText("Nome: " + nomeEvento);
        lblDataEventoIng.setText("Data: " + dataStringIng);
        lblLocalEventoIng.setText("Local: " + localEvento);
        lblDescricaoEventoIng.setText("Descrição: " + descricao);
        lblValorTotalIng.setText("Valor total: R$" + valorTotalIng);
        lblQuantidadeCompradaIng.setText("Quantidade comprada: " + quantidadeIng);
    }
}
