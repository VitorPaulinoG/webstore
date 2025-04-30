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

@Controller
public class FinalizarCompraController {
   @Value("${spring.datasource.url}")
   private String url;

   @Value("${spring.datasource.username}")
   private String user;

   @Value("${spring.datasource.password}")
   private String password;

   @RequestMapping(value = "/finalizar", method = RequestMethod.GET)
   public void finalizarCompra(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      HttpSession session = request.getSession();
      Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");

      if (carrinho == null || carrinho.getItens().isEmpty()) {
         response.getWriter().println("Carrinho vazio!");
         return;
      }
      double total = 0.0;
      try (Connection conn = DriverManager.getConnection(this.url, this.user, this.password)) {

         for (ItemCarrinho item : carrinho.getItens()) {
            int produtoId = item.getProduto().getId();
            int quantidadeComprada = item.getQuantidade();

            PreparedStatement psSelect = conn.prepareStatement("SELECT estoque, preco FROM produtos WHERE id = ?");
            psSelect.setInt(1, produtoId);
            ResultSet rs = psSelect.executeQuery();

            if (rs.next()) {
               int estoqueAtual = rs.getInt("estoque");
               double preco = rs.getDouble("preco");

               if (quantidadeComprada > estoqueAtual) {
                  response.getWriter().println("Estoque insuficiente para o produto ID: " + produtoId);
                  return;
               }

               PreparedStatement psUpdate = conn.prepareStatement("UPDATE produtos SET estoque = ? WHERE id = ?");
               psUpdate.setInt(1, estoqueAtual - quantidadeComprada);
               psUpdate.setInt(2, produtoId);
               psUpdate.executeUpdate();

               total += quantidadeComprada * preco;
            }

            rs.close();
            psSelect.close();
         }
         session.removeAttribute("carrinho");
         response.sendRedirect("/pagar?total=" + total);

      } catch (SQLException e) {
         e.printStackTrace();
         response.getWriter().println("Erro ao finalizar a compra.");
      }
   }

   @RequestMapping(value="/pagar", method = RequestMethod.GET)
   public void finalizandoCompra(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      String totalP = request.getParameter("total");
      double total = 0.0;
      try {
         total = Double.parseDouble(totalP);
      } catch (NumberFormatException e) {
         total = 0.0;
      }
      PrintWriter writer = response.getWriter();
      response.setContentType("text/html");
      writer.println("<html>");
      writer.println("<head>");
      writer.println("<title>Finalizar Compra</title>");
      writer.println("</head>");
      writer.println("<body>");
      writer.println("<h1>Finalizar Compra</h1>");
      writer.println("<p>Total: " + String.format("%.2f", total) + "</p>");
      writer.println("</body>");
      writer.println("</html>");
   }

}
