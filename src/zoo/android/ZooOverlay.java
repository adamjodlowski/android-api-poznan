package zoo.android;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * Warstwa obiektów nakładana na mapę.
 * 
 * @author Adam Jodłowski http://jodlowski.net
 *
 */
public class ZooOverlay extends ItemizedOverlay<OverlayItem> {

	// lista obiektów do pokazania
	private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
	
	// kontekst nadrzędnej aktywności
	private Context ctx = null;
	
	// lista biletomatów
	ArrayList<TicketMachine> ticketMachines = null;
	
	public ZooOverlay(Drawable marker, Context ctx, ArrayList<TicketMachine> ticketMachines) {
		super(marker);
		
		this.ctx = ctx;
		
		// pozycjonujemy środek grafiki markera względem punktu na mapie
		boundCenter(marker);
		
		this.ticketMachines = ticketMachines;
		
		// bonus! dodajemy ZOO do mapy
		Drawable zooMarker = ctx.getResources().getDrawable(R.drawable.zoo);
		zooMarker.setBounds(0, 0, zooMarker.getIntrinsicWidth(), zooMarker.getIntrinsicHeight());
		boundCenter(zooMarker);
		
		OverlayItem zooItem = new OverlayItem(new GeoPoint(52408333, 16908333), "ZOO", "ZOO Coworking");
		zooItem.setMarker(zooMarker);
		items.add(zooItem);
		
		// wypełniamy listę obiektów na mapie pobranymi biletomatami
		for (int i = 0; i < ticketMachines.size(); i++) {
			items.add(new OverlayItem(ticketMachines.get(i).getGeoPoint(), ticketMachines.get(i).getTitle(), ticketMachines.get(i).getDesc()));
		}

		// wypełniamy warstwę, co powoduje jej przerysowanie
		populate();
		
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return(items.get(i));
	}
	
	@Override
	public int size() {
		return(items.size());
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// wyłączamy rysowanie cienia za markerem
		super.draw(canvas, mapView, false);
	}
	
	/*
	 * Reakcja na tapnięcie markera.
	 * 
	 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
	 */
	@Override
	protected boolean onTap(int i) {
		
		// pobieramy własną pozycję i liczymy odległość do biletomatu
		GeoPoint currentLocation = ((Zoo) ctx).getLocation();
		float[] distance = new float[10];
		Location.distanceBetween(currentLocation.getLatitudeE6() / 1e6, currentLocation.getLongitudeE6() / 1e6, items.get(i).getPoint().getLatitudeE6() / 1e6, items.get(i).getPoint().getLongitudeE6() / 1e6, distance);
				
		// przygotowujemy tekst
		String text = items.get(i).getTitle() + " (" + (int) distance[0] + "m)";
		text += "\n" + items.get(i).getSnippet();
		
		// wyświetlamy toast o treści pobranej z markera
		Toast toast = Toast.makeText(ctx, text, Toast.LENGTH_LONG);
		// zmieniamy grawitację toastu
		toast.setGravity(android.view.Gravity.TOP, 0, 36);
		toast.show();
		
		return true;
	}
	
}
