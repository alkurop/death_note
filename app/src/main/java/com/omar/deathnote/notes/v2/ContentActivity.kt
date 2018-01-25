package com.omar.deathnote.notes.v2

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.view.RxView
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.Constants
import com.omar.deathnote.R
import com.omar.deathnote.main.MySpinnerAdapter
import com.omar.deathnote.models.SpinnerItem
import com.omar.deathnote.utility.plusAssign
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_content.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

class ContentActivity : AppCompatActivity() {

    companion object {

        private const val NO_ID = -1L
        private const val DEFAULT_STYLE = 1
        private const val KEY_ID = "key_id"
        private const val KEY_STYLE = "key_style"

        fun openNote(activity: AppCompatActivity, id: Long? = NO_ID, style: Int? = DEFAULT_STYLE) {
            val intent = Intent(activity, ContentActivity::class.java)
            intent.putExtra(KEY_ID, id)
            activity.startActivity(intent)
        }

        fun newNote(activity: AppCompatActivity, style: Int) {
            val intent = Intent(activity, ContentActivity::class.java)
            activity.startActivity(intent)
        }
    }

    @Inject
    lateinit var presenter: ContentPresenter

    val dis = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        ComponentContainer.instance.get(ContentViewComponent::class.java)
                .inject(this)

        initSpinner()
        initList()
        subscribeToListeners()
        subscribeToPresenter()
        val noteById = intent.getLongExtra(KEY_ID, NO_ID)
        val action = if (noteById == NO_ID) {
            ContentAction.CreateNewNote
        } else {
            ContentAction.OpenNoteById(noteById)
        }
        presenter.onAction(action)

        val style = intent.getIntExtra(KEY_STYLE, DEFAULT_STYLE)
        presenter.onAction(ContentAction.UpdateStyle(style))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            presenter.dispose()
            ComponentContainer.instance.remove(ContentViewComponent::class.java)
        }
        dis.clear()
    }

    fun initSpinner() {
        val spinnerItemList = Constants
                .select_images
                .indices
                .filter { it != 0 }
                .map { SpinnerItem(Constants.select_images[it], getString(Constants.select_names[it])) }

        val spinnerAdatper = MySpinnerAdapter(spinnerItemList)
        spinner.adapter = spinnerAdatper
    }


    fun initList() {
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun subscribeToListeners() {
        RxView.clicks(fab).subscribe { presenter.onAction(ContentAction.Add) }
    }

    fun subscribeToPresenter() {
        dis += presenter.navigation.subscribe { navigate(it) }
        dis += presenter.viewState.subscribe { renderView(it) }
    }

    fun renderView(state: NoteViewModel) {
        state.content?.let {
            if (recyclerView.adapter == null) {
                recyclerView.adapter = ContentAdapter()
            }
            (recyclerView.adapter as ContentAdapter).updateList(state.content)
        }
    }

    fun navigate(navigation: ContentNavigation) {
        when (navigation) {
        //noop
        }
    }
}
