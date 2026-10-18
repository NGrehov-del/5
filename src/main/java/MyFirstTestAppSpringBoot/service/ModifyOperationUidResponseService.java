package MyFirstTestAppSpringBoot.service;

import MyFirstTestAppSpringBoot.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service("ModifyOperationUidResponseService")
public class ModifyOperationUidResponseService implements ModifyResponseService {
    @Override
    public Response modify(Response response) {
        String previous = response.getOperationUid();
        response.setOperationUid(UUID.randomUUID().toString());
        log.info("Изменён response.operationUid: {} -> {}", previous, response.getOperationUid());
        return response;
    }
}