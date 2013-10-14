package com.ese2013.mub.model;

import java.util.ArrayList;
import static junit.framework.Assert.*;

public class Model {
	private ArrayList<Mensa> mensas = new ArrayList<Mensa>();
	private static Model instance;
	public Model() {
		createMensaList();
		Model.instance = this;
	}
	private void createMensaList() {
		MensaFactory fac = new MensaFactory();
		mensas = fac.createMensas();
	}
	
	public ArrayList<Mensa> getMensas() {
		return mensas;
	}
	
	public static Model getInstance() {
		assertNotNull(instance);
		return instance;
	}
}
