package com.piola.PiolaSchool.controller;

import com.piola.PiolaSchool.DAO.IProfessor;
import com.piola.PiolaSchool.Response.LoginResponse;
import com.piola.PiolaSchool.model.Professor;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/Professor")
public class ProfessorController {

    @Autowired
    private IProfessor dao;

    @Autowired
    private PasswordEncoder encoder;

    public ProfessorController(IProfessor dao){
        this.dao = dao;
        this.encoder = encoder;
    }

    @GetMapping
    public List<Professor> ProfessorList(){
        return (List<Professor>)dao.findAll();
    }

    @PostMapping
    public Professor criarProfessor(@Valid @RequestBody Professor professor){
        Professor ProfessorCreate = dao.save(professor);
        return ProfessorCreate;
    }

    @DeleteMapping("/{matricula}")
    public Optional<Professor> deletarProfessor(@PathVariable Integer matricula){
        Optional<Professor> Professor = dao.findById(matricula);
        dao.deleteById(matricula);
        return Professor;
    }

    public Map<String,String> handleValidatioExpetion(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        } ) ;
        return errors;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> validarSenha(@RequestParam String nome,
                                                      @RequestParam String senha){

        Optional<Professor> optionalProfessor = dao.findByNome(nome);
        if (optionalProfessor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(false, ""));
        }

        Professor professor = optionalProfessor.get();
        boolean valid = encoder.matches(senha, professor.getSenha());

        HttpStatus status = (valid) ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        if (valid){
            String matricula = String.valueOf(professor.getMatricula());
            LoginResponse response = new LoginResponse(true, matricula);
            return ResponseEntity.status(status).body(response);
        }else {
            return ResponseEntity.status(status).body(new LoginResponse(false, ""));
        }


    }
}
