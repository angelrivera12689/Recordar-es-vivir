package com.example.Medicamento.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data             // Genera getters, setters, toString, equals y hashCode
@AllArgsConstructor // Genera constructor con todos los campos
@NoArgsConstructor  // Genera constructor sin argumentos
public class ResponseDTO {
    private String status;  // Ejemplo: "200 OK", "400 BAD_REQUEST"
    private String message; // Mensaje descriptivo
}
