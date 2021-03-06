package com.omar.deathnote.notes

import android.Manifest.permission.CAMERA
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import com.alkurop.github.mediapicker.MediaPicker
import com.alkurop.github.mediapicker.MediaType
import com.github.alkurop.jpermissionmanager.PermissionOptionalDetails
import com.github.alkurop.jpermissionmanager.PermissionsManager
import com.jakewharton.rxbinding2.view.RxView
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.Constants
import com.omar.deathnote.R
import com.omar.deathnote.main.MySpinnerAdapter
import com.omar.deathnote.main.SpinnerItem
import com.omar.deathnote.notes.content.ContentAdapter
import com.omar.deathnote.notes.content.ContentType
import com.omar.deathnote.notes.content.add.bll.AddDialogPresenter
import com.omar.deathnote.notes.content.add.ui.AddDialog
import com.omar.deathnote.utility.AudioWrapper
import com.omar.deathnote.utility.plusAssign
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_content.background
import kotlinx.android.synthetic.main.activity_content.fab
import kotlinx.android.synthetic.main.activity_content.recyclerView
import kotlinx.android.synthetic.main.toolbar.spinner
import kotlinx.android.synthetic.main.toolbar.toolbar
import javax.inject.Inject

class ContentActivity : AppCompatActivity() {

    companion object {

        private const val NO_ID = -1L
        private const val DEFAULT_STYLE = 1
        private const val KEY_ID = "key_id"
        private const val KEY_STYLE = "key_style"

        fun openNote(activity: AppCompatActivity, id: Long? = NO_ID) {
            val intent = Intent(activity, ContentActivity::class.java)
            intent.putExtra(KEY_ID, id)
            activity.startActivity(intent)
        }

        fun newNote(activity: AppCompatActivity, style: Int) {
            val intent = Intent(activity, ContentActivity::class.java)
            intent.putExtra(KEY_STYLE, style)
            activity.startActivity(intent)
        }
    }

    @Inject
    lateinit var presenter: ContentPresenter

    @Inject
    lateinit var audioWrapper: AudioWrapper

    lateinit var permissionMananager: PermissionsManager

    val dis = CompositeDisposable()

    var alertDialog: AlertDialog? = null
    var progressDialog: ProgressDialog? = null

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

