package com.guilhermereisdev.roomdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.guilhermereisdev.roomdemo.databinding.ActivityMainBinding
import com.guilhermereisdev.roomdemo.db.Subscriber
import com.guilhermereisdev.roomdemo.db.SubscriberDatabase
import com.guilhermereisdev.roomdemo.db.SubscriberRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var subscriberViewModel: SubscriberViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val dao = SubscriberDatabase.getInstance(application).subscriberDAO
        val repository = SubscriberRepository(dao)
        val factory = SubscriberViewModelFactory(repository)

        subscriberViewModel = ViewModelProvider(this, factory)[SubscriberViewModel::class.java]

        binding.myViewModel = subscriberViewModel
        binding.lifecycleOwner = this

        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.recyclerViewSubscriber.layoutManager = LinearLayoutManager(this)
        displaySubscribersList()
    }

    private fun displaySubscribersList() {
        subscriberViewModel.subscribers.observe(this) {
            Log.i("MyTag", it.toString())
            binding.recyclerViewSubscriber.adapter = MyRecyclerViewAdapter(
                it,
                { selectedItem: Subscriber -> listItemClicked(selectedItem) }
            )
        }
    }

    private fun listItemClicked(subscriber: Subscriber) {
        Toast.makeText(this, "Nome: ${subscriber.name}", Toast.LENGTH_SHORT).show()
        subscriberViewModel.initUpdateAndDelete(subscriber)
    }

}
