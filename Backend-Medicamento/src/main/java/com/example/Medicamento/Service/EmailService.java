package com.example.Medicamento.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper; // Para enviar HTML
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage; // Para crear mensajes HTML

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String appBaseUrl;

    // Método para recordatorios de texto plano (puedes mantenerlo si tienes otros usos)
    public void sendReminderEmail(String toEmail, String patientName, String medicationName, LocalDateTime reminderTime) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("angelfaridr1@gmail.com"); // ¡Reemplaza con tu correo!
            message.setTo(toEmail);
            message.setSubject("Recordatorio de Medicamento: " + medicationName);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String formattedTime = reminderTime.format(formatter);

            String text = "Hola " + patientName + ",\n\n"
                        + "Es hora de tomar tu medicamento: " + medicationName + " a las " + formattedTime + ".\n\n"
                        + "¡No olvides tomarlo!\n\n"
                        + "Saludos,\nTu Recordatorio de Medicamentos";
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error al enviar correo de recordatorio de texto plano a: " + toEmail);
            e.printStackTrace();
        }
    }

    // Método principal para enviar correos con botón de confirmación (HTML)
    public void sendReminderEmailWithConfirmation(String toEmail, String patientName, String medicationName, LocalDateTime reminderTime, String confirmationToken) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // Usamos MimeMessageHelper para facilitar la creación de mensajes HTML con UTF-8
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setFrom("angelfaridr1@gmail.com"); // ¡Reemplaza con tu correo!
            helper.setTo(toEmail);
            helper.setSubject("¡Es hora de tu medicamento! " + medicationName);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String formattedTime = reminderTime.format(formatter);

            // Construye la URL de confirmación usando el token único
            String confirmationLink = appBaseUrl + "/confirm?token=" + confirmationToken;

            // Contenido HTML del correo con el botón estilizado
            String htmlContent = "<html>"
                               + "<head>"
                               + "<style>"
                               + "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }"
                               + ".container { background-color: #ffffff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); max-width: 600px; margin: 0 auto; }"
                               + "h2 { color: #333333; }"
                               + "p { color: #555555; line-height: 1.6; }"
                               + ".button { display: inline-block; padding: 12px 25px; margin-top: 20px; font-size: 16px; color: #ffffff; background-color: #007bff; border-radius: 5px; text-decoration: none; }"
                               + ".button:hover { background-color: #0056b3; }"
                               + ".footer { margin-top: 30px; font-size: 0.9em; color: #888888; text-align: center; }"
                               + "</style>"
                               + "</head>"
                               + "<body>"
                               + "<div class='container'>"
                               + "<h2>Hola " + patientName + ",</h2>"
                               + "<p>Es hora de tomar tu medicamento: <strong>" + medicationName + "</strong> a las <strong>" + formattedTime + "</strong>.</p>"
                               + "<p>Por favor, haz clic en el siguiente botón una vez que hayas tomado tu medicamento para <strong>confirmar la toma y pausar los recordatorios por hoy</strong>:</p>"
                               + "<a href=\"" + confirmationLink + "\" class=\"button\">Confirmar Toma de Medicamento</a>"
                               + "<p style=\"margin-top: 30px;\">¡Cuídate mucho!</p>"
                               + "<p>Saludos,<br>Tu Sistema de Recordatorios de Medicamentos</p>"
                               + "</div>"
                               + "<div class='footer'>"
                               + "<p>Este es un correo automático, por favor no respondas a este mensaje.</p>"
                               + "</div>"
                               + "</body>"
                               + "</html>";
            
            helper.setText(htmlContent, true); // 'true' indica que el contenido es HTML
            mailSender.send(mimeMessage);

            System.out.println("Correo HTML con botón de confirmación enviado a: " + toEmail + " para " + medicationName);

        } catch (MessagingException e) {
            System.err.println("Error al construir o enviar el correo HTML a: " + toEmail);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error general al enviar correo HTML a: " + toEmail);
            e.printStackTrace();
        }
    }
}