package zoo.android;

import com.google.android.maps.GeoPoint;

/**
 * Model biletomatu.
 * 
 * @author Adam Jodłowski http://jodlowski.net
 *
 */
public class TicketMachine {
	
	private GeoPoint geoPoint;
	private int id;
	private String title;
	private String desc;
	
	public TicketMachine(GeoPoint geoPoint, int id, String title, String desc) {
		super();
		this.geoPoint = geoPoint;
		this.id = id;
		this.title = title;
		this.desc = desc;
	}

	public GeoPoint getGeoPoint() {
		return geoPoint;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDesc() {
		return desc;
	}
	
	@Override
	public String toString() {
		return geoPoint.getLatitudeE6() + ", " + geoPoint.getLongitudeE6() + " => " + this.title;
	}

}
