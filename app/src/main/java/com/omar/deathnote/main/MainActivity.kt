package com.omar.deathnote.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.Constants
import com.omar.deathnote.R
import com.omar.deathnote.notes.ContentActivity
import com.omar.deathnote.pref.PrefActivity
import com.omar.deathnote.utility.plusAssign
import com.stanfy.enroscar.views.StateHelper
import com.stanfy.enroscar.views.StateHelper.STATE_EMPTY
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var presenter: MainViewPresenter

    val dis = CompositeDisposable()
    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val background = if (resources.configuration.orientation % 2 == 0) {
            Constants.bg_images_main_2[0]
        } else {
            Constants.bg_images_main[0]
        }
        mainLayout.setBackgroundResource(background)

        ComponentContainer.instance.get(MainScreenComponent::class.java).inject(this)
        setSupportActionBar(toolbar)
        initRecyclerView()
        initSpinner()
        subscribeToPresenter()
        subscribeToUiChanges()
        stateView.stateHelper.setStateViewCreator(STATE_EMPTY, object : StateHelper.StateViewCreator() {
            override fun bindView(context: Context, view: View, lastResponseData: Any?, parent: ViewGroup) {
            }

            override fun createView(context: Context?, parent: ViewGroup?): View {
                val view = LayoutInflater.from(context).inflate(R.layout.add_view, parent, false)
                view.setOnClickListener { presenter.onAction(MainViewActions.FabClicked) }
                return view
            }

        })
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MainListAdapter(object : MainAdapterCallback {
            override fun openNote(id: Long) {
                presenter.onAction(MainViewActions.ListItemClicked(id))
            }

            override fun deleteItem(id: Long) {
                presenter.onAction(MainViewActions.DeleteListItemClicked(id))
            }
        })
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialog?.takeIf { it.isShowing }?.dismiss()
        if (isFinishing) {
            presenter.dispose()
            ComponentContainer.instance.remove(MainScreenComponent::class.java)
        }
        dis.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> presenter.onAction(MainViewActions.AboutClicked)
            R.id.add -> presenter.onAction(MainViewActions.FabClicked)
        }

        return super.onOptionsItemSelected(item)
    }

    fun initSpinner() {
        val spinnerItemList = Constants
            .select_images
            .indices
            .map { SpinnerItem(Constants.select_images[it], getString(Constants.select_names[it])) }

        val spinnerAdatper = MySpinnerAdapter(spinnerItemList)
        spinner.adapter = spinnerAdatper
    }

    fun subscribeToUiChanges() {
        RxView.clicks(fab).subscribe { presenter.onAction(MainViewActions.FabClicked) }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                presenter.onAction(MainViewActions.SpinnerItemClicked(i))
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                //noop
            }
        }
    }

    fun subscribeToPresenter() {
        dis += presenter.navigation.observeOn(mainThread()).subscribe { navigate(it) }
        dis += presenter.viewState.observeOn(mainThread()).subscribe { renderView(it) }
    }

    fun renderView(state: MainViewState) {
        state.items?.let {
            val mainListAdapter = recyclerView.adapter as MainListAdapter
            mainListAdapter.setDataList(state.items)
            if (state.items.isEmpty()) {
                stateView.setEmptyState(null)
            } else {
                stateView.setNormalState()
            }
        }
        state.style?.let {
            spinner.setSelection(it)
        }

    }

    fun navigate(navigation: MainViewNavigation) {
        when (navigation) {
            is MainViewNavigation.NavigateNoteDetails -> ContentActivity.openNote(this, navigation.id)
            is MainViewNavigation.NavigateNewNote -> ContentActivity.newNote(this, navigation.style)
            MainViewNavigation.NavigateAbout -> startActivity(Intent(this, PrefActivity::class.java))
            is MainViewNavigation.ConfirmDeleteItem -> {
                alertDialog = AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog)
                    .setTitle(getString(R.string.delete_note_dialog_title))
                    .setTitle(getString(R.string.delete_note_dialog_message))
                    .setNegativeButton(android.R.string.cancel, { _, _ -> })
                    .setPositiveButton(android.R.string.ok, { _, _ ->
                        presenter.onAction(MainViewActions.DeleteListItemConfirmed(navigation.noteId))
                    })
                    .show()
            }
        }
    }
}
