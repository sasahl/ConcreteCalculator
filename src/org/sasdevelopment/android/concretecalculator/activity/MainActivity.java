package org.sasdevelopment.android.concretecalculator.activity;
/*
 * This activity hosts the Mainfragment and is the Main page of the application. This is the first page users will see when 
 * the application is started.
 * It is responsible for setting up the Navigation drawer, and implements the Location listener needed for some 
 * 	of the drawer items.
 * A custom Adapter is used to populate the ListView of the navigation drawer and is defined here as well.
 */
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.sasdevelopment.android.concretecalculator.NavItems;
import org.sasdevelopment.android.concretecalculator.R;
import org.sasdevelopment.android.concretecalculator.fragment.MainFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	
	//public static final String TAG = "mainActivity";
	
	private Timer locationTimer;
	private String[] mDrawerTitles;
	private TypedArray mDrawerIcons;
	private ArrayList<NavItems> mNavItems; //Used to populate the ListView of the Navigation Drawer
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private LocationManager lm;
	private LocationListener mLocationListener;
	private Location mCurrentLocation;
	private ProgressDialog pd; //Shows a progress circle while a location is found
	private String mHardwareStoreStr;
	private String mBestProvider;
	private boolean isLocationFound = true; //this will only be false if the location timer runs out with no location found
	



	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawable(null);
        
      //Force portrait on small devices
      if(getResources().getBoolean(R.bool.portrait_only)) {
      		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        
        //Set the default values of the preferences.
        // parameter false ensures that the default values will only be set once, and this
        //	method is not called again after the first run.
        PreferenceManager.setDefaultValues(this, R.xml.prefs_slab, false);
        
        //The FragmentManager is in charge of adding, removing, or replacing fragments in the Activity.
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        
        //Add an instance of MainFragment to this Activity
        if(fragment == null) {
        	fragment = new MainFragment();
        	fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
        
        //Setup the navigation drawer
        mDrawerIcons = getResources().obtainTypedArray(R.array.icons); //mDrawerIcons hold the array of icons used for the ListView
        mDrawerTitles = getResources().getStringArray(R.array.navigation_drawer);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.navigation_drawer);
        
        //Initialize the arrayList with the values from the resource arrays
        mNavItems = new ArrayList<NavItems>();
        for(int i = 0; i < mDrawerTitles.length; i++) {
        	NavItems  n = new NavItems(mDrawerIcons.getDrawable(i), mDrawerTitles[i]);
        	mNavItems.add(n);
        }
        
        
        //Setup the adapter for the list view in the navigation drawer
        mDrawerList.setAdapter(new NavigationViewAdapter(mNavItems));
        //Set the background and the opacity for the listview background
        mDrawerList.setBackgroundResource(R.drawable.background_blue);
        mDrawerList.getBackground().setAlpha(150);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener()); //register the item click listener for the navigation drawer
       
        
        //Initialize the drawer toggle bar icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);

    	//Set the ic_drawer icon on the actionBar app icon.
    	mDrawerLayout.setDrawerListener(mDrawerToggle);
    	
    	getActionBar().setDisplayHomeAsUpEnabled(true);
    	
    	
    	//Instantiate the LocationManager, and get location data
    	lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	mLocationListener = new CurrentLocationListener();
    	
    	//set the criteria for location provider. Values are set to use Network provider whenever possible.
    	Criteria c = new Criteria();
    	c.setAccuracy(Criteria.ACCURACY_COARSE);
    	c.setBearingRequired(false);
    	c.setAltitudeRequired(false);
    	c.setPowerRequirement(Criteria.POWER_LOW);
    	
    	//get the best location provider
    	mBestProvider = lm.getBestProvider(c, true);
    	
    	
    	if(mBestProvider != null) {
    		//Request single location update using best provider
    		lm.requestSingleUpdate(mBestProvider, mLocationListener, null);
    	}
    	
    }//End onCreate
  

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDrawerIcons.recycle(); //This method basically releases the memory from this array.
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	//Start the settings activity when settings is clicked
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			return true;
        }
        if(id == R.id.action_app_info) {
        	//Start the app info activity when app info is clicked
        	Intent i = new Intent(this, AppInfoActivity.class);
        	startActivity(i);
        	return true;
        }
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    //Throw an intent to open a map application that can find the nearest location of Lowe's, Home Depot, or a concrete supplier.
    private void findHardwareStore(String storeName) {
    	if(mCurrentLocation != null) {
	    	Uri uri = Uri.parse("geo:" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude() + "?q=" + storeName);
	    	Intent i = new Intent(android.content.Intent.ACTION_VIEW, uri);
	    	startActivity(i);
    	}
    }
    
    /*
     * This class implements the the navigation drawer item clicks
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
    	
    	
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	
            switch(position) {
            case 0:
            	//if user don't have any location provider turned on, then just display a toast message
            	if(mBestProvider != null) {
	            	mHardwareStoreStr = "Lowe's";
	            	//if no current location is found, then execute the AsyncTask that shows the progress dialog while a location is found
	            	if(mCurrentLocation == null) {
	            		//start the location timer, in order to dismiss the "finding location" dialog if no location can be found after 30 seconds.
	            		startLocationTimer();
	            		new WaitForLocationTask().execute();
	            	}else
	            		findHardwareStore(mHardwareStoreStr);
            	}else
            		Toast.makeText(getBaseContext(), getResources().getString(R.string.no_location_provider_found), Toast.LENGTH_SHORT).show();
            	break;
            case 1:
            	//if user don't have any location provider turned on, then just display a toast message
            	if(mBestProvider != null) {
	            	mHardwareStoreStr = "Home Depot";
	            	//if no current location is found, then execute the AsyncTask that shows the progress dialog while a location is found
	            	if(mCurrentLocation == null) {
	            		//start the location timer, in order to dismiss the "finding location" dialog if no location can be found after 30 seconds.
	            		startLocationTimer();
	            		new WaitForLocationTask().execute();
	            	}else
	            		findHardwareStore(mHardwareStoreStr);
            	}else
            		Toast.makeText(getBaseContext(), getResources().getString(R.string.no_location_provider_found), Toast.LENGTH_SHORT).show();
            	break;
            case 2:
            	//if user don't have any location provider turned on, then just display a toast message
            	if(mBestProvider != null) {
	            	mHardwareStoreStr = "Concrete Supplier";
	            	//if no current location is found, then execute the AsyncTask that shows the progress dialog while a location is found
	            	if(mCurrentLocation == null) {
	            		//start the location timer, in order to dismiss the "finding location" dialog if no location can be found after 30 seconds.
	            		startLocationTimer();
	            		new WaitForLocationTask().execute();
	            	}else
	            		findHardwareStore(mHardwareStoreStr);
            	}else
            		Toast.makeText(getBaseContext(), getResources().getString(R.string.no_location_provider_found), Toast.LENGTH_SHORT).show();
            	break;
            case 3:
            	//Start the app info activity
            	Intent n = new Intent(getBaseContext(), AppInfoActivity.class);
            	startActivity(n);
            	break;
            case 4: 
            	//Start the settings activity when settings is clicked
    			Intent i = new Intent(getBaseContext(), SettingsActivity.class);
    			startActivity(i);
    			break;
            }
        }
    }//End drawerItemClickListener
    
    /*
     * Helper method that starts a timer if no location has been found, when the user clicks any of the "find Lowes, Home Depot, or concrete supplier" in the navigation drawer
     */
    private void startLocationTimer() {
    	//start the location timer, in order to dismiss the "finding location" dialog if no location can be found after 30 seconds.
		locationTimer = new Timer();
    	locationTimer.schedule(new TimerTask() {
    		public void run() {
    			isLocationFound = false;
    			locationTimer.cancel();
    			}
    		}, 30000);
    }
   
   
    /*
     * This class listens for the location of the user. It is used to find Lowe's and Home Depot stores
     * 		relative to the user.
     */
    private class CurrentLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if(location != null) {
				mCurrentLocation = location;
				if(locationTimer != null)
					locationTimer.cancel(); //Cancel the timer if a location is found.
			}	
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onProviderDisabled(String provider) {}
    	
    }//End CurrentLocationListener
    
    /*
     * This class takes care of showing the progress dialog when a user wants to find Lowes or Home Depot.
     * The progress Dialog is only needed if no location has been found before the user clicks the links 
     * in the navigation drawer. 
     * A location is being searched for as soon as the application starts, so this AsyncTask is only invoked if
     * the user goes right to the navigation drawer link for the hardware store search, and no location has yet been found.
     * A location should be found shortly after the application is first started.
     */
    private class WaitForLocationTask extends AsyncTask<Void, Void, Void> {
    	
    	//called on the UI thread
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//Setup the progress Dialog
			pd = new ProgressDialog(MainActivity.this);
			pd.setMessage(getResources().getString(R.string.finding_location));
			pd.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			//loop while location is not found
			while(mCurrentLocation == null) {
				if(isCancelled() || !isLocationFound) break;
			} 
			return null;
		}

		//Called on the UI thread
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//Dismiss the progress Dialog 
			pd.dismiss();
			if(isLocationFound) {
			   findHardwareStore(mHardwareStoreStr);
			}else {
				Toast.makeText(getBaseContext(), getResources().getString(R.string.no_location_found), Toast.LENGTH_SHORT).show();
				isLocationFound = true; //Reset to true, otherwise if the user clicks the " find lowes" etc several times, the asyncTask would go straight to onPostExecute() method
			}
		}	
    }//End WaitForLocationTask
    
   
    /*
     * Custom adapter that is used to populate the listView of the navigation drawer.
     */
    private class NavigationViewAdapter extends ArrayAdapter<NavItems> {
    	
    	public NavigationViewAdapter(ArrayList<NavItems> navItems) {
    		super(getBaseContext(), 0 , navItems);
    	}

    	/*
    	 * position represents the current position in the array
    	 * convertView is possibly a recycled view from the adapter, if there are more views than the screen
    	 *    can show, then the old views get reused, so the memory needed for the views created are kept to a minimum
    	 *    
    	 *    getView() is the method that builds each view of the ListView
    	 *    
    	 *    For my application convertView is not really needed because I don't inflate more views than can fit on the screen.
    	 *    but for a ListView with many items it is crucial to do this.
    	 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			//If we weren't given a view, inflate one
			if(convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.navigation_textview, parent, false);
			}
			//Get the NavItem object at this position in the array
			NavItems n = getItem(position);
			
			//inflate the textView and use it as a compound drawable
			TextView t = (TextView)convertView.findViewById(R.id.text1);
			t.setText(n.getTitle());
			t.setCompoundDrawablesWithIntrinsicBounds(n.getIcon(), null, null, null);
			return convertView;
			
		}	
    }
}
