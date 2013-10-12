package com.ese2013.mub;

public class Mensa {
	private String name;
	private String adress;
	private String contact;
	private static final String DEFAULT = "N//A";
	
	public Mensa(MensaBuilder builder){
		this.name = builder.name;
		this.adress = builder.address;
		this.contact = builder.contact;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}
		public static class MensaBuilder{
			String name = DEFAULT;
			String address = DEFAULT;
			String contact = DEFAULT;
			
			public MensaBuilder(){
				
			}
			
			public MensaBuilder setName(String name){
				this.name = name;
				return this;
			}
	
			public MensaBuilder setAdress(String adress){
				this.address = adress;
				return this;
			}
			public MensaBuilder setContact(String contact){
				this.contact = contact;
				return this;
			}
			public Mensa build(){
				return new Mensa(this);
			}
			
		}
	
}
