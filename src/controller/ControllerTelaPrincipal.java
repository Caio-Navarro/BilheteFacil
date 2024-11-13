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
import javafx.scene.control.TextField;
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


    //----------------------------------------- tela de ingressos
    @FXML
    private Label lblNomeEvento;

    @FXML
    private Label lblDataEvento;

    @FXML
    private Label lblLocalEvento;

    @FXML
    private Label lblDescricaoEvento;

    @FXML
    private Label lblQuantidadeIngressos;

    @FXML
    private Label lblPrecoEvento;

    @FXML
    private Button btnComprar;

    @FXML
    private Label lblNomeEvento2;

    @FXML
    private Label lblDataEvento2;

    @FXML
    private Label lblLocalEvento2;

    @FXML
    private Label lblDescricaoEvento2;

    @FXML
    private Label lblQuantidadeIngressos2;

    @FXML
    private Label lblPrecoEvento2;

    @FXML
    private Button btnComprar2;
    
    @FXML
    private Label lblNomeEvento3;

    @FXML
    private Label lblDataEvento3;

    @FXML
    private Label lblLocalEvento3;

    @FXML
    private Label lblDescricaoEvento3;

    @FXML
    private Label lblQuantidadeIngressos3;

    @FXML
    private Label lblPrecoEvento3;

    @FXML
    private Button btnComprar3;
    
    @FXML
    private Label lblNomeEvento4;

    @FXML
    private Label lblDataEvento4;

    @FXML
    private Label lblLocalEvento4;

    @FXML
    private Label lblDescricaoEvento4;

    @FXML
    private Label lblQuantidadeIngressos4;

    @FXML
    private Label lblPrecoEvento4;

    @FXML
    private Button btnComprar4;

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
        ControllerTelaCompra cp = new ControllerTelaCompra();

        String sql = "SELECT eventos.id_evento, eventos.nome, eventos.dt_evento, eventos.local_evento, eventos.descricao, "
                + "ingressos.valor, ingressos.quantidade_disponivel "
                + "FROM eventos "
                + "JOIN ingressos ON eventos.id_evento = ingressos.id_evento";

        try (PreparedStatement ps = Conexao.getConexao().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            int index = 1;

            while (rs.next()) {
                int idEvento = rs.getInt("id_evento");
                String nome = rs.getString("nome");
                String dataString = rs.getDate("dt_evento") != null
                        ? new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("dt_evento"))
                        : "Data Indisponível";
                String precoString = "R$: " + rs.getFloat("valor");

                switch (index) {
                    case 1:
                        lblNomeEvento.setText(nome);
                        lblDataEvento.setText(dataString);
                        lblPrecoEvento.setText(precoString);

                        btnComprar.setOnAction(event -> abrirTelaCompra(event, idEvento));
                        break;
                    case 2:
                        lblNomeEvento2.setText(nome);
                        lblDataEvento2.setText(dataString);
                        lblPrecoEvento2.setText(precoString);

                        btnComprar2.setOnAction(event -> abrirTelaCompra(event, idEvento));
                        break;
                    case 3:
                        lblNomeEvento3.setText(nome);
                        lblDataEvento3.setText(dataString);
                        lblPrecoEvento3.setText(precoString);

                        btnComprar3.setOnAction(event -> abrirTelaCompra(event, idEvento));
                    case 4:
                        lblNomeEvento4.setText(nome);
                        lblDataEvento4.setText(dataString);
                        lblPrecoEvento4.setText(precoString);

                        btnComprar4.setOnAction(event -> abrirTelaCompra(event, idEvento));
                    default:
                        System.out.println("Mais eventos encontrados, mas sem labels para exibir.");
                        break;
                }
                index++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

        String sql = "UPDATE usuarios SET nome = ?, usuario = ?, senha = ?, email = ?, telefone = ?, dt_nascimento = ? WHERE usuario = ? AND senha = ?";
        PreparedStatement ps = null;

        if (nomeUsuario.equals("") || userNome.equals("") || senhaAtual.equals("") || senhaNova.equals("") || email.equals("") || telefone.equals("") || dpDataNascimento.getValue() == null) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Erro");

            alerta.setHeaderText("Por favor, inisira todos os campos!");
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
                ps.setString(7, ControllerLogin.usuario);
                ps.setString(8, ControllerLogin.senhaUser);

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
