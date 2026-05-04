package com.example;

import com.example.smarthome.*;
import com.vaadin.flow.theme.aura.Aura;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;

@SpringBootApplication
@StyleSheet(Aura.STYLESHEET)
@StyleSheet("styles.css") // Your custom styles
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner initDemoData(SmartHomeManager manager) {
        return args -> {
            if (manager.getAllDevices().isEmpty()) {
                manager.addDevice(new SmartLight(1, "Deckenleuchte", "Wohnzimmer", 12.5, 80, "Warmweiß"));
                manager.addDevice(new SmartThermostat(2, "Heizung", "Badezimmer", 45.0, 21.5, 23.0));
                manager.addDevice(new SmartSpeaker(3, "Radio", "Küche", 5.0, 40, "Morning Show"));
            }
        };
    }
}
