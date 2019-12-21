package pl.com.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.com.app.dto.UserDTO;
import pl.com.app.exceptions.ExceptionCode;
import pl.com.app.exceptions.MyException;
import pl.com.app.model.Role;
import pl.com.app.model.User;
import pl.com.app.model.VerificationToken;
import pl.com.app.repository.RoleRepository;
import pl.com.app.repository.UserRepository;
import pl.com.app.repository.VerificationTokenRepository;
import pl.com.app.service.listeners.OnRegistrationEvenData;
import pl.com.app.service.mappers.UserMapper;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RegistrationService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RoleRepository roleRepository;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final UserMapper userMapper;


    public void createVerificationToken(User user, String token) {
        try {
            if (user == null) {
                throw new NullPointerException("USER IS NULL");
            }

            if (token == null) {
                throw new NullPointerException("TOKEN IS NULL");
            }
            verificationTokenRepository.findByUserId_Equals(user.getId())
                    .ifPresent(verificationTokenRepository::delete);

            verificationTokenRepository.save(VerificationToken.builder()
                    .user(user)
                    .token(token)
                    .expirationDateTime(LocalDateTime.now().plusDays(1L))
                    .build());
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public void confirmRegistration(String token) {
        try {
            if (token == null) {
                throw new NullPointerException("TOKEN IS NULL");
            }

            VerificationToken verificationToken
                    = verificationTokenRepository.findByToken(token)
                    .orElseThrow(() -> new NullPointerException("USER WITH TOKEN " + token + " DOESN'T EXIST"));

            if (verificationToken.getExpirationDateTime().isBefore(LocalDateTime.now())) {
                throw new NullPointerException("TOKEN HAS BEEN EXPIRED");
            }

            User user = verificationToken.getUser();
            user.setActive(true);
            userRepository.save(user);

            verificationToken.setToken(null);
            verificationTokenRepository.save(verificationToken);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public void registerNewUser(UserDTO userDTO, HttpServletRequest request) {
        try {
            if (userDTO == null) {
                throw new NullPointerException("USER OBJECT IS NULL");
            }

            Role role = roleRepository
                    .findById(userDTO.getRoleDTO().getId())
                    .orElseThrow(NullPointerException::new);

            User user = userMapper.userDTOToUser(userDTO);
            String filename = fileService.addFile(userDTO.getFile());
            user.setPhoto(filename);
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setRole(role);
            user.setActive(false);
            user.setRegistrationDate(LocalDate.now());
            userRepository.save(user);

            final String url = "http://" + request.getServerName() + ":" + request.getServerPort() + "/" + request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationEvenData(url, user));
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }
}
