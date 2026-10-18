package MyFirstTestAppSpringBoot.service;

import MyFirstTestAppSpringBoot.exception.ValidationFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RequestValidationService implements ValidationService {
    @Override
    public void isValid(BindingResult bindingResult) throws ValidationFailedException {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> log.error("Ошибка bindingResult: {}", error));
            String details = bindingResult.getAllErrors().stream()
                    .map(Object::toString).collect(Collectors.joining("; "));
            log.error("Выбрасывается ValidationFailedException: {}", details);
            throw new ValidationFailedException(details);
        }
        log.info("Валидация request завершена успешно");
    }
}