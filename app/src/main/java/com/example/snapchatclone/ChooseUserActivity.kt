package com.example.snapchatclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.gms.common.util.CollectionUtils.mapOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class ChooseUserActivity : AppCompatActivity() {

    var chooseUserListView: ListView? = null

    var emails: ArrayList<String> = ArrayList()

    var keys: ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)

        setTitle("Users List")

        chooseUserListView = findViewById(R.id.usersListsView)

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)

        chooseUserListView?.adapter= arrayAdapter

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                emails.add(snapshot?.child("email")?.value as String)
                keys.add(snapshot.key.toString())

                arrayAdapter.notifyDataSetChanged()

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

        })

        chooseUserListView?.onItemClickListener = AdapterView.OnItemClickListener({adapterView, view, i, l ->

            // map tayyar kr rhe hai

            val snapMap = mutableMapOf <String, String? >()

            snapMap["from"] = FirebaseAuth.getInstance().currentUser!!.email
            snapMap["imageName"]= intent.getStringExtra("imageName")
            snapMap["imageUrl"]= intent.getStringExtra("imageUrl")
            snapMap["message"]= intent.getStringExtra("message")

            FirebaseDatabase.getInstance().getReference().child("users").child(keys.get(i)).child("snaps").push().setValue(snapMap)

            val intent = Intent(this, SnapsAcitivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            startActivity(intent)

        })




    }
}