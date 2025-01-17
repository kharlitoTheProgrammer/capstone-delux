package ai.deepar.deepar_example.patient.services

import ai.deepar.deepar_example.patient.models.PatientAppointmentData
import com.google.firebase.firestore.FirebaseFirestore


class PatientAppointmentService {
    private val db = FirebaseFirestore.getInstance()
    fun getAppointmentsForPatient(patientEmail: String, callback: (List<PatientAppointmentData>) -> Unit) {
        db.collection("appointments")
            .document(patientEmail)
            .collection("patientAppointments")
            .get()
            .addOnSuccessListener { documents ->
                val appointmentsList = documents.mapNotNull { document ->
                    val appointment = document.toObject(PatientAppointmentData::class.java)
                    appointment.copy(id = document.id)
                }
                callback(appointmentsList)
            }
    }

    fun deleteAppointment(patientEmail: String, doctorEmail: String, appointmentId: String, callback: (Boolean) -> Unit) {
        db.collection("appointments")
            .document(patientEmail)
            .collection("patientAppointments")
            .document(appointmentId)
            .delete()
            .addOnSuccessListener {
                db.collection("doctorAppointments")
                    .document(doctorEmail)
                    .collection("appointments")
                    .document(appointmentId)
                    .delete()
                    .addOnSuccessListener {
                        callback(true)
                    }
                    .addOnFailureListener {
                        callback(false)
                    }
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}