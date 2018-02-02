package com.omar.deathnote.notes.picture

import android.content.Intent
import android.view.View
import com.alkurop.database.Content
import com.jakewharton.rxbinding2.view.RxView
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.Constants
import com.omar.deathnote.notes.ContentViewComponent
import com.omar.deathnote.notes.content.ContentViewHolder
import com.omar.deathnote.picview.SingleViewActivity
import com.omar.deathnote.utility.plusAssign
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.note_elem_pic.view.container
import kotlinx.android.synthetic.main.note_elem_pic.view.contentView
import kotlinx.android.synthetic.main.note_elem_pic.view.del
import java.util.ArrayList
import javax.inject.Inject

class PictureViewHolder(itemView: View?) : ContentViewHolder(itemView) {

    @Inject
    lateinit var presenter: PicturePresenter

    val dis = CompositeDisposable()

    override fun bind(content: Content) {
        ComponentContainer.instance[ContentViewComponent::class.java].inject(this)
        presenter.content = content

        Picasso.with(itemView.context).load(content.content)
            .resize(800, 800).centerInside()
            .into(itemView.contentView)

        RxView.clicks(itemView.del).subscribe { presenter.delete() }

        RxView.clicks(itemView.container).subscribe { presenter.openImageViewer() }
        dis += presenter.navSubject.observeOn(mainThread())
            .subscribe {
                val intent = Intent(itemView.context, SingleViewActivity::class.java)
                intent.putExtra(Constants.ID, it.position)
                intent.putStringArrayListExtra(Constants.LIST, ArrayList(it.imageContentListSorted))
                itemView.context.startActivity(intent)
            }
    }

    override fun unbind() {
        dis.clear()
    }

}
