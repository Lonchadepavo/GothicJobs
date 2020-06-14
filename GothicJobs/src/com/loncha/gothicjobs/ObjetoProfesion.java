package com.loncha.gothicjobs;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class ObjetoProfesion {
	String nombreProfesion;
	int nivelProfesion;
	
	List<ItemStack> objetosProfesion;
	List<ItemStack> mecanicasProfesion;
	
	public String getNombreProfesion() {
		return nombreProfesion;
	}
	public void setNombreProfesion(String nombreProfesion) {
		this.nombreProfesion = nombreProfesion;
	}
	public int getNivelProfesion() {
		return nivelProfesion;
	}
	public void setNivelProfesion(int nivelProfesion) {
		this.nivelProfesion = nivelProfesion;
	}
	public List<ItemStack> getObjetosProfesion() {
		return objetosProfesion;
	}
	public void setObjetosProfesion(List<ItemStack> objetosProfesion) {
		this.objetosProfesion = objetosProfesion;
	}
	public List<ItemStack> getMecanicasProfesion() {
		return mecanicasProfesion;
	}
	public void setMecanicasProfesion(List<ItemStack> mecanicasProfesion) {
		this.mecanicasProfesion = mecanicasProfesion;
	}
	
	
	
}
