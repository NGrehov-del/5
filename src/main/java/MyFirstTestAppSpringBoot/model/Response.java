package MyFirstTestAppSpringBoot.model;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    /** Уникальный идентификатор сообщения из запроса. */
    private String uid;
    /** Уникальный идентификатор операции. */
    private String operationUid;
    /** Время формирования ответа, UTC. */
    private String systemTime;
    /** Итог обработки: success или failed. */
    private Codes code;
    /** Годовая премия; null при ошибке. */
    private Double annualBonus;
    /** Квартальная премия; null для неуправленцев и при ошибке. */
    private Double quarterlyBonus;
    /** Код ошибки; пустая строка при успешной обработке. */
    private ErrorCodes errorCode;
    /** Описание ошибки; пустая строка при успешной обработке. */
    private ErrorMessages errorMessage;
}
