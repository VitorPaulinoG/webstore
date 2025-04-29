package com.programacao.web.webstore;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class MainController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public void home(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        var writer = response.getWriter();
        writer.println("<!DOCTYPE html>");
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Home</title>");
        writer.println("<link rel=\"stylesheet\" href=\"/styles.css\">");
        writer.println("</head>");

        writer.println("<body>");

        writer.println("<main class=\"flex w-full h-full justify-center items-center\">");

        writer.println("<div class=\"grid grid-col-2 gap-2 w-30\">");

        writer.println("<a href='/login' class=\"p-2 flex justify-center items-center border rounded h-auto\">");
        writer.println("LOGIN");
        writer.println("</a>");
        writer.println("<a href='/cadastro'' class=\"p-2 flex justify-center items-center border rounded h-auto\">");
        writer.println("CADASTRO");
        writer.println("</a>");

        writer.println("</div>");

        writer.println("</main>");

        writer.println("</body>");
        writer.println("</html>");
    }

}
