package belgrano.prog3.controllers;

import belgrano.prog3.entities.Videojuego;
import belgrano.prog3.services.ServicioVideojuego;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ControladorVideojuego {
    @Autowired
    private ServicioVideojuego servicioVideojuego;
    @GetMapping(value = "/inicio")
    public String inicio(Model model){
        try {
            List<Videojuego> videojuegos = this.servicioVideojuego.findAllByActivo();
            System.out.println(videojuegos);
            model.addAttribute("videojuegos",videojuegos);

            return "views/inicio";
        } catch (Exception e){
            model.addAttribute("error",e.getMessage());
            return "error";
        }
    }

    @GetMapping(value = "/detalle/{id}")
    public String detalleVideojuego(Model model, @PathVariable("id")long id) {
        try {
            Videojuego videojuego = this.servicioVideojuego.findById(id);
            model.addAttribute("videojuego",videojuego);
            return "views/detalle";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}
