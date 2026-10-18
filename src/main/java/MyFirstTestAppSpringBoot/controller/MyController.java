package MyFirstTestAppSpringBoot.controller;
import MyFirstTestAppSpringBoot.model.Request;
import MyFirstTestAppSpringBoot.model.Response;
import MyFirstTestAppSpringBoot.service.FeedbackService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class MyController {
    private final FeedbackService feedbackService;
    public MyController(FeedbackService feedbackService) { this.feedbackService = feedbackService; }
    @PostMapping("/feedback")
    public ResponseEntity<Response> feedback(@Valid @RequestBody Request request, BindingResult errors,
            @RequestHeader(value = "X-Service1-Received-At", required = false) Long sentAt,
            HttpServletRequest servletRequest) {
        long receivedAt = System.currentTimeMillis();
        servletRequest.setAttribute("service1ReceivedAt", receivedAt);
        return feedbackService.process(request, errors, sentAt, receivedAt);
    }
}
