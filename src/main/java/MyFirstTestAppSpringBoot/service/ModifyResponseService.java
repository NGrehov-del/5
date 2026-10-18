package MyFirstTestAppSpringBoot.service;

import org.springframework.stereotype.Service;
import MyFirstTestAppSpringBoot.model.Response;

@Service
public interface ModifyResponseService {

    Response modify(Response response);
}
