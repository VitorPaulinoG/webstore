package com.programacao.web.webstore;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ProdutoController {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @RequestMapping(value = "/cliente/produtos", method = RequestMethod.GET)
    public void listarProdutos(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Produto> produtos = findAll();

        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Lista de Produtos</title>");
        writer.println("<link rel=\"stylesheet\" href=\"/styles.css\">");
        writer.println("</head>");
        writer.println("<body>");

        writer.println("<header class=\"h-3 py-2 flex justify-center\">");
        writer.println("<div class=\"h-full flex items-center justify-between border rounded w-54 px-2\">");
        writer.println("<div></div>");
        writer.println(
                "<form action=\"/logout\" method=\"post\" class=\"w-auto m-0\"><button class=\"w-auto\" type=\"submit\">Deslogar</button></form>");
        writer.println("</div>");
        writer.println("</header>");

        writer.println("<main class=\"grid grid-col-1 grid-row-auto-full w-full h-full justify-center items-center\">");
        writer.println(
                "<div style=\"margin: 30px auto; padding: 20px; min-height: 30rem\" class=\"flex flex-col items-center border rounded w-54 border-box gap-2\">");
        writer.println("<h2 class=\"m-0\">Lista de Produtos</h2>");
        writer.println("<table>");
        writer.println("<tr>");
        writer.println("<th>Nome</th>");
        writer.println("<th>Descrição</th>");
        writer.println("<th>Preço</th>");
        writer.println("<th>Estoque</th>");
        writer.println("<th>Carrinho</th>");
        writer.println("</tr>");

        for (Produto produto : produtos) {
            writer.println("<tr>");
            writer.println("<td>" + produto.getNome() + "</td>");
            writer.println("<td>" + produto.getDescricao() + "</td>");
            writer.println("<td>" + produto.getPreco() + "</td>");
            writer.println("<td>" + produto.getEstoque() + "</td>");
            writer.println("<td><a href='/carrinho/" + produto.getId() + "'>Adicionar</a></td>");
            writer.println("</tr>");
        }

        writer.println("</table>");
        writer.println("<form action='/carrinho' method='get' class=\"w-full\">");
        writer.println("<button type='submit'>Ver Carrinho</button>");
        writer.println("</form>");
        writer.println("</div>");
        writer.println("</main>");
        writer.println("</body>");
        writer.println("</html>");
    }

    @RequestMapping(value = "/lojista/produto", method = RequestMethod.GET)
    public void cadastrarProduto(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Cadastrar Produto</title>");
        writer.println("<link rel=\"stylesheet\" href=\"/styles.css\">");
        writer.println("</head>");
        writer.println("<body>");

        writer.println("<header class=\"h-3 py-2 flex justify-center\">");
        writer.println("<div class=\"h-full flex items-center justify-between border rounded w-54 px-2\">");
        writer.println(
                "<form action=\"produtos\" method=\"get\" class=\"w-auto m-0\"><button class=\"w-auto\" type=\"submit\">Voltar</button></form>");
        writer.println(
                "<form action=\"/logout\" method=\"post\" class=\"w-auto m-0\"><button class=\"w-auto\" type=\"submit\">Deslogar</button></form>");
        writer.println("</div>");
        writer.println("</header>");

        writer.println("<main class=\"grid grid-col-1 grid-row-auto-full w-full h-full justify-center items-center\">");
        writer.println("<head>");
        writer.println("</head>");
        writer.println(
                "<div style=\"margin: 30px auto; padding: 20px;\" class=\"border rounded w-50\">");
        writer.println("<h2>Cadastrar Novo Produto</h2>");
        writer.println("<form class=\"flex flex-col gap-2\"  action='/lojista/produto' method='post'>");
        writer.println("<label for='nome'>Nome do Produto:</label>");
        writer.println("<input type='text' id='nome' name='nome' required>");
        writer.println("<label for='descricao'>Descrição:</label>");
        writer.println("<input type='text' id='descricao' name='descricao' required>");
        writer.println("<label for='preco'>Preço:</label>");
        writer.println("<input type='number' id='preco' name='preco' step='0.01' required>");
        writer.println("<label for='estoque'>Estoque:</label>");
        writer.println("<input type='number' id='estoque' name='estoque' required>");
        writer.println("<button type='submit' class='btn-canto-pagina'>Cadastrar</button>");
        writer.println("</form>");
        writer.println("</div>");
        writer.println("</main>");
        writer.println(
                "<form action=\"/logout\" method=\"post\"><button type=\"submit\" class='btn-deslogar''>Deslogar</button></form>");
        writer.println("</body>");
        writer.println("</html>");

    }

    @RequestMapping(value = "/lojista/produto", method = RequestMethod.POST)
    public void inserirProduto(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");
        double preco = Double.parseDouble(request.getParameter("preco"));
        int estoque = Integer.parseInt(request.getParameter("estoque"));
        Produto p = new Produto(nome, descricao, preco, estoque);

        String sql = "INSERT INTO produtos (nome, descricao, preco, estoque) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(this.url, this.user, this.password);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getDescricao());
            stmt.setDouble(3, p.getPreco());
            stmt.setInt(4, p.getEstoque());

            stmt.executeUpdate();
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.sendRedirect("/lojista/produtos");

        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao inserir produto.");
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/lojista/produtos", method = RequestMethod.GET)
    public void exibirProdutos(HttpServletResponse response) throws IOException {
        List<Produto> produtos = findAll();
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Lista de Produtos</title>");
        writer.println("<link rel=\"stylesheet\" href=\"/styles.css\">");
        writer.println("</head>");
        writer.println("<body>");

        writer.println("<header class=\"h-3 py-2 flex justify-center\">");
        writer.println("<div class=\"h-full flex items-center justify-between border rounded w-54 border-box px-2\">");
        writer.println("<div></div>");
        writer.println(
                "<form action=\"/logout\" method=\"post\" class=\"w-auto m-0\"><button class=\"w-auto\" type=\"submit\">Deslogar</button></form>");
        writer.println("</div>");
        writer.println("</header>");

        writer.println("<main class=\"grid grid-col-1 grid-row-auto-full w-full h-full justify-center items-center\">");

        writer.println(
                "<div style=\"margin: 30px auto; padding: 20px; min-height: 30rem\" class=\"flex flex-col items-center border rounded w-54 border-box gap-2\">");
        writer.println("<h2 class=\"m-0\">Produtos</h2>");
        writer.println("<table>");
        writer.println("<tr>");
        writer.println("<th>Nome</th>");
        writer.println("<th>Descrição</th>");
        writer.println("<th>Preço</th>");
        writer.println("<th>Estoque</th>");
        writer.println("</tr>");

        for (Produto produto : produtos) {
            writer.println("<tr>");
            writer.println("<td>" + produto.getNome() + "</td>");
            writer.println("<td>" + produto.getDescricao() + "</td>");
            writer.println("<td>" + produto.getPreco() + "</td>");
            writer.println("<td>" + produto.getEstoque() + "</td>");
            writer.println("</tr>");
        }
        writer.println("</table>");
        writer.println("<form action='/lojista/produto' method='get' class=\"w-full\">");
        writer.println("<button type='submit'>Cadastrar Novo Produto</button>");
        writer.println("</form>");
        writer.println("</div>");
        writer.println("</main>");
        writer.println("</body>");
        writer.println("</html>");
    }

    public List<Produto> findAll() {
        List<Produto> produtos = new ArrayList<>();

        String sql = "SELECT * FROM produtos";

        try (Connection conn = DriverManager.getConnection(this.url, this.user, this.password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Produto p = new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getDouble("preco"),
                        rs.getInt("estoque"));
                produtos.add(p);
            }

        } catch (SQLException e) {
            if (produtos.isEmpty()) {
                return null;
            }
        }
        return produtos;
    }
}
