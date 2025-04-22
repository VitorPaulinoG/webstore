package com.programacao.web.webstore;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Controller
public class MainController {
    @RequestMapping(value="/", method=RequestMethod.GET)
    public void home(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        var writer = response.getWriter();
        writer.println("<!DOCTYPE html>");
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Home</title>");
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
            </style>
            """);
        writer.println("</head>");

        writer.println("<body>");

        writer.println("<main class=\"flex flex-row\">");

        writer.println("<a href='/login' class=\"p-2 border rounded\">");
        writer.println("LOGIN");
        writer.println("</a>");
        writer.println("<a href='/cadastro'' class=\"p-2 border rounded\">");
        writer.println("CADASTRO");
        writer.println("</a>");

        writer.println("</main>");

        writer.println("</body>");
        writer.println("</html>");
    }
    
}
