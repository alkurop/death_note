package com.omar.deathnote.notes.v2

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import com.jakewharton.rxbinding2.view.RxView
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.Constants
import com.omar.deathnote.R
import com.omar.deathnote.main.MySpinnerAdapter
import com.omar.deathnote.models.SpinnerItem
import com.omar.deathnote.utility.plusAssign
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
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

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        initList()
        subscribeToListeners()
        subscribeToPresenter()
        val noteById = intent.getLongExtra(KEY_ID, NO_ID)
        if (noteById == NO_ID) {
            presenter.onAction(ContentAction.CreateNewNote)
            val style = intent.getIntExtra(KEY_STYLE, DEFAULT_STYLE)
            presenter.onAction(ContentAction.UpdateStyle(style))
        } else {
            presenter.onAction(ContentAction.OpenNoteById(noteById))
        }

        initSpinner()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            presenter.dispose()
            ComponentContainer.instance.remove(ContentViewComponent::class.java)
        }
        dis.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.note, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        // R.id.action_share -> presenter.shareClicked()
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
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
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                presenter.onAction(ContentAction.UpdateStyle(i + 1))
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                //noop
            }
        }
    }

    fun subscribeToPresenter() {
        dis += presenter.navigation.observeOn(AndroidSchedulers.mainThread()).subscribe { navigate(it) }
        dis += presenter.viewState.observeOn(AndroidSchedulers.mainThread()).subscribe { renderView(it) }
    }

    fun renderView(state: NoteViewModel) {
        state.content?.let {
            if (recyclerView.adapter == null) {
                recyclerView.adapter = ContentAdapter()
            }
            (recyclerView.adapter as ContentAdapter).updateList(state.content)
        }
        spinner.setSelection(state.style - 1)
        Picasso.with(this).load(Constants.note_bg_images[state.style - 1]).into(background)
    }

    fun navigate(navigation: ContentNavigation) {
        when (navigation) {
        //noop
        }
    }
}
