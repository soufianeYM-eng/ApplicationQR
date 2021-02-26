package com.example.applicationqr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {
    public static TextView scandata; //Qui va contenir le texte du code QR qu'on va scanner
    EditText qrvalue;// TextView qui va contenir le texte pour lequelle on veut générer son code QR
    Button generateBtn, saveBtn, colorpicker1, colorpicker2,saveExBtn,scanqr;
    DatabaseReference dbref; // Utiliser afin de stocker dans notre databse firebase
    ImageView qrImage; //pour afficher le code QR généré
    Bitmap qrBits;
    int DefaultColor1, DefaultColor2; // Pour les choix des couleurs
    int dimension; // Pour le choix de la dimension
    FileOutputStream outputStream; // Utiliser pour exporter l'image généré

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        qrvalue = findViewById(R.id.qrInput);
        generateBtn = findViewById(R.id.generateBtn);
        saveBtn = findViewById(R.id.saveBtn);
        qrImage = findViewById(R.id.qrPlaceHolder);
        colorpicker1 = findViewById(R.id.buttonColorPicker1);
        colorpicker2 = findViewById(R.id.buttonColorPicker2);
        dbref = FirebaseDatabase.getInstance().getReference("qrdata"); // Nom de notre Base de données Firebase
        saveExBtn =(Button) findViewById(R.id.saveExBtn);
        scanqr = (Button) findViewById(R.id.scanqr);
        scandata = (TextView) findViewById(R.id.scandata);

        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1); //Pour donner la permission afin de stocker nos image dans notre Gallery
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        //SCAN CODE QR

        scanqr.setOnClickListener(new View.OnClickListener() { //Afin d'ouvrir qrscanner Activity qui va nous permettre de scanner un CodeQR et afficher après le resultat
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),qrscanner.class));
            }
        });

        //QR CODE GENERATOR
        generateBtn.setOnClickListener(new View.OnClickListener() { // Methode qui va nous permettre de générer le code QR du texte saisie
            @Override
            public void onClick(View v) {
                String data = qrvalue.getText().toString();
                if(data.isEmpty()){ // afin de créer le code qr il faut vérifier que l'utilisateur a bien saisie le texte
                    qrvalue.setError("Value Required.");
                }
                else {
                    QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, dimension); // Après on va utiliser une instance de la classe QRGEncoder afin de générer le codeQR
                    qrgEncoder.setColorBlack(DefaultColor1); //Pour modifier la couleur de fond du CodeQR
                    qrgEncoder.setColorWhite(DefaultColor2); // Pour modifier la couleur du CodeQR
                    try {

                        //C'est l'étape de génération du CodeQR, et l'initialisation de notre ImageView
                        qrBits = qrgEncoder.getBitmap();
                        qrImage.setImageBitmap(qrBits);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //Pour enregistrer nos CodeQR généré dans notre Base de données FireBase
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageQR();
            }
        });

        //Afin d'afficher la palette de couleurs pour choisir la couleur de fond du CodeQR
        colorpicker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker1();
            }
        });

        //Afin d'afficher la palette de couleurs pour choisir la couleur du CodeQR
        colorpicker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker2();
            }
        });

        //Afin d'enregistrer le CodeQR généré dans notre Gallery
        saveExBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable drawable = (BitmapDrawable) qrImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                //Il faut tout d'abord avoir le chemin ou seront stockées nos images
                File filepath = Environment.getExternalStorageDirectory();
                //Créons un dossier sous le nom CodeQR
                File dir = new File(filepath.getAbsolutePath()+"/CodeQR");
                dir.mkdir();
                //Ainsi choisissons le nom pour chaque CodeQR enregistrer dans notre Gallery
                File file = new File(dir,System.currentTimeMillis()+".jpg");
                try{
                    outputStream = new FileOutputStream(file);
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
                //On Choisis le type JPEG pour nos images et la qualité, on peut effectivement choisir PNG ou autre format
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                Toast.makeText(getApplicationContext(), "Image save to Internal", Toast.LENGTH_SHORT).show();
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Va nou permettre de stocker notre CodeQR généré dans notre BD Firebase
    private void ImageQR(){
        String data = qrvalue.getText().toString();
        dbref.push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"Data Imported into Firebase",Toast.LENGTH_LONG).show();
            }
        });
    }

    //Pour le choix de la dimension
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioSmall:
                if (checked)
                    dimension=150;
                break;
            case R.id.radioMedium:
                if (checked)
                    dimension=250;
                break;
            case R.id.radioLarge:
                if (checked)
                    dimension=500;
                break;
        }
    }
    public void openColorPicker1()
    {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this , DefaultColor1, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {}
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                DefaultColor1=color;
            }
        });
        colorPicker.show();
    }
    public void openColorPicker2()
    {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this , DefaultColor2, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {}
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                DefaultColor2=color;
            }
        });
        colorPicker.show();
    }
}