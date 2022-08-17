package com.singgihrp.adminljr.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.singgihrp.adminljr.model.entity.Report
import com.singgihrp.adminljr.model.entity.User
import kotlinx.coroutines.tasks.await

class AppRepositoryImplement: AppRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userFirebase = auth.currentUser

    override suspend fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun logout(){
        auth.signOut()
    }

    override suspend fun checkRole(): Boolean {
        return if(userFirebase!=null){
            val documentReference = firestore.collection("user").document(userFirebase.uid)
            val result = documentReference.get().await()
            result!=null && result.data?.get("role")=="admin"
        }else{
            false
        }
    }

    override suspend fun getAllReport(): ArrayList<Report> {
        val user: User = getDataUserFromDb()
        val arr = ArrayList<Report>()
        val collectionReference = firestore.collection("report")
            .whereEqualTo("city", user.city.toString())
            .whereEqualTo("status", "Terkirim")
            .orderBy("timestamp", Query.Direction.DESCENDING )
        val result = collectionReference.get().await()
        for(doc in result){
            arr.add(doc.toObject(Report::class.java))
        }
        return arr
    }

    override suspend fun getAccReport(): ArrayList<Report> {
        val user: User = getDataUserFromDb()
        val arr = ArrayList<Report>()
        val collectionReference = firestore.collection("report")
            .whereEqualTo("city", user.city.toString())
            .whereEqualTo("status", "terima")
            .orderBy("timestamp", Query.Direction.DESCENDING )
        val result = collectionReference.get().await()
        for(doc in result){
            arr.add(doc.toObject(Report::class.java))
        }
        return arr
    }

    override suspend fun getDataUserFromDb(): User {
        val documentReferrence =
            firestore.collection("user").document(auth.currentUser?.uid.toString())
        val result = documentReferrence.get().await()
        return User(
            nama = result.data?.get("nama").toString(),
            city = result.data?.get("city").toString(),
            role = result.data?.get("role").toString()
        )
    }

    override suspend fun updateStatusTerimaReport(docid: String, date: String) {
        val documentReference = firestore.collection("report").document(docid)
        documentReference.update(hashMapOf<String, Any?>(
            "status" to "terima",
            "response_time" to "$date WIB"
        )).await()
    }

    override suspend fun updateStatusTolakReport(docid: String, date: String, catatan: String){
        val documentReference = firestore.collection("report").document(docid)
        documentReference.update(hashMapOf<String, Any?>(
            "status" to "tolak",
            "response_time" to "$date WIB",
            "catatan" to catatan
        )).await()
    }

    override suspend fun countAccReport(): String {
        val user: User = getDataUserFromDb()
        val arr = ArrayList<Report>()
        val collectionReference = firestore.collection("report")
            .whereEqualTo("city", user.city.toString())
            .whereEqualTo("status", "terima")
        val result = collectionReference.get().await()
        for(doc in result){
            arr.add(doc.toObject(Report::class.java))
        }
        return arr.size.toString()
    }

    override suspend fun countAllReport(): String {
        val user: User = getDataUserFromDb()
        val arr = ArrayList<Report>()
        val collectionReference = firestore.collection("report")
            .whereEqualTo("city", user.city.toString())
            .whereEqualTo("status", "Terkirim")
        val result = collectionReference.get().await()
        for(doc in result){
            arr.add(doc.toObject(Report::class.java))
        }
        return arr.size.toString()
    }
}