        permissionMananager = PermissionsManager(this)
        val subscribe = MediaPicker.getResult(this)
            .subscribe {
                if (!it.first.equals(MediaType.LOADING)) {
                    showLoading(false)
                }
                when (it.first) {
                    MediaType.AUDIO -> {
                        recyclerView.smoothScrollToPosition(recyclerView.adapter.itemCount - 1)
                        presenter.onAction(ContentAction.AddContent(ContentType.AUDIO_FILE, it.second.toString()))
                    }
                    MediaType.PHOTO -> {
                        recyclerView.smoothScrollToPosition(recyclerView.adapter.itemCount - 1)
                        presenter.onAction(ContentAction.AddContent(ContentType.PICTURE_FILE, it.second.toString()))
                    }
                    MediaType.VIDEO -> {
                    }
                    MediaType.LOADING -> {
                        showLoading(true)
                    }
                }
            }
        dis += subscribe
    }

    private fun showLoading(shouldShow: Boolean) {
        progressDialog?.takeIf { it.isShowing }?.dismiss()
        if (shouldShow) {
            progressDialog = ProgressDialog.show(this, null, getString(R.string.please_wait), true, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialog?.takeIf { it.isShowing }?.dismiss()
        progressDialog?.takeIf { it.isShowing }?.dismiss()
        if (isFinishing) {
            audioWrapper.stopRecording()
            audioWrapper.tearDown()
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
            R.id.action_share -> presenter.onAction(ContentAction.ShareClicked)
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        permissionMananager.onActivityResult(requestCode)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionMananager.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initSpinner() {
        val spinnerItemList = Constants
            .select_images
            .indices
            .filter { it != 0 }
            .map { SpinnerItem(Constants.select_images[it], getString(Constants.select_names[it])) }

        val spinnerAdatper = MySpinnerAdapter(spinnerItemList)
        spinner.adapter = spinnerAdatper
    }

    private fun initList() {
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun subscribeToListeners() {
        RxView.clicks(fab).subscribe { presenter.onAction(ContentAction.FabClicked) }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                presenter.onAction(ContentAction.UpdateStyle(i + 1))
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                //noop
            }
        }
    }

    private fun subscribeToPresenter() {
        dis += presenter.navigation.observeOn(AndroidSchedulers.mainThread()).subscribe { navigate(it) }
        dis += presenter.viewState.distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread()).subscribe { renderView(it) }
    }

    private fun renderView(state: NoteViewModel) {
        state.content?.let {
            if (recyclerView.adapter == null) {
                recyclerView.adapter = ContentAdapter { presenter.onAction(ContentAction.DeleteContent(it)) }
            }
            (recyclerView.adapter as ContentAdapter).updateList(state.content)
        }
        spinner.setSelection(state.style - 1)
        Picasso.with(this).load(Constants.note_bg_images[state.style - 1]).into(background)
    }

    private fun navigate(navigation: ContentNavigation) {
        when (navigation) {
            ContentNavigation.ContentSelector -> {
                val dialogPresenter = AddDialogPresenter()
                dialogPresenter.init { content ->
                    when (content!!) {
                        ContentType.PICTURE_FILE -> askGaleryPermissions { openImageGal() }
                        ContentType.PICTURE_CAPTURE -> askCameraPermissions { captureImageCamera() }
                        ContentType.LINK,
                        ContentType.NOTE -> {
                            recyclerView.smoothScrollToPosition(recyclerView.adapter.itemCount - 1)
                            presenter.onAction(ContentAction.AddContent(content))
                        }
                        ContentType.AUDIO_RECORD -> askRecordPermissions {
                            recyclerView.smoothScrollToPosition(recyclerView.adapter.itemCount - 1)
                            presenter.onAction(ContentAction.AddContent(content))
                        }
                    }
                }
                val addDialog = AddDialog()
                addDialog.setEventHandler(dialogPresenter)
                dialogPresenter.setView(addDialog)
                addDialog.show(supportFragmentManager, "")
            }
            is ContentNavigation.ConfirmDeleteContent -> {
                alertDialog = AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog)
                    .setTitle(getString(R.string.delete_content_dialog_title))
                    .setTitle(getString(R.string.delete_content_dialog_message))
                    .setNegativeButton(android.R.string.cancel) { _, _ -> }
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        presenter.onAction(ContentAction.DeleteContentConfirmed(navigation.id))
                    }
                    .show()
            }
        }
    }

    private fun captureImageCamera() {
        MediaPicker.fromCamera(this, MediaType.PHOTO)
    }

    private fun openImageGal() {
        MediaPicker.fromGallery(this, MediaType.PHOTO)
    }

    private fun askCameraPermissions(onSuccessOperator: (() -> Unit)) {
        permissionMananager.clearPermissionsListeners()
        permissionMananager.clearPermissions()

        val permissionCameraDetails = PermissionOptionalDetails(
            getString(R.string.camera_permission_title),
            getString(R.string.camera_permission_message)
        )

        val permissionStorageDetails = PermissionOptionalDetails(
            getString(R.string.storage_permission_title),
            getString(R.string.storage_permission_message)
        )

        permissionMananager.addPermissions(
            mapOf(
                Pair(CAMERA, permissionCameraDetails),
                Pair(WRITE_EXTERNAL_STORAGE, permissionStorageDetails)
            )
        )

        permissionMananager.addPermissionsListener {
            for (mutableEntry in it) {
                if (mutableEntry.value.not()) {
                    return@addPermissionsListener
                }
            }
            onSuccessOperator.invoke()
        }
        permissionMananager.makePermissionRequest()
    }

    private fun askGaleryPermissions(onSuccessOperator: (() -> Unit)) {
        permissionMananager.clearPermissionsListeners()
        permissionMananager.clearPermissions()

        val permissionStorageDetails = PermissionOptionalDetails(
            getString(R.string.storage_permission_title),
            getString(R.string.storage_permission_message)
        )

        permissionMananager.addPermissions(
            mapOf(
                Pair(WRITE_EXTERNAL_STORAGE, permissionStorageDetails)
            )
        )

        permissionMananager.addPermissionsListener {
            for (mutableEntry in it) {
                if (mutableEntry.value.not()) {
                    return@addPermissionsListener
                }
            }
            onSuccessOperator.invoke()
        }
        permissionMananager.makePermissionRequest()
    }

    private fun askRecordPermissions(onSuccessOperator: (() -> Unit)) {
        permissionMananager.clearPermissionsListeners()
        permissionMananager.clearPermissions()

        val permissionMicDetails = PermissionOptionalDetails(
            getString(R.string.mic_permission_title),
            getString(R.string.mic_permission_message)
        )

        val permissionStorageDetails = PermissionOptionalDetails(
            getString(R.string.storage_permission_title),
            getString(R.string.storage_permission_message)
        )

        permissionMananager.addPermissions(
            mapOf(
                Pair(RECORD_AUDIO, permissionMicDetails),
                Pair(WRITE_EXTERNAL_STORAGE, permissionStorageDetails)
            )
        )

        permissionMananager.addPermissionsListener {
            for (mutableEntry in it) {
                if (mutableEntry.value.not()) {
                    return@addPermissionsListener
                }
            }
            onSuccessOperator.invoke()
        }
        permissionMananager.makePermissionRequest()
    }
}
