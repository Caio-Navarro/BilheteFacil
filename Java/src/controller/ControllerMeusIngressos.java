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
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ControllerMeusIngressos implements Initializable {

    @FXML
    private Label txtNomeUsuario;

    @FXML
    private VBox vboxIngressos;

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

    public void carregarMeusIngressos() {
        String sql = "SELECT c.id_compra, e.nome, c.quantidade, c.valor_total, e.descricao, e.local_evento, e.dt_evento "
                + "FROM compras c "
                + "JOIN eventos e ON c.id_evento = e.id_evento "
                + "JOIN ingressos i ON c.id_ingresso = i.id_ingresso "
                + "WHERE c.id_usuario = ?";

        boolean encontrouIngressos = false;

        try (PreparedStatement ps = Conexao.getConexao().prepareStatement(sql)) {
            ps.setInt(1, ControllerLogin.idUsuario);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                encontrouIngressos = true;

                String nomeEvento = rs.getString("nome");
                int quantidade = rs.getInt("quantidade");
                float valorTotal = rs.getFloat("valor_total");
                String descricao = rs.getString("descricao");
                String localEvento = rs.getString("local_evento");
                Date dataEvento = rs.getDate("dt_evento");
                String dataStringIng = new SimpleDateFormat("dd/MM/yyyy").format(dataEvento);

                criarCardIngresso(nomeEvento, quantidade, valorTotal, descricao, localEvento, dataStringIng);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!encontrouIngressos) {
            criarMensagemSemIngressos();
        }
    }

    private void criarCardIngresso(String nomeEvento, int quantidade, float valorTotal, String descricao, String localEvento, String dataEvento) {
        HBox card = new HBox();
        card.setSpacing(20);
        card.setStyle("-fx-padding: 15; -fx-border-color: #6A5ACD; -fx-border-width: 2; -fx-background-color: #FFFFFF; -fx-background-radius: 10; -fx-border-radius: 10;");

        VBox detalhes = new VBox();
        detalhes.setSpacing(8);

        Label lblNomeEvento = new Label(nomeEvento);
        lblNomeEvento.setFont(new Font("Arial Bold", 18));
        lblNomeEvento.setStyle("-fx-text-fill: #6A5ACD; -fx-font-weight: bold;");

        Label lblDataEvento = new Label("Data: " + dataEvento);
        lblDataEvento.setStyle("-fx-text-fill: #4B4B4B; -fx-font-size: 14;");

        Label lblLocalEvento = new Label("Local: " + localEvento);
        lblLocalEvento.setStyle("-fx-text-fill: #4B4B4B; -fx-font-size: 14;");

        Label lblDescricao = new Label("Descrição: " + descricao);
        lblDescricao.setStyle("-fx-text-fill: #4B4B4B; -fx-font-size: 14;");

        Label lblQuantidade = new Label("Quantidade: " + quantidade);
        lblQuantidade.setStyle("-fx-text-fill: #4B4B4B; -fx-font-size: 14;");

        Label lblValor = new Label("Valor Total: R$" + valorTotal);
        lblValor.setStyle("-fx-text-fill: #4B4B4B; -fx-font-size: 14;");

        detalhes.getChildren().addAll(lblNomeEvento, lblDataEvento, lblLocalEvento, lblDescricao, lblQuantidade, lblValor);

        card.getChildren().add(detalhes);

        vboxIngressos.getChildren().add(card);
    }

    private void criarMensagemSemIngressos() {
        Label lblMensagem = new Label("Você ainda não comprou nenhum ingresso.");
        lblMensagem.setStyle("-fx-text-fill: #666666; -fx-font-size: 16px; -fx-font-weight: bold; -fx-alignment: center; -fx-padding: 20;");

        VBox vboxMensagem = new VBox(lblMensagem);
        vboxMensagem.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #cccccc; -fx-border-width: 2; -fx-padding: 20; -fx-alignment: center;");
        vboxMensagem.setSpacing(10);

        vboxIngressos.getChildren().add(vboxMensagem);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtNomeUsuario.setText(ControllerLogin.nomeUser);
        carregarMeusIngressos();
    }
}
