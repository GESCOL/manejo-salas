package com.example.manejosalas.DAO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.manejosalas.entidad.Solicitud;
import com.example.manejosalas.entidad.Usuario;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SolicitudDAOTest {

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private SolicitudDAO solicitudDAO;
	
	@Autowired
	private UsuarioDAO usuarioDAO;
	
	@Test
	public void findById_thenReturnSolicitud() {
		
		Solicitud solicitud = new Solicitud();
		solicitud.setId(123);
		entityManager.persist(solicitud);
		entityManager.flush();		
				
		Solicitud encontrada = solicitudDAO.findById(solicitud.getID());		
		
		assertThat(encontrada.getID()).isEqualTo(solicitud.getID());
	}
	
	@Test
	public void saveSolicitud_inDataBase() {
		Solicitud solicitud = new Solicitud();
		solicitud.setEstado("A");
		//solicitud.setFecha_prestamo('2020/11/11');
		//solicitud.setFecha_solicitud('2020-11-11 23:59:59');
		solicitud.setId(123);
		solicitud.setSalaEdificioId(453);
		solicitud.setSalaId(205);	    
	    
		solicitudDAO.save(solicitud);
	    
		assertThat(solicitud).hasFieldOrPropertyWithValue("estado","A");
		assertThat(solicitud).hasFieldOrPropertyWithValue("id","123");
		assertThat(solicitud).hasFieldOrPropertyWithValue("salaedificioid","453");
		assertThat(solicitud).hasFieldOrPropertyWithValue("salaid","205");			
	}	
	//----------------------------------------------------------------------
	  @Test
	  public void findUsuario_byId() {
		  
	    Usuario usr1 = new Usuario();
	    usr1.setId(1);
	    usr1.setCorreo("f2@unal.edu.co");
	    usr1.setPassword("kkk");
	    entityManager.persist(usr1);

	    Usuario usr2 = new Usuario();
	    usr2.setId(2);
	    usr2.setCorreo("f@unal.edu.co");
	    usr2.setPassword("kkk");
	    entityManager.persist(usr2);

	    Usuario foundTutorial = usuarioDAO.findById(usr2.getId()).get();

	    assertThat(foundTutorial).isEqualTo(usr2);
	  }
	
	  @Test
	  public void updateUsuario_byId() {
	    Usuario usr1 = new Usuario();
	    usr1.setId(1);
	    usr1.setCorreo("correo1@unal.edu.co");
	    usr1.setPassword("rock1234");
	    entityManager.persist(usr1);

	    Usuario usr2 = new Usuario();
	    usr2.setId(2);
	    usr2.setCorreo("correo2@unal.edu.co");
	    usr2.setPassword("crack1234");
	    entityManager.persist(usr2);

	    Usuario updatedUsr2 = new Usuario();
	    updatedUsr2.setId(2);
	    updatedUsr2.setPassword("macarena");

	    Usuario usr = usuarioDAO.findById(usr2.getId()).get();
	    usr.setPassword(updatedUsr2.getPassword());
	    usuarioDAO.save(usr);

	    Usuario checkTut = usuarioDAO.findById(usr2.getId()).get();
	    
	    assertThat(checkTut.getId()).isEqualTo(usr2.getId());
	    assertThat(checkTut.getPassword()).isEqualTo(updatedUsr2.getPassword());	    
	  }	
	  
	  @Test
	  public void deleteUsuario_byId(){
		  
		  Usuario usr1 = new Usuario();
		  usr1.setId(1);
		  
		  entityManager.persist(usr1);
		  
		  Usuario usr2 = new Usuario();
		  usr2.setId(2);

		  entityManager.persist(usr2);

		  Usuario usr3 = new Usuario();
		  usr3.setId(3);

		  entityManager.persist(usr3);

		  usuarioDAO.deleteById(usr1.getId());
		  
		  Iterable<Usuario> usuarios = usuarioDAO.findAll();
		  		  
		  assertThat(usuarios).hasSize(2).contains(usr2, usr3);		  
	  }

}
