package com.programacao.web.webstore;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        writer.println("<main class=\"grid grid-col-1 grid-row-auto-full w-full h-full justify-center items-center\">");

        writer.println(
                "<div style=\"margin: 30px auto; padding: 20px; min-height: 30rem\" class=\"border rounded w-50\">");
        writer.println("<h2>Lista de Produtos</h2>");
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
        writer.println("</div>");
        writer.println("</main>");
        writer.println("<a href='/carrinho'> Ver Carrinho </a>");
        writer.println("<form action=\"/logout\" method=\"post\"><button type=\"submit\" class='btn-deslogar'>Deslogar</button></form>");
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
        writer.println("<style>");
        writer.println(
                "        body { font-family: Arial, sans-serif; max-width: 500px; margin: 30px auto; padding: 20px; border: 1px solid #ccc; border-radius: 8px; }");
        writer.println(
                "        input[type=\"text\"], input[type=\"number\"] { width: 100%; padding: 8px; margin: 8px 0 16px; box-sizing: border-box; }");
        writer.println(
                "        button { padding: 10px 20px; background-color: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer; }");
        writer.println("        button:hover { background-color: #218838; }");
        writer.println("</style>");
        writer.println("</head>");

        writer.println("<body>");
        writer.println("<main class=\"grid grid-col-1 grid-row-auto-full w-full h-full justify-center items-center\">");

        writer.println(
                "<div style=\"margin: 30px auto; padding: 20px;\" class=\"border rounded w-50\">");
        writer.println("<h2>Cadastrar Novo Produto</h2>");
        writer.println("<form action='/lojista/produto' method='post'>");
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
        writer.println("<form action=\"/logout\" method=\"post\"><button type=\"submit\" class='btn-deslogar''>Deslogar</button></form>");
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
        writer.println("<h2>Produtos</h2>");
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
        writer.println("    <form action='/lojista/produto' method='get'>");
        writer.println("    <button type='submit' class='btn-canto-pagina'>Cadastrar Novo Produto</button>");
        writer.println("    </form>");
        writer.println("<form action=\"/logout\" method=\"post\"><button type=\"submit\" class='btn-deslogar'>Deslogar</button></form>");
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
