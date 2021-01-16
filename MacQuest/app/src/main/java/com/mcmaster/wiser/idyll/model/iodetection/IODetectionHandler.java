package com.mcmaster.wiser.idyll.model.iodetection;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.mcmaster.wiser.idyll.model.iodetection.classifier.ClassifyHandler;

import java.lang.reflect.Method;
import java.util.Calendar;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Created by steve on 2017-06-27.
 */

public class IODetectionHandler implements SensorEventListener {

    private static final String TAG = "IODetectionHandler";

    /**
     * Context
     */
    private Context mContext;

    private OnIOChangeListener onIOChangeListener;

    /**
     * Sensor Manager
     */
    private SensorManager mSensorManager;

    //Telephony Manager
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;

    //GPS Manager
    private LocationManager locationManager;
    private LocationListener locationListenerGPS;
    private LocationListener locationListenerNetwork;
    private GpsStatus.Listener GpsListener;
    private GpsStatus.NmeaListener NmeaListener_1;

    //Sensors
    private Sensor mLight;
    private Sensor mMag;
    private boolean Day = true;
    private float light;
    private float lightCurrent;
    private float lightPrev;
    private float[] Mag;
    private double MagAv;
    private double MagLat;
    private double MagAvCurrent;
    private double MagAvPrev;
    private double IncMagCurrent;
    private double IncMagPrev;

    //Aux
    private long t;
    private float alpha = (float) 0.5;
    private boolean flag = true;
    private double a = 6378.1370;
    private double b = 6356.7523;
    private double B0 = 31.2;
    private double r;
    private double R_;
    private boolean Day_Init;
    private Boolean out = null;

    //GPS - CELL
    private double Lat;
    private double Lng;
    // Number of Satellites
    private int Sat = -1;
    // The signal to noise ratio for the satellite.
    private float Sat_snr = 0;
    private float SatCurrent;
    private float SatPrev;
    private float SnrCurrent;
    private float SnrPrev;
    private int Cell_gsm;
    private int Cell_cdma;
    private float Cell_lte;
    private float CellCurrent;
    private float CellPrev;
    private boolean locationready;

    float Snr = (float) -1;
    float Snr_1 = 0;
    float Snr_2 = 0;
    float Snr_3 = 0;
    float Snr_4 = 0;
    int cont_snr = 0;

    //Probabilities
    private float M1 = 20;
    private float L1 = 0;
    private float L2 = 2500;
    private float S1 = 2;//2
    private float S2 = 20;//18
    private float C1 = 0;
    private float C2 = 100;
    private float SN1 = 0;
    private float SN2 = 36;
    private double P_out = 0;
    private double P_sum = 0;
    private double P_End = 0;
    private double P_EndPrev = -1;
    private double P_light_OUT;
    private double P_light_IN;
    private double P_mag_OUT;
    private double P_mag_IN;
    private double P_sat_OUT;
    private double P_sat_IN;
    private double P_cell_OUT;
    private double P_cell_IN;
    private double P_snr_OUT;
    private double P_snr_IN;
    private int cont = 0;


    public IODetectionHandler(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mMag = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //Set Telephony Manager
        telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        //Set GPS Manager
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        t = System.currentTimeMillis();
        setupGPS();
        //Timer - Day
        //setupTimer();
        Day_Init = false;
        //Cell
        setupCell();
    }

