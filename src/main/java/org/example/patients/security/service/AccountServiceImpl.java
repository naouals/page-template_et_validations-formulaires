package org.example.patients.security.service;

import org.example.patients.security.entities.AppRole;
import org.example.patients.security.entities.AppUser;
import org.example.patients.security.repo.AppRoleRepository;
import org.example.patients.security.repo.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;
    private PasswordEncoder passwordEncoder;

    public AccountServiceImpl(AppRoleRepository appRoleRepository, AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appRoleRepository = appRoleRepository;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AppUser addNewUser(String username, String password, String email, String confirmPassword) {
        AppUser appUser = appUserRepository.findByUsername(username);
        if (appUser != null) throw new RuntimeException("User already exists");
        if(!password.equals(confirmPassword)) throw new RuntimeException("Passwords do not match");
        appUser=AppUser.builder()
                .userId(UUID.randomUUID().toString())
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();
        AppUser savedAppUser=appUserRepository.save(appUser);

        return savedAppUser;
    }

    @Override
    public AppRole addNewRole(String role) {
        return appRoleRepository.findById(role)
                .orElseGet(() -> appRoleRepository.save(
                        AppRole.builder()
                                .role(role)
                                .build()
                ));
    }

    @Override
    public void addRoleToUser(String username, String role) {
        AppRole appRole=appRoleRepository.findById(role).get();
        AppUser appUser=appUserRepository.findByUsername(username);
        appUser.getRoles().add(appRole);
        //appUserRepository.save(appUser);

    }

    @Override
    public void removeRoleFromUser(String username, String role) {
        AppRole appRole=appRoleRepository.findById(role).get();
        AppUser appUser=appUserRepository.findByUsername(username);
        appUser.getRoles().remove(appRole);

    }

    @Override
    public AppUser loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }
}
