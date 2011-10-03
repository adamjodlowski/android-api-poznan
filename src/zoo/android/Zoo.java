package zoo.android;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Aktywność demonstrująca pracę z mapami Google.
 * 
 * @author Adam Jodłowski http://jodlowski.net
 *
 */
public class Zoo extends MapActivity {
	
	// widok mapy zagnieżdzony w layoucie
	private MapView map = null;
	
	// warstwa od Google nakładana na mapę
	private MyLocationOverlay locationOverlay = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        map = (MapView) findViewById(R.id.map);
        
        // ustawienie środka mapy na danym punkcie
        map.getController().setCenter(new GeoPoint(52408333, 16908333)); //52°24'30", 16°54'30"
        
        // ustawienie zbliżenia mapy (od 1 do 21)
        map.getController().setZoom(18);
        
        // zezwolenie na przybliżanie i oddalanie widoku
        map.setBuiltInZoomControls(true);
        
        // zainicjowaliśmy mapę, czas na warstwę obiektów
		
		// doddajemy warstwę od Google, która dostarczy nam kompas i wkazanie naszego położenia
		locationOverlay = new MyLocationOverlay(this, map);
		map.getOverlays().add(locationOverlay);
        
    }
    
    /*
     * Wypełnienie mapy pobranymi z API obiektami za pomocą własnej warstwy.
     */
	public void makeOverlay(ArrayList<TicketMachine> ticketMachines) {

		// pobieramy grafikę markera z zasobów
		Drawable marker = getResources().getDrawable(R.drawable.mpk);

		// ustawienie granic obiektu wymaganych do rysowania
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());

		// dodajemy naszą warstwę do mapy
		map.getOverlays().add(new ZooOverlay(marker, this, ticketMachines));

	}
    
	@Override
	public void onResume() {
		super.onResume();
		
		// włączamy lokalizację urządzenia
		locationOverlay.enableMyLocation();
		
		// włączamy kompas
		locationOverlay.enableCompass();
	}		
	
	@Override
	public void onPause() {
		super.onPause();
		
		// wyłączamy lokalizację urządzenia
		locationOverlay.disableMyLocation();
		
		// wyłączamy kompas
		locationOverlay.disableCompass();
	}

    /*
     * Od momentu zablokowania możliwości nawigowania z map Google,
     * zawsze zwracamy 'false' w tej wymaganej metodzie.
     * 
     * @see com.google.android.maps.MapActivity#isRouteDisplayed()
     */
    @Override
	protected boolean isRouteDisplayed() {
		return false;
	}
    
    /*
     * Metoda pomocnicza zwracająca aktualne położenie
     */
    public GeoPoint getLocation() {
    	
    	// uwaga! jeśli korzystamy z emulatora, po każdym starcie aplikacji należy wysłać do niej
    	// współrzędne za pomocą narzędzia 'Emulator Control', jeśli tego nie zrobimy, dostaniemy
    	// w tym miejscu NullPointerException
    	return new GeoPoint((int) (locationOverlay.getLastFix().getLatitude() * 1e6), (int) (locationOverlay.getLastFix().getLongitude() * 1e6));
    }
    
    // obsługa menu
    
    /*
     * Inicjalizacja menu z zasobu XML.
     */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	/*
	 * Reakcja na kliknięcie pozycji w menu.
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	    switch (item.getItemId()) {
	    
	    case R.id.menu_satellite:
	        
	    	// przełączamy widok satelitarny z mapą
	    	if (map.isSatellite()) {
	    		map.setSatellite(false);
	    	} else {
	    		map.setSatellite(true);
	    	}
	    	
	        return true;
	        
	    case R.id.menu_get_data:
	        
	    	// pobieramy w tle dane o interesujących nas punktach
	    	
	    	GetDataTask getDataTask = new GetDataTask(this);
	        getDataTask.execute("http://www.poznan.pl/featureserver/featureserver.cgi/biletomaty_wgs/");
	    	
	        return true;
	        
	    default:
	        return super.onOptionsItemSelected(item);
	        
	    }
	    
	}
    
}