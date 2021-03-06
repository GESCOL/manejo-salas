package com.example.manejosalas.controlador;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.manejosalas.DAO.UsuarioDAO;
import com.example.manejosalas.entidad.Usuario;
import com.mysql.cj.Session;

@Controller
@RequestMapping("usuarios")
public class UsuarioControlador extends UsuarioServicio{

	@Autowired
	UsuarioDAO usuarioDAO;	

	@GetMapping("/")
	public String index(Model model) {				
		model.addAttribute("userLogin", new Usuario());
		model.addAttribute("userRegister", new Usuario());
		model.addAttribute("perfiles",Perfil.getPerfiles());						
		model.addAttribute("loginTab","active");		
		return "index";		
	}	
	
	@GetMapping("/register")
	public String register(Model model){
		model.addAttribute("userRegister", new Usuario());
		model.addAttribute("perfiles",Perfil.getPerfiles());
		model.addAttribute("userLogin", new Usuario());		
		model.addAttribute("registerTab","active");		
		return "index";
	}
	
	@GetMapping("/login")
	public String login(Model model) throws Exception {
		model.addAttribute("userLogin", new Usuario());
		model.addAttribute("userRegister", new Usuario());
		model.addAttribute("perfiles",Perfil.getPerfiles());				
		model.addAttribute("loginTab","active");		
		
		return "index";
			
	}	
	
	@GetMapping("/login/error=true")
	public String errorLogin(Model model)
	{
		model.addAttribute("userLogin", new Usuario());
		model.addAttribute("userRegister", new Usuario());
		model.addAttribute("perfiles",Perfil.getPerfiles());						
		model.addAttribute("loginTab","active");	
		model.addAttribute("loginError", "true");
		model.addAttribute("loginErrorMessage","Usuario o clave incorrecto");
		return "index";	
	}
		
	@GetMapping("/register/cancel")
	public String cancelRegisterUsuario(ModelMap model) {
		return "redirect:/usuarios/register";
	}
	
	@PostMapping("/register")
	public String registerAction(@ModelAttribute("userRegister")Usuario usuario, BindingResult result, Model model) {
		model.addAttribute("userRegister", new Usuario());
		model.addAttribute("perfiles",Perfil.getPerfiles());
		model.addAttribute("userLogin", new Usuario());			
		model.addAttribute("loginTab","active");	
		
		
		
		try {
			
			
			
			if(verifyUsuarioEncontrado(usuario)){
				
				usuario = usuarioDAO.findByCorreo(usuario.getCorreo());
				
				if(usuario.getPerfil().equals("U")){
					sendVerificationToken(usuario);
				}
				else {
					sendWaitingAutorization(usuario.getCorreo());
				}
				throw new Exception("El usuario ya existe o no ha sido activado");
			}
			else {
				BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
				usuario.setPassword(bCryptPasswordEncoder.encode(usuario.getPassword()));
				
				//Disable user account until the code is verified
				usuario.setEstado(false);
							
				//Then we store the user with disable state
				usuarioDAO.save(usuario);
							
				if(usuario.getPerfil().equals("U")){
					sendVerificationToken(usuario);
				}
				else {
					sendWaitingAutorization(usuario.getCorreo());
				}
			}
			model.addAttribute("registerSuccess", "true");
			model.addAttribute("registerSuccessMessage","Registro exitoso");
			model.addAttribute("loginTab","desactive");
			model.addAttribute("registerTab","active");
			return "index";
			
		}		
		catch(Exception e) {						
						
			model.addAttribute("perfiles",Perfil.getPerfiles());
			model.addAttribute("registerError", "true");
			model.addAttribute("registerErrorMessage","El usuario ya existe o no ha sido activado");
			model.addAttribute("loginTab","desactive");
			model.addAttribute("registerTab","active");						
			
			return "index";
		}
		
	}
		
	
	@GetMapping("/activate-user/{id}/{hashCreated}")
	public String activarUsuario(Model model, @PathVariable(name = "id") int id,  @PathVariable(name = "hashCreated") int hashCreated){		
				
		Usuario usuario = usuarioDAO.findById(id);
		
		int registeredHash = usuario.hashCode();
		if(registeredHash == hashCreated){
			usuario.setEstado(true);
			usuarioDAO.save(usuario);
		}
		else {
			return "redirect:/"; //activate-user/error
		}
		
		return "redirect:/"; //activate-user/success
	}
	
	@GetMapping("/send-mail/forget-password")
	public String correoClaveOlvidada(Model model){
		
		return "usuarios/send-mail-forget-password";
		
	}
	
	@GetMapping("/send-mail/forget-password/{correo}")
	public String correoClaveOlvidadaSender(Model model, @PathVariable(name = "correo") String correo){
		
		Usuario user = usuarioDAO.findByCorreo(correo);
		
		sendForgetLink(user);
		
		return "redirect:/";
		
	}	
	
	@GetMapping("/forget-password/{id}/{hashCreated}")
	public String claveOlvidadaUsuario(Model model, @PathVariable(name = "id") int id,  @PathVariable(name = "hashCreated") int hashCreated){		
				
		Usuario usuario = usuarioDAO.findById(id);
		
		int registeredHash = usuario.hashCode();
		if(registeredHash == hashCreated){
			model.addAttribute("userRegister", usuario);
			model.addAttribute("forgetMode", "true");
			return "/usuarios/forget-password";
		}
		else {
			return "redirect:/"; //activate-user/error
		}
		
		
	}	
	
	@PostMapping("/forget-password")
	public String claveOlvidadaUsuario(@ModelAttribute("userRegister")Usuario usuario, BindingResult result, Model model){
		
		Usuario usuarioAux = usuarioDAO.findById(usuario.getId());		
		
		mapUsuario(usuario, usuarioAux);
		
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
		usuario.setPassword(bCryptPasswordEncoder.encode(usuario.getPassword()));		
		
		usuarioDAO.save(usuario);
		
		return "redirect:/";
		
	}
}


class Perfil{
	
	private String id;
	private String value;	
	
	public Perfil(String id, String value) {
		super();
		this.id = id;
		this.value = value;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public static Iterable<Perfil> getPerfiles(){
		
		ArrayList<Perfil> perfiles = new ArrayList();
				
		perfiles.add(new Perfil("A", "ADMINISTRADOR"));
		perfiles.add(new Perfil("U", "USUARIO"));
		//perfiles.add(new Perfil(3, "SUPER"));
		return perfiles;	
	}
	
}