package zoo.android;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

/**
 * Worker pobierający i parsujący dane z API.
 * 
 * @author Adam Jodłowski http://jodlowski.net
 *
 */
public class GetDataTask extends AsyncTask<String, Integer, ArrayList<TicketMachine>> {
	
	// kontekst nadrzędnej aktywności
	private Context ctx = null;
	
	// TextView z informacją o postępie
	private TextView updateText = null;
	
	GetDataTask(Context context) {
		ctx = context;
		
		// pobieramy uchwyt do TextView
		updateText = (TextView) ((Activity) ctx).findViewById(R.id.update_text);
	}
	
	/*
	 * Inicjalizacja pracy workera.
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		// pokazujemy label z postępem
		updateText.setVisibility(View.VISIBLE);
		updateText.setText("Pobieranie 0%");
	}
	
	/*
	 * Metoda pobierająca i przetwarzające dane w tle.
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected ArrayList<TicketMachine> doInBackground(String... params) {
		
		// pobieramy dane z API, korzystając z Apache HTTP Client
		HttpClient httpclient = new DefaultHttpClient();
		// ułatwiamy sobie życie
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		String response = null;
		try {
			response = httpclient.execute(new HttpGet(params[0]), responseHandler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// wynik przetwarzania czyli lista obiektów
		ArrayList<TicketMachine> result = null;
		
		if (response != null) {
			
			JSONObject json = null;
			
			try {
				
				json = new JSONObject(response);
				
				if (json != null) {
					
					result = new ArrayList<TicketMachine>();
					
					JSONArray figures = json.getJSONArray("features");
					for (int i = 0; i < figures.length(); i++) {
						
						// konstruujemy obiekt pozycji
						GeoPoint point = new GeoPoint((int) (figures.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getDouble(1) * 1000000), (int) (figures.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getDouble(0) * 1000000));
						
						// dodajemy obiekt miejsca do listy
						result.add(new TicketMachine(point, figures.getJSONObject(i).getInt("id"), figures.getJSONObject(i).getJSONObject("properties").getString("opis"), figures.getJSONObject(i).getJSONObject("properties").getString("opis_long")));
						
						// sztuczne wydłużenie czasu przetwarzania na potrzeby aktualizacji postępu
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						// aktualizujemy postęp
						publishProgress((int) (i*1.0 / figures.length() * 100));
						
					}
					
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
		return result;
	}

	/*
	 * Aktualizacja postępu w wątku UI z wątku tła.
	 */
	@Override
    protected void onProgressUpdate(Integer... progress){
		
    	// aktualizacja postępu
		updateText.setText("Pobieranie " + progress[0] + "%");
    	
    }

    /*
     * Reakcja na zakończenie przetwarzania.
     */
	@Override
    protected void onPostExecute(ArrayList<TicketMachine> result){

		// ukrywamy wskaźnik postępu
    	updateText.setVisibility(View.INVISIBLE);
    	
    	// przekazujemy aktywności rezultat przetwarzania
    	((Zoo) ctx).makeOverlay(result);
    	
    }

}
