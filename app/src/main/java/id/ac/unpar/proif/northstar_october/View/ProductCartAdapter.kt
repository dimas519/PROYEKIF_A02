package id.ac.unpar.proif.northstar_october.View

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import id.ac.unpar.proif.northstar_october.Model.Box
import id.ac.unpar.proif.northstar_october.Model.Code
import id.ac.unpar.proif.northstar_october.Presenter.CartPresenter
import id.ac.unpar.proif.northstar_october.databinding.ProductCartItemBinding
import java.text.FieldPosition

class ProductCartAdapter(private val activity: Activity, private var presenter: CartPresenter, private var fragmentManager: FragmentManager) : BaseAdapter() {

    lateinit var binding: ProductCartItemBinding
    var boxes: ArrayList<Box> = ArrayList()

    fun loadData (boxes: ArrayList<Box>) {
        this.boxes = boxes
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return boxes.size
    }

    override fun getItem(i: Int): Box {
        return boxes[i]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {

        var view: View? = view
        if (view == null) {
            binding = ProductCartItemBinding.inflate(activity.layoutInflater)
            view = binding.root
            view.setTag(binding)
        } else {
            binding = view.tag as ProductCartItemBinding
        }

        ViewHolder(getItem(i), binding)

        return view
    }

    inner class ViewHolder(private var currentBox: Box, private var binding:ProductCartItemBinding) : View.OnClickListener {

        init {
            binding.checkbox.setOnClickListener(this::onClick)
            binding.btnDecrease.setOnClickListener(this::onClick)
            binding.btnIncrease.setOnClickListener(this::onClick)

            binding.checkbox.isChecked = currentBox.getIsChecked()
            binding.tvQuantity.text = currentBox.getQuantity().toString()
            binding.tvName.text = currentBox.getProduct().getName()
            binding.tvCategory.text ="Category :"+currentBox.getProduct().getCategory().toString();
            binding.tvCondition.text ="Condition " + currentBox.getProduct().getCondition().toString()+"%";
            binding.tvPrice.text = currentBox.getProduct().getFormattedPrice()

            Glide.with(activity)
                .load(
                    activity.resources.getIdentifier(
                        currentBox.getProduct().getPhotos()[0],
                        "drawable",
                        activity.packageName
                    )
                )
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.ivProduct)
        }

        override fun onClick(view: View?) {
            when (view) {
                binding.checkbox -> {
                    presenter.toogleCheckBox(currentBox)
                    presenter.setTotalCost()
                }
                binding.btnDecrease -> {
                    if (currentBox.getQuantity() > 1) {
                        presenter.changeQuantity(currentBox, -1)
                        presenter.setTotalCost()
                    }else{
                        showDeleteDialog()
                    }
                }
                binding.btnIncrease -> {
                    presenter.changeQuantity(currentBox, 1)
                    presenter.setTotalCost()
                }
            }
        }

        private fun showDeleteDialog () {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(
                "Are you sure you want to delete?"
            )
                .setPositiveButton("Yes") { dialog, id ->
                    // DO SOMETHING
                    presenter.deleteProduct(currentBox)
                    presenter.setTotalCost()
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            builder.show()
        }

    }

}