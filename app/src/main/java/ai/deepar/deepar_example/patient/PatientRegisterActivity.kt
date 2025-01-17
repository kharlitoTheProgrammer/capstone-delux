package ai.deepar.deepar_example.patient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ai.deepar.deepar_example.R
import ai.deepar.deepar_example.patient.models.PatientData
import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PatientRegisterActivity : AppCompatActivity() {
  private lateinit var txtClientName: EditText
  private lateinit var txtClientSurname: EditText
  private lateinit var txtClientAge: EditText
  private lateinit var txtClientEmail: EditText
  private lateinit var txtClientPassword: EditText
  private lateinit var btnConfirm: Button

  lateinit var auth: FirebaseAuth
  lateinit var db: FirebaseFirestore

  private lateinit var ClientName: String
  private lateinit var ClientSurname: String
  private lateinit var ClientAge: String
  private lateinit var ClientEmail: String
  private lateinit var ClientPassword: String

  @SuppressLint("MissingInflatedId")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_patient_register)

    txtClientName = findViewById(R.id.txtClientName)
    txtClientSurname = findViewById(R.id.txtClientSurname)
    txtClientAge = findViewById(R.id.txtClientAge)
    txtClientEmail = findViewById(R.id.txtClientEmail)
    txtClientPassword = findViewById(R.id.txtClientPassword)
    btnConfirm = findViewById(R.id.btnRDocConfirm)

    auth = FirebaseAuth.getInstance()
    db = FirebaseFirestore.getInstance()

    btnConfirm.setOnClickListener {
      ClientName = txtClientName.text.toString()
      ClientSurname = txtClientSurname.text.toString()
      ClientAge = txtClientAge.text.toString()
      ClientEmail = txtClientEmail.text.toString()
      ClientPassword = txtClientPassword.text.toString()
      Log.d("name", ClientName)
      Log.d("surname", ClientSurname)
      Log.d("age", ClientAge)
      Log.d("email", ClientEmail)
      Log.d("password", ClientPassword)

      if (!ClientEmail.contains("@patient")) {
        Toast.makeText(
          this,
          "Enter A Valid Patient Email Address",
          Toast.LENGTH_LONG
        ).show()
      } else if (ClientName != "" && ClientSurname != "" && ClientAge.toString() != "" && ClientEmail.toString() != "" && ClientPassword.toString() != "") {
        auth.createUserWithEmailAndPassword(ClientEmail, ClientPassword)
          .addOnCompleteListener(PatientRegisterActivity()) { task ->
            if (task.isSuccessful) {
              Toast.makeText(this, "User Added Successfully", Toast.LENGTH_LONG).show()
              val user = auth.currentUser
              val client = PatientData(
                user!!.uid,
                ClientName,
                ClientSurname,
                ClientAge,
                ClientEmail,
                ClientPassword,
                "",
              )
              db.collection("patients").document(user.email!!).set(client)
                .addOnSuccessListener {
                  Log.d(
                    "Firestore",
                    "Client DocumentSnapshot successfully written!"
                  )
                }
                .addOnFailureListener { e ->
                  Log.w("Firestore", "Error writing Client DocumentSnapshot", e)
                }
              Log.d("patient", client.toString())
              val intent =
                Intent(
                  this@PatientRegisterActivity,
                  PatientLoginActivity::class.java
                )
              startActivity(intent)
              finish()
            } else {
              Toast.makeText(this, task.exception!!.message, Toast.LENGTH_LONG).show()
            }
          }
      } else {
        Toast.makeText(this, "Incomplete Information", Toast.LENGTH_LONG).show()
      }
    }
  }
}