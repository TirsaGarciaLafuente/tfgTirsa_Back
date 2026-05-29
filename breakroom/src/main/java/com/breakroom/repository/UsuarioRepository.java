package com.breakroom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.breakroom.Models.Entity.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	Usuario findByUsernameAndPassword(String username, String password);
	
	boolean existsByEmailAndUsername(String email, String username);
	
	@Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.username = :username")
	Optional<Usuario> findByEmailAndUsername(@Param("email") String email, @Param("username") String username);
	
	@Query("SELECT u FROM Usuario u WHERE u.username = :username")
	Usuario findByUsername(@Param("username") String username);
	
	Optional<Usuario> findByEmail(String email);
}
