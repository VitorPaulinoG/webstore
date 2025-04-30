package com.programacao.web.webstore;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class CarrinhoController {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @RequestMapping(value = "/carrinho/{id}", method = RequestMethod.GET)
    public void adicionarProduto(@PathVariable("id") int id, HttpSession session, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {

        try {
            Produto produto = buscarProdutoPorId(id);
            if (produto != null) {
                Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");

                if (carrinho == null) {
                    carrinho = new Carrinho();
                    session.setAttribute("carrinho", carrinho);
                    session.setMaxInactiveInterval(20 * 60);
                }

                carrinho.addProduto(produto);
                session.setAttribute("carrinho", carrinho);
                response.sendRedirect("/carrinho");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("<p>Produto não encontrado.</p>");
        }
    }

    @RequestMapping(value = "/carrinho", method = RequestMethod.GET)
    public void listarCarrinho(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var error = Optional.ofNullable(request.getParameter("error-message"));

        HttpSession session = request.getSession(false);
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            response.sendRedirect("/cliente/produtos");
            return;
        }

        response.setContentType("text/html");
        List<ItemCarrinho> produtos = carrinho.getItens();

        PrintWriter writer = response.getWriter();
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Carrinho</title>");
        writer.println("<link rel=\"stylesheet\" href=\"/styles.css\">");
        writer.println("</head>");
        writer.println("<body>");

        writer.println("<header class=\"h-3 py-2 flex justify-center\">");
        writer.println("<div class=\"h-full flex items-center justify-between border rounded w-54 border-box px-2\">");
        writer.println(
                "<form action=\"/cliente/produtos\" method=\"get\" class=\"w-auto m-0\"><button class=\"w-auto\" type=\"submit\">Voltar</button></form>");
        writer.println(
                "<form action=\"/logout\" method=\"post\" class=\"w-auto m-0\"><button class=\"w-auto\" type=\"submit\">Deslogar</button></form>");
        writer.println("</div>");
        writer.println("</header>");

        writer.println("<main class=\"grid grid-col-1 grid-row-auto-full w-full h-full justify-center items-center\">");

        writer.println(
                "<div style=\"margin: 30px auto; padding: 20px; min-height: 30rem\" class=\"flex flex-col items-center border rounded w-54 border-box gap-2\">");
        writer.println("<h1 class=\"m-0\">Carrinho</h1>");
        writer.println("<table>");
        writer.println("<tr>");
        writer.println("<th>Nome</th>");
        writer.println("<th>Descrição</th>");
        writer.println("<th>Preço</th>");
        writer.println("<th>Quantidade</th>");
        writer.println("<th>Ações</th>");
        writer.println("</tr>");

        for (ItemCarrinho p : produtos) {
            writer.println("<tr>");
            writer.println("<td>" + p.getProduto().getNome() + "</td>");
            writer.println("<td>" + p.getProduto().getDescricao() + "</td>");
            writer.println("<td>" + p.getProduto().getPreco() + "</td>");
            writer.println("<td>" + p.getQuantidade() + "</td>");
            writer.println("<td class=\"flex justify-around\">");
            writer.println("<a href='/carrinho/remover/" + p.getProduto().getId() + "'>Remover</a>");
            writer.println("<a href='/carrinho/" + p.getProduto().getId() + "'>Adicionar</a>");
            writer.println("</td>");
            writer.println("</tr>");
        }

        writer.println("</table>");

        writer.println(
                "<form action='/finalizar' method='get' class=\"w-full\"><button type='submit'>Finalizar Carrinho</button></form>");
        error.ifPresent(e -> {
            writer.println("<span class=\"text-red\">" + e + "</span>");
        });
        writer.println("</div>");
        writer.println("</main>");
        writer.println("</body>");
        writer.println("</html>");

    }

    @RequestMapping(value = "/carrinho/remover/{id}", method = RequestMethod.GET)
    public void removerProduto(@PathVariable("id") int id, HttpSession session, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");

        if (carrinho != null) {
            carrinho.removeProduto(id);
        }
        response.sendRedirect("/carrinho");
    }

    private Produto buscarProdutoPorId(int id) {
        Produto produto = null;
        String sql = "SELECT * FROM produtos WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(this.url, this.user, this.password);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    produto = new Produto(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            rs.getDouble("preco"),
                            rs.getInt("estoque"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return produto;
    }
}
