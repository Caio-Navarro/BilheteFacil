package controller;

import conexao.Conexao;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Optional;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ControllerTelaPrincipalConta implements Initializable {

    @FXML
    private Label txtNomeUsuario;

    @FXML
    private Label lblUserNome;

    @FXML
    private Label lblNomeUser;

    @FXML
    private Label lblDataNascimento;

    @FXML
    private Label lblTelefone;

    @FXML
    private Label lblEmail;

    @FXML
    private Button btnEditarInformacoes;

    //------------------------
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
    public void telaCadastrarEvento(ActionEvent event) throws Exception {
        Stage telaConta = (Stage) ((Node) event.getSource()).getScene().getWindow();
        trocarTela(telaConta, "/telas/TelaPrincipalCadastrarEvento.fxml", "BilheteFácil");
    }

    @FXML
    public void telaEditarInfo(ActionEvent event) throws Exception {
        Stage telaEditarInfo = (Stage) ((Node) event.getSource()).getScene().getWindow();
        trocarTela(telaEditarInfo, "/telas/TelaEditarInfo.fxml", "BilheteFácil");
    }
    
    @FXML
    public void telaMeusIngressos(ActionEvent event) throws Exception {
        Stage telaMeusIngressos = (Stage) ((Node) event.getSource()).getScene().getWindow();
        trocarTela(telaMeusIngressos, "/telas/TelaMeusIngressos.fxml", "BilheteFácil");
    }

    public void infoUsuario() {
        String nome = "";
        Date dtNascimento = null;
        String usuarioNome = "";
        String email = "";
        String telefone = "";

        String sql = "SELECT nome, usuario, dt_nascimento, email, telefone FROM usuarios WHERE usuario = ? AND senha = ?";
        PreparedStatement ps = null;

        try {
            ps = Conexao.getConexao().prepareStatement(sql);
            ps.setString(1, ControllerLogin.usuario);
            ps.setString(2, ControllerLogin.senhaUser);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nome = rs.getString("nome");
                dtNascimento = rs.getDate("dt_nascimento");
                usuarioNome = rs.getString("usuario");
                email = rs.getString("email");
                telefone = rs.getString("telefone");

                String dataComoString = (dtNascimento != null)
                        ? new SimpleDateFormat("dd/MM/yyyy").format(dtNascimento) : "";

                lblNomeUser.setTextFill(Color.web("#333333"));
                lblNomeUser.setText(nome);
                lblUserNome.setText(usuarioNome);
                lblDataNascimento.setText(dataComoString);
                lblTelefone.setText(telefone);
                lblEmail.setText(email);
            } else {
                System.out.println("Nenhum usuário encontrado com essas credenciais.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar o usuário: " + e.getMessage());
        }
    }

    public void excluirConta(Event event) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Excluir conta");
        confirmacao.setHeaderText("Tem certeza que deseja excluir sua conta?");
        confirmacao.setContentText("Escolha uma opção:");

        Optional<ButtonType> resultado = confirmacao.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String sql = "DELETE FROM usuarios WHERE usuario = ? AND senha = ?";

            try (PreparedStatement ps = Conexao.getConexao().prepareStatement(sql)){

                ps.setString(1, ControllerLogin.usuario);
                ps.setString(2, ControllerLogin.senhaUser);
                ps.execute();
                
                Alert aviso = new Alert(Alert.AlertType.INFORMATION);
                aviso.setTitle("Conta excluída");
                aviso.setContentText("Sua conta foi excluída com sucesso. Até mais!");
                aviso.showAndWait();
            
                Stage telaLogin = (Stage) ((Node) event.getSource()).getScene().getWindow();
                trocarTela(telaLogin, "/telas/TelaLogin.fxml", "BilheteFácil");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        infoUsuario();
        txtNomeUsuario.setText(ControllerLogin.nomeUser);
    }
}