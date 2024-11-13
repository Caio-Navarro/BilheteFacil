package conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    
    private static final String url = "jdbc:mysql://localhost:3306/vendaingressos";
    private static final String user = "root";
    private static final String password = "";
    
    private static Connection conexao;
    
    public static Connection getConexao(){
        
        try {
            if(conexao == null){
                conexao = DriverManager.getConnection(url, user, password);
                System.out.println("Conexão estabelecida com sucesso!");
            }
            return conexao;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao conectar: " + e.getMessage());
            return null;
        }
    }
}
