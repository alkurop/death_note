package com.omar.deathnote.notes.v2

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.R
import javax.inject.Inject

class ContentActivity : AppCompatActivity() {

    companion object {

        private const val NO_ID = -1
        private const val ID_KEY = "id_key"

        fun openNote(activity: AppCompatActivity, id: Long) {}

        fun newNote(activity: AppCompatActivity) {}
    }

    @Inject
    lateinit var presenter: ContentPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        ComponentContainer.instance.get(ContentViewComponent::class.java)
                .inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            presenter.dispose()
            ComponentContainer.instance.remove(ContentViewComponent::class.java)
        }
    }
}
