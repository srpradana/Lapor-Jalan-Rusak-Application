package com.singgihrp.laporjalanrusakapplication.model


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.singgihrp.laporjalanrusakapplication.model.entity.Report
import com.singgihrp.laporjalanrusakapplication.model.entity.User
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class AppRepositoryImplemen: AppRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    override suspend fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun register(email: String, password: String) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        if(result.user != null) {
            sendEmailVerification()
            val userNew = hashMapOf<String, Any?>(
                "nama" to "",
                "email" to result.user!!.email!!,
                "userid" to result.user!!.uid,
                "date" to "",
                "nohp" to "",
                "kelamin" to "",
                "role" to "pengguna",
                "lengkap_profil" to false,
                "verified" to false
            )
            firestore.collection("user").document(result.user!!.uid)
                .set(userNew)
                .await()
        }
    }

    override suspend fun logout(){
        auth.signOut()
    }

    override suspend fun checkRole(): Boolean {
        return if(auth.currentUser!=null){
            val documentReference = firestore.collection("user").document(auth.currentUser?.uid.toString())
            val result = documentReference.get().await()
            result!=null && result.data?.get("role")=="pengguna"
        }else{
            false
        }
    }

    override suspend fun subscribeRealtimeUpdate() {
        val documentReference = firestore.collection("user").document(auth.currentUser?.uid.toString())
        documentReference.addSnapshotListener{snapshot, e->
            e?.let {
                Log.w("Update User", "updateProfile: ", e)
            }
            snapshot?.let {
                println("data = ${it.data}")
            }
        }
    }

    override suspend fun updateProfile(user: User) {
        val documentReference = firestore.collection("user").document(auth.currentUser?.uid.toString())
        val updateUser = hashMapOf<String, Any?>(
            "nama" to user.name.toString(),
            "date" to user.date.toString(),
            "kelamin" to user.kelamin.toString(),
            "nohp" to user.nohp.toString(),
            "lengkap_profil" to true
        )
        documentReference.update(updateUser).await()
    }

    override suspend fun userFromFirestore(): User {
        val documentReferrence =
            firestore.collection("user").document(auth.currentUser?.uid.toString())
        val result = documentReferrence.get().await()
        return User(
            userid = result?.get("userid").toString(),
            email = result?.get("email").toString(),
            name = result?.get("nama").toString(),
            date = result?.get("date").toString(),
            kelamin = result?.get("kelamin").toString(),
            nohp = result?.get("nohp").toString(),
            lengkap_profil = result.get("lengkap_profil") as Boolean?,
            verified = result.get("verified") as Boolean?,
            role = result.get("role").toString()
        )
    }

    override suspend fun reportCurrentFirestore(): ArrayList<Report>{
        val arr = ArrayList<Report>()
        val collectionReference = firestore.collection("report")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .whereEqualTo("status", "Terkirim")
            .whereEqualTo("uid", auth.currentUser?.uid.toString())
        val result = collectionReference.get().await()
        for(doc in result){
            arr.add(doc.toObject(Report::class.java))
        }
        return arr
    }

    override suspend fun reportAccFirestore(): ArrayList<Report> {
        val arr = ArrayList<Report>()
        val collectionReference = firestore.collection("report")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .whereEqualTo("status", "terima")
            .whereEqualTo("uid", auth.currentUser?.uid.toString())
        val result = collectionReference.get().await()
        for(doc in result){
            arr.add(doc.toObject(Report::class.java))
        }
        return arr
    }

    override suspend fun reportRejFirestore(): ArrayList<Report> {
        val arr = ArrayList<Report>()
        val collectionReference = firestore.collection("report")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .whereEqualTo("status", "tolak")
            .whereEqualTo("uid", auth.currentUser?.uid.toString())
        val result = collectionReference.get().await()
        for(doc in result){
            arr.add(doc.toObject(Report::class.java))
        }
        return arr
    }

    override suspend fun countReportTotal(): String {
        val arr = ArrayList<Report>()
        val collectionReference = firestore.collection("report")
            .whereEqualTo("uid", auth.currentUser?.uid.toString())
            .whereEqualTo("status", "Terkirim")
        val result = collectionReference.get().await()
        for(doc in result){
            arr.add(doc.toObject(Report::class.java))
        }
        return arr.size.toString()
    }

    override suspend fun countReportAcc(): String {
        val arr = ArrayList<Report>()
        val collectionReference = firestore.collection("report")
            .whereEqualTo("status", "terima")
            .whereEqualTo("uid", auth.currentUser?.uid.toString())
        val result = collectionReference.get().await()
        for(doc in result){
            arr.add(doc.toObject(Report::class.java))
        }
        return arr.size.toString()
    }

    override suspend fun countReportRej(): String {
        val arr = ArrayList<Report>()
        val collectionReference = firestore.collection("report")
            .whereEqualTo("status", "tolak")
            .whereEqualTo("uid", auth.currentUser?.uid.toString())
        val result = collectionReference.get().await()
        for(doc in result){
            arr.add(doc.toObject(Report::class.java))
        }
        return arr.size.toString()
    }

    override suspend fun sendReport(report: Report, reportid: String) {
        if(auth.currentUser != null){
            val collectionReference = firestore.collection("report").document(reportid)
            val newReport = hashMapOf<String, Any>(
                "uid" to auth.currentUser!!.uid,
                "nama" to report.nama!!,
                "email" to report.email!!,
                "nohp" to report.nohp!!,
                "lokasi" to report.lokasi!!,
                "rusak" to report.rusak!!,
                "images" to report.images!!,
                "timestamp" to report.timestamp!!,
                "date" to report.date!!,
                "time" to report.time!!,
                "city" to report.city!!,
                "status" to report.status!!,
                "report_id" to reportid
            )
            collectionReference.set(newReport).await()
        }
    }

    override suspend fun uploadImage(byteArray: ByteArray): String {
        val storageReference =
            storage.reference.child("images/${auth.currentUser!!.uid}/IMG${Date().time}")
        storageReference.putBytes(byteArray).await()
        val downloadHandler = storageReference.downloadUrl.await()
        return downloadHandler.toString()
    }

    override suspend fun updateVerified() {
        if(auth.currentUser!=null && auth.currentUser!!.isEmailVerified){
            val documentReference = firestore.collection("user").document(auth.currentUser?.uid.toString())
            val updateVerified = hashMapOf<String, Any?>(
                "verified" to true
            )
            documentReference.update(updateVerified).await()
            auth.currentUser!!.reload()
        }
    }

    override suspend fun availableLocation(location: String): Boolean {
        val arr = ArrayList<Report>()
        val collectionReference = firestore.collection("report")
            .whereEqualTo("lokasi", location)
            .whereEqualTo("status", "Terkirim")
        val result = collectionReference.get().await()
        for(doc in result){
            arr.add(doc.toObject(Report::class.java))
        }
        return arr.size > 0
    }

    override suspend fun sendEmailVerification() {
        if(auth.currentUser!=null && !auth.currentUser!!.isEmailVerified) {
            auth.currentUser!!.sendEmailVerification().await()
            auth.currentUser!!.reload()
        }
    }

    override suspend fun sendResetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun reloadDb() {
        auth.currentUser?.reload()?.await()
    }
}