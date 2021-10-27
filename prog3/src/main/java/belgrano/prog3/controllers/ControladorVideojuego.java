package belgrano.prog3.controllers;

import belgrano.prog3.entities.Videojuego;
import belgrano.prog3.services.ServicioCategoria;
import belgrano.prog3.services.ServicioEstudio;
import belgrano.prog3.services.ServicioVideojuego;
import org.apache.tomcat.jni.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;

@Controller
public class ControladorVideojuego {
    @Autowired
    private ServicioVideojuego servicioVideojuego;
    @Autowired
    private ServicioCategoria servicioCategoria;
    @Autowired
    private ServicioEstudio servicioEstudio;
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
    @GetMapping(value = "/busqueda")
    public String busquedaVideojuego(Model model, @RequestParam(value="query",required = false)String q){
        try {
            List<Videojuego> videojuegos = this.servicioVideojuego.findByTitle(q);
            model.addAttribute("videojuegos",videojuegos);
            return "views/busqueda";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    @GetMapping(value = "/crud")
    public String crudVideojuegos(Model model){
        try {
            List<Videojuego> videojuegos = this.servicioVideojuego.findAll();
            model.addAttribute("videojuegos",videojuegos);
            return "views/crud";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping(value = "/formulario/videojuego/{id}")
    public String formularioVideojuego(Model model,@PathVariable("id")long id){
        try {
            model.addAttribute("categorias",this.servicioCategoria.findAll());
            model.addAttribute("estudios",this.servicioEstudio.findAll());
            if (id==0){
                model.addAttribute("videojuego", new Videojuego());
            }else{
                model.addAttribute("videojuego",this.servicioVideojuego.findById(id));
            }
            return "views/formulario/videojuego";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping(value = "/formulario/videojuego/{id}")
    public String guardarVideojuego(
            @RequestParam("archivo") MultipartFile archivo,
            @Valid @ModelAttribute("videojuego") Videojuego videojuego,
            BindingResult result,
            Model model, @PathVariable("id")long id){
        try {
            model.addAttribute("categorias",this.servicioCategoria.findAll());
            model.addAttribute("estudios",this.servicioEstudio.findAll());
            if (result.hasErrors()){
                return "redirect:/views/formulario/videojuego/";
            }
            String ruta = "C://Users/Nicolas/OneDrive/Desktop/prog3/prog3/src/main/resources/assets";
            int index= archivo.getOriginalFilename().indexOf(".");
            String extension = "";
            extension = '.'+archivo.getOriginalFilename().substring(index+1);
            String nombreImagen = Calendar.getInstance().getTimeInMillis() + extension;
            Path rutaAbsoluta = id != 0 ? Paths.get(ruta + "//" + videojuego.getImagen()) :
                    Paths.get(ruta + "//" + nombreImagen);

            if (id==0){
                if (archivo.isEmpty()){
                    model.addAttribute("ImageErrorMsg","La imágen es requerida");
                    return "views/formulario/videojuego";
                }
                if (!this.validarExtension(archivo)){
                    model.addAttribute("ImageErrorMsg","El tipo de archivo seleccionado no es válido.");
                    return "views/formulario/videojuego";
                }
                if (archivo.getSize()>15000000){
                    model.addAttribute("ImageErrorMsg","El tamaño de la imagen no puede superar los 2MB.");
                    return "views/formulario/videojuego";
                }
                Files.write(rutaAbsoluta,archivo.getBytes());
                videojuego.setImagen(nombreImagen);
                this.servicioVideojuego.saveOne(videojuego);
            }else{
                if (!archivo.isEmpty()){
                    Files.write(rutaAbsoluta,archivo.getBytes());
                }
                this.servicioVideojuego.updateOne(videojuego,id);
            }
            return "redirect:/crud";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping(value = "/eliminar/videojuego/{id}")
    public String eliminarVideojuego(Model model,@PathVariable("id")long id){
        try {
        model.addAttribute("videojuego",servicioVideojuego.findById(id));
            return "views/formulario/eliminar";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping(value = "/eliminar/videojuego/{id}")
    public String desactivarVideojuego(Model model,@PathVariable("id")long id){
        try {
            this.servicioVideojuego.deleteById(id);
            return "redirect:/crud";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    public boolean validarExtension(MultipartFile archivo){
        try {
            ImageIO.read(archivo.getInputStream()).toString();
            return true;
        }catch (Exception e){
            System.out.println(e);
            return false;
        }
    }
}
