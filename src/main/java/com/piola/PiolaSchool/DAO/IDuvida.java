package com.piola.PiolaSchool.DAO;

import com.piola.PiolaSchool.atributes.Duvidas;
import org.springframework.data.repository.CrudRepository;

public interface IDuvida extends CrudRepository<Duvidas, String> {
}
