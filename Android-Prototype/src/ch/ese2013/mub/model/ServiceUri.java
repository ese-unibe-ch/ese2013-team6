package ch.ese2013.mub.model;

public class ServiceUri {
	public static final String GET_MENSAS = "http://mensa.xonix.ch/v1/mensas?tok=6112255ca02b3040711015bbbda8d955";
	public static final String GET_CURRENT_MENUPLAN = "http://mensa.xonix.ch/v1/mensas/:id/dailyplan?tok=6112255ca02b3040711015bbbda8d955";
	public static final String GET_MENUPLAN = "http://mensa.xonix.ch/mensa/:id/plan/:date/?tok=6112255ca02b3040711015bbbda8d955";
	public static final String GET_WEEKLY_MENUPLAN = "http://mensa.xonix.ch/v1/mensas/1/weeklyplan?tok=6112255ca02b3040711015bbbda8d955";
}
