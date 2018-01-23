package com.omar.deathnote.main.v2

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.omar.deathnote.R
import com.omar.deathnote.dagger.ComponentContainer
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity1 : AppCompatActivity() {

    @Inject
    lateinit var presenter: MainViewPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_main.layoutManager = LinearLayoutManager(this)
        ComponentContainer.instance.get(MainScreenComponent::class.java).inject(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            ComponentContainer.instance.remove(MainScreenComponent::class.java)

        }
    }

}
