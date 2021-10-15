package belgrano.prog3.repositories;

import belgrano.prog3.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface RepositorioCategoria extends JpaRepository<Categoria,Long> {
}
