package edu.gatech.cc.agency.ros;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class AndroidNavActivity extends RosActivity
{
	private static final String NOTIFICATION_KEY = "AndroidNav";
	protected static final String TAG = AndroidNavActivity.class.getCanonicalName();
	private AndroidLocationNode locationNode;

	public AndroidNavActivity() {
		super(NOTIFICATION_KEY, NOTIFICATION_KEY);
		// TODO Auto-generated constructor stub
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	
    	// Acquire a reference to the system Location Manager
    	LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    	// Define a listener that responds to location updates
    	LocationListener locationListener = new LocationListener() {
    	    private boolean gpsEnabled;

			public void onLocationChanged(Location location) {
				//take any location, unless the GPS is enabled...the only take GPS positions
				if (!gpsEnabled || location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
					// Called when a new location is found by the network location provider.
					makeUseOfNewLocation(location);
				}
    	    }

    	    public void onStatusChanged(String provider, int status, Bundle extras) {}

    	    public void onProviderEnabled(String provider) {
    	    	if (provider.equals(LocationManager.GPS_PROVIDER)) {
    	    		gpsEnabled = true;
    	    		Log.i(TAG, "Gps Enabled.  Suppressing network location");
    	    	}
    	    }

    	    public void onProviderDisabled(String provider) {
    	    	if (provider.equals(LocationManager.GPS_PROVIDER)) {
    	    		gpsEnabled = false;
    	    		Log.i(TAG, "Gps Disabled.  Using network location");
    	    	}
    	    }
    	  };

    	// Register the listener with the Location Manager to receive location updates
    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    protected void makeUseOfNewLocation(Location location) {
    	if (locationNode != null) {
    		locationNode.updateLocation(location);
    	}
	}

	@Override
	protected void init(NodeMainExecutor nodeMainExecutor) {
	    // At this point, the user has already been prompted to either enter the URI
	    // of a master to use or to start a master locally.
	    String host = InetAddressFactory.newNonLoopback().getHostName();
	    NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(host, getMasterUri());

	    //Execute the location node
	    locationNode = new AndroidLocationNode();
	    nodeMainExecutor.execute(locationNode, nodeConfiguration);
	}
}
