package controller;

import conexao.Conexao;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControllerTelaPrincipal implements Initializable {

    @FXML
    private Label txtNomeUsuario;

    @FXML
    private Label lblDataNascimento;

    @FXML
    private Button btnIngressos;

    @FXML
    private Button btnMeusIngressos;

    @FXML
    private Button btnMinhaConta;

    @FXML
    private Button btnSair;

    //-----------------------------tela de ingressos
    @FXML
    private VBox vboxEventos;

    @FXML
    private ScrollPane scrollPaneEventos;

    //----------------------------------
    @FXML
    private Button btnCadastrarEvento;

    @FXML
    private Button btnTelaCadastrarEvento;

    //-------------------------------------
    @FXML
    private TextField tfNomeUsuario;

    @FXML
    private TextField tfUserNome;

    @FXML
    private DatePicker dpDataNascimento;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfTelefone;

    @FXML
    private TextField tfSenhaAtual;

    @FXML
    private TextField tfSenhaNova;

    @FXML
    private Button btnConfirmar;

    public static int idEvento;

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
    public void telaMinhaConta(ActionEvent event) throws Exception {
        Stage telaConta = (Stage) ((Node) event.getSource()).getScene().getWindow();
        trocarTela(telaConta, "/telas/TelaPrincipalConta.fxml", "BilheteFácil");
    }

    @FXML
    public void telaCompra(ActionEvent event) throws Exception {
        Stage telaCompra = (Stage) ((Node) event.getSource()).getScene().getWindow();
        trocarTela(telaCompra, "/telas/TelaCompra.fxml", "BilheteFácil");
    }

    @FXML
    public void telaMeusIngressos(ActionEvent event) throws Exception {
        Stage telaMeusIngressos = (Stage) ((Node) event.getSource()).getScene().getWindow();
        trocarTela(telaMeusIngressos, "/telas/TelaMeusIngressos.fxml", "BilheteFácil");
    }

    public void inicializarEventos() {
        String sql = "SELECT eventos.id_evento, eventos.nome, eventos.dt_evento, eventos.local_evento, eventos.descricao, "
                + "ingressos.valor, ingressos.quantidade_disponivel "
                + "FROM eventos "
                + "JOIN ingressos ON eventos.id_evento = ingressos.id_evento";

        try (PreparedStatement ps = Conexao.getConexao().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idEvento = rs.getInt("id_evento");
                String nome = rs.getString("nome");
                String dataString = rs.getDate("dt_evento") != null
                        ? new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("dt_evento"))
                        : "Data Indisponível";
                String local = rs.getString("local_evento");
                String descricao = rs.getString("descricao");
                String precoString = "R$: " + rs.getFloat("valor");
                int quantidade = rs.getInt("quantidade_disponivel");

                Pane card = criarCardEvento(idEvento, nome, dataString, local, descricao, precoString, quantidade);
                vboxEventos.getChildren().add(card); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Pane criarCardEvento(int idEvento, String nome, String data, String local, String descricao, String preco, int quantidade) {
        Pane card = new Pane();
        card.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10; -fx-padding: 10;");
        card.setPrefSize(750, 150);

        Label lblNome = new Label(nome);
        lblNome.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #333333;");
        lblNome.setLayoutX(20);
        lblNome.setLayoutY(10);

        Label lblData = new Label("Data: " + data);
        lblData.setStyle("-fx-font-size: 16;");
        lblData.setLayoutX(20);
        lblData.setLayoutY(50);

        Label lblLocal = new Label("Local: " + local);
        lblLocal.setStyle("-fx-font-size: 16;");
        lblLocal.setLayoutX(20);
        lblLocal.setLayoutY(80);

        Label lblPreco = new Label(preco);
        lblPreco.setStyle("-fx-font-size: 16; -fx-text-fill: #6A5ACD;");
        lblPreco.setLayoutX(500);
        lblPreco.setLayoutY(10);

        Label lblQuantidade = new Label("Disponível: " + quantidade);
        lblQuantidade.setStyle("-fx-font-size: 16;");
        lblQuantidade.setLayoutX(500);
        lblQuantidade.setLayoutY(50);

        Button btnComprar = new Button("Comprar");
        btnComprar.setStyle("-fx-background-color: #6A5ACD; -fx-text-fill: white; -fx-background-radius: 15;");
        btnComprar.setPrefSize(100, 30);
        btnComprar.setLayoutX(500);
        btnComprar.setLayoutY(100);
        btnComprar.setOnAction(event -> abrirTelaCompra(event, idEvento));

        card.getChildren().addAll(lblNome, lblData, lblLocal, lblPreco, lblQuantidade, btnComprar);

        return card;
    }

    private void abrirTelaCompra(ActionEvent event, int idEvento) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/TelaCompra.fxml"));
            Parent root = loader.load();

            ControllerTelaCompra controller = loader.getController();
            controller.carregarDetalhesEvento(idEvento);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void alterarInformacoes(Event event) {
        String nomeUsuario = tfNomeUsuario.getText();
        String userNome = tfUserNome.getText();
        LocalDate dataNascimento = dpDataNascimento.getValue();
        String email = tfEmail.getText();
        String telefone = tfTelefone.getText();
        String senhaAtual = tfSenhaAtual.getText();
        String senhaNova = tfSenhaNova.getText();

        String sql = "UPDATE usuarios SET nome = ?, usuario = ?, senha = ?, email = ?, telefone = ?, dt_nascimento = ? WHERE id_usuario = ?";
        PreparedStatement ps = null;

        if (nomeUsuario.equals("") || userNome.equals("") || senhaAtual.equals("") || senhaNova.equals("") || email.equals("") || telefone.equals("") || dpDataNascimento.getValue() == null) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Erro");

            alerta.setHeaderText("Por favor, preencha todos os campos!");
            alerta.showAndWait();
        } else {
            try {
                ps = Conexao.getConexao().prepareStatement(sql);

                ps.setString(1, nomeUsuario);
                ps.setString(2, userNome);
                ps.setString(3, senhaNova);
                ps.setString(4, email);
                ps.setString(5, telefone);
                ps.setDate(6, java.sql.Date.valueOf(dataNascimento));
                ps.setInt(7, ControllerLogin.idUsuario);

                ps.executeUpdate();
                ps.close();

                Alert confirmacao = new Alert(Alert.AlertType.INFORMATION);
                confirmacao.setTitle("Sucesso");
                confirmacao.setHeaderText("Usuario editado com sucesso!");
                confirmacao.showAndWait();

                Stage telaConta = (Stage) ((Node) event.getSource()).getScene().getWindow();
                trocarTela(telaConta, "/telas/TelaPrincipalConta.fxml", "BilheteFácil");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtNomeUsuario.setText(ControllerLogin.nomeUser);

        try {
            inicializarEventos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
