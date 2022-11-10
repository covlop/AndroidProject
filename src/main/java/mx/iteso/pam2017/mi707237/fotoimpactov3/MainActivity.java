package mx.iteso.pam2017.mi707237.fotoimpactov3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import Objects.FirebaseReferences;
import Objects.Siniestros;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        Siniestros siniestro;

    private EditText txtName;
    private EditText txtVIN;
    private EditText txtModel;
    private EditText txtMake;
    private EditText txtPlate;
    private EditText txtColor;
    private ImageView imgGallery;
    private ImageView imgPhoto;
    private ImageView imgCamera;
    public StorageReference filepath;
    private ImageView sendSiniestro;
    private ProgressDialog progressDialog;
    private ImageCompression  imgcompr;
    private String urlPhotocomp;
    private Integer validate=0;


    //Database object (Firebase)
    private static FirebaseDatabase databaseIncidents;
    //Reference to database (Firebase)
    private static DatabaseReference incidentsRef;
    //Reference to storage (Firebase)
    private static StorageReference storage;

    private int GALLERY_INTENT =  2017;
    private int REQUEST_IMAGE_CAPTURE=2018;
    public Uri uri;
    int contentImg = 0;

    public String pathImage;
    private String urlPhoto;
    public String CompPathImage;


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        txtName = (EditText) findViewById(R.id.txtName);
        txtVIN=(EditText) findViewById(R.id.txtVIN);
        txtModel= (EditText) findViewById(R.id.txtModel);
        txtMake=(EditText) findViewById(R.id.txtMake);
        txtColor=(EditText) findViewById(R.id.txtColor);
        txtPlate=(EditText) findViewById(R.id.txtPlate);
        imgGallery= (ImageView) findViewById(R.id.imgGallery);
        imgCamera=(ImageView) findViewById(R.id.imgCamera);
        imgPhoto= (ImageView) findViewById(R.id.imgPhoto);
        sendSiniestro=(ImageView) findViewById(R.id.sendSiniestro);
        imgcompr = new ImageCompression(this.getBaseContext());

        //region INITIALIZE FIREBASE REFERENCES
        //Get instance object from database(Firebase)
        databaseIncidents = FirebaseDatabase.getInstance();
        //Get object reference from database(Firebase)
        incidentsRef = databaseIncidents.getReference(FirebaseReferences.SINIESTROS_REFERENCES);
        //Reference and Instance from Firebase Storage
        storage = FirebaseStorage.getInstance().getReference();
        //endregion

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        verifyStoragePermissions(this);

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

            }
        });

        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call Gallery Menu
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                intent.setType("image/jpeg");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_INTENT );
            }
        });

        sendSiniestro.setClickable(true);
        sendSiniestro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                 SendSiniestro(v);

            }
        });

    }


    public void SendSiniestro(View v){

        //First. Validate data

if ( txtName == null || txtName.getText().toString().isEmpty() || txtVIN == null || txtVIN.getText().toString().isEmpty()
        || txtModel == null || txtModel.getText().toString().isEmpty() || txtColor==null || txtColor.getText().toString().isEmpty()
        || txtColor==null || txtColor.getText().toString().isEmpty() || txtMake==null || txtMake.getText().toString().isEmpty() || txtPlate==null
        || txtPlate.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please fill all the fields with information.", Toast.LENGTH_LONG).show();

            if (!txtVIN.getText().toString().matches("[0-9]+"))
            {
                Toast.makeText(this, "VIN field only accepts numbers", Toast.LENGTH_LONG).show();
            }




        } else

        {
            if (txtVIN.getText().toString().matches("[0-9]+"))

            {

                SendInfoPhoto();
                if (validate == 1) {
                    Toast.makeText(this, "Information sent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Unable to send information", Toast.LENGTH_LONG).show();
                }

            }
            else {Toast.makeText(this, "VIN field only accepts numbers", Toast.LENGTH_LONG).show();}


        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK && null != data) {

            uri = data.getData();
            InputStream inputStream;
            CompPathImage = "";
            pathImage="";
            //We´re getting an input stream based on uri of the image
            try {


                pathImage=getPath(uri);
                CompPathImage=imgcompr.compressImage(pathImage);
                inputStream = getContentResolver().openInputStream(uri);
                //Get bitmap from stream.
                Bitmap img = BitmapFactory.decodeStream(inputStream);
                imgPhoto.setImageBitmap(img);
                imgPhoto.setVisibility(View.VISIBLE);
                //Validate if exists photo in the incident.
                contentImg = 1;

            } catch (FileNotFoundException e)
            {
                imgPhoto.setVisibility(View.GONE);
                e.printStackTrace();
                Toast.makeText(this, "Unable to open image",Toast.LENGTH_LONG).show();
                contentImg = 0;
            }
                //filepath = storage.child("Photos").child(uri.getLastPathSegment());

        }

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && null != data ) {
            CompPathImage = "";
            uri = data.getData();
            try
            {
                /*
                img = (Bitmap) data.getExtras().get("data");           ****************FUNCIONA*********
                imgPhoto.setImageBitmap(img);
                imgPhoto.setVisibility(View.VISIBLE);
                //Validate if exists photo in the incident.
                contentImg = 1;*/

                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = managedQuery(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, null, null, null);
                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToLast();

                pathImage = cursor.getString(column_index_data);
                CompPathImage= imgcompr.compressImage(pathImage);
                Bitmap bitmapImage = BitmapFactory.decodeFile(CompPathImage);

                    imgPhoto.setImageBitmap(bitmapImage);
                    imgPhoto.setVisibility(View.VISIBLE);

                contentImg = 1;

              //  filepath = storage.child("Photos").child(uri.getLastPathSegment());
               // filepath = storage.child("Photos").child(bitmapImage.toString());
                //filepath=storage.child("Photos").child(CompPathImage);


            }
            catch(Exception e)
            {
                imgPhoto.setVisibility(View.GONE);

                e.printStackTrace();
                Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                contentImg = 0;
            }
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED)
        {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private String Customer () {
        String Customer=txtName.getText().toString();
        return Customer;
    }

    private String VIN () {
        String VIN=txtVIN.getText().toString();
        return VIN;
    }

    private String Model () {
        String Model=txtModel.getText().toString();
        return Model;
    }

    private String Color () {
        String Color=txtColor.getText().toString();
        return Color;
    }
    private String Make () {
        String Make=txtMake.getText().toString();
        return Make;
    }

    private String Plate () {
        String Plate=txtPlate.getText().toString();
        return Plate;
    }

    private String Photo()
    {
        String Photo=urlPhotocomp;
        return Photo;
    }
    private Siniestros CrearSiniestros(String Customer, String VIN, String model, String color, String make, String PlateNumber, String Photo)
    {
        Siniestros siniestro = new Siniestros (Customer, VIN, model, color, make, PlateNumber, Photo);
        return siniestro;
    }



    public void SendInfoPhoto(){

//        progressDialog.setMessage("Sending Incident..."  );
// progressDialog.show();


           uri=uri.fromFile(new File(CompPathImage));
           urlPhotocomp=uri.getLastPathSegment();
           filepath = storage.child("Photos").child(urlPhotocomp);
           filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


            @Override

            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot ) {
                urlPhoto = taskSnapshot.getDownloadUrl().toString();


             //  urlPhotocomp=imgcompr.compressImage(urlPhoto);
               // urlPhotocomp=CompPathImage;


         //       progressDialog.dismiss();

                //Second.- Send incident information
                //Create and Instance incident's object
                Siniestros incident = CrearSiniestros(Customer(), VIN(), Model(), Color(), Make(), Plate(), Photo() );
                incidentsRef.child(FirebaseReferences.SINIESTROS_REFERENCES).push().setValue(incident);





                //Call the method RedirectToIncidentSuccess to redirect to view

            }

        });

        validate=1;


    }

    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }




}
