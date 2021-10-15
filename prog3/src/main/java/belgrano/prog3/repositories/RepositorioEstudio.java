package belgrano.prog3.repositories;

import belgrano.prog3.entities.Estudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface RepositorioEstudio extends JpaRepository<Estudio,Long> {
}
