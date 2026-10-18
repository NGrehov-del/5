package MyFirstTestAppSpringBoot.model;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Systems {
    ERP("ERP"), CRM("CRM"), WMS("WMS"),
    // Для пересылки запроса, сохранённой из лабораторной 4.
    SERVICE1("Service 1");
    private final String value;
    Systems(String value) { this.value = value; }
    @JsonValue
    public String getValue() { return value; }
}
