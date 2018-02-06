package com.omar.deathnote.notes.content.picture

import android.content.Intent
import android.graphics.Color
import android.support.v4.widget.DrawerLayout
import android.view.View
import android.view.ViewTreeObserver
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
import kotlinx.android.synthetic.main.note_elem_pic.view.contentView
import java.util.ArrayList
import javax.inject.Inject
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import kotlinx.android.synthetic.main.note_elem_pic.view.container
import kotlinx.android.synthetic.main.note_elem_pic.view.del


class PictureViewHolder(
        itemView: View?,
        onDeleteCallback: (Long) -> Unit
) : ContentViewHolder(itemView, onDeleteCallback) {

    @Inject
    lateinit var presenter: PicturePresenter

    val dis = CompositeDisposable()

    override fun bind(content: Content) {
        ComponentContainer.instance[ContentViewComponent::class.java].inject(this)
        presenter.content = content

        val transformation = RoundedTransformationBuilder()
            .cornerRadiusDp(20f)
            .oval(false)
            .build()

        itemView.contentView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                itemView.contentView.viewTreeObserver.removeOnPreDrawListener(this)
                Picasso.with(itemView.context)
                    .load(content.content)
                    .fit()
                    .transform(transformation)
                    .into(itemView.contentView)
                return true
            }
        })

        RxView.clicks(itemView.del).subscribe { onDeleteCallback.invoke(content.id) }

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
