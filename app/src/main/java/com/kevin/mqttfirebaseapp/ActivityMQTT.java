package com.kevin.mqttfirebaseapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


//LIBRERIAS MQTT
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class ActivityMQTT extends AppCompatActivity {
    private static String mqttHost ="tcp://dogvulture524.cloud.shiftr.io:1883"; //IP DEL SERVIDOR MQTT
    private static String IdUsuario = "AppAndroid"; //Nombre del dispostivo que se conectara

    private static String Topico = "Mensaje"; //Topico al que se suscribira
    private static String User = "dogvulture524"; //Usuario
    private static String Pass = "J4JCkp41JHbBd01H"; //Contraseña o token


    private TextView textView;
    private EditText editTextMessage;
    private Button botonEnvio;

    private MqttClient mqttClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt);

        textView = findViewById(R.id.textView);
        editTextMessage = findViewById(R.id.txtMensaje);
        botonEnvio = findViewById(R.id.botonEnvioMensaje);
        try {
            //creacion del cliente mqtt

            mqttClient = new MqttClient(mqttHost, IdUsuario,null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(User);
            options.setPassword(Pass.toCharArray());

            mqttClient.connect(options);
            Toast.makeText(this, "Apliciacion conectada al servidor MQTT", Toast.LENGTH_SHORT).show();

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                    Log.d("MQTT","Conexion perdida");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                    String payload = new String(message.getPayload());
                    runOnUiThread(()-> textView.setText(payload));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                    Log.d("MQTT", "Entrega completa");
                }
            });

        }catch (MqttException e){
            e.printStackTrace();
        }
        //Al dar click en el bototn envia el mensaje del topico
        botonEnvio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){

                String mensaje = editTextMessage.getText().toString();
                try {
                    //Verifica si la conexion mqtt esta activa
                    if (mqttClient != null && mqttClient.isConnected()) {
                        //publicar el mensaje en el topico especificado

                        mqttClient.publish(Topico, mensaje.getBytes(), 0, false);
                        //Mostrar el mensaje enviado en el textview
                        textView.append("\n - " + mensaje);

                    } else {
                        Toast.makeText(ActivityMQTT.this, "Error:No se pudo enviar el mensaje", Toast.LENGTH_SHORT).show();
                    }
                }catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}