package com.programacao.web.webstore;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UsuarioController {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String password;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var error = Optional.ofNullable(request.getParameter("error-message"));

        response.setContentType("text/html");
        var writer = response.getWriter();
        writer.println("<!DOCTYPE html>");
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Login</title>");
        writer.println("<link rel=\"stylesheet\" href=\"/styles.css\">");
        writer.println("</head>");

        writer.println("<body>");

        writer.println("<main class=\"flex w-full h-full items-center justify-center\">");
        writer.println("<div class=\"flex flex-col  w-30 border rounded p-6\">");
        writer.println("<h1>LOGIN</h1>");
        writer.println("<form class=\"flex flex-col gap-2\" action=\"/auth\" method=\"post\">");
        writer.println("<label for=\"email\">");
        writer.println("Email");
        writer.println("</label>");
        writer.println("<input type=\"text\" id=\"email\" name=\"email\"/>");

        error.filter(e -> e.contains("Email não encontrado")).ifPresent(e -> {
            writer.println("<span class=\"text-red\">Usuário não encontrado com esse email!</span>");
        });

        writer.println("<label for=\"password\">");
        writer.println("Senha");
        writer.println("</label>");
        writer.println("<input type=\"password\" id=\"password\" name=\"password\"/>");

        error.filter(e -> e.contains("Senha incorreta")).ifPresent(e -> {
            writer.println("<span class=\"text-red\">Senha incorreta!</span>");
        });

        writer.println("<button type=\"submit\">Entrar</button>");

        writer.println("</form>");
        writer.println("</div>");

        writer.println("</main>");

        writer.println("</body>");
        writer.println("</html>");
    }

    @RequestMapping(value = "/cadastro", method = RequestMethod.GET)
    public void cadastro(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var error = Optional.ofNullable(request.getParameter("error-message"));

        response.setContentType("text/html");
        var writer = response.getWriter();
        writer.println("<!DOCTYPE html>");
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Cadastro</title>");
        writer.println("<link rel=\"stylesheet\" href=\"/styles.css\">");
        writer.println("</head>");

        writer.println("<body>");

        writer.println("<main class=\"flex w-full h-full items-center justify-center\">");
        writer.println("<div class=\"flex flex-col  w-30 border rounded p-6\">");

        writer.println("<h1>Cadastro</h1>");
        writer.println("<form class=\"flex flex-col gap-2\" action=\"/register\" method=\"post\">");

        writer.println("<label for=\"name\">");
        writer.println("Nome");
        writer.println("</label>");
        writer.println("<input type=\"text\" id=\"name\" name=\"name\"/>");

        writer.println("<label for=\"email\">");
        writer.println("Email");
        writer.println("</label>");
        writer.println("<input type=\"text\" id=\"email\" name=\"email\"/>");

        error.filter(e -> e.contains("Email inválido")).ifPresent(e -> {
            writer.println("<span class=\"text-red\">Email Inválido! Digite novamente.</span>");
        });

        writer.println("<label for=\"password\">");
        writer.println("Senha");
        writer.println("</label>");
        writer.println("<input type=\"password\" id=\"password\" name=\"password\"/>");

        error.filter(e -> e.contains("Senha inválida")).ifPresent(e -> {
            writer.println("<span class=\"text-red\">Senha Inválida! Digite novamente.</span>");
        });

        writer.println("<label for=\"role\">");
        writer.println("Papel");
        writer.println("</label>");
        writer.println("<select id=\"role\" name=\"role\">");
        writer.println("<option value=\"Cliente\">Cliente</option>");
        writer.println("<option value=\"Lojista\">Logista</option>");
        writer.println("</select>");

        writer.println("<button type=\"submit\">Entrar</button>");

        writer.println("</form>");
        writer.println("</div>");

        writer.println("</main>");

        writer.println("</body>");
        writer.println("</html>");
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void realizarCadastro(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String VALID_EMAIL_PATTERN = "^[^@]+@[^@]+\\.[a-zA-Z]{2,}$";

        Usuario usuario = new Usuario();
        usuario.setName(request.getParameter("name"));
        usuario.setEmail(request.getParameter("email"));
        usuario.setPassword(request.getParameter("password"));
        usuario.setRole(request.getParameter("role"));

        if (!usuario.getEmail().matches(VALID_EMAIL_PATTERN)) {
            response.sendRedirect(UriComponentsBuilder.fromUriString("/cadastro")
                    .queryParam("error-message", "Email inválido")
                    .toUriString());
            return;
        }

        if (usuario.getPassword().length() < 5) {
            response.sendRedirect(UriComponentsBuilder.fromUriString("/cadastro")
                    .queryParam("error-message", "Senha inválida")
                    .toUriString());
            return;
        }

        String sql = """
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

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public void autenticar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String sql = """
                select * from usuario
                where email = ?
                """;
        Usuario usuarioAtual = null;
        try (Connection conn = getConnection()) {
            PreparedStatement statement = conn.prepareStatement(sql);

            statement.setString(1, email);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                usuarioAtual = new Usuario();
                usuarioAtual.setId(result.getLong("id"));
                usuarioAtual.setName(result.getString("name"));
                usuarioAtual.setEmail(result.getString("email"));
                usuarioAtual.setPassword(result.getString("password"));
                usuarioAtual.setRole(result.getString("role"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (usuarioAtual == null) {
            response.sendRedirect(UriComponentsBuilder.fromUriString("/login")
                    .queryParam("error-message", "Email não encontrado")
                    .toUriString());
            return;
        }

        if (!usuarioAtual.getPassword().equals(password)) {
            response.sendRedirect(UriComponentsBuilder.fromUriString("/login")
                    .queryParam("error-message", "Senha incorreta")
                    .toUriString());
            return;
        }

        var session = request.getSession();
        if (session.isNew()) {
            session.setAttribute("user-id", usuarioAtual.getId());
            session.setAttribute("user-role", usuarioAtual.getRole());
        }

        switch (usuarioAtual.getRole()) {
            case "Cliente":
                // Lista Produtos (caso de uso)
                break;
            case "Lojista":
                // Exibe Produtos (caso de uso)
                break;
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

}
