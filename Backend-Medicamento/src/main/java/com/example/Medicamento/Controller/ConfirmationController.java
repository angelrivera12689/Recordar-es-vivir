package com.example.Medicamento.Controller;

import com.example.Medicamento.Model.ReminderLog;
import com.example.Medicamento.Repository.ReminderLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class ConfirmationController {

    @Autowired
    private ReminderLogRepository reminderLogRepository;

    // Esta URL base se inyecta desde application.properties
    @Value("${app.base-url}") // Asegúrate de tener esta propiedad configurada
    private String appBaseUrl;

    // Ya no necesitamos un método para generar la URL aquí, se hará directamente en el Scheduler
    // ya que el token se generará al mismo tiempo que se crea el ReminderLog.

    @GetMapping("/confirm")
    @Transactional // Asegura que la operación de actualización de la base de datos se realice dentro de una transacción
    public ModelAndView confirmMedicationTaken(@RequestParam("token") String confirmationToken) { // <-- ¡Ahora esperamos un 'token'!
        ModelAndView modelAndView = new ModelAndView("confirmation_status"); // Nombre de tu plantilla HTML

        // Busca el ReminderLog usando el token de confirmación
        Optional<ReminderLog> optionalReminderLog = reminderLogRepository.findByConfirmationToken(confirmationToken); // <-- ¡NUEVO!

        if (optionalReminderLog.isPresent()) {
            ReminderLog log = optionalReminderLog.get();

            // Podrías añadir lógica aquí para verificar si el token ha "caducado"
            // si decides implementar una caducidad de tokens.

            if (!log.isConfirmed()) {
                log.setConfirmed(true);
                log.setConfirmationTime(LocalDateTime.now()); // Establece la hora de confirmación
                reminderLogRepository.save(log); // Guarda los cambios en la base de datos

                modelAndView.addObject("message", "¡Toma de medicamento confirmada con éxito! Los recordatorios para esta medicación han sido pausados por hoy.");
                modelAndView.addObject("success", true);
            } else {
                modelAndView.addObject("message", "Esta toma de medicamento ya había sido confirmada previamente.");
                modelAndView.addObject("success", false);
            }
        } else {
            modelAndView.addObject("message", "Error: Enlace de confirmación inválido o recordatorio no encontrado.");
            modelAndView.addObject("success", false);
        }
        return modelAndView;
    }
}