    private void setupGPS() {

        locationListenerNetwork = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d(TAG, "locationListenerNetwork: onLocationChanged...");
                Lat = location.getLatitude();
                Lng = location.getLongitude();
                locationready = true;
                Sat = 1;
                //Initialize Day
                if (!Day_Init) {
                    Calendar cal = Calendar.getInstance();
//                    Day = sun(Lat, Lng, cal);
                    Day_Init = true;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Sat = -1;
                Snr = -1;
                locationready = false;
            }
        };
        //////////////////////////////
        locationListenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        ///////////NMEA///////////////
        NmeaListener_1 = new GpsStatus.NmeaListener() {

            @Override
            public void onNmeaReceived(long timestamp, String nmea) {
                String[] Nmea_Info = nmea.split(",");
                if ("$GPGSV".equals(Nmea_Info[0])) {

                    switch (Integer.valueOf(Nmea_Info[1])) {
                        case 1:
                            Snr = snr(Nmea_Info);
                            break;
                        case 2:
                            switch (Integer.valueOf(Nmea_Info[2])) {
                                case 1:
                                    Snr_1 = snr(Nmea_Info);
                                    break;
                                case 2:
                                    Snr_2 = snr(Nmea_Info);
                                    cont_snr = 0;
                                    if (Snr_1 != 0) {
                                        cont_snr++;
                                    }
                                    if (Snr_2 != 0) {
                                        cont_snr++;
                                    }
                                    if (cont_snr != 0) {
                                        Snr = (Snr_1 + Snr_2) / cont_snr;
                                    } else {
                                        Snr = 0;
                                    }
                                    break;
                                default:
                                    break;
                            }
                            Snr_2 = snr(Nmea_Info);
                            break;

                        case 3:
                            switch (Integer.valueOf(Nmea_Info[2])) {
                                case 1:
                                    Snr_1 = snr(Nmea_Info);
                                    break;
                                case 2:
                                    Snr_2 = snr(Nmea_Info);
                                    break;
                                case 3:
                                    Snr_3 = snr(Nmea_Info);
                                    cont_snr = 0;
                                    if (Snr_1 != 0) {
                                        cont_snr++;
                                    }
                                    if (Snr_2 != 0) {
                                        cont_snr++;
                                    }
                                    if (Snr_3 != 0) {
                                        cont_snr++;
                                    }
                                    if (cont_snr != 0) {
                                        Snr = (Snr_1 + Snr_2 + Snr_3) / cont_snr;
                                    } else {
                                        Snr = 0;
                                    }
                                    break;

                                default:
                                    break;
                            }
                            break;
                        case 4:
                            switch (Integer.valueOf(Nmea_Info[2])) {
                                case 1:
                                    Snr_1 = snr(Nmea_Info);
                                    break;
                                case 2:
                                    Snr_2 = snr(Nmea_Info);
                                    break;
                                case 3:
                                    Snr_3 = snr(Nmea_Info);
                                    break;
                                case 4:
                                    Snr_4 = snr(Nmea_Info);
                                    cont_snr = 0;
                                    if (Snr_1 != 0) {
                                        cont_snr++;
                                    }
                                    if (Snr_2 != 0) {
                                        cont_snr++;
                                    }
                                    if (Snr_3 != 0) {
                                        cont_snr++;
                                    }
                                    if (Snr_4 != 0) {
                                        cont_snr++;
                                    }
                                    if (cont_snr != 0) {
                                        Snr = (Snr_1 + Snr_2 + Snr_3 + Snr_4) / cont_snr;
                                    } else {
                                        Snr = 0;
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        default:
                            break;
                    }

                }

            }
        };
        //////////GPS Status/////////////
        GpsListener = new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
                switch (event) {
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                        int MaxSat = gpsStatus.getMaxSatellites();
                        Sat = 0;
                        Sat_snr = 0;
                        Iterable<GpsSatellite> list = gpsStatus.getSatellites();
                        for (GpsSatellite satellite : list) {
                            if (satellite.usedInFix()) {
                                Sat++;
                                Sat_snr += satellite.getSnr();
                            }
                        }
                        Sat_snr = Sat_snr / Sat;
                        break;
                    default:
                        break;
                }
            }
        };
    }


    private float snr(String[] info) {
        try {
            float Snr_aux = 0;
            int cont_abcd = 0;
            int Snr_a = 0;
            int Snr_b = 0;
            int Snr_c = 0;
            int Snr_d = 0;

            //only one message
            if (info[7].isEmpty()) {
                Snr_a = 0;
            } else {
                String[] parts = info[7].split("\\*");
                if (parts[0].isEmpty()) {
                    Snr_a = 0;
                } else {
                    Snr_a = Integer.valueOf(parts[0]);
                    cont_abcd++;
                }

            }
            if (info.length > 11) {
                if (info[11].isEmpty()) {
                    Snr_b = 0;
                } else {
                    String[] parts = info[11].split("\\*");
                    if (parts[0].isEmpty()) {
                        Snr_b = 0;
                    } else {
                        Snr_b = Integer.valueOf(parts[0]);
                        cont_abcd++;
                    }
                }
            } else {
                Snr_b = 0;
            }
            if (info.length > 15) {
                if (info[15].isEmpty()) {
                    Snr_c = 0;
                } else {
                    String[] parts = info[15].split("\\*");
                    if (parts[0].isEmpty()) {
                        Snr_c = 0;
                    } else {
                        Snr_c = Integer.valueOf(parts[0]);
                        cont_abcd++;
                    }
                }
            } else {
                Snr_c = 0;
            }
            if (info.length > 19) {
                if (info[19].isEmpty()) {
                    Snr_d = 0;
                } else {
                    String[] parts = info[19].split("\\*");
                    if (parts[0].isEmpty()) {
                        Snr_d = 0;
                    } else {
                        Snr_d = Integer.valueOf(parts[0]);
                        cont_abcd++;
                    }
                }
            } else {
                Snr_d = 0;
            }
            if (cont_abcd != 0) {
                Snr_aux = (Snr_a + Snr_b + Snr_c + Snr_d) / cont_abcd;
            } else {
                Snr_aux = 0;
            }
            return Snr_aux;
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }


    private void setupCell() {

        phoneStateListener = new PhoneStateListener() {

            @TargetApi(Build.VERSION_CODES.M)
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {

                // Old
                /*

                String ssignal = signalStrength.toString();
                String[] parts = ssignal.split(" ");
                int dbm = -110;
                if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
                    dbm = Integer.parseInt(parts[8]) * 2 - 113;
                } else {
                    if (signalStrength.getGsmSignalStrength() != 99) {
                        dbm = -113 + 2 * signalStrength.getGsmSignalStrength();
                    }
                }
                Cell_lte = (float) (5.0 * (dbm + 110) / 3.0);

                */

                // New
                int dbm = -110;
                if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
                    try {
                        Method[] methods = android.telephony.SignalStrength.class.getMethods();

                        for (Method mthd : methods) {
                            if (mthd.getName().equals("getLteSignalStrength")) {
                                int LTEsignalStrength = (Integer) mthd.invoke(signalStrength, new Object[] {});
                                dbm = LTEsignalStrength * 2 - 113;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("LTE_Tag", "Failed to get Signal Strength: " + e.toString());
                    }
                } else {
                    if (signalStrength.getGsmSignalStrength() != 99) {
                        dbm = -113 + 2 * signalStrength.getGsmSignalStrength();
                    }
                }
                Cell_lte = (float) (5.0 * (dbm + 110) / 3.0);
            }
        };
    }

    public void onResume() {
        // Register a listener for the sensor.
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMag, SensorManager.SENSOR_DELAY_NORMAL);
        //Telephony
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        //GPS Stuff
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);//
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
        locationManager.addNmeaListener(NmeaListener_1); //Nmea
        locationManager.addGpsStatusListener(GpsListener); //Satellites
    }

    public void onStop() {
        // Be sure to unregister the sensor when the activity pauses.
        mSensorManager.unregisterListener(this);
        //Telephony
        //GPS Stuff
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locationListenerNetwork);
        locationManager.removeUpdates(locationListenerGPS);
        locationManager.removeNmeaListener(NmeaListener_1);
        locationManager.removeGpsStatusListener(GpsListener);

    }

    ///////////////
    protected boolean sun(double Lat, double Lng, Calendar now) {

        int Year = now.get(Calendar.YEAR);
        int Month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        int Day = now.get(Calendar.DAY_OF_MONTH);
        int Hour = now.get(Calendar.HOUR_OF_DAY);
        int Minute = now.get(Calendar.MINUTE);

        // Get SUNSET-SUNRISE
        float PI = (float) 3.1416;

        int localOffset = 2;
        float Zenith = (float) 90.8333;
        float D2R = PI / 180;
        float R2D = 180 / PI;

        // Day of the Year
        double N1 = Math.floor(275 * Month / 9);
        double N2 = Math.floor((Month + 9) / 12);
        double N3 = (1 + Math.floor((Year - 4 * Math.floor(Year / 4) + 2) / 3));
        double N = N1 - (N2 * N3) + Day - 30;

        double lnHour = Lng / 15;

        //sunrise aprox time
        double t_sunrise = N + ((6 - lnHour) / 24);
        //sunset aprox time
        double t_sunset = N + ((18 - lnHour) / 24);

        //Sun's mean anomaly
        double M_sunrise = (0.9856 * t_sunrise) - 3.289;
        double M_sunset = (0.9856 * t_sunset) - 3.289;

        //Sun's true longitude
        double L_sunrise = M_sunrise + (1.916 * sin(M_sunrise * D2R)) + (0.020 * sin(2 * M_sunrise * D2R)) + 282.634;
        double L_sunset = M_sunset + (1.916 * sin(M_sunset * D2R)) + (0.020 * sin(2 * M_sunset * D2R)) + 282.634;

        if (L_sunrise > 360)
            L_sunrise = L_sunrise - 360;
        else if (L_sunrise < 0)
            L_sunrise = L_sunrise + 360;

        if (L_sunset > 360)
            L_sunset = L_sunset - 360;
        else if (L_sunset < 0)
            L_sunset = L_sunset + 360;


        //Sun's right ascension
        double RA_sunrise = R2D * Math.atan(0.91764 * Math.tan(L_sunrise * D2R));
        double RA_sunset = R2D * Math.atan(0.91764 * Math.tan(L_sunset * D2R));

        if (RA_sunrise > 360)
            RA_sunrise = RA_sunrise - 360;
        else if (RA_sunrise < 0)
            RA_sunrise = RA_sunrise + 360;

        if (RA_sunset > 360)
            RA_sunset = RA_sunset - 360;
        else if (RA_sunset < 0)
            RA_sunset = RA_sunset + 360;

        // RA value needs to be in the same qua
        double Lquadrant_sunrise = (Math.floor(L_sunrise / (90))) * 90;
        double RAquadrant_sunrise = (Math.floor(RA_sunrise / 90)) * 90;
        RA_sunrise = RA_sunrise + (Lquadrant_sunrise - RAquadrant_sunrise);

        double Lquadrant_sunset = (Math.floor(L_sunset / (90))) * 90;
        double RAquadrant_sunset = (Math.floor(RA_sunset / 90)) * 90;
        RA_sunset = RA_sunset + (Lquadrant_sunset - RAquadrant_sunset);

        //RA needs to be converted to hours
        RA_sunrise = RA_sunrise / 15;
        RA_sunset = RA_sunset / 15;

        // Calculate de Sun's declination
        double sinDec_sunrise = 0.39782 * sin(L_sunrise * D2R);
        double cosDec_sunrise = cos(Math.asin(sinDec_sunrise));

        double sinDec_sunset = 0.39782 * sin(L_sunset * D2R);
        double cosDec_sunset = cos(Math.asin(sinDec_sunset));

        // Sun's local hour angle
        double cosH_sunrise = (cos(Zenith * D2R) - (sinDec_sunrise * sin(Lat * D2R))) / (cosDec_sunrise * cos(Lat * D2R));
        double cosH_sunset = (cos(Zenith * D2R) - (sinDec_sunset * sin(Lat * D2R))) / (cosDec_sunset * cos(Lat * D2R));

        double H_sunrise = (360 - R2D * Math.acos(cosH_sunrise)) / 15;
        double H_sunset = (R2D * Math.acos(cosH_sunset)) / 15;

        // Calculate local mean time of rising/setting
        double T_sunrise = H_sunrise + RA_sunrise - (0.06571 * t_sunrise) - 6.622;
        double T_sunset = H_sunset + RA_sunset - (0.06571 * t_sunset) - 6.622;

        // Adjust back to UTC
        double UT_sunrise = T_sunrise - lnHour;
        double UT_sunset = T_sunset - lnHour;

        if (UT_sunrise > 24)
            UT_sunrise = UT_sunrise - 24;
        else if (UT_sunrise < 0)
            UT_sunrise = UT_sunrise + 24;

        if (UT_sunset > 24)
            UT_sunset = UT_sunset - 24;
        else if (UT_sunset < 0)
            UT_sunset = UT_sunset + 24;

        // Convert UT value to local time zone

        double sunrise = UT_sunrise + localOffset;
        double sunset = UT_sunset + localOffset;

        double Time = Hour + Minute / 60;

        return Time > sunrise && Time < sunset; //true = day ... false = night

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                Mag = event.values.clone();
                MagAv = sqrt(pow(Mag[0], 2) + pow(Mag[1], 2) + pow(Mag[2], 2));
                break;
            case Sensor.TYPE_LIGHT:
                light = event.values[0];
                break;
        }
