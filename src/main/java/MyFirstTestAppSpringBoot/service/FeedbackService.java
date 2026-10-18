package MyFirstTestAppSpringBoot.service;
import MyFirstTestAppSpringBoot.exception.*;
import MyFirstTestAppSpringBoot.model.*;
import MyFirstTestAppSpringBoot.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import java.util.Date;

@Slf4j
@Service
public class FeedbackService {
    private final ValidationService validationService;
    private final ModifyResponseService responseModifier;
    private final ModifyRequestService requestModifier;
    private final AnnualBonusService annualBonusService;
    private final QuarterlyBonusService quarterlyBonusService;
    private final boolean forwardingEnabled;
    public FeedbackService(ValidationService validationService,
            @Qualifier("ModifySystemTimeResponseService") ModifyResponseService responseModifier,
            @Qualifier("ModifySystemNameRequestService") ModifyRequestService requestModifier,
            AnnualBonusService annualBonusService, QuarterlyBonusService quarterlyBonusService,
            @Value("${app.forwarding.enabled:true}") boolean forwardingEnabled) {
        this.validationService = validationService;
        this.responseModifier = responseModifier;
        this.requestModifier = requestModifier;
        this.annualBonusService = annualBonusService;
        this.quarterlyBonusService = quarterlyBonusService;
        this.forwardingEnabled = forwardingEnabled;
    }
    public ResponseEntity<Response> process(Request request, BindingResult errors, Long sentAt, long receivedAt) {
        log.info("Получен request: {}", request);
        if (!forwardingEnabled && sentAt != null) {
            log.info("Время передачи запроса от Сервиса 1 до Сервиса 2: {} мс; uid={}", receivedAt - sentAt, request.getUid());
        }
        Response response = Response.builder().uid(request.getUid()).operationUid(request.getOperationUid())
                .systemTime(DateTimeUtil.getCustomFormat().format(new Date()))
                .code(Codes.SUCCESS).errorCode(ErrorCodes.EMPTY).errorMessage(ErrorMessages.EMPTY).build();
        log.info("Создан response: {}", response);
        try {
            validationService.isValid(errors);
            if ("123".equals(request.getUid())) {
                log.error("Выбрасывается UnsupportedCodeException: uid=123");
                throw new UnsupportedCodeException("Значение uid 123 не поддерживается");
            }
            double annual = annualBonusService.calculate(request.getPosition(), request.getSalary(), request.getBonus(), request.getWorkDays());
            Double quarterly = request.getPosition().isManager()
                    ? quarterlyBonusService.calculate(request.getPosition(), request.getSalary(), request.getBonus(), request.getWorkDays()) : null;
            response.setAnnualBonus(annual);
            log.info("Изменён response.annualBonus: {}", annual);
            response.setQuarterlyBonus(quarterly);
            log.info("Изменён response.quarterlyBonus: {}", quarterly);
            response = responseModifier.modify(response);
            if (forwardingEnabled) requestModifier.modify(request);
            log.info("Отправляется response: {}", response);
            return ResponseEntity.ok(response);
        } catch (ValidationFailedException | IllegalArgumentException e) {
            log.error("Ошибка валидации request: {}", e.getMessage());
            return failure(response, ErrorCodes.VALIDATION_EXCEPTION, ErrorMessages.VALIDATION, HttpStatus.BAD_REQUEST);
        } catch (UnsupportedCodeException e) {
            log.error("Неподдерживаемый uid", e);
            return failure(response, ErrorCodes.UNSUPPORTED_EXCEPTION, ErrorMessages.UNSUPPORTED, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка обработки request", e);
            return failure(response, ErrorCodes.UNKNOWN_EXCEPTION, ErrorMessages.UNKNOWN, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private ResponseEntity<Response> failure(Response response, ErrorCodes code, ErrorMessages message, HttpStatus status) {
        response.setCode(Codes.FAILURE);
        response.setErrorCode(code);
        response.setErrorMessage(message);
        response.setAnnualBonus(null);
        response.setQuarterlyBonus(null);
        log.info("Response изменён и отправляется с HTTP {}: {}", status.value(), response);
        return ResponseEntity.status(status).body(response);
    }
}
