package com.example.mediafilter2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class Dashboard : AppCompatActivity(), DashboardAdapter.OnItemClickListener {
    private var myDatasetBackup1: ArrayList<DashboardItem> = arrayListOf()
    var myDataset1 = mutableListOf<DashboardItem>()
    private lateinit var recyclerView1: RecyclerView
    private lateinit var navItemsDataSet: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard2)

        myDataset1 = Datasource().loadDashboardItems() as MutableList<DashboardItem>

        myDatasetBackup1.addAll(myDataset1)
        navItemsDataSet = arrayOf("Text Recognition", "Image Gallery")
        recyclerView1 = findViewById<RecyclerView>(R.id.recycler_view1)
        recyclerView1.adapter = DashboardAdapter(this, myDataset1, this)
        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView1.setHasFixedSize(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val menuItem = menu!!.findItem(R.id.logout)
        //val menuItem2 = menu!!.findItem(R.id.search)
        if (menuItem != null) {
            val textView = menuItem.actionView as TextView
        }
        //TODO implement searching for recycler view...was too complicated because of the way the datasource
        // DashboardItem is set up...may have to rethink how this clas is written
        // also complicated by the onclick listener in the recycle view
        /*if (menuItem2 != null) {
            val searchView = menuItem2.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    println("new text is: " + newText + "ok")
                    if (newText!!.isNotEmpty() && newText != "") {

                        val search = newText.toLowerCase(Locale.getDefault())
                        navItemsDataSet.forEach {
                            if (!it.contains(search)) {

                                if (it.equals("Text Recognition")) {
                                    if (myDataset1.size > 0) {
                                        var db =
                                            myDataset1.find { dbi -> "Text Recognition".equals(dbi.stringResourceId1.toString()) }
                                        if (db != null) {
                                            myDataset1.remove(db)
                                            recyclerView1.adapter!!.notifyDataSetChanged()
                                        }

                                    }
                                } else if (it.equals("Image Gallery Filter")) {
                                    if (myDataset1.size > 0) {
                                        var db = myDataset1.find { dbi ->
                                            "Image Gallery Filter".equals(dbi.stringResourceId1.toString())
                                        }
                                        if (db != null) {
                                            myDataset1.remove(db)
                                            recyclerView1.adapter!!.notifyDataSetChanged()
                                        }
                                    }
                                }

                            } else {
                                if (it.equals("Text Recognition")) {
                                    if (myDataset1.size > 0) {
                                        var db1 =
                                            myDataset1.find { dbi -> "Text Recognition".equals(dbi.stringResourceId1.toString()) }
                                        var db2 = myDataset1.find { dbi ->
                                            "Image Gallery Filter".equals(dbi.stringResourceId1.toString())
                                        }
                                        if (db1 == null) {
                                            myDataset1.add(
                                                DashboardItem(
                                                    R.string.item1Title,
                                                    R.string.item1Description,
                                                    R.drawable.image1
                                                )
                                            )
                                            recyclerView1.adapter!!.notifyDataSetChanged()
                                        }
                                        if (db2 != null) {
                                            myDataset1.remove(db2)
                                            recyclerView1.adapter!!.notifyDataSetChanged()
                                        }
                                    }

                                }
                                if (it.equals("Image Gallery Filter")) {
                                    if (myDataset1.size > 0) {
                                        var db1 =
                                            myDataset1.find { dbi -> "Text Recognition".equals(dbi.stringResourceId1.toString()) }
                                        var db2 = myDataset1.find { dbi ->
                                            "Image Gallery Filter".equals(dbi.stringResourceId1.toString())
                                        }
                                        if (db2 == null) {
                                            myDataset1.add(
                                                DashboardItem(
                                                    R.string.item2Title,
                                                    R.string.item2Description,
                                                    R.drawable.image2
                                                )
                                            )
                                            recyclerView1.adapter!!.notifyDataSetChanged()
                                        }
                                        if (db1 != null) {
                                            myDataset1.remove(db1)
                                            recyclerView1.adapter!!.notifyDataSetChanged()
                                        }

                                    }
                                }
                            }

                        }
                    } else if (newText.isNullOrBlank() || newText == "") {

                        if (myDataset1.size == myDatasetBackup1.size) {
                            println(null)

                            println(myDatasetBackup1.size)
                            return true
                        } else {
                            println(myDataset1.size)
                            println(myDatasetBackup1.size)
                            myDataset1.clear()
                            myDataset1.addAll(myDatasetBackup1)
                            recyclerView1.adapter!!.notifyDataSetChanged()
                        }

                    }
                    return true
                }

            })
        }*/


        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val id = item.itemId
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(position: Int) {
       Toast.makeText(this, "Item $position clicked", Toast.LENGTH_SHORT).show()
        val clickedItem:DashboardItem = myDataset1[position]
        println(clickedItem.stringResourceId1)
        if(position == 0){
            startActivity(Intent(this, TextRecognitionActivity::class.java))

        }
        else if (position == 1){
            startActivity(Intent(this, MediaFilterActivity::class.java))
        }
    }

}