//        Toast.makeText(mContext.getApplicationContext(),"
//        ",Toast.LENGTH_SHORT).show();

        if (System.currentTimeMillis() - t >= 500) {
            main(); //Call the main function
            t = System.currentTimeMillis();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Estimate indoor/outdoor
     *
     * @param magAvCurrent
     * @param lightCurrent
     * @param cellCurrent
     * @param satCurrent
     * @param snrCurrent
     * @return true if outdoor, false if indoor
     */
    private boolean isOutByClassifier(double magAvCurrent, float lightCurrent, float cellCurrent, float satCurrent, float snrCurrent) {
        ClassifyHandler classifyHandler = new ClassifyHandler();
        boolean result = false;
        try {
            result = classifyHandler.isOut(magAvCurrent, lightCurrent, cellCurrent, satCurrent, snrCurrent, Day);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private boolean isOutByThreshold() {
        // LIGHT
        if (lightCurrent > L2) {
            P_light_OUT = 0.06;
            P_light_IN = 0;
        } else {
            if (lightCurrent < L1) {
                P_light_OUT = 0;
                P_light_IN = 0.12;
            } else {
                P_light_OUT = 0.012 * pow(lightCurrent / 100 - L1 / 100, 0.5);
                P_light_IN = 0.000192 * pow(lightCurrent / 100 - L2 / 100, 2);
            }
        }

        // MAGNETIC FIELD

        if (IncMagCurrent > M1) {
            P_mag_OUT = 0.0375;
            P_mag_IN = 0.0625;
        } else {
            P_mag_OUT = 0.0625 - 0.00125 * IncMagCurrent;
            P_mag_IN = 0.0375 + 0.00125 * IncMagCurrent;
        }

        // CELL STRENGTH

        if (CellCurrent > C2) {
            P_cell_OUT = 0.2;
            P_cell_IN = 0;
        } else {
            if (CellCurrent < C1) {
                P_cell_OUT = 0;
                P_cell_IN = 0.2;
            } else {
                P_cell_OUT = 0.02 * (CellCurrent / 10 - C1 / 10);
                P_cell_IN = 0.2 - 0.02 * (CellCurrent / 10 - C1 / 10);
            }
        }

        // Satellites Availables
        if (SatCurrent > 0) {
            //Satellites Availables
            if (SatCurrent > S2) {
                P_sat_OUT = 0.0936;
                P_sat_IN = 0;
            } else {
                if (SatCurrent < S1) {
                    P_sat_OUT = 0;
                    P_sat_IN = 0.187;
                } else {
                    P_sat_OUT = 0.0234 * pow(SatCurrent - S1, 0.5);
                    P_sat_IN = 0.000732 * pow(SatCurrent - S2, 2);
                }
            }
            if (SnrCurrent > SN2) { //Signal-Noise Ratio
                P_snr_OUT = 0.0416;
                P_snr_IN = 0;
            } else {
                if (SnrCurrent < SN1) {
                    P_snr_OUT = 0;
                    P_snr_IN = 0.083;
                } else {
                    P_snr_OUT = 0.00694 * pow(SnrCurrent - SN1, 0.5);
                    P_snr_IN = 0.0000643 * pow(SnrCurrent - SN2, 2);
                }
            }

            //P_snr_OUT = 0.5;
            //P_snr_IN = 0.5;
            Log.d(TAG, "P_sat_OUT=" + P_sat_OUT + ", P_sat_IN=" + P_sat_IN + ", P_snr_OUT=" + P_snr_OUT + ", P_snr_IN=" + P_snr_IN + ", SatCurrent=" + SatCurrent);

            if (Day) //take into account everything
                if (0.5 * P_light_OUT * P_mag_OUT * P_cell_OUT * P_sat_OUT * P_snr_OUT + 0.5 * P_light_IN * P_mag_IN * P_cell_IN * P_sat_IN * P_snr_IN > 0)
                    P_out = 0.5 * P_light_OUT * P_mag_OUT * P_cell_OUT * P_sat_OUT * P_snr_OUT / (0.5 * P_light_OUT * P_mag_OUT * P_cell_OUT * P_sat_OUT * P_snr_OUT + 0.5 * P_light_IN * P_mag_IN * P_cell_IN * P_sat_IN * P_snr_IN);
                else
                    P_out = 1;
            else //take into account sat mag cell
                if (0.5 * P_mag_OUT * P_cell_OUT * P_sat_OUT * P_snr_OUT + 0.5 * P_mag_IN * P_cell_IN * P_sat_IN * P_snr_IN > 0)
                    P_out = 0.5 * P_mag_OUT * P_cell_OUT * P_sat_OUT * P_snr_OUT / (0.5 * P_mag_OUT * P_cell_OUT * P_sat_OUT * P_snr_OUT + 0.5 * P_mag_IN * P_cell_IN * P_sat_IN * P_snr_IN);
                else
                    P_out = 1;
        } else {
            //Satellites not availables
            if (Day) // take into account light mag cell
                if (0.5 * P_light_OUT * P_mag_OUT * P_cell_OUT + 0.5 * P_light_IN * P_mag_IN * P_cell_IN > 0)
                    P_out = 0.5 * P_light_OUT * P_mag_OUT * P_cell_OUT / (0.5 * P_light_OUT * P_mag_OUT * P_cell_OUT + 0.5 * P_light_IN * P_mag_IN * P_cell_IN);
                else
                    P_out = 1;
            else // take into account mag cell
                if (0.5 * P_mag_OUT * P_cell_OUT + 0.5 * P_mag_IN * P_cell_IN > 0)
                    P_out = 0.5 * P_mag_OUT * P_cell_OUT / (0.5 * P_mag_OUT * P_cell_OUT + 0.5 * P_mag_IN * P_cell_IN);
                else
                    P_out = 1;
        }

        //Final Probabilities
        Log.d(TAG, "P_light_OUT=" + P_light_OUT + ", P_mag_OUT=" + P_mag_OUT + ", P_cell_OUT=" + P_cell_OUT +
                ", P_light_IN=" + P_light_IN + ", P_mag_IN=" + P_mag_IN + ", P_cell_IN=" + P_cell_IN + ", Day=" + Day);

        P_sum += P_out;
        cont++;

        if (cont >= 5) {
            P_End = P_sum / cont;
            if (P_EndPrev >= 0) {
                P_End = P_End * 0.6 + P_EndPrev * 0.4;
            }
            P_EndPrev = P_End;
            P_sum = 0;
            cont = 0;
        }

        if (P_End > 0.5) {
            return true;
        } else {
            return false;
        }
    }


    public void main() {
        long timestamp = System.currentTimeMillis();
        //Main function
        if (flag) {

            MagAvCurrent = MagAv;
            lightCurrent = light;
            CellCurrent = Cell_lte;
            SatCurrent = Sat;
            SnrCurrent = Snr;
            IncMagCurrent = 0;
            flag = false;

        } else {

            MagAvPrev = MagAvCurrent;
            lightPrev = lightCurrent;
            CellPrev = CellCurrent;
            SatPrev = SatCurrent;
            SnrPrev = SnrCurrent;
            MagAvCurrent = alpha * MagAvPrev + (1 - alpha) * MagAv;
            lightCurrent = alpha * lightPrev + (1 - alpha) * light;
            CellCurrent = alpha * CellPrev + (1 - alpha) * Cell_lte;
            SatCurrent = alpha * SatPrev + (1 - alpha) * Sat;
            SnrCurrent = alpha * SnrPrev + (1 - alpha) * Snr;

            r = sqrt((pow(a * a * cos(Math.toRadians(Lat)), 2) + pow(b * b * sin(Math.toRadians(Lat)), 2)) / (pow(a * cos(Math.toRadians(Lat)), 2) + pow(b * sin(Math.toRadians(Lat)), 2)));
            R_ = r / a;

            MagLat = (B0 / pow(R_, 3)) * sqrt(1 + 3 * pow(sin(Math.toRadians(Lat)), 2));

            IncMagPrev = IncMagCurrent;
            IncMagCurrent = alpha * IncMagPrev + (1 - alpha) * abs(MagAvCurrent - MagLat);
        }

        //Do the probabilities
        boolean isOut = false;
        boolean isOutByThreshold = isOutByThreshold();
        boolean isOutByClassifier = isOutByClassifier(IncMagCurrent, lightCurrent, CellCurrent, SatCurrent, SnrCurrent);
        isOut = isOutByThreshold;
        Log.d(TAG, "isOutByClassifier=" + isOutByClassifier + ", isOutByThreshold=" + isOutByThreshold);
        if (out == null || out ^ isOut) {
            if (onIOChangeListener != null) {
                onIOChangeListener.onIOChange(isOut);
            }
            out = isOut;
        }
//        Toast.makeText(mContext.getApplicationContext(), Boolean.toString(out), Toast.LENGTH_SHORT).show();

    }

    public void setOnIOChangeListener(OnIOChangeListener onIOChangeListener) {
        this.onIOChangeListener = onIOChangeListener;
    }

    public interface OnIOChangeListener {
        public void onIOChange(boolean isOut);
    }


}
