package MyFirstTestAppSpringBoot.model;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    /** Уникальный идентификатор сообщения, не более 32 символов. */
    @NotBlank @Size(max = 32)
    private String uid;
    /** Уникальный идентификатор операции, не более 32 символов. */
    @NotBlank @Size(max = 32)
    private String operationUid;
    /** Система — отправитель сообщения. */
    private Systems systemName;
    /** Время создания сообщения. */
    @NotBlank
    private String systemTime;
    /** Наименование ресурса, откуда поступил запрос. */
    private String source;
    /** Должность сотрудника, определяющая коэффициент премии. */
    @NotNull
    private Positions position;
    /** Оклад сотрудника, положительное число. */
    @NotNull @DecimalMin(value = "0", inclusive = false)
    private Double salary;
    /** Множитель премии; ноль означает отсутствие премии. */
    @NotNull @DecimalMin("0")
    private Double bonus;
    /** Отработанные дни за год: от 1 до числа дней текущего года. */
    @NotNull @Min(1) @Max(366)
    private Integer workDays;
    /** Уникальный идентификатор коммуникации, от 1 до 100000. */
    @NotNull @Min(1) @Max(100000)
    private Integer communicationId;
    /** Уникальный идентификатор шаблона; необязательное поле. */
    private Integer templateId;
    /** Код продукта; необязательное поле. */
    private Integer productCode;
    /** СМС-код; необязательное поле. */
    private Integer smsCode;
}
