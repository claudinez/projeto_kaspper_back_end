package com.example.sistema_servicos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.sistema_servicos.model.Cliente;
import com.example.sistema_servicos.model.Servico;
import com.example.sistema_servicos.repository.ClienteRepository;
import com.example.sistema_servicos.repository.ServicoRepository;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/email") // Define a URL base para esse controlador
public class EmailController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/enviarRelatorio/{clienteId}")
    public ResponseEntity<String> enviarRelatorio(@PathVariable Long clienteId) {
        try {
            Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);

            if (clienteOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Cliente não encontrado.");
            }

            Cliente cliente = clienteOpt.get();
            List<Servico> servicos = servicoRepository.findByClienteId(clienteId);

            if (servicos.isEmpty()) {
                return ResponseEntity.status(404).body("Nenhum serviço encontrado para o cliente.");
            }

            // Construindo o HTML do e-mail
            StringBuilder tabelaHtml = new StringBuilder()
                    .append("<html><head><style>")
                    .append("table { width: 100%; border-collapse: collapse; }")
                    .append("th, td { border: 1px solid black; padding: 8px; text-align: left; }")
                    .append("th { background-color: #f2f2f2; }")
                    .append("</style></head><body>")
                    .append("<h1>Relatório de Serviços</h1>")
                    .append("<p>Olá, ").append(cliente.getNome()).append("!</p>")
                    .append("<p>Segue a lista de serviços associados a você:</p>")
                    .append("<table>")
                    .append("<thead><tr><th>Descrição</th><th>Linguagem</th><th>Valor Total</th></tr></thead><tbody>");

            for (Servico servico : servicos) {
                tabelaHtml.append("<tr>")
                        .append("<td>").append(servico.getDescricao()).append("</td>")
                        .append("<td>").append(servico.getLinguagem()).append("</td>")
                        .append("<td>").append(String.format("R$ %.2f", servico.getValorTotalProjeto())).append("</td>")
                        .append("</tr>");
            }

            tabelaHtml.append("</tbody></table>")
                    .append("<p>Obrigado por utilizar nossos serviços!</p>")
                    .append("</body></html>");

            // Enviar o e-mail HTML
            emailService.enviarEmailHtml(cliente.getEmail(), "Relatório de Serviços", tabelaHtml.toString());

            return ResponseEntity.ok("Relatório enviado com sucesso para " + cliente.getEmail());

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }
}
