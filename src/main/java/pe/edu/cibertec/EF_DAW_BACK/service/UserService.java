package pe.edu.cibertec.EF_DAW_BACK.service;

import pe.edu.cibertec.EF_DAW_BACK.dto.LoginRequestDTO;

import java.io.IOException;
import java.util.List;

public interface UserService {
    String[] validarUser(LoginRequestDTO loginRequestDTO) throws IOException;
    List<String[]> listarUsers() throws IOException;
}
