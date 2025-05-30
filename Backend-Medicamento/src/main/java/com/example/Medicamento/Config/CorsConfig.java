package com.example.Medicamento.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Permitir solicitudes desde múltiples orígenes
        config.addAllowedOrigin("http://127.0.0.1:5500");
        config.addAllowedOrigin("http://172.30.6.79:5500");
        config.addAllowedOrigin("http://localhost:5500");
        config.addAllowedOrigin("https://tu-dominio-produccion.com");

        // Permitir métodos HTTP específicos
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");

        // Permitir encabezados específicos
        config.addAllowedHeader("*"); // Para permitir todos los encabezados
        config.addAllowedHeader("Authorization");
        config.addAllowedHeader("Content-Type");

        // Configurar credenciales
        config.setAllowCredentials(true);

        // Aplicar esta configuración a todas las rutas
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}