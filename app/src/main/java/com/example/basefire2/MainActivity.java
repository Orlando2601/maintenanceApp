package com.example.basefire2;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    Firebase firebase2;   //DEFINICION VARIABLE DE BASE DE DATOS
    EditText equipo;      //EDITE TEXT DEL NUMERO DEL EQUIPO
    ImageButton save;      //BOTON DE GUARDAR INFORMACION
    EditText descripcion;
    EditText correct;
    ImageButton mostrar;
    DatabaseReference databaseReference;
    ListView listView;
    ArrayList<String> arrayList= new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);



        firebase2 = new Firebase("https://basefire2-d0adc.firebaseio.com/"); //UBICACION DE LA BASE DE DATOS EN TIEMPO REAL

        save = (ImageButton) findViewById(R.id.btn_save);// DECLARACION DE UBICACION DE INFORMACION DE BOTON
        equipo = (EditText) findViewById(R.id.edt_equipo);// DECLARACION DE UBICACION DE INFORMACION DE EDIT TEXT DE EQUIPO
        descripcion = (EditText) findViewById(R.id.edt_descripcion);//DECLARACION DE UBICACION DE CAJA DE TEXTO DE DESCRIPCION
        correct = (EditText)findViewById(R.id.edt_descripcion_correctivo);
        mostrar = (ImageButton) findViewById(R.id.btn_mostrar);
    }

    //BOTON GUARDAR

    public void Guardar(View view) {

        // EXTRAER DIA
        Date dias = new Date();
        SimpleDateFormat d=new SimpleDateFormat("d");
        String sDias=d.format(dias);

        //FECHA COMPLETA
        Date date = new Date();
        SimpleDateFormat fechaC=new SimpleDateFormat("d  MMMM 'del'  yyyy");
        String sFecha=fechaC.format(date);


        //EXTRAER HORA
        Date horas= new Date();
        SimpleDateFormat h=new SimpleDateFormat("H:m:s");
        String SHora=h.format(horas);

        //VALIDACION DE ESPACIOS VACIOS EQUIPO Y DESCRIPCION


        if (equipo.length() == 0 || descripcion.length() == 0) {


           if (equipo.getText().toString().trim().equalsIgnoreCase(""))
              equipo.setError("Ingresar equipo");
            if (descripcion.getText().toString().trim().equalsIgnoreCase(""))
                descripcion.setError("Ingresar Descripcion");
            if (correct.length() ==0){
                if (correct.getText().toString().trim().equalsIgnoreCase(""))
                    correct.setError("Ingresar Correctivo");
                return;
            }

            Toast.makeText(this, "Llenar campos vacios", Toast.LENGTH_LONG).show();



            return;
        }
        if (correct.length() ==0){
            if (correct.getText().toString().trim().equalsIgnoreCase(""))
                correct.setError("Ingresar Correctivo");
            return;
        }

        //COMO NO ESTA VACIO PASA A GUARDAR...


        String value = equipo.getText().toString();
        String suceso = descripcion.getText().toString();
        String correctivo = correct.getText().toString();
        User user = new User(suceso, correctivo);

       ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("reporte","*EH"+value+"*" +"\r\n"+ "_*Suceso:*_ " +suceso +"\r\n" + "_*Correctivo:*_ "+correctivo);
        clipboard.setPrimaryClip(clip);

// firebase2.child(value).child(sFecha).child(SHora).setValue(user);
        firebase2.child(value).child(sFecha+"-"+SHora).setValue(user);
        arrayList.clear();


        //MUESTRA QUE EL DATO SE GUARDO CON EXITO
        Toast.makeText(this, "Datos Guardados", Toast.LENGTH_LONG).show();

        //VACIAR CASILLAS
        if (!(equipo.length() == 0) || !(descripcion.length() == 0)) {
            equipo.setText("");
            descripcion.setText("");
            correct.setText("");

        }
        arrayList.clear();
        }

//BOTON DE MOSTRAR INFORMACION DE LA BASE DE DATOS
     public void Mostrar (View view){
        //AQUI SE AVALUA SI SE INGRESO O NO EL EQUIPO
         arrayList.clear();
          if (equipo.length() ==0){                 //SI EL NUMERO DE CARACTERES ES DIFERENTE DE CERO ENTONCES MUESTRA MENSAJE DE DEBE INGRESAR EQUIPO
             equipo.setError("Ingresar equipo");
             return;
         }
         String valor = equipo.getText().toString();
         databaseReference= FirebaseDatabase.getInstance().getReference(valor);

         listView=(ListView) findViewById(R.id.lvtxt_1);
         arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
         listView.setAdapter(arrayAdapter);
         databaseReference.addChildEventListener(new ChildEventListener() {
             @Override
             public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                 String clubkey = dataSnapshot.getKey();
                 String datos=dataSnapshot.getValue(User.class).toString();
                 arrayList.add(clubkey + "\r\n" + datos );
                 arrayAdapter.notifyDataSetChanged();

             }

             @Override
             public void onChildChanged(DataSnapshot dataSnapshot, String s) {

             }

             @Override
             public void onChildRemoved(DataSnapshot dataSnapshot) {

             }

             @Override
             public void onChildMoved(DataSnapshot dataSnapshot, String s) {

             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });


     }

    }


class User {

    public String FALLO;
    public String REPARACION;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String FALLO, String REPARACION) {
        this.FALLO = FALLO;
        this.REPARACION = REPARACION;
    }


    @Override
    public String toString() {
        return ("SUCESO:  "+FALLO +"\r\n"+ "CORRECTIVO:  " + REPARACION);
    }
}
