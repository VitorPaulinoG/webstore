package com.programacao.web.webstore;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping(value = "/produtos", method = RequestMethod.GET)
    public void listarProdutos(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Produto> produtos = new ArrayList<>();
        HttpSession session = request.getSession(false);
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
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
                        rs.getInt("estoque")
                );
                produtos.add(p);
            }

        } catch (SQLException e) {
            response.setContentType("text/html");
            response.getWriter().write("<p>Erro ao listar produtos: " + e.getMessage() + "</p>");
        }

        if ("cliente".equals(tipoUsuario)) {
            PrintWriter writer = response.getWriter();

            writer.println("<html><body>");
            writer.println("<h2>Lista de Produtos</h2><ul>");
            writer.println("<table border='1'>");
            writer.println("<tr><th>Nome</th><th>Descrição</th><th>Preço</th><th>Estoque</th><th>Carrinho</th></tr>");

            for (Produto p : produtos) {
                writer.println("<tr><td>" + p.getNome() + "</td><td>" + p.getDescricao() + "</td><td>" + p.getPreco() + "</td><td>" + p.getEstoque() + "</td><td><a href='adicionarProduto?id=" + p.getId() + "'>Adicionar</a></td></tr>");
            }

            writer.println("</table>");
            writer.println("</body></html>");

        } else if ("lojista".equals(tipoUsuario)) {
            PrintWriter writer = response.getWriter();

            writer.println("<html><body>");
            writer.println("<h2>Lista de Produtos</h2><ul>");
            writer.println("<table border='1'>");
            writer.println("<tr><th>Nome</th><th>Descrição</th><th>Preço</th><th>Estoque</th></tr>");

            for (Produto p : produtos) {
                writer.println("<tr><td>" + p.getNome() + "</td><td>" + p.getDescricao() + "</td><td>" + p.getPreco() + "</td><td>" + p.getEstoque() + "</td><td><a href='/inserirProdutos'>Cadastrar Novo Produto</a></td></tr>");
            }

            writer.println("</table>");
            writer.println("</body></html>");
        }

    }

    @RequestMapping(value = "/inserirProdutos", method = RequestMethod.POST)
    public void inserirProduto(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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
            response.getWriter().write("Produto inserido com sucesso!");

        } catch (SQLException e) {
            response.getWriter().write("Erro ao inserir produto!");
            System.out.println(e.getMessage());
        }
    }
}





