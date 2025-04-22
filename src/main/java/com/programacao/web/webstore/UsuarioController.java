package com.programacao.web.webstore;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



@Controller
public class UsuarioController {
    @Value("${spring.datasource.url}")
    private String user;
    @Value("${spring.datasource.username}")
    private String password;
    @Value("${spring.datasource.password}")
    private String url;


    @RequestMapping(value="/usuario", method=RequestMethod.POST)
    public void realizarCadastro(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Usuario usuario = new Usuario();
        usuario.setName(request.getParameter("name"));
        usuario.setEmail(request.getParameter("email"));
        usuario.setPassword(request.getParameter("password"));
        usuario.setRole(request.getParameter("role"));
        
        String sql = 
            """
            insert into usuario (name, email, password, role) 
            values (?, ?, ?, ?) 
            """;

        try (Connection conn = getConnection()) {
            PreparedStatement statement = conn.prepareStatement(sql);
            
            statement.setString(1, usuario.getName());
            statement.setString(2, usuario.getEmail());
            statement.setString(3, usuario.getPassword());
            statement.setString(4, usuario.getRole());

            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        response.sendRedirect("/login");
    }

    @RequestMapping(value="/auth", method=RequestMethod.GET)
    public void autenticar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String sql = 
            """
            select * from usuario 
            where email = ?
            """;
        Usuario usuarioAtual = new Usuario();
        try (Connection conn = getConnection()) {
            PreparedStatement statement = conn.prepareStatement(sql);
            
            statement.setString(1, email);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                usuarioAtual.setName(result.getString("name"));
                usuarioAtual.setEmail(result.getString("email"));
                usuarioAtual.setPassword(result.getString("password"));
                usuarioAtual.setRole(result.getString("role"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        if(!usuarioAtual.getPassword().equals(password)) {
            response.sendRedirect("/login");
        }

        // Criar Sessão

        // Redirecionar para página correspondente
        // Se role = Cliente, Lista Produtos (caso de uso)
        // Se role = Lojista, Exibe Produtos (caso de uso)
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
    
}
