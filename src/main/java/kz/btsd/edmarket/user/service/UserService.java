package kz.btsd.edmarket.user.service;

import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.file.model.File;
import kz.btsd.edmarket.file.repository.FileRepository;
import kz.btsd.edmarket.security.config.JwtTokenProvider;
import kz.btsd.edmarket.user.listener.UserCreatedEvent;
import kz.btsd.edmarket.user.model.AppleAuthRequest;
import kz.btsd.edmarket.user.model.ConfirmUserPhoneDto;
import kz.btsd.edmarket.user.model.Platform;
import kz.btsd.edmarket.user.model.ResetConfirmedDto;
import kz.btsd.edmarket.user.model.ResetUserCheckDto;
import kz.btsd.edmarket.user.model.SignupDto;
import kz.btsd.edmarket.user.model.SignupEmailDto;
import kz.btsd.edmarket.user.model.SignupOrgCheckDto;
import kz.btsd.edmarket.user.model.SmsCodeVerification;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserChangePersonalRequest;
import kz.btsd.edmarket.user.model.UserConverter;
import kz.btsd.edmarket.user.model.UserDto;
import kz.btsd.edmarket.user.model.VerificationType;
import kz.btsd.edmarket.user.model.dto.PhoneEmailRequest;
import kz.btsd.edmarket.user.model.erg.SignupEmployeeDto;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserConverter userConverter;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UserEmailSender userEmailSender;

    //todo временно добавить findByPhone
    public User findById(String id) {
        Optional<User> optionalUser = userRepository.findById(Long.valueOf(id));
        if (optionalUser.isPresent()) {
            return userRepository.findById(Long.valueOf(id))
                    .orElseThrow(() -> new EntityNotFoundException("Could not find user by id " + id));
        } else {
            return findByPhone(id);
        }
    }

    public UserDto findByIdtoDto(String id) {
        User user = findById(id);
        return userConverter.convertToDto(user);
    }

    public UserDto findByPhoneToDto(String phone) {
        return userRepository.findByPhoneAndDeletedFalse(phone)
                .map(userConverter::convertToDto)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user by phone " + phone));
    }

    public User findByPhone(String phone) {
        return userRepository.findByPhoneAndDeletedFalse(phone)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user by phone " + phone));
    }

    public boolean checkPassword(String phone, String password) {
        String hashedPassword = userRepository.findByPhoneAndDeletedFalse(phone)
                .map(User::getPassword)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user by phone " + phone));
        return passwordEncoder.matches(password, hashedPassword);
    }

    public boolean existsPassword(Long id) {
        User user = userRepository.findById(id).get();
        return StringUtils.isNoneBlank(user.getPassword());
    }

    public String getFullName(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id))
                .getName();
    }

    public UserDto create(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setCreatedDate(new Date());
        user = userRepository.save(user);
        UserDto userDto = userConverter.convertToDto(user);
        publisher.publishEvent(new UserCreatedEvent(this, user, "createByMobileSms"));
        return userDto;
    }

    public boolean existsByPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return false;
        } else {
            return userRepository.existsByPhoneAndDeletedFalse(phone);
        }
    }

    public boolean existsByEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        } else {
            return userRepository.existsByEmailAndDeletedFalse(email);
        }
    }

    public SignupDto signup(SignupOrgCheckDto signup) {
        // todo pass phone in request
        String phone = verificationService.findById(signup.getRegistrationId()).getPhone();

        validateEmailAndPhone(signup.getEmail(), phone, signup.getPlatform());
        if (!verificationService.verify(signup.getRegistrationId(),
                VerificationType.REGISTRATION, signup.getSmsCode())) {
            return new SignupDto(false);
        }

        User user = userRepository.save(new User(phone, passwordEncoder.encode(signup.getPassword()),
                signup.getEmail(), signup.getName(), signup.getPlatform()));

        publisher.publishEvent(new UserCreatedEvent(this, user, signup.getBitrixPage(), signup.getPassword()));
        String token = jwtTokenProvider.createToken(user.getId());

        return new SignupDto(true, userConverter.convertToDto(user), user.getId(), token);
    }

    public SignupDto signup(SignupEmployeeDto signup) {
        validateEmailAndPhone(signup.getEmail(), signup.getPhone(), signup.getPlatform());
        validateEmployeeId(signup.getEmployeeId());

        User user = userConverter.convertToEntity(signup, passwordEncoder.encode(signup.getPassword()));
        user.setPhoneConfirmed(false);
        user.setSystemPassword(true);
        user = userRepository.save(user);

        publisher.publishEvent(new UserCreatedEvent(this, user, "createByAdminPanel", signup.getPassword()));
        return new SignupDto(true, userConverter.convertToDto(user), user.getId());
    }

    public void validateEmailAndPhone(String email, String phone, Platform platform) {
        if (StringUtils.isNoneBlank(email) && userRepository.existsByEmailAndPlatformAndDeletedFalse(email, platform)) {
            throw new BadCredentialsException("Пользователь с почтой " + email + " уже зарегистрирован");
        }
        if (StringUtils.isNoneBlank(phone) && userRepository.existsByPhoneAndPlatformAndDeletedFalse(phone, platform)) {
            throw new BadCredentialsException("Пользователь с номером " + phone + " уже зарегистрирован");
        }
    }

    public void validateEmployeeId(String employeeId) {
        if (StringUtils.isNoneBlank(employeeId) && userRepository.existsByEmployeeIdAndDeletedFalse(employeeId)) {
            throw new BadCredentialsException("Пользователь с табельным номером " + employeeId + " уже зарегистрирован");
        }
    }

    public void signup(SignupEmailDto signup) {
        validateEmailAndPhone(signup.getEmail(), signup.getPhone(), signup.getPlatform());
        User user = userConverter.convertToEntity(signup, passwordEncoder.encode(signup.getPassword()));
        user = userRepository.save(user);

        publisher.publishEvent(new UserCreatedEvent(this, user, signup.getBitrixPage(), signup.getPassword()));
    }

    public ResetConfirmedDto resetPassword(ResetUserCheckDto reset) {
        if (!verificationService.verify(reset.getRegistrationId(), VerificationType.RESET_PASSWORD,
                reset.getSmsCode())) {
            return new ResetConfirmedDto(false);
        }
        SmsCodeVerification smsCodeVerification = verificationService.findById(reset.getRegistrationId());
        User user = userRepository.findById(smsCodeVerification.getUserId())
                .orElseThrow(() -> new BadCredentialsException("Неверный номер телефона или пароль"));
        user.setPassword(passwordEncoder.encode(reset.getPassword()));
        userRepository.save(user);

        return new ResetConfirmedDto(true);
    }

    public ResetConfirmedDto confirmPhone(ConfirmUserPhoneDto reset) {
        if (!verificationService.verify(reset.getRegistrationId(), VerificationType.CHANGE_PHONE,
                reset.getSmsCode())) {
            return new ResetConfirmedDto(false);
        }
        SmsCodeVerification smsCodeVerification = verificationService.findById(reset.getRegistrationId());
        User user = userRepository.findByPhoneAndDeletedFalse(smsCodeVerification.getPhone())
                .orElseThrow(() -> new BadCredentialsException("Неверный номер телефона или пароль"));
        user.setPhoneConfirmed(true);
        userRepository.save(user);

        return new ResetConfirmedDto(true);
    }

    public UserDto update(Long id, UserChangePersonalRequest request) {
        boolean sendConfirmEmail = false;
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user by id " + id));
        user.setFileId(request.getFileId());
        user.setName(request.getName());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        if (request.getEmail() != null) {
            Optional<User> optionalUser = userRepository.findByEmailAndPlatformAndDeletedFalse(request.getEmail(), user.getPlatform());
            if (optionalUser.isPresent() && !optionalUser.get().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Пользователь с почтой " + request.getEmail() + " уже зарегистрирован");
            }
            //проверка что почта меняется и сбрасывает подтверждение
            if (!request.getEmail().equals(user.getEmail())) {
                user.setEmailConfirmed(false);
                sendConfirmEmail = true;
            }
        }
        user.setEmail(request.getEmail());
        if (request.getPhone() != null) {
            Optional<User> optionalUser = userRepository.findByPhoneAndPlatformAndDeletedFalse(request.getPhone(), user.getPlatform());
            if (optionalUser.isPresent() && !optionalUser.get().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Пользователь с телефоном " + request.getPhone() + " уже зарегистрирован");
            }
            //проверка что телефон меняется и сбрасывает подтверждение
            if (!request.getPhone().equals(user.getPhone())) {
                user.setPhoneConfirmed(false);
                //       sendConfirmEmail = true;
            }
        }
        user.setPhone(request.getPhone());
        user.setPosition(request.getPosition());
        user.setCompany(request.getCompany());
        user.setBirthdate(request.getBirthdate());
        user.setActivity(request.getActivity());
        user.setJob(request.getJob());
        user.setSpecialization(request.getSpecialization());
        user.setInformation(request.getInformation());
        user = userRepository.save(user);
        if (sendConfirmEmail) {
            userEmailSender.processSendConfirmEmail(user);
        }
        return userConverter.convertToDto(user);
    }

    public void updateAvatar(Long id, MultipartFile multipartFile) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user by id " + id));
        String uuid = UUID.randomUUID().toString();
        File file = new File(uuid, multipartFile.getOriginalFilename(), multipartFile.getBytes());
        String fileId = fileRepository.save(file).getId();
        user.setFileId(fileId);
        userRepository.save(user);
    }

    public UserDto findByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email)
                .map(userConverter::convertToDto)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user by email " + email));
    }

    // apple id user create
    // todo extract duplicated code
    public UserDto create(AppleAuthRequest request) {
        User user = new User();
        // apple только email возвращает
        user.setEmail(request.getEmail());
        user.setPhone(request.getEmail());
        user.setFirstName(request.getGivenName());
        user.setLastName(request.getFamilyName());
        user.setCreatedDate(new Date());
        user = userRepository.save(user);
        UserDto userDto = userConverter.convertToDto(user);
        publisher.publishEvent(new UserCreatedEvent(this, user, "createByAppleId"));
        return userDto;
    }

    @Async
    public void userLastActivityDateAsync(String id) {
        User user = null;
        if (id.contains("+")) { //todo временно, удалить когда в токене не будет телефонов, в феврале
            user = userRepository.findByPhoneAndDeletedFalse(id).get();
        } else {
            user = userRepository.findById(Long.valueOf(id)).get();
        }
        user.setLastActivityDate(new Date());
        userRepository.save(user);
    }

    public void checkHost(User user, HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getHeader("origin").contains("ecommerce") && !user.getPlatform().equals(Platform.ECOMMERCE)) {
            throw new BadCredentialsException("Нельзя войти, аккаунт из другой платформы");
        }
        //todo цмтис проверка
        if (httpServletRequest.getHeader("origin").contains("cmtis") && !user.getPlatform().equals(Platform.CMTIS)) {
            throw new BadCredentialsException("Нельзя войти, аккаунт из другой платформы");
        }
    }

    public Platform getPlatform(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getHeader("origin").contains("ecommerce")) {
            return Platform.ECOMMERCE;
        }
        if (httpServletRequest.getHeader("origin").contains("cmtis")) {
            return Platform.CMTIS;
        }
        if (httpServletRequest.getHeader("origin").contains("ergdu")) {
            return Platform.ERG;
        }
        if (httpServletRequest.getHeader("origin").contains("btsd")) {
            return Platform.BTSD;
        }
        if (httpServletRequest.getHeader("origin").contains("demo")) {
            return Platform.DEMO;
        }
        return Platform.JUMYSBAR;
    }

    public void changeDeletedStatus(User user, Boolean deletedStatus) {
        user.setDeleted(deletedStatus);
        userRepository.save(user);
    }

    public void checkUniqueness(PhoneEmailRequest phoneEmailRequest) {
        if (Objects.nonNull(phoneEmailRequest)) {
            if (StringUtils.isNoneBlank(phoneEmailRequest.getEmail()) && userRepository.existsByEmailAndPlatformAndDeletedFalse(phoneEmailRequest.getEmail(), phoneEmailRequest.getPlatform())) {
                throw new BadCredentialsException("Пользователь с почтой " + phoneEmailRequest.getEmail() + " уже зарегистрирован");
            }
            if (StringUtils.isNoneBlank(phoneEmailRequest.getPhone()) && userRepository.existsByPhoneAndPlatformAndDeletedFalse(phoneEmailRequest.getPhone(), phoneEmailRequest.getPlatform())) {
                throw new BadCredentialsException("Пользователь с номером " + phoneEmailRequest.getPhone() + " уже зарегистрирован");
            }
        }
    }
}
