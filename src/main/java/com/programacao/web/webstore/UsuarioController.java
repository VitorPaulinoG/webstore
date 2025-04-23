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

    @RequestMapping(value="/login", method=RequestMethod.GET)
    public void login (HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        var writer = response.getWriter();
        writer.println("<!DOCTYPE html>");
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Login</title>");
        writer.println(
            """
            <style>
                html, body {
                    width: 100%;
                    height: 100%;
                }

                .flex {
                    display: flex;
                }

                .flex-row {
                    flex-direction: row;
                }

                .flex-col {
                    flex-direction: column;
                }

                .p-2 {
                    padding: 0.5rem; 
                }

                .border {
                    border: 1px solid black;
                }
                .rounded {
                    border-radius: 8px; 
                }

                .w-30 {
                    width: 30rem;
                }
            </style>
            """);
        writer.println("</head>");

        writer.println("<body>");

        writer.println("<main class=\"flex flex-col w-30\">");

        writer.println("<h1>LOGIN</h1>");
        writer.println("<form class=\"flex flex-col\" action=\"/auth\" method=\"post\">");
        writer.println("<label for=\"email\">");
        writer.println("Email");
        writer.println("</label>");
        writer.println("<input type=\"text\" id=\"email\" name=\"email\"");
        writer.println("<label for=\"password\">");
        writer.println("Senha");
        writer.println("</label>");
        writer.println("<input type=\"password\" id=\"password\" name=\"password\"/>");
        writer.println("<button type=\"submit\">Entrar</button>");
        
        writer.println("</form>");
        
        writer.println("</main>");

        writer.println("</body>");
        writer.println("</html>");
    }   
    

    @RequestMapping(value="/register", method=RequestMethod.POST)
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
