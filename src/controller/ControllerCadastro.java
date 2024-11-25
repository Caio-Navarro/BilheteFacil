package controller;

import conexao.Conexao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerCadastro {
    
    @FXML
    private TextField nome;

    @FXML
    private TextField usuario;

    @FXML
    private TextField senha1;

    @FXML
    private TextField senha2;

    @FXML
    private TextField emailUsuario;

    @FXML
    private TextField telefoneUsuario;

    @FXML
    private DatePicker dataNascimentoUsuario;

    @FXML
    private Button btnCadastrar;
    
    @FXML
    private Hyperlink hlTelaLogin;
    
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
    
    @FXML
    private void cadastrarUsuario(ActionEvent event) {
        String nomeUsuario = nome.getText();
        String email = emailUsuario.getText();
        String telefone = telefoneUsuario.getText();
        LocalDate dataNascimento = dataNascimentoUsuario.getValue();
        String usuarioNome = usuario.getText();
        String senha = senha1.getText();
        String confirmarSenha = senha2.getText();

        if (usuarioExistente(usuarioNome)) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Erro");

            alerta.setHeaderText("Já existe algúem com esse nome de usuário!");
            alerta.showAndWait();
            return;
        }

        if (nomeUsuario.equals("") || usuarioNome.equals("") || senha.equals("") || confirmarSenha.equals("") || email.equals("") || telefone.equals("") || dataNascimentoUsuario.getValue() == null) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Erro");

            alerta.setHeaderText("Por favor, inisira todos os campos!");
            alerta.showAndWait();
        } else if (!senha.equals(confirmarSenha)) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Erro");

            alerta.setHeaderText("As senhas não coincidem!");
            alerta.showAndWait();
            return;
        } else {
            String dataNascimentoStr = dataNascimento.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            String sql = "INSERT INTO usuarios(nome, usuario, senha, email, telefone, dt_nascimento) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = null;

            try {
                ps = Conexao.getConexao().prepareStatement(sql);
                ps.setString(1, nomeUsuario);
                ps.setString(2, usuarioNome);
                ps.setString(3, senha);
                ps.setString(4, email);
                ps.setString(5, telefone);
                ps.setString(6, dataNascimentoStr);

                Alert confirmacao = new Alert(Alert.AlertType.INFORMATION);
                confirmacao.setTitle("Sucesso");
                confirmacao.setHeaderText("Usuario cadastrado com sucesso!");
                confirmacao.showAndWait();
                
                Stage telaLogin = (Stage) ((Node) event.getSource()).getScene().getWindow();
                trocarTela(telaLogin, "/telas/TelaLogin.fxml", "BilheteFácil");

                ps.execute();
                ps.close();
                
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean verificarCredenciais(String usuario, String senha) {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        PreparedStatement ps = null;

        try {
            ps = Conexao.getConexao().prepareStatement(sql);

            ps.setString(1, usuario);
            ps.setString(2, senha);

            ResultSet rs = ps.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
}
