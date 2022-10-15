package tutorschedule.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutor_schedule.api.authentication.DefaultApi;
import tutor_schedule.api.model.authentication.UserLoginRequestDto;
import tutorschedule.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/session")
public class AuthenticationApiImpl implements DefaultApi {

    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<String> login(@RequestBody UserLoginRequestDto loginRequest) {
        boolean loginSuccess = authenticationService.logIn(loginRequest.getEmail(), loginRequest.getPassword());
        if (loginSuccess) {
            return ResponseEntity.accepted().body("Login success");
        }
        return ResponseEntity.badRequest().body("Invalid email or password");
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> logOut(@PathVariable Long id) {
        authenticationService.logOut(id);
        return ResponseEntity.ok().body("Logout successfully");

    }
}

