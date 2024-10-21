package pe.edu.cibertec.EF_DAW_BACK.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.EF_DAW_BACK.dto.LoginRequestDTO;
import pe.edu.cibertec.EF_DAW_BACK.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private ResourceLoader resourceLoader;

    private Resource resource;

    @Override
    public String[] validarUser(LoginRequestDTO loginRequestDTO) throws IOException {
        if (loginRequestDTO == null || loginRequestDTO.userCode() == null || loginRequestDTO.password() == null) {
            logger.error("LoginRequestDTO or its fields are null");
            throw new IllegalArgumentException("Invalid login request");
        }

        List<String[]> allUsers = leerUsuarios();

        for (String[] user : allUsers) {
            if (user.length < 7) {
                logger.warn("Invalid user data format: " + Arrays.toString(user));
                continue;
            }

            if (loginRequestDTO.userCode().equals(user[0]) && loginRequestDTO.password().equals(user[6])) {
                return user;
            }
        }

        logger.info("No matching user found for userCode: " + loginRequestDTO.userCode());
        return null;
    }

    @Override
    public List<String[]> listarUsers() throws IOException {
        List<String[]> allUsers = leerUsuarios();
        List<String[]> usersList = new ArrayList<>();

        for (String[] user : allUsers) {
            if (user.length >= 6) {
                String[] filteredUser = Arrays.copyOf(user, 6);
                usersList.add(filteredUser);
            } else {
                logger.warn("Invalid user data format: " + Arrays.toString(user));
            }
        }
        return usersList;
    }

    private List<String[]> leerUsuarios() throws IOException {
        if (resource == null) {
            resource = resourceLoader.getResource("classpath:usuarios.txt");
            if (!resource.exists()) {
                logger.error("usuarios.txt file not found");
                throw new IOException("usuarios.txt file not found");
            }
        }

        List<String[]> usuarios = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length < 7) {
                    logger.warn("Invalid line in usuarios.txt: " + line);
                    continue;
                }
                usuarios.add(data);
            }
        } catch (IOException e) {
            logger.error("Error reading usuarios.txt", e);
            throw new IOException("Error reading usuarios.txt", e);
        }

        if (usuarios.isEmpty()) {
            logger.warn("No users found in usuarios.txt");
        }

        return usuarios;
    }
}
