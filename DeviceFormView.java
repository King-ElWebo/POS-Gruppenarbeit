package com.example.smarthome.ui;

import com.example.smarthome.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("add-device")
@PageTitle("Neues Gerät")
@Menu(order = 3, icon = "line-awesome/svg/plus-solid.svg", title = "Gerät hinzufügen")
public class DeviceFormView extends VerticalLayout {
    private final SmartHomeManager manager;

    private final NumberField idField = new NumberField("ID");
    private final TextField nameField = new TextField("Name");
    private final TextField roomField = new TextField("Raum");
    private final NumberField powerField = new NumberField("Stromverbrauch (W)");
    
    public DeviceFormView(SmartHomeManager manager) {
        this.manager = manager;
        createForm();
    }

    private void createForm() {
        Button saveBtn = new Button("Als smarte Lampe speichern", e -> saveDevice());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        add(idField, nameField, roomField, powerField, saveBtn);
    }

    private void saveDevice() {
        try {
            SmartDevice light = new SmartLight(idField.getValue().intValue(), nameField.getValue(),
                    roomField.getValue(), powerField.getValue(), 100, "Weiß");
            manager.addDevice(light);
            Notification.show("Gerät gespeichert!");
            clearForm();
        } catch (Exception e) {
            Notification.show("Fehler: " + e.getMessage());
        }
    }

    private void clearForm() {
        idField.clear(); nameField.clear(); roomField.clear(); powerField.clear();
    }
}