package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import conexao.Conexao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class ControllerLogin implements Initializable {

    @FXML
    private Button btnLogin;

    @FXML
    private TextField user;

    @FXML
    private PasswordField password;

    @FXML
    private Hyperlink hlCadastrese;

    public static String nomeUser;
    public static String senhaUser;
    public static String usuario;
    public static int idUsuario;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

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
    public void telaDeCadastro(ActionEvent event) throws Exception {
        Stage telaCadastro = (Stage) ((Node) event.getSource()).getScene().getWindow();
        trocarTela(telaCadastro, "/telas/TelaCadastro.fxml", "BilheteFácil");
    }

    public boolean verificarCredenciais(String usuario, String senha) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND senha = ?";
        PreparedStatement ps = null;

        try {
            ps = Conexao.getConexao().prepareStatement(sql);

            ps.setString(1, usuario);
            ps.setString(2, senha);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Define o nome do usuário logado
                nomeUser = rs.getString("nome");
                senhaUser = rs.getString("senha");
                idUsuario = rs.getInt("id_usuario");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean usuarioExistente(String usuario) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ?";
        PreparedStatement ps = null;

        try {
            ps = Conexao.getConexao().prepareStatement(sql);

            ps.setString(1, usuario);

            ResultSet rs = ps.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    public void login(ActionEvent event) {
        usuario = user.getText();
        String senha = password.getText();

        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle("Erro");

        if (usuario.equals("") || senha.equals("")) {
            alerta.setHeaderText("Insira todos os campos!");
            alerta.showAndWait();
            return;
        }
        
        if(usuario.equals("admin") && senha.equals("123")){
            Alert confirmacao = new Alert(AlertType.INFORMATION);
            confirmacao.setTitle("Sucesso");
            confirmacao.setHeaderText("Usuario logado como administrador.");
            confirmacao.showAndWait();
            
            Stage telaAdmin = (Stage) ((Node) event.getSource()).getScene().getWindow();
            trocarTela(telaAdmin, "/telas/TelaAdmin.fxml", "BilheteFácil - ADMIN");
            return;
        }

        if (verificarCredenciais(usuario, senha)) {
            Alert confirmacao = new Alert(AlertType.INFORMATION);
            confirmacao.setTitle("Sucesso");
            confirmacao.setHeaderText("Usuario logado com sucesso!");
            confirmacao.showAndWait();

            Stage telaPrincipal = (Stage) ((Node) event.getSource()).getScene().getWindow();
            trocarTela(telaPrincipal, "/telas/TelaPrincipalIngressos.fxml", "BilheteFácil");
        } else {
            alerta.setHeaderText("Usuario ou senha invalidos!");
            alerta.showAndWait();
        }
    }
}
