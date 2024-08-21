package com.springlearning.catalog.services;

import com.springlearning.catalog.domain.PasswordRecover;
import com.springlearning.catalog.domain.User;
import com.springlearning.catalog.dto.EmailDTO;
import com.springlearning.catalog.dto.NewPasswordDTO;
import com.springlearning.catalog.repositories.PasswordRecoverRepository;
import com.springlearning.catalog.repositories.UserRepository;
import com.springlearning.catalog.services.exceptions.ForbiddenException;
import com.springlearning.catalog.services.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String recoveruri;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;

    @Autowired
    private EmailService emailService;

    public void validateSelfOrAdmin(Long userId) {
        User me = userService.authenticated();
        if (!me.hasRole("ROLE_ADMIN") && !me.getId().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }
    }

    @Transactional
    public void createRecoverToken(EmailDTO body){
    User user = userRepository.findByEmail(body.getEmail());
    if(user == null){
        throw new ResourceNotFoundException("User not found");
    }
        String token = UUID.randomUUID().toString();

        PasswordRecover entity = new PasswordRecover();
        entity.setEmail(body.getEmail());
        entity.setToken(token);
        entity.setExpiration(Instant.now().plusSeconds(tokenMinutes*60L));
        entity = passwordRecoverRepository.save(entity);

        String text = "Acesse o link para definir uma nova senha\n\n"
                + recoveruri + token +". Validade de "+ tokenMinutes + " minutos";
        emailService.sendEmail(body.getEmail(), "Recuperar Senha", text);
    }

    @Transactional
    public void saveNewPassword(NewPasswordDTO body) {
        List<PasswordRecover> result = passwordRecoverRepository.searchValidTokens(body.getToken(), Instant.now());
        if (result.size() == 0) {
            throw new ResourceNotFoundException("Token not found");
        }

        User user =  userRepository.findByEmail(result.get(0).getEmail());
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        user = userRepository.save(user);
    }
}