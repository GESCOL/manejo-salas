package com.example.manejosalas.DAO;
import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.FetchProfile.FetchOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.manejosalas.entidad.Sala;
import com.example.manejosalas.entidad.Usuario;

@Repository
public interface SalaDAO extends JpaRepository<Sala, Integer>{
	
	public List<Sala> findAll();
	
	public Sala findById(int id);
	
	public Sala findByIdAndEdificioId(int id, int edificioId);
	public Sala findByedificioId(int id);
	public List <Sala> findAllByencargado(Usuario encargado);
			
}
