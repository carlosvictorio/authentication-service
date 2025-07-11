package com.nogueira.authentication_service.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.nogueira.authentication_service.dtos.*;
import com.nogueira.authentication_service.enums.StatusEnum;
import com.nogueira.authentication_service.exceptions.*;
import com.nogueira.authentication_service.models.User;
import com.nogueira.authentication_service.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAuthService userAuthService;

    // --- register ---

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterUserDto dto = new RegisterUserDto("João", "joao@email.com", "123");

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("encrypted");

        userAuthService.register(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals(dto.name(), savedUser.getName());
        assertEquals(dto.email(), savedUser.getEmail());
        assertEquals("encrypted", savedUser.getPassword());
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        RegisterUserDto dto = new RegisterUserDto("João", "joao@email.com", "123");

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userAuthService.register(dto));
        verify(userRepository, never()).save(any());
    }

    // --- activateStatus ---

    @Test
    void shouldActivateUserStatusSuccessfully() {
        EmailDto dto = new EmailDto("user@email.com");
        User user = new User("User", dto.email(), "pass");
        user.setStatus(StatusEnum.PENDING_PAYMENT);

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);
        when(userRepository.findByEmail(dto.email())).thenReturn(user);

        String result = userAuthService.activateStatus(dto);

        assertEquals("User status updated successfully!", result);
        assertEquals(StatusEnum.ACTIVE, user.getStatus());
        verify(userRepository).save(user);
    }

    @Test
    void shouldReturnMessageIfAlreadyActive() {
        EmailDto dto = new EmailDto("user@email.com");
        User user = new User("User", dto.email(), "pass");
        user.setStatus(StatusEnum.ACTIVE);

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);
        when(userRepository.findByEmail(dto.email())).thenReturn(user);

        String result = userAuthService.activateStatus(dto);

        assertEquals("User status already ACTIVE!", result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenActivatingNonexistentUser() {
        EmailDto dto = new EmailDto("nonexistent@email.com");

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userAuthService.activateStatus(dto));
    }

    // --- pendingStatus ---

    @Test
    void shouldSetPendingStatusSuccessfully() {
        EmailDto dto = new EmailDto("user@email.com");
        User user = new User("User", dto.email(), "pass");
        user.setStatus(StatusEnum.ACTIVE);

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);
        when(userRepository.findByEmail(dto.email())).thenReturn(user);

        String result = userAuthService.pendingStatus(dto);

        assertEquals("User status updated successfully!", result);
        assertEquals(StatusEnum.PENDING_PAYMENT, user.getStatus());
        verify(userRepository).save(user);
    }

    @Test
    void shouldReturnMessageIfAlreadyPending() {
        EmailDto dto = new EmailDto("user@email.com");
        User user = new User("User", dto.email(), "pass");
        user.setStatus(StatusEnum.PENDING_PAYMENT);

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);
        when(userRepository.findByEmail(dto.email())).thenReturn(user);

        String result = userAuthService.pendingStatus(dto);

        assertEquals("User status already PENDING_PAYMENT!", result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPendingStatusUserNotFound() {
        EmailDto dto = new EmailDto("user@email.com");

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userAuthService.pendingStatus(dto));
    }

    // --- login ---

    @Test
    void shouldLoginSuccessfully() {
        LoginUserDto dto = new LoginUserDto("user@email.com", "password");
        User user = new User("User", dto.email(), "encrypted");
        user.setStatus(StatusEnum.ACTIVE);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenService.generateAccessToken(user)).thenReturn("access-token");
        when(tokenService.generateRefreshToken(user)).thenReturn("refresh-token");

        TokensDto result = userAuthService.login(dto);

        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());
    }

    @Test
    void shouldThrowWhenUserIsNotActive() {
        LoginUserDto dto = new LoginUserDto("user@email.com", "password");
        User user = new User("User", dto.email(), "encrypted");
        user.setStatus(StatusEnum.PENDING_PAYMENT);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        assertThrows(UserUnauthorizedException.class, () -> userAuthService.login(dto));
    }

    @Test
    void shouldThrowBadCredentialsException() {
        LoginUserDto dto = new LoginUserDto("user@email.com", "password");

        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> userAuthService.login(dto));
    }

    // --- refresh ---

    @Test
    void shouldRefreshAccessTokenSuccessfully() {
        RefreshTokenDto dto = new RefreshTokenDto("refresh-token");
        User user = new User("User", "user@email.com", "encrypted");

        when(tokenService.validateRefreshToken(dto.refreshToken())).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(tokenService.generateAccessToken(user)).thenReturn("new-access-token");

        AccessTokenDto result = userAuthService.refresh(dto);

        assertEquals("new-access-token", result.accessToken());
    }

    @Test
    void shouldThrowWhenRefreshTokenIsNull() {
        RefreshTokenDto dto = new RefreshTokenDto(null);

        assertThrows(UserUnauthorizedException.class, () -> userAuthService.refresh(dto));
    }

    @Test
    void shouldThrowWhenUserNotFoundByRefreshToken() {
        RefreshTokenDto dto = new RefreshTokenDto("refresh-token");

        when(tokenService.validateRefreshToken(dto.refreshToken())).thenReturn("user@email.com");
        when(userRepository.findByEmail("user@email.com")).thenReturn(null);

        assertThrows(UserUnauthorizedException.class, () -> userAuthService.refresh(dto));
    }
}
